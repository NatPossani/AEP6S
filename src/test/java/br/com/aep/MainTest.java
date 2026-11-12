package br.com.aep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import br.com.aep.cli.Terminal;
import br.com.aep.model.Doacao;
import br.com.aep.model.ItemDoacao;
import br.com.aep.repository.MongoConnection;
import br.com.aep.repository.MongoDoacaoRepository;

/**
 * Testa o menu de terminal sem banco de dados real: MongoConnection e
 * MongoDoacaoRepository são substituídos por mocks no momento em que o Main os instancia.
 */
class MainTest {

    private static final String VALIDADE =
            LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    private InputStream entradaOriginal;
    private PrintStream saidaOriginal;

    private MockedConstruction<MongoConnection> conexoes;
    private MockedConstruction<MongoDoacaoRepository> repositorios;
    private MongoDoacaoRepository repository;

    @BeforeEach
    void configurar() {
        entradaOriginal = System.in;
        saidaOriginal = System.out;

        repository = mock(MongoDoacaoRepository.class);
        conexoes = mockConstruction(MongoConnection.class);
        repositorios = mockConstruction(MongoDoacaoRepository.class,
                (mockCriado, contexto) -> configurarDelegacao(mockCriado));
    }

    @AfterEach
    void restaurar() {
        repositorios.close();
        conexoes.close();
        System.setIn(entradaOriginal);
        System.setOut(saidaOriginal);
    }

    /** Faz o repositório criado pelo Main delegar para o mock controlado pelo teste. */
    private void configurarDelegacao(MongoDoacaoRepository criado) {
        when(criado.listar()).thenAnswer(i -> repository.listar());
        when(criado.buscarPorCodigo(anyString()))
                .thenAnswer(i -> repository.buscarPorCodigo(i.getArgument(0)));
        when(criado.atualizar(anyString(), any(Doacao.class)))
                .thenAnswer(i -> repository.atualizar(i.getArgument(0), i.getArgument(1)));
        when(criado.excluir(anyString()))
                .thenAnswer(i -> repository.excluir(i.getArgument(0)));
        org.mockito.Mockito.doAnswer(i -> {
            repository.salvar(i.getArgument(0));
            return null;
        }).when(criado).salvar(any(Doacao.class));
    }

    private static Doacao doacaoExemplo() {
        return new Doacao("ABC12345", "D001", "I001", "01/01/2030", "RECEBIDA",
                List.of(new ItemDoacao("Arroz", 10, "kg", "31/12/2030")));
    }

    private String executarMain(String... linhas) {
        return Terminal.removerCores(executarMainComCores(linhas));
    }

    private String executarMainComCores(String... linhas) {
        String entrada = String.join("\n", linhas) + "\n";
        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        System.setIn(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));

        Main.main(new String[0]);

        return saida.toString(StandardCharsets.UTF_8);
    }

    @Test
    void devePermitirInstanciarMain() {
        assertNotNull(new Main());
    }

    @Test
    void deveExibirMenuEEncerrarFechandoConexao() {
        String saida = executarMain("0");

        assertTrue(saida.contains("BANCO DE ALIMENTOS COMUNITÁRIO"));
        assertTrue(saida.contains("MENU PRINCIPAL"));
        assertTrue(saida.contains("[1]  Cadastrar doação"));
        assertTrue(saida.contains("[5]  Excluir doação"));
        assertTrue(saida.contains("[0]  Sair"));
        assertTrue(saida.contains("Sistema encerrado. Até logo!"));

        assertEquals(1, conexoes.constructed().size());
        verify(conexoes.constructed().get(0)).fechar();
    }

    @Test
    void deveCadastrarDoacaoComMultiplosItens() {
        String saida = executarMain(
                "1",
                "d001",
                "i001",
                "Arroz", "10", "kg", VALIDADE,
                "Feijão", "5", "kg", VALIDADE,
                "",
                "0");

        assertTrue(saida.contains("NOVA DOAÇÃO"));
        assertTrue(saida.contains("┄ Item 1"));
        assertTrue(saida.contains("┄ Item 3"));
        assertTrue(saida.contains("✔ Item adicionado: 10 kg de Arroz"));
        assertTrue(saida.contains("✔ Item adicionado: 5 kg de Feijão"));
        assertTrue(saida.contains("✔ Doação cadastrada com sucesso!"));

        ArgumentCaptor<Doacao> captor = ArgumentCaptor.forClass(Doacao.class);
        verify(repository).salvar(captor.capture());

        Doacao salva = captor.getValue();
        assertEquals("D001", salva.getDoadorCodigo());
        assertEquals("I001", salva.getInstituicaoCodigo());
        assertEquals(2, salva.getItens().size());
        assertEquals("Arroz", salva.getItens().get(0).getAlimento());
        assertEquals("Feijão", salva.getItens().get(1).getAlimento());
        assertTrue(saida.contains("Código: " + salva.getCodigo()));
    }

    @Test
    void deveListarDoacoesComItens() {
        when(repository.listar()).thenReturn(List.of(doacaoExemplo()));

        String saida = executarMain("2", "0");

        assertTrue(saida.contains("DOAÇÕES CADASTRADAS"));
        assertTrue(saida.contains("│ CÓDIGO   │ DOADOR │ INSTITUIÇÃO │ DATA       │ STATUS   │ ITENS │"));
        assertTrue(saida.contains("│ ABC12345 │ D001   │ I001        │ 01/01/2030 │ RECEBIDA │ 1     │"));
        assertTrue(saida.contains("1 doação encontrada."));
    }

    @Test
    void deveListarDoacaoSemItens() {
        Doacao semItens = mock(Doacao.class);
        when(semItens.getCodigo()).thenReturn("SEMITENS");
        when(semItens.getItens()).thenReturn(null);
        when(repository.listar()).thenReturn(List.of(semItens));

        String saida = executarMain("2", "0");

        assertTrue(saida.contains("│ SEMITENS │ —      │ —           │ —    │ —      │ 0     │"));
        assertTrue(saida.contains("1 doação encontrada."));
    }

    @Test
    void deveInformarQuandoNaoHouverDoacoes() {
        when(repository.listar()).thenReturn(List.of());

        String saida = executarMain("2", "0");

        assertTrue(saida.contains("! Nenhuma doação cadastrada."));
    }

    @Test
    void deveBuscarDoacaoExistente() {
        when(repository.buscarPorCodigo("ABC12345")).thenReturn(doacaoExemplo());

        String saida = executarMain("3", "abc12345", "0");

        assertTrue(saida.contains("┌─ DOAÇÃO ABC12345 "));
        assertTrue(saida.contains("Doador      D001"));
        assertTrue(saida.contains("Instituição I001"));
        assertTrue(saida.contains("Data        01/01/2030"));
        assertTrue(saida.contains("Status      RECEBIDA"));
        assertTrue(saida.contains("│ ALIMENTO │ QTD │ UNIDADE │ VALIDADE   │"));
        assertTrue(saida.contains("│ Arroz    │ 10  │ kg      │ 31/12/2030 │"));
    }

    @Test
    void deveInformarBuscaDeCodigoInexistente() {
        String saida = executarMain("3", "inexistente", "0");

        assertTrue(saida.contains("! Doação não encontrada."));
    }

    @Test
    void deveAtualizarDoacaoExistente() {
        when(repository.buscarPorCodigo("ABC12345")).thenReturn(doacaoExemplo());
        when(repository.atualizar(eq("ABC12345"), any(Doacao.class))).thenReturn(true);

        String saida = executarMain(
                "4", "abc12345",
                "D002", "I002",
                "Macarrão", "8", "pacote", VALIDADE,
                "",
                "0");

        assertTrue(saida.contains("ATUALIZAR DOAÇÃO"));
        assertTrue(saida.contains("┌─ DOAÇÃO ABC12345 "));
        assertTrue(saida.contains("Informe os novos dados da doação."));
        assertTrue(saida.contains("✔ Doação atualizada com sucesso!"));

        ArgumentCaptor<Doacao> captor = ArgumentCaptor.forClass(Doacao.class);
        verify(repository).atualizar(eq("ABC12345"), captor.capture());
        assertEquals("D002", captor.getValue().getDoadorCodigo());
        assertEquals("Macarrão", captor.getValue().getItens().get(0).getAlimento());
    }

    @Test
    void deveInformarFalhaAoAtualizar() {
        when(repository.buscarPorCodigo("ABC12345")).thenReturn(doacaoExemplo());
        when(repository.atualizar(eq("ABC12345"), any(Doacao.class))).thenReturn(false);

        String saida = executarMain(
                "4", "ABC12345",
                "D002", "I002",
                "Arroz", "1", "kg", VALIDADE,
                "",
                "0");

        assertTrue(saida.contains("✖ Não foi possível atualizar a doação."));
    }

    @Test
    void deveInformarAtualizacaoDeCodigoInexistente() {
        String saida = executarMain("4", "INEXISTENTE", "0");

        assertTrue(saida.contains("! Doação não encontrada."));
        verify(repository, never()).atualizar(anyString(), any(Doacao.class));
    }

    @Test
    void deveExcluirDoacao() {
        when(repository.excluir("ABC12345")).thenReturn(true);

        String saida = executarMain("5", "abc12345", "0");

        assertTrue(saida.contains("EXCLUIR DOAÇÃO"));
        assertTrue(saida.contains("✔ Doação excluída com sucesso!"));
    }

    @Test
    void deveInformarExclusaoDeCodigoInexistente() {
        when(repository.excluir("INEXISTENTE")).thenReturn(false);

        String saida = executarMain("5", "INEXISTENTE", "0");

        assertTrue(saida.contains("! Doação não encontrada."));
    }

    @Test
    void deveTratarOpcaoInvalida() {
        String saida = executarMain("9", "0");

        assertTrue(saida.contains("✖ Opção inválida. Escolha um número entre 0 e 5."));
        assertTrue(saida.contains("Sistema encerrado."));
    }

    @Test
    void deveTratarQuantidadeInvalida() {
        String saida = executarMain("1", "D001", "I001", "Arroz", "abc", "0");

        assertTrue(saida.contains("✖ Quantidade inválida. Informe apenas números inteiros."));
        verify(repository, never()).salvar(any(Doacao.class));
    }

    @Test
    void deveTratarErroDeValidacao() {
        String saida = executarMain("1", "", "I001", "Arroz", "10", "kg", VALIDADE, "", "0");

        assertTrue(saida.contains("✖ Código do doador é obrigatório."));
        verify(repository, never()).salvar(any(Doacao.class));
    }

    @Test
    void deveTratarErroInesperado() {
        when(repository.listar()).thenThrow(new RuntimeException("falha no banco"));

        String saida = executarMain("2", "0");

        assertTrue(saida.contains("✖ Não foi possível concluir a operação."));
        assertTrue(saida.contains("Sistema encerrado."));
    }

    @Test
    void deveExibirCoresAnsiNaSaida() {
        String saida = executarMainComCores("0");

        assertTrue(saida.contains(Terminal.CIANO));
        assertTrue(saida.contains(Terminal.RESET));
    }

    @Test
    void deveAceitarOpcaoEQuantidadeComEspacos() {
        String saida = executarMain(
                " 1 ", "D001", "I001",
                "Arroz", " 7 ", "kg", VALIDADE,
                "",
                " 0 ");

        assertTrue(saida.contains("✔ Item adicionado: 7 kg de Arroz"));
        assertTrue(saida.contains("✔ Doação cadastrada com sucesso!"));
        assertTrue(saida.contains("Sistema encerrado."));
    }

    @Test
    void deveInformarQuantidadeDeDoacoesNoPlural() {
        Doacao outra = new Doacao("XYZ98765", "D002", "I002", "02/01/2030", "RECEBIDA",
                List.of(new ItemDoacao("Leite", 2, "litros", "01/02/2030")));
        when(repository.listar()).thenReturn(List.of(doacaoExemplo(), outra));

        String saida = executarMain("2", "0");

        assertTrue(saida.contains("│ XYZ98765 │"));
        assertTrue(saida.contains("2 doações encontradas."));
    }

    @Test
    void deveBuscarDoacaoSemItens() {
        when(repository.buscarPorCodigo("VAZIA")).thenReturn(
                new Doacao("VAZIA", "D001", "I001", "01/01/2030", "RECEBIDA", List.of()));

        String saida = executarMain("3", "vazia", "0");

        assertTrue(saida.contains("┌─ DOAÇÃO VAZIA "));
        assertTrue(saida.contains("! Nenhum item registrado nesta doação."));
    }

    @Test
    void deveBuscarDoacaoComCamposEItensNulos() {
        Doacao incompleta = mock(Doacao.class);
        when(incompleta.getItens()).thenReturn(null);
        when(repository.buscarPorCodigo("INCOMPLETA")).thenReturn(incompleta);

        String saida = executarMain("3", "INCOMPLETA", "0");

        assertTrue(saida.contains("┌─ DOAÇÃO — "));
        assertTrue(saida.contains("Doador      —"));
        assertTrue(saida.contains("Status      —"));
        assertTrue(saida.contains("! Nenhum item registrado nesta doação."));
    }

    @Test
    void deveExibirItemComCamposNulos() {
        Doacao doacao = new Doacao("NULOS", "D001", "I001", "01/01/2030", "RECEBIDA",
                List.of(new ItemDoacao(null, 3, null, null)));
        when(repository.buscarPorCodigo("NULOS")).thenReturn(doacao);

        String saida = executarMain("3", "NULOS", "0");

        assertTrue(saida.contains("│ —        │ 3   │ —       │ —        │"));
    }
}

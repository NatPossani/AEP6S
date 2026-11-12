package br.com.aep.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import br.com.aep.model.Doacao;
import br.com.aep.model.ItemDoacao;
import br.com.aep.repository.DoacaoRepository;

class DoacaoServiceTest {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DoacaoRepository repository;
    private DoacaoService service;

    @BeforeEach
    void configurar() {
        repository = mock(DoacaoRepository.class);
        service = new DoacaoService(repository);
    }

    private static String dataFutura() {
        return LocalDate.now().plusDays(30).format(FORMATO);
    }

    private static ItemDoacao itemValido() {
        return new ItemDoacao("Arroz", 10, "kg", dataFutura());
    }

    private static List<ItemDoacao> itensValidos() {
        return List.of(itemValido());
    }

    private void assertErroCadastro(String mensagem, String doador, String instituicao,
                                    List<ItemDoacao> itens) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(doador, instituicao, itens)
        );

        assertEquals(mensagem, exception.getMessage());
        verifyNoInteractions(repository);
    }

    private void assertErroItem(String mensagem, ItemDoacao item) {
        List<ItemDoacao> itens = new ArrayList<>();
        itens.add(item);
        assertErroCadastro(mensagem, "D001", "I001", itens);
    }

    // ---------- cadastrar ----------

    @Test
    void deveCadastrarDoacaoNormalizandoDados() {
        String validade = dataFutura();
        List<ItemDoacao> itens = List.of(new ItemDoacao("  Arroz  ", 10, " kg ", validade));

        Doacao doacao = service.cadastrar(" d001 ", " i001 ", itens);

        assertNotNull(doacao.getCodigo());
        assertEquals(8, doacao.getCodigo().length());
        assertEquals(doacao.getCodigo().toUpperCase(), doacao.getCodigo());
        assertEquals("D001", doacao.getDoadorCodigo());
        assertEquals("I001", doacao.getInstituicaoCodigo());
        assertEquals(LocalDate.now().format(FORMATO), doacao.getDataRegistro());
        assertEquals("RECEBIDA", doacao.getStatus());
        assertEquals(1, doacao.getItens().size());

        ItemDoacao item = doacao.getItens().get(0);
        assertEquals("Arroz", item.getAlimento());
        assertEquals(10, item.getQuantidade());
        assertEquals("kg", item.getUnidade());
        assertEquals(validade, item.getValidade());

        verify(repository).salvar(doacao);
    }

    @Test
    void deveCadastrarComValidadeIgualAHoje() {
        String hoje = LocalDate.now().format(FORMATO);

        Doacao doacao = service.cadastrar("D001", "I001",
                List.of(new ItemDoacao("Leite", 2, "litros", hoje)));

        assertEquals(hoje, doacao.getItens().get(0).getValidade());
        verify(repository).salvar(doacao);
    }

    @Test
    void naoDeveCadastrarSemDoadorNulo() {
        assertErroCadastro("Código do doador é obrigatório.", null, "I001", itensValidos());
    }

    @Test
    void naoDeveCadastrarSemDoadorEmBranco() {
        assertErroCadastro("Código do doador é obrigatório.", "  ", "I001", itensValidos());
    }

    @Test
    void naoDeveCadastrarSemInstituicaoNula() {
        assertErroCadastro("Código da instituição é obrigatório.", "D001", null, itensValidos());
    }

    @Test
    void naoDeveCadastrarSemInstituicaoEmBranco() {
        assertErroCadastro("Código da instituição é obrigatório.", "D001", "", itensValidos());
    }

    @Test
    void naoDeveCadastrarComItensNulos() {
        assertErroCadastro("A doação deve ter pelo menos um item.", "D001", "I001", null);
    }

    @Test
    void naoDeveCadastrarComItensVazios() {
        assertErroCadastro("A doação deve ter pelo menos um item.", "D001", "I001",
                Collections.emptyList());
    }

    @Test
    void naoDeveCadastrarComItemNulo() {
        assertErroItem("Item da doação é obrigatório.", null);
    }

    @Test
    void naoDeveCadastrarComAlimentoNulo() {
        assertErroItem("Alimento é obrigatório.", new ItemDoacao(null, 10, "kg", dataFutura()));
    }

    @Test
    void naoDeveCadastrarComAlimentoEmBranco() {
        assertErroItem("Alimento é obrigatório.", new ItemDoacao(" ", 10, "kg", dataFutura()));
    }

    @Test
    void naoDeveCadastrarComQuantidadeZero() {
        assertErroItem("Quantidade deve ser maior que zero.",
                new ItemDoacao("Arroz", 0, "kg", dataFutura()));
    }

    @Test
    void naoDeveCadastrarComQuantidadeNegativa() {
        assertErroItem("Quantidade deve ser maior que zero.",
                new ItemDoacao("Arroz", -1, "kg", dataFutura()));
    }

    @Test
    void naoDeveCadastrarComUnidadeNula() {
        assertErroItem("Unidade é obrigatória.", new ItemDoacao("Arroz", 10, null, dataFutura()));
    }

    @Test
    void naoDeveCadastrarComUnidadeEmBranco() {
        assertErroItem("Unidade é obrigatória.", new ItemDoacao("Arroz", 10, "", dataFutura()));
    }

    @Test
    void naoDeveCadastrarComValidadeNula() {
        assertErroItem("Validade é obrigatória.", new ItemDoacao("Arroz", 10, "kg", null));
    }

    @Test
    void naoDeveCadastrarComValidadeEmBranco() {
        assertErroItem("Validade é obrigatória.", new ItemDoacao("Arroz", 10, "kg", " "));
    }

    @Test
    void naoDeveCadastrarComValidadeEmFormatoInvalido() {
        assertErroItem("Validade deve estar no formato dd/MM/yyyy.",
                new ItemDoacao("Arroz", 10, "kg", "2030-12-31"));
    }

    @Test
    void naoDeveCadastrarComValidadeVencida() {
        String ontem = LocalDate.now().minusDays(1).format(FORMATO);

        assertErroItem("A validade não pode ser anterior à data atual.",
                new ItemDoacao("Arroz", 10, "kg", ontem));
    }

    @Test
    void naoDeveCadastrarQuandoSegundoItemForInvalido() {
        List<ItemDoacao> itens = List.of(
                itemValido(),
                new ItemDoacao("Feijão", 0, "kg", dataFutura())
        );

        assertErroCadastro("Quantidade deve ser maior que zero.", "D001", "I001", itens);
    }

    // ---------- listar ----------

    @Test
    void deveListarDoacoes() {
        Doacao doacao = new Doacao("ABC12345", "D001", "I001",
                "01/01/2030", "RECEBIDA", itensValidos());
        List<Doacao> doacoes = List.of(doacao);

        when(repository.listar()).thenReturn(doacoes);

        assertSame(doacoes, service.listar());
        verify(repository).listar();
    }

    // ---------- buscar ----------

    @Test
    void deveBuscarDoacaoNormalizandoCodigo() {
        Doacao doacao = new Doacao("ABC12345", "D001", "I001",
                "01/01/2030", "RECEBIDA", itensValidos());

        when(repository.buscarPorCodigo("ABC12345")).thenReturn(doacao);

        assertSame(doacao, service.buscar(" abc12345 "));
    }

    @Test
    void deveRetornarNuloAoBuscarCodigoNulo() {
        assertNull(service.buscar(null));
        verifyNoInteractions(repository);
    }

    @Test
    void deveRetornarNuloAoBuscarCodigoEmBranco() {
        assertNull(service.buscar(" "));
        verifyNoInteractions(repository);
    }

    // ---------- atualizar ----------

    @Test
    void deveAtualizarDoacaoNormalizandoDados() {
        when(repository.atualizar(eq("ABC12345"), any(Doacao.class))).thenReturn(true);

        boolean resultado = service.atualizar(" abc12345 ", " d002 ", " i002 ",
                List.of(new ItemDoacao(" Feijão ", 15, " kg ", dataFutura())));

        assertTrue(resultado);

        ArgumentCaptor<Doacao> captor = ArgumentCaptor.forClass(Doacao.class);
        verify(repository).atualizar(eq("ABC12345"), captor.capture());

        Doacao enviada = captor.getValue();
        assertEquals("ABC12345", enviada.getCodigo());
        assertEquals("D002", enviada.getDoadorCodigo());
        assertEquals("I002", enviada.getInstituicaoCodigo());
        assertEquals(LocalDate.now().format(FORMATO), enviada.getDataRegistro());
        assertEquals("RECEBIDA", enviada.getStatus());
        assertEquals("Feijão", enviada.getItens().get(0).getAlimento());
        assertEquals("kg", enviada.getItens().get(0).getUnidade());
    }

    @Test
    void deveRetornarFalsoQuandoRepositorioNaoAtualizar() {
        when(repository.atualizar(eq("ABC12345"), any(Doacao.class))).thenReturn(false);

        assertFalse(service.atualizar("ABC12345", "D001", "I001", itensValidos()));
    }

    @Test
    void naoDeveAtualizarComDadosInvalidos() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizar("ABC12345", "", "I001", itensValidos())
        );

        assertEquals("Código do doador é obrigatório.", exception.getMessage());
        verifyNoInteractions(repository);
    }

    // ---------- excluir ----------

    @Test
    void deveExcluirDoacaoNormalizandoCodigo() {
        when(repository.excluir("ABC12345")).thenReturn(true);

        assertTrue(service.excluir(" abc12345 "));
        verify(repository).excluir("ABC12345");
    }

    @Test
    void deveRetornarFalsoQuandoRepositorioNaoExcluir() {
        when(repository.excluir("ABC12345")).thenReturn(false);

        assertFalse(service.excluir("ABC12345"));
    }

    @Test
    void deveRetornarFalsoAoExcluirCodigoNulo() {
        assertFalse(service.excluir(null));
        verifyNoInteractions(repository);
    }

    @Test
    void deveRetornarFalsoAoExcluirCodigoEmBranco() {
        assertFalse(service.excluir(""));
        verifyNoInteractions(repository);
    }
}

package br.com.aep;

import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.aep.model.Doacao;
import br.com.aep.repository.MongoDoacaoRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest {

    private InputStream entradaOriginal;
    private PrintStream saidaOriginal;

    @BeforeEach
    void configurarTerminal() {
        entradaOriginal = System.in;
        saidaOriginal = System.out;
    }

    @AfterEach
    void restaurarTerminal() {
        System.setIn(entradaOriginal);
        System.setOut(saidaOriginal);
    }

    @Test
    void deveCadastrarListarEEncerrarSistema() {
        String entrada = String.join("\n",
                "1",
                "Doador Main Teste",
                "Arroz",
                "10",
                "kg",
                "31/12/2026",
                "Cesta básica",
                "2",
                "0"
        ) + "\n";

        String saida = executarMain(entrada);

        assertTrue(saida.contains("Doação cadastrada com sucesso!"));
        assertTrue(saida.contains("Doador Main Teste"));
        assertTrue(saida.contains("Arroz"));
        assertTrue(saida.contains("Sistema encerrado."));

        limparDoacoesPorDoador("Doador Main Teste");
    }

    @Test
    void deveBuscarAtualizarEExcluirDoacao() {
        MongoDoacaoRepository repository =
                new MongoDoacaoRepository("mongodb://localhost:27017");

        Doacao doacao = new Doacao(
                "MAIN-TEST-001",
                "Doador Original",
                "Feijão",
                5,
                "kg",
                "30/11/2026",
                "Instituição original"
        );

        repository.salvar(doacao);
        repository.fechar();

        String entrada = String.join("\n",
                "3",
                "MAIN-TEST-001",
                "4",
                "MAIN-TEST-001",
                "Doador Atualizado",
                "Arroz",
                "20",
                "kg",
                "31/12/2026",
                "Cesta básica",
                "3",
                "MAIN-TEST-001",
                "5",
                "MAIN-TEST-001",
                "0"
        ) + "\n";

        String saida = executarMain(entrada);

        assertTrue(saida.contains("Doador Original"));
        assertTrue(saida.contains("Doação atualizada com sucesso!"));
        assertTrue(saida.contains("Doador Atualizado"));
        assertTrue(saida.contains("Doação excluída com sucesso!"));
        assertTrue(saida.contains("Sistema encerrado."));

        MongoDoacaoRepository limpeza =
                new MongoDoacaoRepository("mongodb://localhost:27017");

        assertTrue(limpeza.buscarPorCodigo("MAIN-TEST-001") == null);

        limpeza.fechar();
    }

    @Test
    void deveTratarOpcaoInvalida() {
        String entrada = String.join("\n",
                "9",
                "0"
        ) + "\n";

        String saida = executarMain(entrada);

        assertTrue(saida.contains("Opção inválida."));
        assertTrue(saida.contains("Sistema encerrado."));
    }

    @Test
    void deveTratarQuantidadeInvalida() {
        String entrada = String.join("\n",
                "1",
                "Doador Quantidade Teste",
                "Arroz",
                "abc",
                "0"
        ) + "\n";

        String saida = executarMain(entrada);

        assertTrue(saida.contains("Quantidade inválida."));
        assertTrue(saida.contains("Sistema encerrado."));
    }

    @Test
    void deveTratarDadosObrigatoriosNaoInformados() {
        String entrada = String.join("\n",
                "1",
                "",
                "Arroz",
                "10",
                "kg",
                "31/12/2026",
                "Cesta básica",
                "0"
        ) + "\n";

        String saida = executarMain(entrada);

        assertTrue(saida.contains("Erro:"));
    }

    @Test
    void deveTratarBuscaComCodigoInexistente() {
        String entrada = String.join("\n",
                "3",
                "CODIGO-MAIN-INEXISTENTE",
                "0"
        ) + "\n";

        String saida = executarMain(entrada);

        assertTrue(saida.contains("Doação não encontrada."));
        assertTrue(saida.contains("Sistema encerrado."));
    }

    @Test
    void deveTratarAtualizacaoDeCodigoInexistente() {
        String entrada = String.join("\n",
                "4",
                "CODIGO-MAIN-INEXISTENTE",
                "Doador Teste",
                "Arroz",
                "10",
                "kg",
                "31/12/2026",
                "Cesta básica",
                "0"
        ) + "\n";

        String saida = executarMain(entrada);

        assertTrue(saida.contains("Doação não encontrada."));
        assertTrue(saida.contains("Sistema encerrado."));
    }

    @Test
    void deveTratarExclusaoDeCodigoInexistente() {
        String entrada = String.join("\n",
                "5",
                "CODIGO-MAIN-INEXISTENTE",
                "0"
        ) + "\n";

        String saida = executarMain(entrada);

        assertTrue(saida.contains("Doação não encontrada."));
        assertTrue(saida.contains("Sistema encerrado."));
    }

    private String executarMain(String entrada) {
        ByteArrayInputStream entradaSimulada =
                new ByteArrayInputStream(
                        entrada.getBytes(StandardCharsets.UTF_8)
                );

        ByteArrayOutputStream saidaCapturada =
                new ByteArrayOutputStream();

        PrintStream printStream =
                new PrintStream(saidaCapturada, true, StandardCharsets.UTF_8);

        System.setIn(entradaSimulada);
        System.setOut(printStream);

        Main.main(new String[0]);

        return saidaCapturada.toString(StandardCharsets.UTF_8);
    }

    private void limparDoacoesPorDoador(String nomeDoador) {
        MongoDoacaoRepository repository =
                new MongoDoacaoRepository("mongodb://localhost:27017");

        for (Doacao doacao : repository.listar()) {
            if (nomeDoador.equals(doacao.getNomeDoador())) {
                repository.excluir(doacao.getCodigo());
            }
        }

        repository.fechar();
    }
}
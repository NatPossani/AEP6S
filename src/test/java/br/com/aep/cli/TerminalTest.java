package br.com.aep.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TerminalTest {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private Terminal terminal(boolean cores) {
        return new Terminal(new PrintStream(buffer, true, StandardCharsets.UTF_8), cores);
    }

    private String saida() {
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private List<String> linhasSemCores() {
        return Terminal.removerCores(saida()).lines().toList();
    }

    // ---------- configuração de cores ----------

    @Test
    void deveHabilitarCoresQuandoNoColorNaoEstiverDefinido() {
        assertTrue(Terminal.coresHabilitadas(Map.of()));
    }

    @Test
    void deveHabilitarCoresQuandoNoColorEstiverVazio() {
        assertTrue(Terminal.coresHabilitadas(Map.of("NO_COLOR", "")));
    }

    @Test
    void deveDesabilitarCoresQuandoNoColorEstiverDefinido() {
        assertFalse(Terminal.coresHabilitadas(Map.of("NO_COLOR", "1")));
    }

    @Test
    void deveCriarTerminalPadrao() {
        assertNotNull(Terminal.padrao());
    }

    // ---------- estilos ----------

    @Test
    void deveAplicarCodigosAnsiQuandoCoresHabilitadas() {
        Terminal terminal = terminal(true);

        assertEquals(Terminal.NEGRITO + Terminal.VERDE + "ok" + Terminal.RESET,
                terminal.estilo("ok", Terminal.NEGRITO, Terminal.VERDE));
        assertEquals(Terminal.NEGRITO + "a" + Terminal.RESET, terminal.negrito("a"));
        assertEquals(Terminal.FRACO + "b" + Terminal.RESET, terminal.fraco("b"));
        assertEquals(Terminal.NEGRITO + Terminal.CIANO + "c" + Terminal.RESET,
                terminal.destaque("c"));
    }

    @Test
    void naoDeveAlterarTextoSemCodigos() {
        assertEquals("texto", terminal(true).estilo("texto"));
    }

    @Test
    void naoDeveAplicarCodigosQuandoCoresDesabilitadas() {
        Terminal terminal = terminal(false);

        assertEquals("ok", terminal.estilo("ok", Terminal.VERDE));
        assertEquals("a", terminal.negrito("a"));
    }

    @Test
    void naoDeveEmitirCodigosAnsiQuandoCoresDesabilitadas() {
        Terminal terminal = terminal(false);

        terminal.banner("TITULO", "subtitulo");
        terminal.secao("SECAO");
        terminal.caixa("CAIXA", List.of("linha"));
        terminal.tabela(List.of("A"), List.of(List.of("1")));
        terminal.sucesso("s");
        terminal.erro("e");
        terminal.aviso("a");
        terminal.info("i");
        terminal.dica("d");
        terminal.prompt("p");

        assertFalse(saida().contains("\u001B["));
    }

    @Test
    void deveRemoverCoresECalcularLarguraVisivel() {
        String colorido = Terminal.VERMELHO + "Ação" + Terminal.RESET;

        assertEquals("Ação", Terminal.removerCores(colorido));
        assertEquals(4, Terminal.larguraVisivel(colorido));
    }

    // ---------- blocos ----------

    @Test
    void deveImprimirBannerCentralizado() {
        terminal(true).banner("TITULO", "sub");

        List<String> linhas = linhasSemCores();
        assertEquals("", linhas.get(0));
        assertEquals("  ╔" + "═".repeat(Terminal.LARGURA) + "╗", linhas.get(1));
        assertEquals("  ║" + " ".repeat(26) + "TITULO" + " ".repeat(26) + "║", linhas.get(2));
        assertEquals("  ║" + " ".repeat(27) + "sub" + " ".repeat(28) + "║", linhas.get(3));
        assertEquals("  ╚" + "═".repeat(Terminal.LARGURA) + "╝", linhas.get(4));
    }

    @Test
    void naoDeveQuebrarBannerComTituloMaiorQueALargura() {
        String longo = "X".repeat(Terminal.LARGURA + 5);

        terminal(false).banner(longo, "");

        assertEquals("  ║" + longo + "║", linhasSemCores().get(2));
    }

    @Test
    void deveImprimirSecao() {
        terminal(true).secao("TITULO");

        List<String> linhas = linhasSemCores();
        assertEquals("", linhas.get(0));
        assertEquals("  ━━ TITULO " + "━".repeat(Terminal.LARGURA - 10), linhas.get(1));
    }

    @Test
    void deveImprimirSecaoComTituloLongo() {
        String longo = "X".repeat(Terminal.LARGURA + 5);

        terminal(false).secao(longo);

        assertEquals("  ━━ " + longo + " ", linhasSemCores().get(1));
    }

    @Test
    void deveImprimirSubtitulo() {
        terminal(true).subtitulo("Item 1");

        assertEquals(List.of("", "  ┄ Item 1"), linhasSemCores());
    }

    @Test
    void deveImprimirCaixaAlinhada() {
        Terminal terminal = terminal(true);

        terminal.caixa("MENU", List.of(terminal.destaque("[1]") + " Opção", ""));

        List<String> linhas = linhasSemCores();
        int larguraTotal = Terminal.LARGURA + 4;

        assertEquals(4, linhas.size());
        assertEquals("  ┌─ MENU " + "─".repeat(Terminal.LARGURA - 7) + "┐", linhas.get(0));
        assertEquals("  │ [1] Opção" + " ".repeat(Terminal.LARGURA - 11) + " │", linhas.get(1));
        assertEquals("  │" + " ".repeat(Terminal.LARGURA) + "│", linhas.get(2));
        assertEquals("  └" + "─".repeat(Terminal.LARGURA) + "┘", linhas.get(3));

        for (String linha : linhas) {
            assertEquals(larguraTotal, linha.length());
        }
    }

    @Test
    void deveImprimirCaixaComTituloELinhaMaioresQueALargura() {
        String longo = "X".repeat(Terminal.LARGURA + 5);

        terminal(false).caixa(longo, List.of(longo));

        List<String> linhas = linhasSemCores();
        assertEquals("  ┌─ " + longo + " ┐", linhas.get(0));
        assertEquals("  │ " + longo + " │", linhas.get(1));
    }

    @Test
    void deveImprimirTabelaComLargurasAjustadas() {
        Terminal terminal = terminal(true);

        terminal.tabela(
                List.of("COD", "NOME"),
                List.of(
                        List.of("A1", terminal.estilo("Feijão preto", Terminal.VERDE)),
                        List.of("ABCDE", "Arroz")
                ));

        assertEquals(List.of(
                "  ┌───────┬──────────────┐",
                "  │ COD   │ NOME         │",
                "  ├───────┼──────────────┤",
                "  │ A1    │ Feijão preto │",
                "  │ ABCDE │ Arroz        │",
                "  └───────┴──────────────┘"
        ), linhasSemCores());
    }

    @Test
    void deveImprimirTabelaSemLinhas() {
        terminal(false).tabela(List.of("A", "B"), List.of());

        assertEquals(List.of(
                "  ┌───┬───┐",
                "  │ A │ B │",
                "  ├───┼───┤",
                "  └───┴───┘"
        ), linhasSemCores());
    }

    // ---------- mensagens ----------

    @Test
    void deveImprimirMensagensPadronizadas() {
        Terminal terminal = terminal(true);

        terminal.sucesso("feito");
        terminal.erro("falhou");
        terminal.aviso("atenção");
        terminal.info("informação");
        terminal.dica("dica");
        terminal.linhaEmBranco();
        terminal.prompt("Nome");

        assertEquals(List.of(
                "  ✔ feito",
                "  ✖ falhou",
                "  ! atenção",
                "  • informação",
                "  dica",
                "",
                "  › Nome: "
        ), linhasSemCores());

        String saida = saida();
        assertTrue(saida.contains(Terminal.VERDE + "feito"));
        assertTrue(saida.contains(Terminal.VERMELHO + "falhou"));
        assertTrue(saida.contains(Terminal.AMARELO + "atenção"));
        assertFalse(saida.endsWith("\n"));
    }
}

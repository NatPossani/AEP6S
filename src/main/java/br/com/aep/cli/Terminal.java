package br.com.aep.cli;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utilitário de saída para o terminal: cores ANSI, caixas, tabelas e mensagens padronizadas.
 * As cores podem ser desativadas com a variável de ambiente NO_COLOR (https://no-color.org).
 */
public class Terminal {

    public static final String RESET = "\u001B[0m";
    public static final String NEGRITO = "\u001B[1m";
    public static final String FRACO = "\u001B[2m";
    public static final String VERMELHO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARELO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String CIANO = "\u001B[36m";

    /** Largura interna padrão das caixas. */
    public static final int LARGURA = 58;

    private static final String MARGEM = "  ";
    private static final Pattern ANSI = Pattern.compile("\u001B\\[[0-9;]*m");

    private final PrintStream out;
    private final boolean cores;

    public Terminal(PrintStream out, boolean cores) {
        this.out = out;
        this.cores = cores;
    }

    public static Terminal padrao() {
        return new Terminal(System.out, coresHabilitadas(System.getenv()));
    }

    static boolean coresHabilitadas(Map<String, String> ambiente) {
        String noColor = ambiente.get("NO_COLOR");
        return noColor == null || noColor.isEmpty();
    }

    public static String removerCores(String texto) {
        return ANSI.matcher(texto).replaceAll("");
    }

    public static int larguraVisivel(String texto) {
        return removerCores(texto).length();
    }

    // ---------- estilos ----------

    public String estilo(String texto, String... codigos) {
        if (!cores || codigos.length == 0) {
            return texto;
        }
        return String.join("", codigos) + texto + RESET;
    }

    public String negrito(String texto) {
        return estilo(texto, NEGRITO);
    }

    public String fraco(String texto) {
        return estilo(texto, FRACO);
    }

    public String destaque(String texto) {
        return estilo(texto, NEGRITO, CIANO);
    }

    // ---------- blocos ----------

    public void linhaEmBranco() {
        out.println();
    }

    public void banner(String titulo, String subtitulo) {
        String borda = "═".repeat(LARGURA);

        out.println();
        out.println(MARGEM + estilo("╔" + borda + "╗", CIANO));
        out.println(MARGEM + estilo("║", CIANO)
                + centralizar(estilo(titulo, NEGRITO), LARGURA)
                + estilo("║", CIANO));
        out.println(MARGEM + estilo("║", CIANO)
                + centralizar(fraco(subtitulo), LARGURA)
                + estilo("║", CIANO));
        out.println(MARGEM + estilo("╚" + borda + "╝", CIANO));
    }

    public void secao(String titulo) {
        int restante = Math.max(0, LARGURA - larguraVisivel(titulo) - 4);

        out.println();
        out.println(MARGEM + estilo("━━ ", AZUL) + estilo(titulo, NEGRITO, AZUL)
                + estilo(" " + "━".repeat(restante), AZUL));
    }

    public void subtitulo(String texto) {
        out.println();
        out.println(MARGEM + estilo("┄ " + texto, CIANO));
    }

    public void caixa(String titulo, List<String> linhas) {
        int tracos = Math.max(0, LARGURA - larguraVisivel(titulo) - 3);

        out.println(MARGEM + estilo("┌─ ", AZUL) + estilo(titulo, NEGRITO)
                + estilo(" " + "─".repeat(tracos) + "┐", AZUL));

        for (String linha : linhas) {
            out.println(MARGEM + estilo("│", AZUL) + " "
                    + preencher(linha, LARGURA - 2) + " " + estilo("│", AZUL));
        }

        out.println(MARGEM + estilo("└" + "─".repeat(LARGURA) + "┘", AZUL));
    }

    public void tabela(List<String> cabecalhos, List<List<String>> linhas) {
        int[] larguras = new int[cabecalhos.size()];

        for (int i = 0; i < cabecalhos.size(); i++) {
            larguras[i] = larguraVisivel(cabecalhos.get(i));
        }

        for (List<String> linha : linhas) {
            for (int i = 0; i < larguras.length; i++) {
                larguras[i] = Math.max(larguras[i], larguraVisivel(linha.get(i)));
            }
        }

        List<String> cabecalhosEstilizados = new ArrayList<>();
        for (String cabecalho : cabecalhos) {
            cabecalhosEstilizados.add(estilo(cabecalho, NEGRITO, CIANO));
        }

        out.println(MARGEM + bordaTabela("┌", "┬", "┐", larguras));
        out.println(MARGEM + linhaTabela(cabecalhosEstilizados, larguras));
        out.println(MARGEM + bordaTabela("├", "┼", "┤", larguras));

        for (List<String> linha : linhas) {
            out.println(MARGEM + linhaTabela(linha, larguras));
        }

        out.println(MARGEM + bordaTabela("└", "┴", "┘", larguras));
    }

    // ---------- mensagens ----------

    public void sucesso(String mensagem) {
        out.println(MARGEM + estilo("✔ ", NEGRITO, VERDE) + estilo(mensagem, VERDE));
    }

    public void erro(String mensagem) {
        out.println(MARGEM + estilo("✖ ", NEGRITO, VERMELHO) + estilo(mensagem, VERMELHO));
    }

    public void aviso(String mensagem) {
        out.println(MARGEM + estilo("! ", NEGRITO, AMARELO) + estilo(mensagem, AMARELO));
    }

    public void info(String mensagem) {
        out.println(MARGEM + estilo("• ", CIANO) + mensagem);
    }

    public void dica(String mensagem) {
        out.println(MARGEM + fraco(mensagem));
    }

    public void prompt(String rotulo) {
        out.print(MARGEM + estilo("› ", NEGRITO, CIANO) + rotulo + ": ");
    }

    // ---------- auxiliares ----------

    private String bordaTabela(String esquerda, String meio, String direita, int[] larguras) {
        StringBuilder borda = new StringBuilder(esquerda);

        for (int i = 0; i < larguras.length; i++) {
            borda.append("─".repeat(larguras[i] + 2));
            borda.append(i < larguras.length - 1 ? meio : direita);
        }

        return estilo(borda.toString(), AZUL);
    }

    private String linhaTabela(List<String> celulas, int[] larguras) {
        String separador = estilo("│", AZUL);
        StringBuilder linha = new StringBuilder(separador);

        for (int i = 0; i < larguras.length; i++) {
            linha.append(' ')
                    .append(preencher(celulas.get(i), larguras[i]))
                    .append(' ')
                    .append(separador);
        }

        return linha.toString();
    }

    private static String preencher(String texto, int largura) {
        return texto + " ".repeat(Math.max(0, largura - larguraVisivel(texto)));
    }

    private static String centralizar(String texto, int largura) {
        int sobra = Math.max(0, largura - larguraVisivel(texto));
        int esquerda = sobra / 2;

        return " ".repeat(esquerda) + texto + " ".repeat(sobra - esquerda);
    }
}

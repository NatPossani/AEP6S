package br.com.aep;

import br.com.aep.cli.Terminal;
import br.com.aep.model.Doacao;
import br.com.aep.model.ItemDoacao;
import br.com.aep.repository.MongoConnection;
import br.com.aep.repository.MongoDoacaoRepository;
import br.com.aep.service.DoacaoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final List<String> CABECALHO_DOACOES =
            List.of("CÓDIGO", "DOADOR", "INSTITUIÇÃO", "DATA", "STATUS", "ITENS");

    private static final List<String> CABECALHO_ITENS =
            List.of("ALIMENTO", "QTD", "UNIDADE", "VALIDADE");

    public static void main(String[] args) {
        String mongoUri = System.getenv()
                .getOrDefault("MONGO_URI", "mongodb://localhost:27017");

        MongoConnection mongo = new MongoConnection(mongoUri);
        MongoDoacaoRepository repository = new MongoDoacaoRepository(mongo.getDatabase());
        DoacaoService service = new DoacaoService(repository);
        Scanner scanner = new Scanner(System.in);
        Terminal terminal = Terminal.padrao();

        boolean executando = true;

        terminal.banner("BANCO DE ALIMENTOS COMUNITÁRIO",
                "Gestão de doações · ODS 2 – Fome Zero");

        while (executando) {
            exibirMenu(terminal);
            String opcao = scanner.nextLine().trim();

            try {
                switch (opcao) {
                    case "1" -> cadastrar(scanner, service, terminal);
                    case "2" -> listar(service, terminal);
                    case "3" -> buscar(scanner, service, terminal);
                    case "4" -> atualizar(scanner, service, terminal);
                    case "5" -> excluir(scanner, service, terminal);
                    case "0" -> {
                        executando = false;
                        terminal.linhaEmBranco();
                        terminal.info("Sistema encerrado. Até logo!");
                    }
                    default -> terminal.erro("Opção inválida. Escolha um número entre 0 e 5.");
                }
            } catch (NumberFormatException e) {
                terminal.erro("Quantidade inválida. Informe apenas números inteiros.");
            } catch (IllegalArgumentException e) {
                terminal.erro(e.getMessage());
            } catch (Exception e) {
                terminal.erro("Não foi possível concluir a operação.");
            }
        }

        mongo.fechar();
        scanner.close();
    }

    private static void exibirMenu(Terminal terminal) {
        terminal.linhaEmBranco();
        terminal.caixa("MENU PRINCIPAL", List.of(
                opcaoMenu(terminal, "1", "Cadastrar doação"),
                opcaoMenu(terminal, "2", "Listar doações"),
                opcaoMenu(terminal, "3", "Buscar doação"),
                opcaoMenu(terminal, "4", "Atualizar doação"),
                opcaoMenu(terminal, "5", "Excluir doação"),
                "",
                opcaoMenu(terminal, "0", "Sair")
        ));
        terminal.prompt("Escolha uma opção");
    }

    private static String opcaoMenu(Terminal terminal, String numero, String descricao) {
        return terminal.destaque("[" + numero + "]") + "  " + descricao;
    }

    private static void cadastrar(Scanner scanner, DoacaoService service, Terminal terminal) {
        terminal.secao("NOVA DOAÇÃO");
        DadosDoacao dados = lerDados(scanner, terminal);

        Doacao doacao = service.cadastrar(
                dados.doadorCodigo(),
                dados.instituicaoCodigo(),
                dados.itens()
        );

        terminal.linhaEmBranco();
        terminal.sucesso("Doação cadastrada com sucesso!");
        terminal.info("Código: " + terminal.destaque(doacao.getCodigo()));
    }

    private static void listar(DoacaoService service, Terminal terminal) {
        terminal.secao("DOAÇÕES CADASTRADAS");
        List<Doacao> doacoes = service.listar();

        if (doacoes.isEmpty()) {
            terminal.aviso("Nenhuma doação cadastrada.");
            return;
        }

        List<List<String>> linhas = new ArrayList<>();

        for (Doacao doacao : doacoes) {
            int quantidadeItens = doacao.getItens() == null ? 0 : doacao.getItens().size();

            linhas.add(List.of(
                    terminal.negrito(valor(doacao.getCodigo())),
                    valor(doacao.getDoadorCodigo()),
                    valor(doacao.getInstituicaoCodigo()),
                    valor(doacao.getDataRegistro()),
                    status(terminal, doacao.getStatus()),
                    String.valueOf(quantidadeItens)
            ));
        }

        terminal.tabela(CABECALHO_DOACOES, linhas);
        terminal.info(doacoes.size() == 1
                ? "1 doação encontrada."
                : doacoes.size() + " doações encontradas.");
    }

    private static void buscar(Scanner scanner, DoacaoService service, Terminal terminal) {
        terminal.secao("BUSCAR DOAÇÃO");
        String codigo = ler(scanner, terminal, "Código da doação");

        Doacao doacao = service.buscar(codigo);

        terminal.linhaEmBranco();
        if (doacao == null) {
            terminal.aviso("Doação não encontrada.");
        } else {
            imprimir(doacao, terminal);
        }
    }

    private static void atualizar(Scanner scanner, DoacaoService service, Terminal terminal) {
        terminal.secao("ATUALIZAR DOAÇÃO");
        String codigo = ler(scanner, terminal, "Código da doação");

        Doacao atual = service.buscar(codigo);

        terminal.linhaEmBranco();
        if (atual == null) {
            terminal.aviso("Doação não encontrada.");
            return;
        }

        imprimir(atual, terminal);
        terminal.linhaEmBranco();
        terminal.info("Informe os novos dados da doação.");

        DadosDoacao dados = lerDados(scanner, terminal);

        terminal.linhaEmBranco();
        if (service.atualizar(
                codigo,
                dados.doadorCodigo(),
                dados.instituicaoCodigo(),
                dados.itens())) {
            terminal.sucesso("Doação atualizada com sucesso!");
        } else {
            terminal.erro("Não foi possível atualizar a doação.");
        }
    }

    private static void excluir(Scanner scanner, DoacaoService service, Terminal terminal) {
        terminal.secao("EXCLUIR DOAÇÃO");
        String codigo = ler(scanner, terminal, "Código da doação");

        terminal.linhaEmBranco();
        if (service.excluir(codigo)) {
            terminal.sucesso("Doação excluída com sucesso!");
        } else {
            terminal.aviso("Doação não encontrada.");
        }
    }

    private static DadosDoacao lerDados(Scanner scanner, Terminal terminal) {
        String doadorCodigo = ler(scanner, terminal, "Código do doador");
        String instituicaoCodigo = ler(scanner, terminal, "Código da instituição");

        List<ItemDoacao> itens = new ArrayList<>();

        terminal.linhaEmBranco();
        terminal.dica("Informe os itens da doação. Deixe o alimento em branco para finalizar.");

        while (true) {
            terminal.subtitulo("Item " + (itens.size() + 1));
            String alimento = ler(scanner, terminal, "Alimento");

            if (alimento.isBlank()) {
                break;
            }

            int quantidade = Integer.parseInt(
                    ler(scanner, terminal, "Quantidade (somente números)").trim());
            String unidade = ler(scanner, terminal, "Unidade (kg, unidade, caixa...)");
            String validade = ler(scanner, terminal, "Validade (DD/MM/AAAA)");

            itens.add(new ItemDoacao(alimento, quantidade, unidade, validade));
            terminal.sucesso("Item adicionado: " + quantidade + " " + unidade + " de " + alimento);
        }

        return new DadosDoacao(doadorCodigo, instituicaoCodigo, itens);
    }

    private static String ler(Scanner scanner, Terminal terminal, String rotulo) {
        terminal.prompt(rotulo);
        return scanner.nextLine();
    }

    private static void imprimir(Doacao doacao, Terminal terminal) {
        terminal.caixa("DOAÇÃO " + valor(doacao.getCodigo()), List.of(
                campo(terminal, "Doador", valor(doacao.getDoadorCodigo())),
                campo(terminal, "Instituição", valor(doacao.getInstituicaoCodigo())),
                campo(terminal, "Data", valor(doacao.getDataRegistro())),
                campo(terminal, "Status", status(terminal, doacao.getStatus()))
        ));

        List<ItemDoacao> itens = doacao.getItens();

        if (itens == null || itens.isEmpty()) {
            terminal.aviso("Nenhum item registrado nesta doação.");
            return;
        }

        List<List<String>> linhas = new ArrayList<>();

        for (ItemDoacao item : itens) {
            linhas.add(List.of(
                    valor(item.getAlimento()),
                    String.valueOf(item.getQuantidade()),
                    valor(item.getUnidade()),
                    valor(item.getValidade())
            ));
        }

        terminal.tabela(CABECALHO_ITENS, linhas);
    }

    private static String campo(Terminal terminal, String rotulo, String valor) {
        return terminal.fraco(String.format("%-12s", rotulo)) + valor;
    }

    private static String status(Terminal terminal, String status) {
        return terminal.estilo(valor(status), Terminal.NEGRITO, Terminal.VERDE);
    }

    private static String valor(String texto) {
        return texto == null ? "—" : texto;
    }

    private record DadosDoacao(
            String doadorCodigo,
            String instituicaoCodigo,
            List<ItemDoacao> itens
    ) {
    }
}

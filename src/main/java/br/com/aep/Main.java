package br.com.aep;

import br.com.aep.model.Doacao;
import br.com.aep.repository.MongoDoacaoRepository;
import br.com.aep.service.DoacaoService;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String mongoUri = System.getenv()
                .getOrDefault("MONGO_URI", "mongodb://localhost:27017");

        MongoDoacaoRepository repository = new MongoDoacaoRepository(mongoUri);
        DoacaoService service = new DoacaoService(repository);
        Scanner scanner = new Scanner(System.in);

        boolean executando = true;

        System.out.println("====================================");
        System.out.println("   BANCO DE ALIMENTOS COMUNITARIO");
        System.out.println("====================================");

        while (executando) {
            exibirMenu();
            String opcao = scanner.nextLine();

            try {
                switch (opcao) {
                    case "1" -> cadastrar(scanner, service);
                    case "2" -> listar(service);
                    case "3" -> buscar(scanner, service);
                    case "4" -> atualizar(scanner, service);
                    case "5" -> excluir(scanner, service);
                    case "0" -> {
                        executando = false;
                        System.out.println("\nSistema encerrado.");
                    }
                    default -> System.out.println("\nOpçao inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nQuantidade inválida.");
            } catch (IllegalArgumentException e) {
                System.out.println("\nErro: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\nNao foi possível concluir a operaçao.");
            }
        }

        repository.fechar();
        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n------------------------------------");
        System.out.println("1 - Cadastrar doaçao");
        System.out.println("2 - Listar doaçoes");
        System.out.println("3 - Buscar doaçao");
        System.out.println("4 - Atualizar doaçao");
        System.out.println("5 - Excluir doaçao");
        System.out.println("0 - Sair");
        System.out.println("------------------------------------");
        System.out.print("Escolha uma opçao: ");
    }

    private static void cadastrar(Scanner scanner, DoacaoService service) {
        DadosDoacao dados = lerDados(scanner);

        Doacao doacao = service.cadastrar(
                dados.nomeDoador(),
                dados.alimento(),
                dados.quantidade(),
                dados.unidade(),
                dados.validade(),
                dados.destino()
        );

        System.out.println("\nDoaçao cadastrada com sucesso!");
        System.out.println("Código: " + doacao.getCodigo());
    }

    private static void listar(DoacaoService service) {
        List<Doacao> doacoes = service.listar();

        if (doacoes.isEmpty()) {
            System.out.println("\nNenhuma doaçao cadastrada.");
            return;
        }

        for (Doacao doacao : doacoes) {
            imprimir(doacao);
        }
    }

    private static void buscar(Scanner scanner, DoacaoService service) {
        System.out.print("\nDigite o código: ");
        String codigo = scanner.nextLine();

        Doacao doacao = service.buscar(codigo);

        if (doacao == null) {
            System.out.println("\nDoaçao nao encontrada.");
        } else {
            imprimir(doacao);
        }
    }

    private static void atualizar(Scanner scanner, DoacaoService service) {
        System.out.print("\nDigite o código da doaçao: ");
        String codigo = scanner.nextLine();

        if (service.buscar(codigo) == null) {
            System.out.println("\nDoaçao nao encontrada.");
            return;
        }

        DadosDoacao dados = lerDados(scanner);

        if (service.atualizar(
                codigo,
                dados.nomeDoador(),
                dados.alimento(),
                dados.quantidade(),
                dados.unidade(),
                dados.validade(),
                dados.destino())) {
            System.out.println("\nDoaçao atualizada com sucesso!");
        } else {
            System.out.println("\nNao foi possível atualizar.");
        }
    }

    private static void excluir(Scanner scanner, DoacaoService service) {
        System.out.print("\nDigite o código da doaçao: ");
        String codigo = scanner.nextLine();

        if (service.excluir(codigo)) {
            System.out.println("\nDoaçao excluída com sucesso!");
        } else {
            System.out.println("\nDoaçao nao encontrada.");
        }
    }

    private static DadosDoacao lerDados(Scanner scanner) {
        System.out.print("Nome do doador: ");
        String nome = scanner.nextLine();

        System.out.print("Alimento: ");
        String alimento = scanner.nextLine();

        System.out.print("Quantidade(somente números): ");
        int quantidade = Integer.parseInt(scanner.nextLine());

        System.out.print("Unidade (kg, unidade, caixa...): ");
        String unidade = scanner.nextLine();

        System.out.print("Validade (DD/MM/AAAA): ");
        String validade = scanner.nextLine();

        System.out.print("Destino: ");
        String destino = scanner.nextLine();

        return new DadosDoacao(
                nome, alimento, quantidade, unidade, validade, destino
        );
    }

    private static void imprimir(Doacao doacao) {
        System.out.println("\n====================================");
        System.out.println("Código: " + doacao.getCodigo());
        System.out.println("Doador: " + doacao.getNomeDoador());
        System.out.println("Alimento: " + doacao.getAlimento());
        System.out.println("Quantidade: " + doacao.getQuantidade()
                + " " + doacao.getUnidade());
        System.out.println("Validade: " + doacao.getValidade());
        System.out.println("Destino: " + doacao.getDestino());
        System.out.println("====================================");
    }

    private record DadosDoacao(
            String nomeDoador,
            String alimento,
            int quantidade,
            String unidade,
            String validade,
            String destino
    ) {
    }
}

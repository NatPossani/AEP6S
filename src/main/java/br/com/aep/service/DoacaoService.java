package br.com.aep.service;

import br.com.aep.model.Doacao;
import br.com.aep.model.ItemDoacao;
import br.com.aep.repository.DoacaoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DoacaoService {

    private final DoacaoRepository repository;

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DoacaoService(DoacaoRepository repository) {
        this.repository = repository;
    }

    public Doacao cadastrar(String doadorCodigo, String instituicaoCodigo,
                            List<ItemDoacao> itens) {
        List<ItemDoacao> itensNormalizados = validar(doadorCodigo, instituicaoCodigo, itens);

        String codigo = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        Doacao doacao = new Doacao(
                codigo,
                doadorCodigo.trim().toUpperCase(),
                instituicaoCodigo.trim().toUpperCase(),
                LocalDate.now().format(FORMATO_DATA),
                "RECEBIDA",
                itensNormalizados
        );

        repository.salvar(doacao);
        return doacao;
    }

    public List<Doacao> listar() {
        return repository.listar();
    }

    public Doacao buscar(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        return repository.buscarPorCodigo(codigo.trim().toUpperCase());
    }

    public boolean atualizar(String codigo, String doadorCodigo,
                             String instituicaoCodigo, List<ItemDoacao> itens) {
        List<ItemDoacao> itensNormalizados = validar(doadorCodigo, instituicaoCodigo, itens);

        Doacao doacao = new Doacao(
                codigo.trim().toUpperCase(),
                doadorCodigo.trim().toUpperCase(),
                instituicaoCodigo.trim().toUpperCase(),
                LocalDate.now().format(FORMATO_DATA),
                "RECEBIDA",
                itensNormalizados
        );

        return repository.atualizar(codigo.trim().toUpperCase(), doacao);
    }

    public boolean excluir(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return false;
        }

        return repository.excluir(codigo.trim().toUpperCase());
    }

    private List<ItemDoacao> validar(String doadorCodigo, String instituicaoCodigo,
                                     List<ItemDoacao> itens) {
        if (doadorCodigo == null || doadorCodigo.isBlank()) {
            throw new IllegalArgumentException("Código do doador é obrigatório.");
        }

        if (instituicaoCodigo == null || instituicaoCodigo.isBlank()) {
            throw new IllegalArgumentException("Código da instituição é obrigatório.");
        }

        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("A doação deve ter pelo menos um item.");
        }

        List<ItemDoacao> itensNormalizados = new ArrayList<>();

        for (ItemDoacao item : itens) {
            itensNormalizados.add(validarItem(item));
        }

        return itensNormalizados;
    }

    private ItemDoacao validarItem(ItemDoacao item) {
        if (item == null) {
            throw new IllegalArgumentException("Item da doação é obrigatório.");
        }

        if (item.getAlimento() == null || item.getAlimento().isBlank()) {
            throw new IllegalArgumentException("Alimento é obrigatório.");
        }

        if (item.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        if (item.getUnidade() == null || item.getUnidade().isBlank()) {
            throw new IllegalArgumentException("Unidade é obrigatória.");
        }

        if (item.getValidade() == null || item.getValidade().isBlank()) {
            throw new IllegalArgumentException("Validade é obrigatória.");
        }

        try {
            LocalDate dataValidade = LocalDate.parse(item.getValidade(), FORMATO_DATA);

            if (dataValidade.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "A validade não pode ser anterior à data atual."
                );
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Validade deve estar no formato dd/MM/yyyy."
            );
        }

        return new ItemDoacao(
                item.getAlimento().trim(),
                item.getQuantidade(),
                item.getUnidade().trim(),
                item.getValidade().trim()
        );
    }
}

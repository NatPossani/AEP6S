package br.com.aep.service;

import br.com.aep.model.Doacao;
import br.com.aep.repository.DoacaoRepository;

import java.util.List;
import java.util.UUID;

public class DoacaoService {

    private final DoacaoRepository repository;

    public DoacaoService(DoacaoRepository repository) {
        this.repository = repository;
    }

    public Doacao cadastrar(String nomeDoador, String alimento, int quantidade,
                            String unidade, String validade, String destino) {
        validar(nomeDoador, alimento, quantidade, unidade, validade, destino);

        String codigo = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        Doacao doacao = new Doacao(
                codigo,
                nomeDoador.trim(),
                alimento.trim(),
                quantidade,
                unidade.trim(),
                validade.trim(),
                destino.trim()
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

    public boolean atualizar(String codigo, String nomeDoador, String alimento,
                             int quantidade, String unidade, String validade,
                             String destino) {
        validar(nomeDoador, alimento, quantidade, unidade, validade, destino);

        Doacao doacao = new Doacao(
                codigo.trim().toUpperCase(),
                nomeDoador.trim(),
                alimento.trim(),
                quantidade,
                unidade.trim(),
                validade.trim(),
                destino.trim()
        );

        return repository.atualizar(codigo.trim().toUpperCase(), doacao);
    }

    public boolean excluir(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return false;
        }

        return repository.excluir(codigo.trim().toUpperCase());
    }

    private void validar(String nomeDoador, String alimento, int quantidade,
                         String unidade, String validade, String destino) {
        if (nomeDoador == null || nomeDoador.isBlank()) {
            throw new IllegalArgumentException("O nome do doador é obrigatório.");
        }

        if (alimento == null || alimento.isBlank()) {
            throw new IllegalArgumentException("O alimento é obrigatório.");
        }

        if (unidade == null || unidade.isBlank()) {
            throw new IllegalArgumentException("A unidade é obrigatória.");
        }

        if (validade == null || validade.isBlank()) {
            throw new IllegalArgumentException("A validade é obrigatória.");
        }

        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("O destino é obrigatório.");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }
}

package br.com.aep.repository;

import br.com.aep.model.Doacao;

import java.util.List;

public interface DoacaoRepository {

    void salvar(Doacao doacao);

    List<Doacao> listar();

    Doacao buscarPorCodigo(String codigo);

    boolean atualizar(String codigo, Doacao doacao);

    boolean excluir(String codigo);
}

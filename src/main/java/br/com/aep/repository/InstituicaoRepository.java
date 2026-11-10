package br.com.aep.repository;

import br.com.aep.model.Instituicao;

import java.util.List;

public interface InstituicaoRepository {

    void salvar(Instituicao instituicao);

    List<Instituicao> listar();

    Instituicao buscarPorCodigo(String codigo);

    boolean atualizar(String codigo, Instituicao instituicao);

    boolean excluir(String codigo);
}

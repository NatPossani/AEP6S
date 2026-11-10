package br.com.aep.repository;

import br.com.aep.model.Doador;

import java.util.List;

public interface DoadorRepository {

    void salvar(Doador doador);

    List<Doador> listar();

    Doador buscarPorCodigo(String codigo);

    boolean atualizar(String codigo, Doador doador);

    boolean excluir(String codigo);
}

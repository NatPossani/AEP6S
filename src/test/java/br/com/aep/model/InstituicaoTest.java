package br.com.aep.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class InstituicaoTest {

    @Test
    void deveCriarInstituicaoComConstrutorVazio() {
        Instituicao instituicao = new Instituicao();

        assertNull(instituicao.getCodigo());
        assertNull(instituicao.getNome());
        assertNull(instituicao.getEndereco());
    }

    @Test
    void deveCriarInstituicaoComConstrutorCompleto() {
        Endereco endereco = new Endereco("Rua A, 100", "Maringá", "PR");

        Instituicao instituicao = new Instituicao("I001", "Instituição Esperança", endereco);

        assertEquals("I001", instituicao.getCodigo());
        assertEquals("Instituição Esperança", instituicao.getNome());
        assertSame(endereco, instituicao.getEndereco());
    }

    @Test
    void deveAlterarDadosComSetters() {
        Instituicao instituicao = new Instituicao();
        Endereco endereco = new Endereco("Av. Brasil, 200", "Londrina", "PR");

        instituicao.setCodigo("I002");
        instituicao.setNome("Casa de Apoio");
        instituicao.setEndereco(endereco);

        assertEquals("I002", instituicao.getCodigo());
        assertEquals("Casa de Apoio", instituicao.getNome());
        assertSame(endereco, instituicao.getEndereco());
    }
}

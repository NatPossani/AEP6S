package br.com.aep.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class EnderecoTest {

    @Test
    void deveCriarEnderecoComConstrutorVazio() {
        Endereco endereco = new Endereco();

        assertNull(endereco.getRua());
        assertNull(endereco.getCidade());
        assertNull(endereco.getEstado());
    }

    @Test
    void deveCriarEnderecoComConstrutorCompleto() {
        Endereco endereco = new Endereco("Rua A, 100", "Maringá", "PR");

        assertEquals("Rua A, 100", endereco.getRua());
        assertEquals("Maringá", endereco.getCidade());
        assertEquals("PR", endereco.getEstado());
    }

    @Test
    void deveAlterarDadosComSetters() {
        Endereco endereco = new Endereco();

        endereco.setRua("Av. Brasil, 200");
        endereco.setCidade("Londrina");
        endereco.setEstado("SP");

        assertEquals("Av. Brasil, 200", endereco.getRua());
        assertEquals("Londrina", endereco.getCidade());
        assertEquals("SP", endereco.getEstado());
    }
}

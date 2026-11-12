package br.com.aep.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ContatoTest {

    @Test
    void deveCriarContatoComConstrutorVazio() {
        Contato contato = new Contato();

        assertNull(contato.getTelefone());
        assertNull(contato.getEmail());
    }

    @Test
    void deveCriarContatoComConstrutorCompleto() {
        Contato contato = new Contato("44999990000", "doador@teste.com");

        assertEquals("44999990000", contato.getTelefone());
        assertEquals("doador@teste.com", contato.getEmail());
    }

    @Test
    void deveAlterarDadosComSetters() {
        Contato contato = new Contato();

        contato.setTelefone("44988880000");
        contato.setEmail("novo@teste.com");

        assertEquals("44988880000", contato.getTelefone());
        assertEquals("novo@teste.com", contato.getEmail());
    }
}

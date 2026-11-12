package br.com.aep.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class DoadorTest {

    @Test
    void deveCriarDoadorComConstrutorVazio() {
        Doador doador = new Doador();

        assertNull(doador.getCodigo());
        assertNull(doador.getNome());
        assertNull(doador.getTipo());
        assertNull(doador.getContato());
    }

    @Test
    void deveCriarDoadorComConstrutorCompleto() {
        Contato contato = new Contato("44999990000", "mercado@teste.com");

        Doador doador = new Doador("D001", "Mercado Central", "EMPRESA", contato);

        assertEquals("D001", doador.getCodigo());
        assertEquals("Mercado Central", doador.getNome());
        assertEquals("EMPRESA", doador.getTipo());
        assertSame(contato, doador.getContato());
    }

    @Test
    void deveAlterarDadosComSetters() {
        Doador doador = new Doador();
        Contato contato = new Contato("44988880000", "maria@teste.com");

        doador.setCodigo("D002");
        doador.setNome("Maria");
        doador.setTipo("PESSOA_FISICA");
        doador.setContato(contato);

        assertEquals("D002", doador.getCodigo());
        assertEquals("Maria", doador.getNome());
        assertEquals("PESSOA_FISICA", doador.getTipo());
        assertSame(contato, doador.getContato());
    }
}

package br.com.aep.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ItemDoacaoTest {

    @Test
    void deveCriarItemComConstrutorVazio() {
        ItemDoacao item = new ItemDoacao();

        assertNull(item.getAlimento());
        assertEquals(0, item.getQuantidade());
        assertNull(item.getUnidade());
        assertNull(item.getValidade());
    }

    @Test
    void deveCriarItemComConstrutorCompleto() {
        ItemDoacao item = new ItemDoacao("Arroz", 10, "kg", "31/12/2030");

        assertEquals("Arroz", item.getAlimento());
        assertEquals(10, item.getQuantidade());
        assertEquals("kg", item.getUnidade());
        assertEquals("31/12/2030", item.getValidade());
    }

    @Test
    void deveAlterarDadosComSetters() {
        ItemDoacao item = new ItemDoacao();

        item.setAlimento("Leite");
        item.setQuantidade(3);
        item.setUnidade("litros");
        item.setValidade("01/01/2031");

        assertEquals("Leite", item.getAlimento());
        assertEquals(3, item.getQuantidade());
        assertEquals("litros", item.getUnidade());
        assertEquals("01/01/2031", item.getValidade());
    }
}

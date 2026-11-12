package br.com.aep.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class DoacaoTest {

    @Test
    void deveCriarDoacaoComConstrutorVazio() {
        Doacao doacao = new Doacao();

        assertNull(doacao.getCodigo());
        assertNull(doacao.getDoadorCodigo());
        assertNull(doacao.getInstituicaoCodigo());
        assertNull(doacao.getDataRegistro());
        assertNull(doacao.getStatus());
        assertNotNull(doacao.getItens());
        assertTrue(doacao.getItens().isEmpty());
    }

    @Test
    void deveCriarDoacaoComConstrutorCompleto() {
        List<ItemDoacao> itens = List.of(new ItemDoacao("Arroz", 10, "kg", "31/12/2030"));

        Doacao doacao = new Doacao("TESTE-001", "D001", "I001",
                "01/01/2030", "RECEBIDA", itens);

        assertEquals("TESTE-001", doacao.getCodigo());
        assertEquals("D001", doacao.getDoadorCodigo());
        assertEquals("I001", doacao.getInstituicaoCodigo());
        assertEquals("01/01/2030", doacao.getDataRegistro());
        assertEquals("RECEBIDA", doacao.getStatus());
        assertSame(itens, doacao.getItens());
    }

    @Test
    void deveCriarListaVaziaQuandoItensNulosNoConstrutor() {
        Doacao doacao = new Doacao("TESTE-001", "D001", "I001",
                "01/01/2030", "RECEBIDA", null);

        assertNotNull(doacao.getItens());
        assertTrue(doacao.getItens().isEmpty());
    }

    @Test
    void deveAlterarDadosDaDoacaoComSetters() {
        Doacao doacao = new Doacao();
        List<ItemDoacao> itens = new ArrayList<>();
        itens.add(new ItemDoacao("Feijão", 5, "kg", "30/11/2030"));

        doacao.setCodigo("TESTE-002");
        doacao.setDoadorCodigo("D002");
        doacao.setInstituicaoCodigo("I002");
        doacao.setDataRegistro("02/02/2030");
        doacao.setStatus("ENTREGUE");
        doacao.setItens(itens);

        assertEquals("TESTE-002", doacao.getCodigo());
        assertEquals("D002", doacao.getDoadorCodigo());
        assertEquals("I002", doacao.getInstituicaoCodigo());
        assertEquals("02/02/2030", doacao.getDataRegistro());
        assertEquals("ENTREGUE", doacao.getStatus());
        assertSame(itens, doacao.getItens());
    }

    @Test
    void deveCriarListaVaziaQuandoSetItensNulo() {
        Doacao doacao = new Doacao();

        doacao.setItens(null);

        assertNotNull(doacao.getItens());
        assertTrue(doacao.getItens().isEmpty());
    }
}

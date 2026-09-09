package br.com.aep.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DoacaoTest {

    @Test
    void deveCriarDoacaoComConstrutorCompleto() {
        Doacao doacao = new Doacao(
                "TESTE-001",
                "João da Silva",
                "Arroz",
                10,
                "kg",
                "31/12/2026",
                "Cesta básica"
        );

        assertEquals("TESTE-001", doacao.getCodigo());
        assertEquals("João da Silva", doacao.getNomeDoador());
        assertEquals("Arroz", doacao.getAlimento());
        assertEquals(10, doacao.getQuantidade());
        assertEquals("kg", doacao.getUnidade());
        assertEquals("31/12/2026", doacao.getValidade());
        assertEquals("Cesta básica", doacao.getDestino());
    }

    @Test
    void deveAlterarDadosDaDoacaoComSetters() {
        Doacao doacao = new Doacao();

        doacao.setCodigo("TESTE-002");
        doacao.setNomeDoador("Maria");
        doacao.setAlimento("Feijão");
        doacao.setQuantidade(5);
        doacao.setUnidade("kg");
        doacao.setValidade("30/11/2026");
        doacao.setDestino("Instituição social");

        assertEquals("TESTE-002", doacao.getCodigo());
        assertEquals("Maria", doacao.getNomeDoador());
        assertEquals("Feijão", doacao.getAlimento());
        assertEquals(5, doacao.getQuantidade());
        assertEquals("kg", doacao.getUnidade());
        assertEquals("30/11/2026", doacao.getValidade());
        assertEquals("Instituição social", doacao.getDestino());
    }
}
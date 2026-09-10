package br.com.aep.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.com.aep.model.Doacao;

class MongoDoacaoRepositoryTest {

    private static MongoDoacaoRepository repository;

    @BeforeAll
    static void configurarBanco() {
        repository = new MongoDoacaoRepository(
                "mongodb://localhost:27017"
        );
    }

    @AfterAll
    static void fecharBanco() {
        repository.fechar();
    }

    @Test
    void deveSalvarEBuscarDoacao() {
        // Arrange
        Doacao doacao = new Doacao(
                "TESTE-001",
                "Doador Teste",
                "Arroz",
                10,
                "kg",
                "31/12/2026",
                "Cesta básica"
        );

        // Act
        repository.salvar(doacao);
        Doacao resultado = repository.buscarPorCodigo("TESTE-001");

        // Assert
        assertNotNull(resultado);
        assertEquals("TESTE-001", resultado.getCodigo());
        assertEquals("Doador Teste", resultado.getNomeDoador());
        assertEquals("Arroz", resultado.getAlimento());
        assertEquals(10, resultado.getQuantidade());
        assertEquals("kg", resultado.getUnidade());
        assertEquals("31/12/2026", resultado.getValidade());
        assertEquals("Cesta básica", resultado.getDestino());

        // Limpeza
        repository.excluir("TESTE-001");
    }

    @Test
    void deveListarDoacoes() {
        Doacao doacao = new Doacao(
                "TESTE-002",
                "Doador Teste",
                "Feijão",
                5,
                "kg",
                "31/12/2026",
                "Cesta básica"
        );

        repository.salvar(doacao);

        List<Doacao> resultado = repository.listar();

        assertFalse(resultado.isEmpty());

        boolean encontrou = resultado.stream()
                .anyMatch(d -> "TESTE-002".equals(d.getCodigo()));

        assertTrue(encontrou);

        repository.excluir("TESTE-002");
    }

    @Test
    void deveAtualizarDoacao(){
        
        Doacao original = new Doacao(
                "TESTE-003",
                "Doador Teste",
                "Feijão",
                5,
                "kg",
                "31/12/2026",
                "Cesta básica"
        );

        Doacao atualizada = new Doacao(
                "TESTE-003",
                "Doador Atualizado",
                "Arroz Integral",
                20,
                "kg",
                "30/06/2027",
                "Instituição parceira"
            );

        repository.salvar(original);

        boolean resultado = repository.atualizar("TESTE-003", atualizada);

        Doacao doacaoEncontrada = repository.buscarPorCodigo("TESTE-003");

        assertTrue(resultado);
        assertNotNull(doacaoEncontrada);
        assertEquals("Doador Atualizado", doacaoEncontrada.getNomeDoador());
        assertEquals("Arroz Integral", doacaoEncontrada.getAlimento());
        assertEquals(20, doacaoEncontrada.getQuantidade());
        assertEquals("kg", doacaoEncontrada.getUnidade());
        assertEquals("30/06/2027", doacaoEncontrada.getValidade());
        assertEquals("Instituição parceira", doacaoEncontrada.getDestino());

        repository.excluir("TESTE-003");

    }

    @Test 
    void deveExcluirDoacao(){
        Doacao doacao = new Doacao(
                "TESTE-004",
                "Doador Teste",
                "Macarrão",
                8,
                "kg",
                "31/12/2026",
                "Cesta básica"
        );
        
        repository.salvar(doacao);

        boolean  resultado = repository.excluir("TESTE-004");

        assertTrue(resultado);

        Doacao encontrada = repository.buscarPorCodigo("TESTE-004");

        assertNull(encontrada);

    }

    @Test
    void deveRetornarNullQuandoCodigoNaoExistir(){
        Doacao resultado = repository.buscarPorCodigo("CODIGO-INEXISTENTE");

        assertNull(resultado);
    }

    @Test
    void deveRetornarFalseAoAtualizarCodigoInexistente() {
        Doacao doacao = new Doacao(
                "TESTE-005",
                "Doador Teste",
                "Leite",
                10,
                "litros",
                "31/12/2026",
                "Instituição parceira"
        );

        boolean resultado =
                repository.atualizar("CODIGO-INEXISTENTE", doacao);

        assertFalse(resultado);
    }

    @Test
    void deveRetornarFalseAoExcluirCodigoInexistente() {
        boolean resultado =
                repository.excluir("CODIGO-INEXISTENTE");

        assertFalse(resultado);
    }

    @AfterAll
    static void limparDadosDeTesteDepois() {
        repository.listar().stream()
                .filter(doacao -> doacao.getCodigo().startsWith("TESTE-"))
                .forEach(doacao -> repository.excluir(doacao.getCodigo()));

        repository.fechar();
    }

}
package br.com.aep.repository;

import static br.com.aep.repository.MongoMocks.findIterable;
import static br.com.aep.repository.MongoMocks.mockCollection;
import static br.com.aep.repository.MongoMocks.mockDatabase;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

import br.com.aep.model.Doacao;
import br.com.aep.model.ItemDoacao;

class MongoDoacaoRepositoryTest {

    private MongoCollection<Document> collection;
    private MongoDoacaoRepository repository;

    @BeforeEach
    void configurar() {
        collection = mockCollection();
        repository = new MongoDoacaoRepository(
                mockDatabase(MongoDoacaoRepository.COLLECTION_NAME, collection));
    }

    private static Doacao doacaoExemplo() {
        return new Doacao("TESTE-001", "D001", "I001", "01/01/2030", "RECEBIDA",
                List.of(new ItemDoacao("Arroz", 10, "kg", "31/12/2030"),
                        new ItemDoacao("Feijão", 5, "kg", "30/11/2030")));
    }

    private static Document documentoExemplo() {
        return new Document("codigo", "TESTE-001")
                .append("doadorCodigo", "D001")
                .append("instituicaoCodigo", "I001")
                .append("dataRegistro", "01/01/2030")
                .append("status", "RECEBIDA")
                .append("itens", List.of(
                        new Document("alimento", "Arroz")
                                .append("quantidade", 10)
                                .append("unidade", "kg")
                                .append("validade", "31/12/2030")));
    }

    @Test
    void deveUsarColecaoDoacoes() {
        assertEquals("doacoes", MongoDoacaoRepository.COLLECTION_NAME);
    }

    @Test
    void deveSalvarDoacaoComoDocumentoComSubdocumentos() {
        repository.salvar(doacaoExemplo());

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(collection).insertOne(captor.capture());

        Document documento = captor.getValue();
        assertEquals("TESTE-001", documento.getString("codigo"));
        assertEquals("D001", documento.getString("doadorCodigo"));
        assertEquals("I001", documento.getString("instituicaoCodigo"));
        assertEquals("01/01/2030", documento.getString("dataRegistro"));
        assertEquals("RECEBIDA", documento.getString("status"));

        List<Document> itens = documento.getList("itens", Document.class);
        assertEquals(2, itens.size());
        assertEquals("Arroz", itens.get(0).getString("alimento"));
        assertEquals(10, itens.get(0).getInteger("quantidade"));
        assertEquals("kg", itens.get(0).getString("unidade"));
        assertEquals("31/12/2030", itens.get(0).getString("validade"));
        assertEquals("Feijão", itens.get(1).getString("alimento"));
    }

    @Test
    void deveSalvarListaDeItensVaziaQuandoItensForemNulos() {
        Doacao doacao = mock(Doacao.class);
        when(doacao.getCodigo()).thenReturn("TESTE-002");
        when(doacao.getItens()).thenReturn(null);

        repository.salvar(doacao);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(collection).insertOne(captor.capture());

        assertEquals("TESTE-002", captor.getValue().getString("codigo"));
        assertTrue(captor.getValue().getList("itens", Document.class).isEmpty());
    }

    @Test
    void deveListarDoacoes() {
        Document semItens = new Document("codigo", "TESTE-002").append("doadorCodigo", "D002");
        var resultadoFind = findIterable(List.of(documentoExemplo(), semItens));
        when(collection.find()).thenReturn(resultadoFind);

        List<Doacao> resultado = repository.listar();

        assertEquals(2, resultado.size());

        Doacao primeira = resultado.get(0);
        assertEquals("TESTE-001", primeira.getCodigo());
        assertEquals("D001", primeira.getDoadorCodigo());
        assertEquals("I001", primeira.getInstituicaoCodigo());
        assertEquals("01/01/2030", primeira.getDataRegistro());
        assertEquals("RECEBIDA", primeira.getStatus());
        assertEquals(1, primeira.getItens().size());
        assertEquals("Arroz", primeira.getItens().get(0).getAlimento());
        assertEquals(10, primeira.getItens().get(0).getQuantidade());
        assertEquals("kg", primeira.getItens().get(0).getUnidade());
        assertEquals("31/12/2030", primeira.getItens().get(0).getValidade());

        Doacao segunda = resultado.get(1);
        assertEquals("TESTE-002", segunda.getCodigo());
        assertNotNull(segunda.getItens());
        assertTrue(segunda.getItens().isEmpty());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverDoacoes() {
        var resultadoFind = findIterable(List.of());
        when(collection.find()).thenReturn(resultadoFind);

        assertTrue(repository.listar().isEmpty());
    }

    @Test
    void deveBuscarDoacaoPorCodigo() {
        var resultadoFind = findIterable(List.of(documentoExemplo()));
        when(collection.find(any(Bson.class))).thenReturn(resultadoFind);

        Doacao resultado = repository.buscarPorCodigo("TESTE-001");

        assertNotNull(resultado);
        assertEquals("TESTE-001", resultado.getCodigo());
        assertEquals("Arroz", resultado.getItens().get(0).getAlimento());
    }

    @Test
    void deveRetornarNullQuandoCodigoNaoExistir() {
        var resultadoFind = findIterable(List.of());
        when(collection.find(any(Bson.class))).thenReturn(resultadoFind);

        assertNull(repository.buscarPorCodigo("CODIGO-INEXISTENTE"));
    }

    @Test
    void deveAtualizarDoacao() {
        when(collection.findOneAndReplace(any(Bson.class), any(Document.class)))
                .thenReturn(documentoExemplo());

        assertTrue(repository.atualizar("TESTE-001", doacaoExemplo()));

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(collection).findOneAndReplace(any(Bson.class), captor.capture());
        assertEquals("TESTE-001", captor.getValue().getString("codigo"));
    }

    @Test
    void deveRetornarFalseAoAtualizarCodigoInexistente() {
        when(collection.findOneAndReplace(any(Bson.class), any(Document.class))).thenReturn(null);

        assertFalse(repository.atualizar("CODIGO-INEXISTENTE", doacaoExemplo()));
    }

    @Test
    void deveExcluirDoacao() {
        when(collection.deleteOne(any(Bson.class))).thenReturn(DeleteResult.acknowledged(1));

        assertTrue(repository.excluir("TESTE-001"));
    }

    @Test
    void deveRetornarFalseAoExcluirCodigoInexistente() {
        when(collection.deleteOne(any(Bson.class))).thenReturn(DeleteResult.acknowledged(0));

        assertFalse(repository.excluir("CODIGO-INEXISTENTE"));
    }

    @Test
    void fecharNaoDeveFazerNadaQuandoCriadoComDatabase() {
        assertDoesNotThrow(repository::fechar);
    }

    @Test
    void deveCriarComUriEFecharConexao() {
        // O driver conecta de forma preguiçosa: nenhuma operação é feita no servidor aqui.
        MongoDoacaoRepository repositorioComUri =
                new MongoDoacaoRepository("mongodb://localhost:27017");

        assertDoesNotThrow(repositorioComUri::fechar);
    }
}

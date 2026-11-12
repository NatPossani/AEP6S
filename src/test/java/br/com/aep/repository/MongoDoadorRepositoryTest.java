package br.com.aep.repository;

import static br.com.aep.repository.MongoMocks.findIterable;
import static br.com.aep.repository.MongoMocks.mockCollection;
import static br.com.aep.repository.MongoMocks.mockDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import br.com.aep.model.Contato;
import br.com.aep.model.Doador;

class MongoDoadorRepositoryTest {

    private MongoCollection<Document> collection;
    private MongoDoadorRepository repository;

    @BeforeEach
    void configurar() {
        collection = mockCollection();
        repository = new MongoDoadorRepository(
                mockDatabase(MongoDoadorRepository.COLLECTION_NAME, collection));
    }

    private static Doador doadorExemplo() {
        return new Doador("D001", "Mercado Central", "EMPRESA",
                new Contato("44999990000", "mercado@teste.com"));
    }

    private static Document documentoExemplo() {
        return new Document("codigo", "D001")
                .append("nome", "Mercado Central")
                .append("tipo", "EMPRESA")
                .append("contato", new Document("telefone", "44999990000")
                        .append("email", "mercado@teste.com"));
    }

    private Document capturarInsert() {
        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(collection).insertOne(captor.capture());
        return captor.getValue();
    }

    @Test
    void deveUsarColecaoDoadores() {
        assertEquals("doadores", MongoDoadorRepository.COLLECTION_NAME);
    }

    @Test
    void deveSalvarDoadorComContatoComoSubdocumento() {
        repository.salvar(doadorExemplo());

        Document documento = capturarInsert();
        assertEquals("D001", documento.getString("codigo"));
        assertEquals("Mercado Central", documento.getString("nome"));
        assertEquals("EMPRESA", documento.getString("tipo"));

        Document contato = documento.get("contato", Document.class);
        assertEquals("44999990000", contato.getString("telefone"));
        assertEquals("mercado@teste.com", contato.getString("email"));
    }

    @Test
    void deveSalvarContatoVazioQuandoDoadorNaoTiverContato() {
        repository.salvar(new Doador("D002", "Maria", "PESSOA_FISICA", null));

        Document contato = capturarInsert().get("contato", Document.class);
        assertNotNull(contato);
        assertTrue(contato.isEmpty());
    }

    @Test
    void deveListarDoadores() {
        Document semContato = new Document("codigo", "D002").append("nome", "Maria");
        var resultadoFind = findIterable(List.of(documentoExemplo(), semContato));
        when(collection.find()).thenReturn(resultadoFind);

        List<Doador> resultado = repository.listar();

        assertEquals(2, resultado.size());

        Doador primeiro = resultado.get(0);
        assertEquals("D001", primeiro.getCodigo());
        assertEquals("Mercado Central", primeiro.getNome());
        assertEquals("EMPRESA", primeiro.getTipo());
        assertEquals("44999990000", primeiro.getContato().getTelefone());
        assertEquals("mercado@teste.com", primeiro.getContato().getEmail());

        Doador segundo = resultado.get(1);
        assertEquals("D002", segundo.getCodigo());
        assertNull(segundo.getContato());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverDoadores() {
        var resultadoFind = findIterable(List.of());
        when(collection.find()).thenReturn(resultadoFind);

        assertTrue(repository.listar().isEmpty());
    }

    @Test
    void deveBuscarDoadorPorCodigo() {
        var resultadoFind = findIterable(List.of(documentoExemplo()));
        when(collection.find(any(Bson.class))).thenReturn(resultadoFind);

        Doador resultado = repository.buscarPorCodigo("D001");

        assertNotNull(resultado);
        assertEquals("Mercado Central", resultado.getNome());
    }

    @Test
    void deveRetornarNullQuandoDoadorNaoExistir() {
        var resultadoFind = findIterable(List.of());
        when(collection.find(any(Bson.class))).thenReturn(resultadoFind);

        assertNull(repository.buscarPorCodigo("INEXISTENTE"));
    }

    @Test
    void deveAtualizarDoador() {
        when(collection.findOneAndReplace(any(Bson.class), any(Document.class)))
                .thenReturn(documentoExemplo());

        assertTrue(repository.atualizar("D001", doadorExemplo()));
    }

    @Test
    void deveRetornarFalseAoAtualizarDoadorInexistente() {
        when(collection.findOneAndReplace(any(Bson.class), any(Document.class))).thenReturn(null);

        assertFalse(repository.atualizar("INEXISTENTE", doadorExemplo()));
    }

    @Test
    void deveExcluirDoador() {
        when(collection.deleteOne(any(Bson.class))).thenReturn(DeleteResult.acknowledged(1));

        assertTrue(repository.excluir("D001"));
    }

    @Test
    void deveRetornarFalseAoExcluirDoadorInexistente() {
        when(collection.deleteOne(any(Bson.class))).thenReturn(DeleteResult.acknowledged(0));

        assertFalse(repository.excluir("INEXISTENTE"));
    }
}

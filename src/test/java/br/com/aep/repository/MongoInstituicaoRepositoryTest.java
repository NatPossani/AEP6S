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

import br.com.aep.model.Endereco;
import br.com.aep.model.Instituicao;

class MongoInstituicaoRepositoryTest {

    private MongoCollection<Document> collection;
    private MongoInstituicaoRepository repository;

    @BeforeEach
    void configurar() {
        collection = mockCollection();
        repository = new MongoInstituicaoRepository(
                mockDatabase(MongoInstituicaoRepository.COLLECTION_NAME, collection));
    }

    private static Instituicao instituicaoExemplo() {
        return new Instituicao("I001", "Instituição Esperança",
                new Endereco("Rua A, 100", "Maringá", "PR"));
    }

    private static Document documentoExemplo() {
        return new Document("codigo", "I001")
                .append("nome", "Instituição Esperança")
                .append("endereco", new Document("rua", "Rua A, 100")
                        .append("cidade", "Maringá")
                        .append("estado", "PR"));
    }

    private Document capturarInsert() {
        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(collection).insertOne(captor.capture());
        return captor.getValue();
    }

    @Test
    void deveUsarColecaoInstituicoes() {
        assertEquals("instituicoes", MongoInstituicaoRepository.COLLECTION_NAME);
    }

    @Test
    void deveSalvarInstituicaoComEnderecoComoSubdocumento() {
        repository.salvar(instituicaoExemplo());

        Document documento = capturarInsert();
        assertEquals("I001", documento.getString("codigo"));
        assertEquals("Instituição Esperança", documento.getString("nome"));

        Document endereco = documento.get("endereco", Document.class);
        assertEquals("Rua A, 100", endereco.getString("rua"));
        assertEquals("Maringá", endereco.getString("cidade"));
        assertEquals("PR", endereco.getString("estado"));
    }

    @Test
    void deveSalvarEnderecoVazioQuandoInstituicaoNaoTiverEndereco() {
        repository.salvar(new Instituicao("I002", "Casa de Apoio", null));

        Document endereco = capturarInsert().get("endereco", Document.class);
        assertNotNull(endereco);
        assertTrue(endereco.isEmpty());
    }

    @Test
    void deveListarInstituicoes() {
        Document semEndereco = new Document("codigo", "I002").append("nome", "Casa de Apoio");
        var resultadoFind = findIterable(List.of(documentoExemplo(), semEndereco));
        when(collection.find()).thenReturn(resultadoFind);

        List<Instituicao> resultado = repository.listar();

        assertEquals(2, resultado.size());

        Instituicao primeira = resultado.get(0);
        assertEquals("I001", primeira.getCodigo());
        assertEquals("Instituição Esperança", primeira.getNome());
        assertEquals("Rua A, 100", primeira.getEndereco().getRua());
        assertEquals("Maringá", primeira.getEndereco().getCidade());
        assertEquals("PR", primeira.getEndereco().getEstado());

        Instituicao segunda = resultado.get(1);
        assertEquals("I002", segunda.getCodigo());
        assertNull(segunda.getEndereco());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverInstituicoes() {
        var resultadoFind = findIterable(List.of());
        when(collection.find()).thenReturn(resultadoFind);

        assertTrue(repository.listar().isEmpty());
    }

    @Test
    void deveBuscarInstituicaoPorCodigo() {
        var resultadoFind = findIterable(List.of(documentoExemplo()));
        when(collection.find(any(Bson.class))).thenReturn(resultadoFind);

        Instituicao resultado = repository.buscarPorCodigo("I001");

        assertNotNull(resultado);
        assertEquals("Instituição Esperança", resultado.getNome());
    }

    @Test
    void deveRetornarNullQuandoInstituicaoNaoExistir() {
        var resultadoFind = findIterable(List.of());
        when(collection.find(any(Bson.class))).thenReturn(resultadoFind);

        assertNull(repository.buscarPorCodigo("INEXISTENTE"));
    }

    @Test
    void deveAtualizarInstituicao() {
        when(collection.findOneAndReplace(any(Bson.class), any(Document.class)))
                .thenReturn(documentoExemplo());

        assertTrue(repository.atualizar("I001", instituicaoExemplo()));
    }

    @Test
    void deveRetornarFalseAoAtualizarInstituicaoInexistente() {
        when(collection.findOneAndReplace(any(Bson.class), any(Document.class))).thenReturn(null);

        assertFalse(repository.atualizar("INEXISTENTE", instituicaoExemplo()));
    }

    @Test
    void deveExcluirInstituicao() {
        when(collection.deleteOne(any(Bson.class))).thenReturn(DeleteResult.acknowledged(1));

        assertTrue(repository.excluir("I001"));
    }

    @Test
    void deveRetornarFalseAoExcluirInstituicaoInexistente() {
        when(collection.deleteOne(any(Bson.class))).thenReturn(DeleteResult.acknowledged(0));

        assertFalse(repository.excluir("INEXISTENTE"));
    }
}

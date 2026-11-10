package br.com.aep.repository;

import br.com.aep.model.Endereco;
import br.com.aep.model.Instituicao;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoInstituicaoRepository implements InstituicaoRepository {

    static final String COLLECTION_NAME = "instituicoes";

    private final MongoCollection<Document> collection;

    public MongoInstituicaoRepository(MongoDatabase database) {
        collection = database.getCollection(COLLECTION_NAME);
    }

    @Override
    public void salvar(Instituicao instituicao) {
        collection.insertOne(toDocument(instituicao));
    }

    @Override
    public List<Instituicao> listar() {
        List<Instituicao> instituicoes = new ArrayList<>();

        for (Document documento : collection.find()) {
            instituicoes.add(fromDocument(documento));
        }

        return instituicoes;
    }

    @Override
    public Instituicao buscarPorCodigo(String codigo) {
        Document documento = collection.find(eq("codigo", codigo)).first();

        if (documento == null) {
            return null;
        }

        return fromDocument(documento);
    }

    @Override
    public boolean atualizar(String codigo, Instituicao instituicao) {
        Document resultado = collection.findOneAndReplace(
                eq("codigo", codigo),
                toDocument(instituicao)
        );

        return resultado != null;
    }

    @Override
    public boolean excluir(String codigo) {
        return collection.deleteOne(eq("codigo", codigo)).getDeletedCount() > 0;
    }

    private Document toDocument(Instituicao instituicao) {
        Document endereco = new Document();

        if (instituicao.getEndereco() != null) {
            endereco.append("rua", instituicao.getEndereco().getRua())
                    .append("cidade", instituicao.getEndereco().getCidade())
                    .append("estado", instituicao.getEndereco().getEstado());
        }

        return new Document("codigo", instituicao.getCodigo())
                .append("nome", instituicao.getNome())
                .append("endereco", endereco);
    }

    private Instituicao fromDocument(Document documento) {
        Document enderecoDocumento = documento.get("endereco", Document.class);
        Endereco endereco = null;

        if (enderecoDocumento != null) {
            endereco = new Endereco(
                    enderecoDocumento.getString("rua"),
                    enderecoDocumento.getString("cidade"),
                    enderecoDocumento.getString("estado")
            );
        }

        return new Instituicao(
                documento.getString("codigo"),
                documento.getString("nome"),
                endereco
        );
    }
}

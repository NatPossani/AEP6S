package br.com.aep.repository;

import br.com.aep.model.Doacao;
import br.com.aep.model.ItemDoacao;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoDoacaoRepository implements DoacaoRepository {

    static final String COLLECTION_NAME = "doacoes";

    private final MongoCollection<Document> collection;
    private final MongoConnection connection;

    public MongoDoacaoRepository(String uri) {
        connection = new MongoConnection(uri);
        collection = connection.getDatabase().getCollection(COLLECTION_NAME);
    }

    public MongoDoacaoRepository(MongoDatabase database) {
        connection = null;
        collection = database.getCollection(COLLECTION_NAME);
    }

    @Override
    public void salvar(Doacao doacao) {
        collection.insertOne(toDocument(doacao));
    }

    @Override
    public List<Doacao> listar() {
        List<Doacao> doacoes = new ArrayList<>();

        for (Document documento : collection.find()) {
            doacoes.add(fromDocument(documento));
        }

        return doacoes;
    }

    @Override
    public Doacao buscarPorCodigo(String codigo) {
        Document documento = collection.find(eq("codigo", codigo)).first();

        if (documento == null) {
            return null;
        }

        return fromDocument(documento);
    }

    @Override
    public boolean atualizar(String codigo, Doacao doacao) {
        Document resultado = collection.findOneAndReplace(
                eq("codigo", codigo),
                toDocument(doacao)
        );

        return resultado != null;
    }

    @Override
    public boolean excluir(String codigo) {
        return collection.deleteOne(eq("codigo", codigo)).getDeletedCount() > 0;
    }

    public void fechar() {
        if (connection != null) {
            connection.fechar();
        }
    }

    private Document toDocument(Doacao doacao) {
        List<Document> itens = new ArrayList<>();

        if (doacao.getItens() != null) {
            for (ItemDoacao item : doacao.getItens()) {
                itens.add(new Document("alimento", item.getAlimento())
                        .append("quantidade", item.getQuantidade())
                        .append("unidade", item.getUnidade())
                        .append("validade", item.getValidade()));
            }
        }

        return new Document("codigo", doacao.getCodigo())
                .append("doadorCodigo", doacao.getDoadorCodigo())
                .append("instituicaoCodigo", doacao.getInstituicaoCodigo())
                .append("dataRegistro", doacao.getDataRegistro())
                .append("status", doacao.getStatus())
                .append("itens", itens);
    }

    private Doacao fromDocument(Document documento) {
        List<ItemDoacao> itens = new ArrayList<>();
        List<Document> itensDocumento = documento.getList("itens", Document.class);

        if (itensDocumento != null) {
            for (Document item : itensDocumento) {
                itens.add(new ItemDoacao(
                        item.getString("alimento"),
                        item.getInteger("quantidade"),
                        item.getString("unidade"),
                        item.getString("validade")
                ));
            }
        }

        return new Doacao(
                documento.getString("codigo"),
                documento.getString("doadorCodigo"),
                documento.getString("instituicaoCodigo"),
                documento.getString("dataRegistro"),
                documento.getString("status"),
                itens
        );
    }
}

package br.com.aep.repository;

import br.com.aep.model.Doacao;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoDoacaoRepository implements DoacaoRepository {

    private final MongoClient client;
    private final MongoCollection<Document> collection;

    public MongoDoacaoRepository(String uri) {
        client = MongoClients.create(uri);
        MongoDatabase database = client.getDatabase("aep_banco_alimentos");
        collection = database.getCollection("doacoes");
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

    private Document toDocument(Doacao doacao) {
        return new Document("codigo", doacao.getCodigo())
                .append("nomeDoador", doacao.getNomeDoador())
                .append("alimento", doacao.getAlimento())
                .append("quantidade", doacao.getQuantidade())
                .append("unidade", doacao.getUnidade())
                .append("validade", doacao.getValidade())
                .append("destino", doacao.getDestino());
    }

    private Doacao fromDocument(Document documento) {
        return new Doacao(
                documento.getString("codigo"),
                documento.getString("nomeDoador"),
                documento.getString("alimento"),
                documento.getInteger("quantidade"),
                documento.getString("unidade"),
                documento.getString("validade"),
                documento.getString("destino")
        );
    }

    public void fechar() {
        client.close();
    }
}

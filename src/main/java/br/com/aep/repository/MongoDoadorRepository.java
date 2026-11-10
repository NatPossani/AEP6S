package br.com.aep.repository;

import br.com.aep.model.Contato;
import br.com.aep.model.Doador;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoDoadorRepository implements DoadorRepository {

    static final String COLLECTION_NAME = "doadores";

    private final MongoCollection<Document> collection;

    public MongoDoadorRepository(MongoDatabase database) {
        collection = database.getCollection(COLLECTION_NAME);
    }

    @Override
    public void salvar(Doador doador) {
        collection.insertOne(toDocument(doador));
    }

    @Override
    public List<Doador> listar() {
        List<Doador> doadores = new ArrayList<>();

        for (Document documento : collection.find()) {
            doadores.add(fromDocument(documento));
        }

        return doadores;
    }

    @Override
    public Doador buscarPorCodigo(String codigo) {
        Document documento = collection.find(eq("codigo", codigo)).first();

        if (documento == null) {
            return null;
        }

        return fromDocument(documento);
    }

    @Override
    public boolean atualizar(String codigo, Doador doador) {
        Document resultado = collection.findOneAndReplace(
                eq("codigo", codigo),
                toDocument(doador)
        );

        return resultado != null;
    }

    @Override
    public boolean excluir(String codigo) {
        return collection.deleteOne(eq("codigo", codigo)).getDeletedCount() > 0;
    }

    private Document toDocument(Doador doador) {
        Document contato = new Document();

        if (doador.getContato() != null) {
            contato.append("telefone", doador.getContato().getTelefone())
                    .append("email", doador.getContato().getEmail());
        }

        return new Document("codigo", doador.getCodigo())
                .append("nome", doador.getNome())
                .append("tipo", doador.getTipo())
                .append("contato", contato);
    }

    private Doador fromDocument(Document documento) {
        Document contatoDocumento = documento.get("contato", Document.class);
        Contato contato = null;

        if (contatoDocumento != null) {
            contato = new Contato(
                    contatoDocumento.getString("telefone"),
                    contatoDocumento.getString("email")
            );
        }

        return new Doador(
                documento.getString("codigo"),
                documento.getString("nome"),
                documento.getString("tipo"),
                contato
        );
    }
}

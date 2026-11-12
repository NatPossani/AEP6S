package br.com.aep.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * Utilitários para simular o driver do MongoDB nos testes, sem precisar de um banco real.
 */
final class MongoMocks {

    private MongoMocks() {
    }

    @SuppressWarnings("unchecked")
    static MongoCollection<Document> mockCollection() {
        return mock(MongoCollection.class);
    }

    static MongoDatabase mockDatabase(String collectionName, MongoCollection<Document> collection) {
        MongoDatabase database = mock(MongoDatabase.class);
        when(database.getCollection(collectionName)).thenReturn(collection);
        return database;
    }

    @SuppressWarnings("unchecked")
    static FindIterable<Document> findIterable(List<Document> documentos) {
        FindIterable<Document> iterable = mock(FindIterable.class);
        when(iterable.iterator()).thenAnswer(invocation -> cursor(documentos.iterator()));
        when(iterable.first()).thenReturn(documentos.isEmpty() ? null : documentos.get(0));
        return iterable;
    }

    @SuppressWarnings("unchecked")
    private static MongoCursor<Document> cursor(Iterator<Document> iterator) {
        MongoCursor<Document> cursor = mock(MongoCursor.class);
        when(cursor.hasNext()).thenAnswer(invocation -> iterator.hasNext());
        when(cursor.next()).thenAnswer(invocation -> iterator.next());
        return cursor;
    }
}

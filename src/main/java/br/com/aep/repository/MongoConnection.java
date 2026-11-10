package br.com.aep.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {

    public static final String DATABASE_NAME = "aep_banco_alimentos";

    private final MongoClient client;
    private final MongoDatabase database;

    public MongoConnection(String uri) {
        client = MongoClients.create(uri);
        database = client.getDatabase(DATABASE_NAME);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void fechar() {
        client.close();
    }
}

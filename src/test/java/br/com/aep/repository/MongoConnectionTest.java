package br.com.aep.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoDatabase;

class MongoConnectionTest {

    @Test
    void deveCriarConexaoComBancoPadraoEFechar() {
        // O driver conecta de forma preguiçosa: nenhuma operação é feita no servidor aqui.
        MongoConnection connection = new MongoConnection("mongodb://localhost:27017");

        MongoDatabase database = connection.getDatabase();

        assertNotNull(database);
        assertEquals(MongoConnection.DATABASE_NAME, database.getName());
        assertEquals("aep_banco_alimentos", database.getName());
        assertDoesNotThrow(connection::fechar);
    }
}

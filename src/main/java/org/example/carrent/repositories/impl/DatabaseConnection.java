package org.example.carrent.repositories.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection get() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = System.getenv("DATABASE_URL");
                if (url == null) throw new RuntimeException("Brak zmiennej DATABASE_URL!");
                connection = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd połączenia z bazą: " + e.getMessage());
        }
        return connection;
    }
}
package com.example.baum;

import java.sql.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseManager {
    private Connection connection;

    public void connect() {
        try {
            String dbURL = "jdbc:mysql://localhost:25000/bambus";
            String username = "root";
            String password = "bigbrother";
            connection = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTablesIfNotExists() {
        String createTableQuery;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get("MySQL/Generate.sql"));
            createTableQuery = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            Statement statement = connection.createStatement();
            String[] queries = createTableQuery.split(";");

            for (String query : queries) {
                query = query.trim();

                if (!query.isEmpty()) {
                    statement.executeUpdate(query);
                }
            }

            System.out.println("All SQL commands executed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

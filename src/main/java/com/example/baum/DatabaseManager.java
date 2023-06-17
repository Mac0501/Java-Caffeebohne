package com.example.baum;

import java.sql.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A utility class that manages the database connection and performs database
 * operations.
 */
public class DatabaseManager {
    private Connection connection;

    private String dbURL;
    private String username;
    private String password;

    /**
     * Constructs a new `DatabaseManager` with the specified database connection
     * settings.
     *
     * @param dbURL    the URL of the database
     * @param username the username for the database connection
     * @param password the password for the database connection
     */
    public DatabaseManager(String dbURL, String username, String password) {
        this.dbURL = dbURL;
        this.username = username;
        this.password = password;
    }

    /**
     * Connects to the database using the specified connection settings.
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the database tables if they do not already exist.
     */
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

    /**
     * Returns the underlying `Connection` object for the database connection.
     *
     * @return the `Connection` object
     */
    public Connection getConnection() {
        return connection;
    }
}

package com.example.baum;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    public void connect() {
        try {
            String dbURL = "jdbc:mysql://localhost:3307/bambus";
            String username = "root";
            String password = "root";
            connection = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS students (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(50)," +
                "class VARCHAR(50)," +
                "company VARCHAR(50)" +
                ")";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

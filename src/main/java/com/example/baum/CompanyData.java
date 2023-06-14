package com.example.baum;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CompanyData {
    private final ObservableList<Company> companyList;
    private final DatabaseManager databaseManager;

    public CompanyData(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        companyList = FXCollections.observableArrayList();
    }

    public ObservableList<Company> getCompanyList() {
        return companyList;
    }

    public void fetchCompaniesFromDatabase() {
        String selectQuery = "SELECT * FROM company";
        try {
            Statement statement = databaseManager.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Company company = new Company(id, name);
                companyList.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCompany(String name) {
        String insertQuery = "INSERT INTO company (name) VALUES (?)";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(insertQuery);
            statement.setString(1, name);
            statement.executeUpdate();

            int lastInsertedId = getLastInsertedId();
            Company newCompany = new Company(lastInsertedId, name);
            companyList.add(newCompany);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeCompany(Company company) {
        if (company != null) {
            try {
                String deleteQuery = "DELETE FROM company WHERE id = ?";
                PreparedStatement statement = databaseManager.getConnection().prepareStatement(deleteQuery);
                statement.setInt(1, company.getId());
                statement.executeUpdate();
                companyList.remove(company);
            } catch (SQLException e) {
                // Display an error alert
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error: Delete Company");
                alert.setHeaderText("Failed to delete the company.");
                alert.setContentText("The company has associated students.");

                alert.showAndWait();
            }
        }
    }


    public Company getCompanyById(int id) {
        for (Company company : companyList) {
            if (company.getId() == id) {
                return company;
            }
        }
        return null; // Company with the specified ID not found
    }

    public Company getCompanyByName(String name) {
        for (Company company : companyList) {
            if (company.getName().equals(name)) {
                return company;
            }
        }
        return null; // Company with the specified name not found
    }

    private int getLastInsertedId() throws SQLException {
        Statement statement = databaseManager.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }
    public int getCompanyIdByName(String companyName) {
        for (Company company : companyList) {
            if (company.getName().equals(companyName)) {
                return company.getId();
            }
        }
        return -1; // Return -1 if the company with the specified name is not found
    }
    private void clearFields() {
        // Clear text fields after adding a company
        // You can also update the GUI with a notification or feedback to the user
        // indicating that the operation was successful
        // nameField.clear();
    }

    public ObservableList<Company> searchCompaniesByName(String searchTerm) {
        ObservableList<Company> searchResults = FXCollections.observableArrayList();
        String searchQuery = "SELECT * FROM company WHERE LOWER(name) LIKE ?";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(searchQuery);
            statement.setString(1, "%" + searchTerm.toLowerCase() + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Company company = new Company(id, name);
                searchResults.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

}

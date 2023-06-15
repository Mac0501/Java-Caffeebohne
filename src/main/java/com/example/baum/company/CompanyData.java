package com.example.baum.company;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.baum.DatabaseManager;

/**
 * The CompanyData class manages the retrieval, addition, and removal of
 * companies from the database.
 */
public class CompanyData {
    private final ObservableList<Company> companyList;
    private final DatabaseManager databaseManager;

    /**
     * Constructs a new CompanyData object with the specified DatabaseManager.
     *
     * @param databaseManager The DatabaseManager used to connect to the database.
     */
    public CompanyData(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        companyList = FXCollections.observableArrayList();
    }

    /**
     * Returns the list of companies.
     *
     * @return The ObservableList of Company objects.
     */
    public ObservableList<Company> getCompanyList() {
        return companyList;
    }

    /**
     * Retrieves companies from the database and populates the company list.
     */
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

    /**
     * Adds a new company to the database and updates the company list.
     *
     * @param name The name of the company.
     */
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

    /**
     * Removes a company from the database and updates the company list.
     *
     * @param company The company to be removed.
     */
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

    /**
     * Retrieves a company by its ID.
     *
     * @param id The ID of the company.
     * @return The Company object with the specified ID, or null if not found.
     */
    public Company getCompanyById(int id) {
        for (Company company : companyList) {
            if (company.getId() == id) {
                return company;
            }
        }
        return null; // Company with the specified ID not found
    }

    /**
     * Retrieves a company by its name.
     *
     * @param name The name of the company.
     * @return The Company object with the specified name, or null if not found.
     */
    public Company getCompanyByName(String name) {
        for (Company company : companyList) {
            if (company.getName().equals(name)) {
                return company;
            }
        }
        return null; // Company with the specified name not found
    }

    /**
     * Retrieves the last inserted ID from the database.
     *
     * @return The last inserted ID, or -1 if not found.
     * @throws SQLException If an SQL exception occurs.
     */
    private int getLastInsertedId() throws SQLException {
        Statement statement = databaseManager.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    /**
     * Retrieves the ID of a company by its name.
     *
     * @param companyName The name of the company.
     * @return The ID of the company with the specified name, or -1 if not found.
     */
    public int getCompanyIdByName(String companyName) {
        for (Company company : companyList) {
            if (company.getName().equals(companyName)) {
                return company.getId();
            }
        }
        return -1; // Return -1 if the company with the specified name is not found
    }

    /**
     * Clears the input fields after adding a company.
     */
    private void clearFields() {
        // Clear text fields after adding a company
        // You can also update the GUI with a notification or feedback to the user
        // indicating that the operation was successful
        // nameField.clear();
    }

    /**
     * Searches for companies by name in the database.
     *
     * @param searchTerm The search term to match the company name.
     * @return The ObservableList of matching Company objects.
     */
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

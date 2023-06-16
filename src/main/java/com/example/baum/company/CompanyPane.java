package com.example.baum.company;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * A custom GridPane that represents the Company pane in the application.
 * It allows adding, removing, and searching for companies.
 */
public class CompanyPane extends GridPane {
    private final CompanyData companyData;
    private final TextField nameField;
    private final Label errorLabel;
    private final TableView<Company> companyTableView;

    /**
     * Constructs a CompanyPane with the specified CompanyData.
     *
     * @param companyData The CompanyData object to be used for managing companies.
     */
    public CompanyPane(CompanyData companyData) {
        this.companyData = companyData;
        this.nameField = createCompanyNameField();
        this.errorLabel = createCompanyErrorLabel();
        this.companyTableView = createCompanyTableView();

        initialize();
    }

    /**
     * Initializes the CompanyPane by creating and configuring its components.
     */
    private void initialize() {
        TextField searchField = createCompanySearchField();
        Button addButton = createAddCompanyButton();
        Button removeButton = createRemoveCompanyButton();

        configureLayout(nameField, addButton, removeButton, errorLabel, searchField, companyTableView);

        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    // Model
    /**
     * Displays a validation error message in the error label.
     *
     * @param message The error message to display.
     */
    private void displayValidationError(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(true);
    }

    /**
     * Clears the validation error message from the error label.
     */
    private void clearValidationError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.getStyleClass().remove("error-label");
    }

    // View
    private TextField createCompanySearchField() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Companies...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            companyTableView.setItems(companyData.searchCompaniesByName(searchTerm));
        });

        return searchField;
    }

    private TextField createCompanyNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Company Name");

        return nameField;
    }

    private Label createCompanyErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        return errorLabel;
    }

    private TableView<Company> createCompanyTableView() {
        TableView<Company> table = new TableView<>();
        table.setItems(companyData.getCompanyList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Company, String> nameColumn = new TableColumn<>("Company Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().add(nameColumn);

        return table;
    }

    // Controller
    private Button createAddCompanyButton() {
        Button addButton = new Button("Add Company");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                displayValidationError("Please enter a company name.");
                return;
            }

            clearValidationError();
            companyData.addCompany(nameField.getText());
            nameField.clear();
        });

        return addButton;
    }

    private Button createRemoveCompanyButton() {
        Button removeButton = new Button("Remove Company");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Company selected = companyTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                companyData.removeCompany(selected);
            }
        });

        removeButton.getStyleClass().add("remove-button");

        return removeButton;
    }

    // Layout configuration
    private void configureLayout(TextField nameField, Button addButton,
                                 Button removeButton, Label errorLabel,
                                 TextField searchField, TableView<Company> table) {
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        this.getColumnConstraints().addAll(col1, col2);

        this.add(nameField, 0, 0);
        this.add(addButton, 0, 1);
        this.add(removeButton, 1, 1);
        this.add(errorLabel, 0, 2, 2, 1);
        this.add(searchField, 0, 3, 2, 1);
        this.add(table, 0, 4, 2, 1);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(addButton, Priority.ALWAYS);
        GridPane.setHgrow(removeButton, Priority.ALWAYS);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);
    }
}

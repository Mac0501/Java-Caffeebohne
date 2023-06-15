package com.example.baum.company;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


/**
 * A custom GridPane that represents the Company pane in the application.
 * It allows adding, removing, and searching for company.
 */
public class CompanyPane extends GridPane {
    private CompanyData companyData;

    /**
     * Constructs a new instance of CompanyPane with the provided CompanyData object.
     *
     * @param companyData the CompanyData object to be associated with this CompanyPane
     */
    public CompanyPane(CompanyData companyData) {
        this.companyData = companyData;
        initialize();
    }

    /**
     * Initializes the CompanyPane by creating and adding the necessary UI elements.
     */
    private void initialize() {
        TableView<Company> table = createCompanyTableView();
        TextField searchField = createCompanySearchField(table);
        TextField nameField = createCompanyNameField();
        Label errorLabel = createErrorLabel();
        Button addButton = createAddCompanyButton(nameField, errorLabel);
        Button removeButton = createRemoveCompanyButton(table);

        GridPane gridPane = createCompanyGridPane(nameField, addButton, removeButton,
                errorLabel, searchField, table);

        this.getChildren().add(gridPane);
    }

    /**
     * Creates and configures the TableView for displaying the list of companies.
     *
     * @return the configured TableView
     */
    private TableView<Company> createCompanyTableView() {
        TableView<Company> table = new TableView<>();
        table.setItems(companyData.getCompanyList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Company, String> nameColumn = new TableColumn<>("Company Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().add(nameColumn);

        return table;
    }

    /**
     * Creates and configures the TextField for searching companies by name.
     *
     * @param table the TableView to be filtered based on the search input
     * @return the configured search TextField
     */
    private TextField createCompanySearchField(TableView<Company> table) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Companies...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(companyData.searchCompaniesByName(searchTerm));
        });

        return searchField;
    }

    /**
     * Creates and configures the TextField for entering a company name.
     *
     * @return the configured name TextField
     */
    private TextField createCompanyNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Company Name");

        return nameField;
    }

    /**
     * Creates and configures the Button for adding a new company.
     *
     * @param nameField   the TextField containing the company name input
     * @param errorLabel  the Label used to display error messages
     * @return the configured add company Button
     */
    private Button createAddCompanyButton(TextField nameField, Label errorLabel) {
        Button addButton = new Button("Add Company");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                displayValidationError(errorLabel, "Please enter a company name.");
                return;
            }

            clearValidationError(errorLabel);

            companyData.addCompany(nameField.getText());
            nameField.clear();
        });

        return addButton;
    }

    /**
     * Creates and configures the Button for removing a company.
     *
     * @param table the TableView containing the list of companies
     * @return the configured remove company Button
     */
    private Button createRemoveCompanyButton(TableView<Company> table) {
        Button removeButton = new Button("Remove Company");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Company selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                companyData.removeCompany(selected);
            }
        });

        removeButton.getStyleClass().add("remove-button");

        return removeButton;
    }

    /**
     * Creates and configures the main GridPane layout for the CompanyPane.
     *
     * @param nameField     the TextField for entering a company name
     * @param addButton     the Button for adding a new company
     * @param removeButton  the Button for removing a company
     * @param errorLabel    the Label used to display error messages
     * @param searchField   the TextField for searching companies by name
     * @param table         the TableView for displaying the list of companies
     * @return the configured main GridPane
     */
    private GridPane createCompanyGridPane(TextField nameField, Button addButton,
                                           Button removeButton, Label errorLabel,
                                           TextField searchField, TableView<Company> table) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        gridPane.getColumnConstraints().addAll(col1, col2);

        gridPane.add(nameField, 0, 0);
        gridPane.add(addButton, 0, 1, 1, 1);
        gridPane.add(removeButton, 1, 1, 1, 1);
        gridPane.add(errorLabel, 0, 2, 2, 1);
        gridPane.add(searchField, 0, 3, 2, 1);
        gridPane.add(table, 0, 4, 2, 1);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(addButton, Priority.ALWAYS);
        GridPane.setHgrow(removeButton, Priority.ALWAYS);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        return gridPane;
    }

    /**
     * Creates and configures the Label used to display error messages.
     *
     * @return the configured error Label
     */
    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        return errorLabel;
    }

    /**
     * Displays a validation error message in the provided error Label.
     *
     * @param errorLabel  the Label used to display the error message
     * @param message     the validation error message to be displayed
     */
    private void displayValidationError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(true);
    }

    /**
     * Clears the validation error message from the provided error Label.
     *
     * @param errorLabel  the Label used to display the error message
     */
    private void clearValidationError(Label errorLabel) {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.getStyleClass().remove("error-label");
    }
}

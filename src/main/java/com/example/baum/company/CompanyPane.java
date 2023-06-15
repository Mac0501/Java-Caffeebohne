package com.example.baum.company;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class CompanyPane extends GridPane {
    private CompanyData companyData;

    public CompanyPane(CompanyData companyData) {
        this.companyData = companyData;
        initialize();
    }

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

    private TableView<Company> createCompanyTableView() {
        TableView<Company> table = new TableView<>();
        table.setItems(companyData.getCompanyList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Company, String> nameColumn = new TableColumn<>("Company Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().add(nameColumn);

        return table;
    }

    private TextField createCompanySearchField(TableView<Company> table) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Companies...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(companyData.searchCompaniesByName(searchTerm));
        });

        return searchField;
    }

    private TextField createCompanyNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Company Name");

        return nameField;
    }

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

    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        return errorLabel;
    }

    // Helper method to display validation error message
    private void displayValidationError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(true);
    }

    // Helper method to clear validation error message
    private void clearValidationError(Label errorLabel) {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.getStyleClass().remove("error-label");
    }
}

package com.example.baum.room;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class RoomPane extends GridPane {
    private RoomData roomData;

    public RoomPane(RoomData roomData) {
        this.roomData = roomData;
        initialize();
    }

    private void initialize() {
        TableView<Room> table = createRoomTableView();
        TextField searchField = createRoomSearchField(table);
        TextField nameField = createRoomNameField();
        Label errorLabel = createRoomErrorLabel();
        Button addButton = createAddRoomButton(nameField, errorLabel);
        Button removeButton = createRemoveRoomButton(table);

        GridPane gridPane = createRoomGridPane(nameField, addButton, removeButton,
                errorLabel, searchField, table);

        this.getChildren().add(gridPane);
    }

    private TableView<Room> createRoomTableView() {
        TableView<Room> table = new TableView<>();
        table.setItems(roomData.getRoomList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, String> nameColumn = new TableColumn<>("Room Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().add(nameColumn);

        return table;
    }

    private TextField createRoomSearchField(TableView<Room> table) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Rooms...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(roomData.searchRoomsByName(searchTerm));
        });

        return searchField;
    }

    private TextField createRoomNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Room Name");

        return nameField;
    }

    private Button createAddRoomButton(TextField nameField, Label errorLabel) {
        Button addButton = new Button("Add Room");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                displayValidationError(errorLabel, "Please enter a room name.");
                return;
            }

            clearValidationError(errorLabel);

            roomData.addRoom(nameField.getText());
            nameField.clear();
        });

        return addButton;
    }

    private Button createRemoveRoomButton(TableView<Room> table) {
        Button removeButton = new Button("Remove Room");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Room selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                roomData.removeRoom(selected);
            }
        });

        removeButton.getStyleClass().add("remove-button");

        return removeButton;
    }

    private GridPane createRoomGridPane(TextField nameField, Button addButton,
                                        Button removeButton, Label errorLabel,
                                        TextField searchField, TableView<Room> table) {
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

    private Label createRoomErrorLabel() {
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


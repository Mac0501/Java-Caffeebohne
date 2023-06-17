package com.example.baum.room;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * A custom GridPane that represents the Room pane in the application.
 * It allows adding, removing, and searching for rooms.
 */
public class RoomPane extends GridPane {

    private final RoomData roomData;
    private final TextField nameField;
    private final Label errorLabel;
    private final TableView<Room> roomTableView;

    /**
     * Constructs a RoomPane with the specified RoomData.
     *
     * @param roomData The RoomData object to be used for managing rooms.
     */
    public RoomPane(RoomData roomData) {
        this.roomData = roomData;
        this.nameField = createRoomNameField();
        this.errorLabel = createRoomErrorLabel();
        this.roomTableView = createRoomTableView();

        updateRoomTableView();
        
        initialize();
    }

    /**
     * Initializes the RoomPane by creating and configuring its components.
     */
    private void initialize() {
        TextField searchField = createRoomSearchField();
        Button addButton = createAddRoomButton();
        Button removeButton = createRemoveRoomButton();

        configureLayout(nameField, addButton, removeButton, errorLabel, searchField, roomTableView);

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

    /**
     * Updates the TableView with the latest room list.
     */
    private void updateRoomTableView() {
        roomTableView.setItems(roomData.getRoomList());
    }

    // View
    private TextField createRoomSearchField() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Rooms...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            roomTableView.setItems(roomData.searchRoomsByName(searchTerm));
        });

        return searchField;
    }

    private TextField createRoomNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Room Name");

        return nameField;
    }

    private Label createRoomErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        return errorLabel;
    }

    private TableView<Room> createRoomTableView() {
        TableView<Room> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, String> nameColumn = new TableColumn<>("Room Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().add(nameColumn);

        return table;
    }

    // Controller
    private Button createAddRoomButton() {
        Button addButton = new Button("Add Room");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                displayValidationError("Please enter a room name.");
                return;
            }

            clearValidationError();
            roomData.addRoom(nameField.getText());
            nameField.clear();
            updateRoomTableView(); // Update TableView after adding a room
        });

        return addButton;
    }

    private Button createRemoveRoomButton() {
        Button removeButton = new Button("Remove Room");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Room selected = roomTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                roomData.removeRoom(selected);
                updateRoomTableView(); // Update TableView after removing a room
            }
        });

        removeButton.getStyleClass().add("remove-button");

        return removeButton;
    }

    // Layout configuration
    private void configureLayout(TextField nameField, Button addButton,
                                 Button removeButton, Label errorLabel,
                                 TextField searchField, TableView<Room> table) {
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        this.getColumnConstraints().addAll(col1, col2);

        this.add(nameField, 0, 0, 2, 1);
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

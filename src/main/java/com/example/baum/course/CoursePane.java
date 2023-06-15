package com.example.baum.course;

import com.example.baum.room.Room;
import com.example.baum.room.RoomData;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * The CoursePane class manages the appearance and functionality of the course pane.
 * It provides a user interface for adding and removing courses, as well as searching for courses.
 */
public class CoursePane extends GridPane {
    private CourseData courseData;
    private RoomData roomData;

    /**
     * Constructs a CoursePane object with the specified CourseData and RoomData.
     *
     * @param courseData The CourseData object containing the course information.
     * @param roomData   The RoomData object containing the room information.
     */
    public CoursePane(CourseData courseData, RoomData roomData) {
        this.courseData = courseData;
        this.roomData = roomData;
        initialize();
    }

    /**
     * Initializes the course pane by creating and adding the necessary UI components.
     */
    private void initialize() {
        TableView<Course> table = createCourseTableView();
        TextField searchField = createCourseSearchField(table);
        TextField nameField = createCourseNameField();
        Label errorLabel = createErrorLabel();
        ComboBox<Room> roomComboBox = createRoomComboBox();
        Button addButton = createAddCourseButton(nameField, roomComboBox, errorLabel);
        Button removeButton = createRemoveCourseButton(table);

        GridPane gridPane = createCourseGridPane(nameField, roomComboBox, addButton, removeButton,
                errorLabel, searchField, table);

        this.getChildren().add(gridPane);
    }

    /**
     * Creates and configures the TableView for displaying the list of courses.
     *
     * @return The configured TableView object.
     */
    private TableView<Course> createCourseTableView() {
        TableView<Course> table = new TableView<>();
        table.setItems(courseData.getCourseList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> nameColumn = new TableColumn<>("Course Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> roomColumn = new TableColumn<>("Room");
        roomColumn.setCellValueFactory(cellData -> cellData.getValue().getRoom().nameProperty());

        table.getColumns().add(nameColumn);
        table.getColumns().add(roomColumn);

        return table;
    }

    /**
     * Creates and configures the search field for searching courses.
     *
     * @param table The TableView object to update with the search results.
     * @return The configured TextField object.
     */
    private TextField createCourseSearchField(TableView<Course> table) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Courses...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(courseData.searchCoursesByName(searchTerm));
        });

        return searchField;
    }

    /**
     * Creates and configures the text field for entering the course name.
     *
     * @return The configured TextField object.
     */
    private TextField createCourseNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");

        return nameField;
    }

    /**
     * Creates and configures the combo box for selecting the room.
     *
     * @return The configured ComboBox object.
     */
    private ComboBox<Room> createRoomComboBox() {
        ComboBox<Room> roomComboBox = new ComboBox<>(roomData.getRoomList());
        roomComboBox.setPromptText("Select Room");

        roomComboBox.setCellFactory(param -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    setText(room.getName());
                }
            }
        });
        roomComboBox.setMaxWidth(Double.MAX_VALUE);

        roomComboBox.setButtonCell(new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    setText(room.getName());
                }
            }
        });

        return roomComboBox;
    }

    /**
     * Creates and configures the button for adding a course.
     *
     * @param nameField    The TextField for entering the course name.
     * @param roomComboBox The ComboBox for selecting the room.
     * @param errorLabel   The Label for displaying validation errors.
     * @return The configured Button object.
     */
    private Button createAddCourseButton(TextField nameField, ComboBox<Room> roomComboBox, Label errorLabel) {
        Button addButton = new Button("Add Course");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            Room selectedRoom = roomComboBox.getSelectionModel().getSelectedItem();

            // Validate input
            if (nameField.getText().isEmpty()) {
                displayValidationError(errorLabel, "Please enter a course name.");
                return;
            }

            if (selectedRoom == null) {
                displayValidationError(errorLabel, "Please select a room.");
                return;
            }

            clearValidationError(errorLabel); // Clear any existing error messages

            courseData.addCourse(nameField.getText(), selectedRoom.getId());
            nameField.clear();
            roomComboBox.getSelectionModel().clearSelection();
        });

        return addButton;
    }

    /**
     * Creates and configures the button for removing a course.
     *
     * @param table The TableView containing the list of courses.
     * @return The configured Button object.
     */
    private Button createRemoveCourseButton(TableView<Course> table) {
        Button removeButton = new Button("Remove Course");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Course selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                courseData.removeCourse(selected);
            }
        });

        removeButton.getStyleClass().add("remove-button");

        return removeButton;
    }

    /**
     * Creates and configures the main GridPane for the course pane.
     *
     * @param nameField      The TextField for entering the course name.
     * @param roomComboBox   The ComboBox for selecting the room.
     * @param addButton      The Button for adding a course.
     * @param removeButton   The Button for removing a course.
     * @param errorLabel     The Label for displaying validation errors.
     * @param searchField    The TextField for searching courses.
     * @param table          The TableView containing the list of courses.
     * @return The configured GridPane object.
     */
    private GridPane createCourseGridPane(TextField nameField, ComboBox<Room> roomComboBox, Button addButton,
                                          Button removeButton, Label errorLabel, TextField searchField, TableView<Course> table) {
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
        gridPane.add(roomComboBox, 1, 0);
        gridPane.add(addButton, 0, 1, 1, 1);
        gridPane.add(removeButton, 1, 1, 1, 1);
        gridPane.add(errorLabel, 0, 2, 2, 1); // Add the error label
        gridPane.add(searchField, 0, 3, 2, 1);
        gridPane.add(table, 0, 4, 2, 1);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(roomComboBox, Priority.ALWAYS);
        GridPane.setHgrow(addButton, Priority.ALWAYS);
        GridPane.setHgrow(removeButton, Priority.ALWAYS);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        return gridPane;
    }

    /**
     * Creates and configures the label for displaying validation errors.
     *
     * @return The configured Label object.
     */
    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        return errorLabel;
    }

    /**
     * Displays a validation error message in the error label.
     *
     * @param errorLabel The Label for displaying validation errors.
     * @param message    The error message to display.
     */
    private void displayValidationError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(true);
    }

    /**
     * Clears the validation error message from the error label.
     *
     * @param errorLabel The Label for displaying validation errors.
     */
    private void clearValidationError(Label errorLabel) {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.getStyleClass().remove("error-label");
    }
}

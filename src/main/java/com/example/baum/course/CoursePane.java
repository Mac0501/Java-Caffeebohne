package com.example.baum.course;

import com.example.baum.room.Room;
import com.example.baum.room.RoomData;
import com.example.baum.student.Student;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class CoursePane extends GridPane {
    private CourseData courseData;
    private RoomData roomData;
    private final TableView<Course> courseTableView;
    private final TableView<Student> studentTableView;

    public CoursePane(CourseData courseData, RoomData roomData) {
        this.courseData = courseData;
        this.roomData = roomData;
        this.courseTableView = createCourseTableView();
        this.studentTableView = createStudentTableView();
        initialize();
    }

    private void initialize() {
        TextField searchField = createCourseSearchField(courseTableView);
        TextField nameField = createCourseNameField();
        Label errorLabel = createErrorLabel();
        ComboBox<Room> roomComboBox = createRoomComboBox();
        Button addButton = createAddCourseButton(nameField, roomComboBox, errorLabel);
        Button editButton = createEditCourseButton(courseTableView);
        Button removeButton = createRemoveCourseButton(courseTableView);

        configureLayout(nameField, roomComboBox, addButton, editButton, removeButton,
                errorLabel, searchField, courseTableView, studentTableView);
    }

    /**
     * Updates the TableView with the latest course list.
     */
    private void updateCourseTableView() {
        courseTableView.setItems(courseData.getCourseList());
    }

    /**
     * Updates the TableView with the students in the selected course.
     */
    private void updateStudentTableView(Course course) {
        if (course != null) {
            studentTableView.setItems(courseData.getCourseStudentList(course));
        } else {
            studentTableView.setItems(FXCollections.observableArrayList());
        }
    }

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

        // Add listener to update the student table when a course is selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateStudentTableView(newSelection);
        });

        return table;
    }

    private TableView<Student> createStudentTableView() {
        TableView<Student> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> surnameColumn = new TableColumn<>("Surname");
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Student, Integer> javaSkillsColumn = new TableColumn<>("Java Skills");
        javaSkillsColumn.setCellValueFactory(new PropertyValueFactory<>("javaSkills"));
        javaSkillsColumn.setCellFactory(column -> new TableCell<Student, Integer>() {
            private final ProgressBar progressBar = new ProgressBar();

            {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(progressBar);
                progressBar.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(Integer javaSkills, boolean empty) {
                super.updateItem(javaSkills, empty);
                if (empty || javaSkills == null) {
                    progressBar.setProgress(0);
                    setGraphic(null);
                } else {
                    progressBar.setProgress(javaSkills.doubleValue() / 100);
                    setGraphic(progressBar);
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                progressBar.prefWidthProperty().bind(widthProperty());
            }
        });

        TableColumn<Student, String> companyNameColumn = new TableColumn<>("Company");
        companyNameColumn.setCellValueFactory(cellData -> cellData.getValue().getCompany().nameProperty());

        table.getColumns().addAll(nameColumn, surnameColumn, javaSkillsColumn, companyNameColumn);

        return table;
    }


    private TextField createCourseSearchField(TableView<Course> table) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Courses...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(courseData.searchCoursesByName(searchTerm));
        });

        return searchField;
    }

    private TextField createCourseNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");

        return nameField;
    }

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

    private Button createAddCourseButton(TextField nameField, ComboBox<Room> roomComboBox, Label errorLabel) {
        Button addButton = new Button("Add Course");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            Room selectedRoom = roomComboBox.getSelectionModel().getSelectedItem();

            if (nameField.getText().isEmpty()) {
                displayValidationError(errorLabel, "Please enter a course name.");
                return;
            }

            if (selectedRoom == null) {
                displayValidationError(errorLabel, "Please select a room.");
                return;
            }

            clearValidationError(errorLabel);

            courseData.addCourse(nameField.getText(), selectedRoom.getId());
            nameField.clear();
            roomComboBox.getSelectionModel().clearSelection();
            updateCourseTableView();
        });

        return addButton;
    }

    private Button createEditCourseButton(TableView<Course> table) {
    Button editButton = new Button("Edit Course");
    editButton.setMaxWidth(Double.MAX_VALUE);
    editButton.setDisable(true); // Disable the button initially
    editButton.getStyleClass().add("edit-button");

    // Add a listener to enable/disable the button based on selection
    table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            editButton.setDisable(false); // Enable the button when a course is selected
        } else {
            editButton.setDisable(true); // Disable the button when no course is selected
        }
    });

    editButton.setOnAction(e -> {
        Course selectedCourse = table.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            showEditCourseDialog(selectedCourse);
            updateCourseTableView();
        }
    });

    return editButton;
}

    private void showEditCourseDialog(Course course) {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Edit Course");
        dialog.setHeaderText("Edit Course Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(course.getName());
        ComboBox<Room> roomComboBox = new ComboBox<>(roomData.getRoomList());
        roomComboBox.getSelectionModel().select(course.getRoom());

        gridPane.add(new Label("Course Name:"), 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(new Label("Room:"), 0, 1);
        gridPane.add(roomComboBox, 1, 1);

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Course(course.getId(), nameField.getText(), roomComboBox.getSelectionModel().getSelectedItem());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(editedCourse -> {
            courseData.updateCourse(editedCourse);
            updateCourseTableView();
        });
    }

    private Button createRemoveCourseButton(TableView<Course> table) {
        Button removeButton = new Button("Remove Course");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(e -> {
            Course selectedCourse = table.getSelectionModel().getSelectedItem();
            if (selectedCourse != null) {
                courseData.removeCourse(selectedCourse);
                updateCourseTableView();
            }
        });

        return removeButton;
    }

    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(errorLabel, Priority.ALWAYS);
        GridPane.setColumnSpan(errorLabel, 2);

        return errorLabel;
    }

    private void configureLayout(TextField nameField, ComboBox<Room> roomComboBox, Button addButton,
                                 Button editButton, Button removeButton, Label errorLabel, TextField searchField,
                                 TableView<Course> courseTable, TableView<Student> studentTable) {
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        this.getColumnConstraints().addAll(col1, col2);

        // Create a new GridPane to hold the tables
        GridPane tableGrid = new GridPane();
        tableGrid.setHgap(10);
        tableGrid.setVgap(10);

        // Add header label for the course table
        Label courseTableHeader = new Label("Courses");
        courseTableHeader.getStyleClass().add("table-header-label");
        tableGrid.add(courseTableHeader, 0, 0);

        // Add header label for the student table
        Label studentTableHeader = new Label("Students of Course");
        studentTableHeader.getStyleClass().add("table-header-label");
        tableGrid.add(studentTableHeader, 1, 0);

        tableGrid.add(courseTable, 0, 1);
        tableGrid.add(studentTable, 1, 1);

        GridPane.setHgrow(courseTable, Priority.ALWAYS);
        GridPane.setHgrow(studentTable, Priority.ALWAYS);
        GridPane.setVgrow(studentTable, Priority.ALWAYS);

        this.add(nameField, 0, 0);
        this.add(roomComboBox, 1, 0);

        // Create an HBox to hold the buttons
        HBox buttonBox = new HBox(10); // Spacing between buttons

        // Set the width of the buttonBox to fill 100% of the available space
        buttonBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(buttonBox, Priority.ALWAYS);

        // Add buttons to the buttonBox
        buttonBox.getChildren().addAll(addButton, editButton, removeButton);

        // Make buttons take all the available horizontal space
        addButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(editButton, Priority.ALWAYS);
        HBox.setHgrow(removeButton, Priority.ALWAYS);

        this.add(buttonBox, 0, 1, 2, 1);
        this.add(errorLabel, 0, 2, 2, 1);
        this.add(searchField, 0, 3, 2, 1);
        this.add(tableGrid, 0, 4, 2, 1);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(roomComboBox, Priority.ALWAYS);
        GridPane.setHgrow(tableGrid, Priority.ALWAYS);
        GridPane.setVgrow(tableGrid, Priority.ALWAYS);
    }






    private void displayValidationError(Label errorLabel, String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

    private void clearValidationError(Label errorLabel) {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}

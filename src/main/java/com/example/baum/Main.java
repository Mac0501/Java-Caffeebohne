package com.example.baum;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.geometry.Pos;


import java.util.Objects;

public class Main extends Application {

    private DatabaseManager databaseManager;
    private StudentData studentData;
    private CourseData courseData;
    private CompanyData companyData;
    private RoomData roomData;

    @Override
    public void start(Stage primaryStage) {
        databaseManager = new DatabaseManager();
        databaseManager.connect();
        databaseManager.createTablesIfNotExists();

        roomData = new RoomData(databaseManager);
        courseData = new CourseData(databaseManager, roomData);
        companyData = new CompanyData(databaseManager);
        studentData = new StudentData(databaseManager, courseData, companyData);

        roomData.fetchRoomsFromDatabase();
        courseData.fetchCoursesFromDatabase();
        companyData.fetchCompaniesFromDatabase();
        studentData.fetchStudentsFromDatabase();

        primaryStage.setTitle("Student Manager");

        TabPane tabPane = new TabPane();

        Tab studentTab = new Tab("Students", createStudentPane());
        Tab courseTab = new Tab("Courses", createCoursePane());
        Tab companyTab = new Tab("Company", createCompanyPane());
        Tab roomTab = new Tab("Room", createRoomPane());

        tabPane.getTabs().add(studentTab);
        tabPane.getTabs().add(courseTab);
        tabPane.getTabs().add(companyTab);
        tabPane.getTabs().add(roomTab);

        studentTab.setClosable(false);
        courseTab.setClosable(false);
        companyTab.setClosable(false);
        roomTab.setClosable(false);

        primaryStage.setScene(new Scene(tabPane, 800, 600));
        primaryStage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/baum/style.css")).toExternalForm());

        primaryStage.show();

    }

    private GridPane createStudentPane() {
        TableView<Student> table = new TableView<>();
        table.setItems(studentData.getStudentList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> surnameColumn = new TableColumn<>("Surname");
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Student, Integer> javaSkillsColumn = new TableColumn<>("Java Skills");
        javaSkillsColumn.setCellValueFactory(new PropertyValueFactory<>("javaskills"));
        javaSkillsColumn.setCellFactory(column -> new TableCell<Student, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    ProgressBar progressBar = new ProgressBar(item / 100.0);
                    progressBar.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(progressBar, Priority.ALWAYS);
                    HBox hBox = new HBox(progressBar);
                    setGraphic(hBox);
                }
            }
        });

        TableColumn<Student, Course> courseColumn = new TableColumn<>("Course");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Student, Company> companyColumn = new TableColumn<>("Company");
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));

        table.getColumns().addAll(nameColumn, surnameColumn, courseColumn, companyColumn,javaSkillsColumn);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Students...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(studentData.searchStudentsByName(searchTerm));
        });

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");

        Label errorLabel = new Label(); // Error label for validation errors
        errorLabel.getStyleClass().add("error-label");

        Slider javaSkillsSlider = new Slider(0, 100, 0);
        javaSkillsSlider.setShowTickLabels(true);
        javaSkillsSlider.setShowTickMarks(true);
        javaSkillsSlider.setMajorTickUnit(100);
        javaSkillsSlider.setMinorTickCount(0);
        javaSkillsSlider.setSnapToTicks(false);
        javaSkillsSlider.setMinWidth(200);
        javaSkillsSlider.setMaxWidth(Double.MAX_VALUE);

        Label skillsLabel = new Label("Student's Java Skills:");
        skillsLabel.setMinWidth(Label.USE_PREF_SIZE);

        HBox skillsBox = new HBox(10);
        skillsBox.setAlignment(Pos.CENTER_LEFT);
        skillsBox.getChildren().addAll(skillsLabel, javaSkillsSlider);
        HBox.setHgrow(javaSkillsSlider, Priority.ALWAYS);

        ComboBox<Course> courseComboBox = new ComboBox<>(courseData.getCourseList());
        courseComboBox.setPromptText("Select Course");
        courseComboBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Company> companyComboBox = new ComboBox<>(companyData.getCompanyList());
        companyComboBox.setPromptText("Select Company");
        companyComboBox.setMaxWidth(Double.MAX_VALUE);

        Button addButton = new Button("Add Student");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
            Company selectedCompany = companyComboBox.getSelectionModel().getSelectedItem();

            // Validate input
            if (nameField.getText().isEmpty()) {
                displayValidationError(errorLabel, "Please enter a name.");
                return;
            }

            if (surnameField.getText().isEmpty()) {
                displayValidationError(errorLabel, "Please enter a surname.");
                return;
            }

            if (selectedCourse == null) {
                displayValidationError(errorLabel, "Please select a course.");
                return;
            }

            if (selectedCompany == null) {
                displayValidationError(errorLabel, "Please select a company.");
                return;
            }

            clearValidationError(errorLabel); // Clear any existing error messages

            studentData.addStudent(nameField.getText(), surnameField.getText(),
                    (int) javaSkillsSlider.getValue(), selectedCourse.getId(), selectedCompany.getId());
            nameField.clear();
            surnameField.clear();
            javaSkillsSlider.setValue(0);
        });

        Button removeButton = new Button("Remove Student");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Student selected = table.getSelectionModel().getSelectedItem();
            studentData.removeStudent(selected);
        });

        removeButton.getStyleClass().add("remove-button");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        gridPane.getColumnConstraints().addAll(col1, col2);

        gridPane.add(nameField, 0, 1);
        gridPane.add(surnameField, 1, 1);
        gridPane.add(courseComboBox, 0, 2, 1, 1);
        gridPane.add(companyComboBox, 1, 2, 1, 1);
        gridPane.add(skillsBox, 0, 3, 2, 1);
        gridPane.add(addButton, 0, 4, 1, 1);
        gridPane.add(removeButton, 1, 4, 1, 1);
        gridPane.add(errorLabel, 0, 5, 2, 1); // Add the error label
        gridPane.add(searchField, 0, 6, 2, 1);
        gridPane.add(table, 0, 7, 2, 1);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(surnameField, Priority.ALWAYS);
        GridPane.setHgrow(courseComboBox, Priority.ALWAYS);
        GridPane.setHgrow(skillsBox, Priority.ALWAYS);
        GridPane.setHgrow(companyComboBox, Priority.ALWAYS);
        GridPane.setHgrow(addButton, Priority.ALWAYS);
        GridPane.setHgrow(removeButton, Priority.ALWAYS);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        return gridPane;
    }















    private GridPane createCoursePane() {
        TableView<Course> table = new TableView<>();
        table.setItems(courseData.getCourseList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> nameColumn = new TableColumn<>("Course Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> roomColumn = new TableColumn<>("Room");
        roomColumn.setCellValueFactory(cellData -> cellData.getValue().getRoom().nameProperty());

        table.getColumns().add(nameColumn);
        table.getColumns().add(roomColumn);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Courses...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(courseData.searchCoursesByName(searchTerm));
        });

        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");

        Label errorLabel = new Label(); // Error label for validation errors
        errorLabel.getStyleClass().add("error-label");

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

        Button removeButton = new Button("Remove Course");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Course selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                courseData.removeCourse(selected);
            }
        });

        removeButton.getStyleClass().add("remove-button");

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
    // Helper method to display validation error message




    private GridPane createCompanyPane() {
        TableView<Company> table = new TableView<>();
        table.setItems(companyData.getCompanyList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Company, String> nameColumn = new TableColumn<>("Company Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().add(nameColumn);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Companies...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(companyData.searchCompaniesByName(searchTerm));
        });

        TextField nameField = new TextField();
        nameField.setPromptText("Company Name");

        Label errorLabel = new Label(); // Error label for validation errors
        errorLabel.getStyleClass().add("error-label");

        Button addButton = new Button("Add Company");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            // Validate input
            if (nameField.getText().isEmpty()) {
                displayValidationError(errorLabel, "Please enter a company name.");
                return;
            }

            clearValidationError(errorLabel); // Clear any existing error messages

            companyData.addCompany(nameField.getText());
            nameField.clear();
        });

        Button removeButton = new Button("Remove Company");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Company selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                companyData.removeCompany(selected);
            }
        });

        removeButton.getStyleClass().add("remove-button");

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
        gridPane.add(errorLabel, 0, 2, 2, 1); // Add the error label
        gridPane.add(searchField, 0, 3, 2, 1);
        gridPane.add(table, 0, 4, 2, 1);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(addButton, Priority.ALWAYS);
        GridPane.setHgrow(removeButton, Priority.ALWAYS);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        return gridPane;
    }




    private GridPane createRoomPane() {
        TableView<Room> table = new TableView<>();
        table.setItems(roomData.getRoomList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, String> nameColumn = new TableColumn<>("Room Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().add(nameColumn);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Rooms...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(roomData.searchRoomsByName(searchTerm));
        });

        TextField nameField = new TextField();
        nameField.setPromptText("Room Name");

        Label errorLabel = new Label(); // Error label for validation errors
        errorLabel.getStyleClass().add("error-label");

        Button addButton = new Button("Add Room");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            // Validate input
            if (nameField.getText().isEmpty()) {
                displayValidationError(errorLabel, "Please enter a room name.");
                return;
            }

            clearValidationError(errorLabel); // Clear any existing error messages

            roomData.addRoom(nameField.getText());
            nameField.clear();
        });

        Button removeButton = new Button("Remove Room");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Room selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                roomData.removeRoom(selected);
            }
        });

        removeButton.getStyleClass().add("remove-button");

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
        gridPane.add(errorLabel, 0, 2, 2, 1); // Add the error label
        gridPane.add(searchField, 0, 3, 2, 1);
        gridPane.add(table, 0, 4, 2, 1);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(addButton, Priority.ALWAYS);
        GridPane.setHgrow(removeButton, Priority.ALWAYS);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        return gridPane;
    }

    // Helper method to display validation error message
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

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply a custom style to the dialog

        alert.showAndWait();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

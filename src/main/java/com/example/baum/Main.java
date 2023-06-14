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

/**
 * The main class for the Student Manager application.
 */
public class Main extends Application {

    private DatabaseManager databaseManager;
    private StudentData studentData;
    private CourseData courseData;
    private CompanyData companyData;
    private RoomData roomData;

    /**
     * Starts the Student Manager application.
     *
     * @param primaryStage The primary stage for the application.
     */
    @Override
    public void start(Stage primaryStage) {
        initializeDatabaseAndData();

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
        primaryStage.getScene().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/com/example/baum/style.css")).toExternalForm());

        primaryStage.show();
    }

    /**
     * Initializes the database and data objects.
     */
    private void initializeDatabaseAndData() {
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
    }

    /**
     * Creates the pane for the Students tab.
     *
     * @return The pane for the Students tab.
     */
    private GridPane createStudentPane() {
        TableView<Student> table = createStudentTable();
        TextField searchField = createSearchField(table);
        TextField nameField = createNameField();
        TextField surnameField = createSurnameField();
        Label errorLabel = createErrorLabel();
        Slider javaSkillsSlider = createJavaSkillsSlider();
        ComboBox<Course> courseComboBox = createCourseComboBox();
        ComboBox<Company> companyComboBox = createCompanyComboBox();
        Button addButton = createAddButton(nameField, surnameField, javaSkillsSlider, courseComboBox, companyComboBox,
                errorLabel);
        Button removeButton = createRemoveButton(table);
        GridPane gridPane = createGridPane();
        HBox skillsBox = createSkillsBox(javaSkillsSlider);

        gridPane.add(nameField, 0, 1);
        gridPane.add(surnameField, 1, 1);
        gridPane.add(courseComboBox, 0, 2, 1, 1);
        gridPane.add(companyComboBox, 1, 2, 1, 1);
        gridPane.add(skillsBox, 0, 3, 2, 1);
        gridPane.add(addButton, 0, 4, 1, 1);
        gridPane.add(removeButton, 1, 4, 1, 1);
        gridPane.add(errorLabel, 0, 5, 2, 1);
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

    /**
     * Creates the TableView for displaying students.
     *
     * @return The TableView for students.
     */
    private TableView<Student> createStudentTable() {
        TableView<Student> table = new TableView<>();
        table.setItems(studentData.getStudentList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> surnameColumn = new TableColumn<>("Surname");
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Student, Integer> javaSkillsColumn = createJavaSkillsColumn();
        TableColumn<Student, Course> courseColumn = createCourseColumn();
        TableColumn<Student, Company> companyColumn = createCompanyColumn();

        table.getColumns().addAll(nameColumn, surnameColumn, courseColumn, companyColumn, javaSkillsColumn);

        return table;
    }

    /**
     * Creates the TableColumn for Java Skills.
     *
     * @return The TableColumn for Java Skills.
     */
    private TableColumn<Student, Integer> createJavaSkillsColumn() {
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

        return javaSkillsColumn;
    }

    /**
     * Creates the TableColumn for Course.
     *
     * @return The TableColumn for Course.
     */
    private TableColumn<Student, Course> createCourseColumn() {
        TableColumn<Student, Course> courseColumn = new TableColumn<>("Course");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        return courseColumn;
    }

    /**
     * Creates the TableColumn for Company.
     *
     * @return The TableColumn for Company.
     */
    private TableColumn<Student, Company> createCompanyColumn() {
        TableColumn<Student, Company> companyColumn = new TableColumn<>("Company");
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));

        return companyColumn;
    }

    /**
     * Creates the TextField for searching students.
     *
     * @param table The TableView of students.
     * @return The TextField for searching students.
     */
    private TextField createSearchField(TableView<Student> table) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Students...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(studentData.searchStudentsByName(searchTerm));
        });

        return searchField;
    }

    /**
     * Creates the TextField for entering the student's name.
     *
     * @return The TextField for the student's name.
     */
    private TextField createNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        return nameField;
    }

    /**
     * Creates the TextField for entering the student's surname.
     *
     * @return The TextField for the student's surname.
     */
    private TextField createSurnameField() {
        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");

        return surnameField;
    }

    /**
     * Creates the Label for displaying validation errors.
     *
     * @return The Label for validation errors.
     */
    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        return errorLabel;
    }

    /**
     * Creates the Slider for selecting the student's Java skills.
     *
     * @return The Slider for Java skills.
     */
    private Slider createJavaSkillsSlider() {
        Slider javaSkillsSlider = new Slider(0, 100, 0);
        javaSkillsSlider.setShowTickLabels(true);
        javaSkillsSlider.setShowTickMarks(true);
        javaSkillsSlider.setMajorTickUnit(100);
        javaSkillsSlider.setMinorTickCount(0);
        javaSkillsSlider.setSnapToTicks(false);
        javaSkillsSlider.setMinWidth(200);
        javaSkillsSlider.setMaxWidth(Double.MAX_VALUE);

        return javaSkillsSlider;
    }

    /**
     * Creates the HBox container for the Java skills Slider and label.
     *
     * @param javaSkillsSlider The Slider for Java skills.
     * @return The HBox container for Java skills.
     */
    private HBox createSkillsBox(Slider javaSkillsSlider) {
        Label skillsLabel = new Label("Student's Java Skills:");
        skillsLabel.setMinWidth(Label.USE_PREF_SIZE);

        HBox skillsBox = new HBox(10);
        skillsBox.setAlignment(Pos.CENTER_LEFT);
        skillsBox.getChildren().addAll(skillsLabel, javaSkillsSlider);
        HBox.setHgrow(javaSkillsSlider, Priority.ALWAYS);

        return skillsBox;
    }

    /**
     * Creates the ComboBox for selecting the student's course.
     *
     * @return The ComboBox for selecting the course.
     */
    private ComboBox<Course> createCourseComboBox() {
        ComboBox<Course> courseComboBox = new ComboBox<>(courseData.getCourseList());
        courseComboBox.setPromptText("Select Course");
        courseComboBox.setMaxWidth(Double.MAX_VALUE);

        return courseComboBox;
    }

    /**
     * Creates the ComboBox for selecting the student's company.
     *
     * @return The ComboBox for selecting the company.
     */
    private ComboBox<Company> createCompanyComboBox() {
        ComboBox<Company> companyComboBox = new ComboBox<>(companyData.getCompanyList());
        companyComboBox.setPromptText("Select Company");
        companyComboBox.setMaxWidth(Double.MAX_VALUE);

        return companyComboBox;
    }

    /**
     * Creates the Button for adding a student.
     *
     * @param nameField        The TextField for the student's name.
     * @param surnameField     The TextField for the student's surname.
     * @param javaSkillsSlider The Slider for Java skills.
     * @param courseComboBox   The ComboBox for selecting the course.
     * @param companyComboBox  The ComboBox for selecting the company.
     * @param errorLabel       The Label for validation errors.
     * @return The Button for adding a student.
     */
    private Button createAddButton(TextField nameField, TextField surnameField, Slider javaSkillsSlider,
            ComboBox<Course> courseComboBox, ComboBox<Company> companyComboBox, Label errorLabel) {
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

        return addButton;
    }

    /**
     * Creates the Button for removing a student.
     *
     * @param table The TableView of students.
     * @return The Button for removing a student.
     */
    private Button createRemoveButton(TableView<Student> table) {
        Button removeButton = new Button("Remove Student");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            Student selected = table.getSelectionModel().getSelectedItem();
            studentData.removeStudent(selected);
        });

        removeButton.getStyleClass().add("remove-button");

        return removeButton;
    }

    /**
     * Creates the GridPane container for the student pane.
     *
     * @return The GridPane container for the student pane.
     */
    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        gridPane.getColumnConstraints().addAll(col1, col2);

        return gridPane;
    }

    /**
     * Creates the TableView for displaying the list of courses.
     *
     * @return The TableView for displaying the list of courses.
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
     * Creates the TextField for searching courses.
     *
     * @return The TextField for searching courses.
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
     * Creates the TextField for entering the course name.
     *
     * @return The TextField for entering the course name.
     */
    private TextField createCourseNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");

        return nameField;
    }

    /**
     * Creates the ComboBox for selecting the room.
     *
     * @return The ComboBox for selecting the room.
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
     * Creates the Button for adding a course.
     *
     * @param nameField    The TextField for the course name.
     * @param roomComboBox The ComboBox for selecting the room.
     * @param errorLabel   The Label for validation errors.
     * @return The Button for adding a course.
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
     * Creates the Button for removing a course.
     *
     * @param table The TableView for displaying the list of courses.
     * @return The Button for removing a course.
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
     * Creates and configures the GridPane for the course pane.
     *
     * @param nameField    The TextField for the course name.
     * @param roomComboBox The ComboBox for selecting the room.
     * @param addButton    The Button for adding a course.
     * @param removeButton The Button for removing a course.
     * @param errorLabel   The Label for validation errors.
     * @param searchField  The TextField for searching courses.
     * @param table        The TableView for displaying the list of courses.
     * @return The configured GridPane for the course pane.
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
     * Creates the pane for managing courses.
     *
     * @return The pane for managing courses.
     */
    private GridPane createCoursePane() {
        TableView<Course> table = createCourseTableView();
        TextField searchField = createCourseSearchField(table);
        TextField nameField = createCourseNameField();
        Label errorLabel = createErrorLabel();
        ComboBox<Room> roomComboBox = createRoomComboBox();
        Button addButton = createAddCourseButton(nameField, roomComboBox, errorLabel);
        Button removeButton = createRemoveCourseButton(table);

        GridPane gridPane = createCourseGridPane(nameField, roomComboBox, addButton, removeButton,
                errorLabel, searchField, table);

        return gridPane;
    }

    /**
     * Creates the TableView for displaying the list of companies.
     *
     * @return The TableView for displaying the list of companies.
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
     * Creates the TextField for searching companies.
     *
     * @return The TextField for searching companies.
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
     * Creates the TextField for entering the company name.
     *
     * @return The TextField for entering the company name.
     */
    private TextField createCompanyNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Company Name");

        return nameField;
    }

    /**
     * Creates the Button for adding a company.
     *
     * @param nameField  The TextField for the company name.
     * @param errorLabel The Label for validation errors.
     * @return The Button for adding a company.
     */
    private Button createAddCompanyButton(TextField nameField, Label errorLabel) {
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

        return addButton;
    }

    /**
     * Creates the Button for removing a company.
     *
     * @param table The TableView for displaying the list of companies.
     * @return The Button for removing a company.
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
     * Creates and configures the GridPane for the company pane.
     *
     * @param nameField    The TextField for the company name.
     * @param addButton    The Button for adding a company.
     * @param removeButton The Button for removing a company.
     * @param errorLabel   The Label for validation errors.
     * @param searchField  The TextField for searching companies.
     * @param table        The TableView for displaying the list of companies.
     * @return The configured GridPane for the company pane.
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

    /**
     * Creates the pane for managing companies.
     *
     * @return The pane for managing companies.
     */
    private GridPane createCompanyPane() {
        TableView<Company> table = createCompanyTableView();
        TextField searchField = createCompanySearchField(table);
        TextField nameField = createCompanyNameField();
        Label errorLabel = createErrorLabel();
        Button addButton = createAddCompanyButton(nameField, errorLabel);
        Button removeButton = createRemoveCompanyButton(table);

        GridPane gridPane = createCompanyGridPane(nameField, addButton, removeButton,
                errorLabel, searchField, table);

        return gridPane;
    }

    /**
     * Creates the TableView for displaying the list of rooms.
     *
     * @return The TableView for displaying the list of rooms.
     */
    private TableView<Room> createRoomTableView() {
        TableView<Room> table = new TableView<>();
        table.setItems(roomData.getRoomList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, String> nameColumn = new TableColumn<>("Room Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().add(nameColumn);

        return table;
    }

    /**
     * Creates the TextField for searching rooms.
     *
     * @return The TextField for searching rooms.
     */
    private TextField createRoomSearchField(TableView<Room> table) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Rooms...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(roomData.searchRoomsByName(searchTerm));
        });

        return searchField;
    }

    /**
     * Creates the TextField for entering the room name.
     *
     * @return The TextField for entering the room name.
     */
    private TextField createRoomNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Room Name");

        return nameField;
    }

    /**
     * Creates the Label for displaying validation errors.
     *
     * @return The Label for displaying validation errors.
     */
    private Label createRoomErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        return errorLabel;
    }

    /**
     * Creates the Button for adding a room.
     *
     * @param nameField  The TextField for the room name.
     * @param errorLabel The Label for validation errors.
     * @return The Button for adding a room.
     */
    private Button createAddRoomButton(TextField nameField, Label errorLabel) {
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

        return addButton;
    }

    /**
     * Creates the Button for removing a room.
     *
     * @param table The TableView for displaying the list of rooms.
     * @return The Button for removing a room.
     */
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

    /**
     * Creates and configures the GridPane for the room pane.
     *
     * @param nameField    The TextField for the room name.
     * @param addButton    The Button for adding a room.
     * @param removeButton The Button for removing a room.
     * @param errorLabel   The Label for validation errors.
     * @param searchField  The TextField for searching rooms.
     * @param table        The TableView for displaying the list of rooms.
     * @return The configured GridPane for the room pane.
     */
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

    /**
     * Creates the pane for managing rooms.
     *
     * @return The pane for managing rooms.
     */
    private GridPane createRoomPane() {
        TableView<Room> table = createRoomTableView();
        TextField searchField = createRoomSearchField(table);
        TextField nameField = createRoomNameField();
        Label errorLabel = createRoomErrorLabel();
        Button addButton = createAddRoomButton(nameField, errorLabel);
        Button removeButton = createRemoveRoomButton(table);

        GridPane gridPane = createRoomGridPane(nameField, addButton, removeButton,
                errorLabel, searchField, table);

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

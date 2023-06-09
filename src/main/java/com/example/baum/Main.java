package com.example.baum;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
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
        Tab courseTab = new Tab("Courses",createCoursePane());
        Tab companyTab = new Tab();
        Tab roomTab = new Tab();

        tabPane.getTabs().add(studentTab);
        tabPane.getTabs().add(courseTab);
        tabPane.getTabs().add(companyTab);
        tabPane.getTabs().add(roomTab);


        primaryStage.setScene(new Scene(tabPane, 800, 600));
        primaryStage.show();
    }

    private Pane createStudentPane() {
        TableView<Student> table = new TableView<>();
        table.setItems(studentData.getStudentList());

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> surnameColumn = new TableColumn<>("Nachname");
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Student, Course> courseColumn = new TableColumn<>("Course");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Student, Company> companyColumn = new TableColumn<>("Company");
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));


        table.getColumns().add(nameColumn);
        table.getColumns().add(surnameColumn);
        table.getColumns().add(courseColumn);
        table.getColumns().add(companyColumn);

        TextField nameField = new TextField();
        TextField surnameField = new TextField();

        ComboBox<Course> courseComboBox = new ComboBox<>(courseData.getCourseList());
        courseComboBox.setPromptText("Select Course");

        ComboBox<Company> companyComboBox = new ComboBox<>(companyData.getCompanyList());
        companyComboBox.setPromptText("Select Company");

        Button addButton = new Button("Add Student");
        addButton.setOnAction(e -> {
            Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
            Company selectedCompany = companyComboBox.getSelectionModel().getSelectedItem();
            if (selectedCourse != null && selectedCompany != null) {
                studentData.addStudent(nameField.getText(), surnameField.getText(), selectedCourse.getId(), selectedCompany.getId());
                nameField.clear();
                surnameField.clear();
                courseComboBox.getSelectionModel().clearSelection();
                companyComboBox.getSelectionModel().clearSelection();
            }
        });

        Button removeButton = new Button("Remove Student");
        removeButton.setOnAction(e -> {
            Student selected = table.getSelectionModel().getSelectedItem();
            studentData.removeStudent(selected);
        });

        VBox vbox = new VBox();
        vbox.getChildren().addAll(nameField, surnameField, courseComboBox, companyComboBox, addButton, removeButton, table);

        return vbox;
    }

    private GridPane createCoursePane() {
        TableView<Course> table = new TableView<>();
        table.setItems(courseData.getCourseList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> roomColumn = new TableColumn<>("Room");
        roomColumn.setCellValueFactory(cellData -> cellData.getValue().getRoom().nameProperty());

        table.getColumns().add(nameColumn);
        table.getColumns().add(roomColumn);

        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");

        ComboBox<Room> roomComboBox = new ComboBox<>(roomData.getRoomList());
        roomComboBox.setPromptText("Select Room");

        roomComboBox.setCellFactory(param -> new ListCell<>() {
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

        roomComboBox.setButtonCell(new ListCell<>() {
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
        addButton.setOnAction(e -> {
            Room selectedRoom = roomComboBox.getSelectionModel().getSelectedItem();
            if (selectedRoom != null) {
                courseData.addCourse(nameField.getText(), selectedRoom.getId());
                nameField.clear();
                roomComboBox.getSelectionModel().clearSelection();
            }
        });

        Button removeButton = new Button("Remove Course");
        removeButton.setOnAction(e -> {
            Course selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                courseData.removeCourse(selected);
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(0, 10, 0, 10));

        HBox inputBox = new HBox(nameField, roomComboBox);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        HBox.setHgrow(roomComboBox, Priority.ALWAYS);
        inputBox.setSpacing(10);

        HBox buttonBox = new HBox(addButton, removeButton);
        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(removeButton, Priority.ALWAYS);
        buttonBox.setSpacing(10);

        gridPane.add(inputBox, 0, 0);
        gridPane.add(buttonBox, 0, 1);
        gridPane.add(table, 0, 2);
        GridPane.setVgrow(table, Priority.ALWAYS);
        GridPane.setFillWidth(table, true);

        return gridPane;
    }


    private int createCompanyPane() {
        return 1;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

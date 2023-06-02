package com.example.baum;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentManagementApp extends Application {
    private DatabaseManager databaseManager;
    private TableView<Student> studentTable;
    private StudentData studentData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        databaseManager = new DatabaseManager();
        connectToDatabase();
        createTableIfNotExists();

        // Initialize student table and data
        studentTable = new TableView<>();
        studentData = new StudentData(databaseManager);
        studentData.fetchStudentsFromDatabase();

        // Set up GUI
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // Labels and TextFields for student details
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label classLabel = new Label("Class:");
        TextField classField = new TextField();
        Label companyLabel = new Label("Company:");
        TextField companyField = new TextField();

        // Buttons for student operations
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> studentData.addStudent(nameField.getText(), classField.getText(), companyField.getText()));

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> studentData.removeStudent(studentTable.getSelectionModel().getSelectedItem()));

        // Student table setup
        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Student, String> classColumn = new TableColumn<>("Class");
        classColumn.setCellValueFactory(cellData -> cellData.getValue().classProperty());

        TableColumn<Student, String> companyColumn = new TableColumn<>("Company");
        companyColumn.setCellValueFactory(cellData -> cellData.getValue().companyProperty());

        studentTable.getColumns().addAll(nameColumn, classColumn, companyColumn);
        studentTable.setItems(studentData.getStudentList());

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(studentTable, new HBox(10, nameLabel, nameField),
                new HBox(10, classLabel, classField), new HBox(10, companyLabel, companyField),
                new HBox(10, addButton, removeButton));
        gridPane.add(vbox, 0, 0);

        Scene scene = new Scene(gridPane);
        primaryStage.setTitle("Student Management App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void connectToDatabase() {
        databaseManager.connect();
    }

    private void createTableIfNotExists() {
        databaseManager.createTablesIfNotExists();
    }
}

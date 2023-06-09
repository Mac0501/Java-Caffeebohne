package com.example.baum;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StudentManagementApp extends Application {

    private TableView<Student> tableView;
    private ObservableList<Student> students;
    private ObservableList<Student> filteredStudents;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Management");

        // Create the table view
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Student, String> nameColumn = createTableColumn("Name", "name");
        TableColumn<Student, String> surnameColumn = createTableColumn("Surname", "surname");
        TableColumn<Student, String> courseColumn = createTableColumn("Course", "course");
        TableColumn<Student, String> companyColumn = createTableColumn("Company", "company");
        TableColumn<Student, String> javaSkillsColumn = createTableColumn("Java Skills", "javaSkills");
        TableColumn<Student, String> actionsColumn = createTableColumn("Actions", "actions");
        tableView.getColumns().addAll(nameColumn, surnameColumn, courseColumn, companyColumn, javaSkillsColumn, actionsColumn);

        // Create the search fields
        TextField nameSearchField = createSearchField(nameColumn);
        TextField surnameSearchField = createSearchField(surnameColumn);
        TextField courseSearchField = createSearchField(courseColumn);
        TextField companySearchField = createSearchField(companyColumn);
        TextField javaSkillsSearchField = createSearchField(javaSkillsColumn);

        // Create the buttons
        Button addStudentButton = new Button("Add Student");
        Button openCompanyButton = new Button("Open by Company");
        Button openCourseButton = new Button("Open by Course");

        // Set button styles
        addStudentButton.setMaxWidth(Double.MAX_VALUE);
        openCompanyButton.setMaxWidth(Double.MAX_VALUE);
        openCourseButton.setMaxWidth(Double.MAX_VALUE);

        // Handle button actions
        addStudentButton.setOnAction(e -> showAddStudentDialog());
        openCompanyButton.setOnAction(e -> openFilteredWindow(companyColumn));
        openCourseButton.setOnAction(e -> openFilteredWindow(courseColumn));

        // Create the layout
        HBox buttonContainer = new HBox(10, addStudentButton, openCompanyButton, openCourseButton);
        buttonContainer.setHgrow(addStudentButton, Priority.ALWAYS);
        buttonContainer.setHgrow(openCompanyButton, Priority.ALWAYS);
        buttonContainer.setHgrow(openCourseButton, Priority.ALWAYS);
        buttonContainer.setFillHeight(true);
        buttonContainer.setPadding(new Insets(10));

        HBox searchFields = new HBox(10, nameSearchField, surnameSearchField, courseSearchField,
                companySearchField, javaSkillsSearchField);
        searchFields.setPadding(new Insets(10));

        VBox root = new VBox(buttonContainer, searchFields, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        root.setPadding(new Insets(10));

        // Create the scene and show the stage
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize sample data


        filteredStudents = FXCollections.observableArrayList();
        //filteredStudents.addAll(students);

        tableView.setItems(filteredStudents);
    }

    private TableColumn<Student, String> createTableColumn(String title, String property) {
        TableColumn<Student, String> tableColumn = new TableColumn<>(title);

        if (property.equals("actions")) {
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(""));
            tableColumn.setCellFactory(column -> new TableCell<Student, String>() {
                private final Button deleteButton = new Button("DEL");
                private final Button editButton = new Button("EDIT");

                {
                    deleteButton.setOnAction(event -> {
                        Student student = getTableRow().getItem();
                        students.remove(student);
                        filteredStudents.remove(student);
                    });

                    editButton.setOnAction(event -> {
                        Student student = getTableRow().getItem();
                        showEditStudentDialog(student);
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        HBox actionsContainer = new HBox(10, deleteButton, editButton);
                        setGraphic(actionsContainer);
                    } else {
                        setGraphic(null);
                    }
                }
            });
        } else {
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(property));
        }

        return tableColumn;
    }

    private void showEditStudentDialog(Student student) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Edit student details");

        // Create the dialog controls with the pre-populated student details
        TextField nameField = new TextField("student.getName()");
        TextField surnameField = new TextField("student.getSurname()");
        TextField courseField = new TextField("student.getCourse()");
        TextField companyField = new TextField("student.getCompany()");
        TextField javaSkillsField = new TextField("student.getJavaSkills()");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Surname:"), surnameField);
        grid.addRow(2, new Label("Course:"), courseField);
        grid.addRow(3, new Label("Company:"), companyField);
        grid.addRow(4, new Label("Java Skills:"), javaSkillsField);

        dialog.getDialogPane().setContent(grid);

        // Set the result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                /*student.setName(nameField.getText());
                student.setSurname(surnameField.getText());
                student.setCourse(courseField.getText());
                student.setCompany(companyField.getText());
                student.setJavaSkills(javaSkillsField.getText());*/
                return student;
            }
            return null;
        });

        // Add OK button to the dialog pane
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait();
        tableView.refresh();
    }

    private TextField createSearchField(TableColumn<Student, String> column) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filterStudents(newValue, column)
        );
        return searchField;
    }

    private void filterStudents(String keyword, TableColumn<Student, String> column) {
        filteredStudents.clear();
        for (Student student : students) {
            String cellValue = column.getCellData(student);
            if (cellValue != null && cellValue.toLowerCase().contains(keyword.toLowerCase())) {
                filteredStudents.add(student);
            }
        }
    }

    private void showAddStudentDialog() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add Student");
        dialog.setHeaderText("Enter student details");

        // Create the dialog controls
        TextField nameField = new TextField();
        TextField surnameField = new TextField();
        TextField courseField = new TextField();
        TextField companyField = new TextField();
        TextField javaSkillsField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Surname:"), surnameField);
        grid.addRow(2, new Label("Course:"), courseField);
        grid.addRow(3, new Label("Company:"), companyField);
        grid.addRow(4, new Label("Java Skills:"), javaSkillsField);

        dialog.getDialogPane().setContent(grid);

        // Set the result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String name = nameField.getText();
                String surname = surnameField.getText();
                String course = courseField.getText();
                String company = companyField.getText();
                String javaSkills = javaSkillsField.getText();
                //return new Student(name, surname, course, company, javaSkills);
            }
            return null;
        });

        // Add OK button to the dialog pane
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(student -> {
            students.add(student);
            filteredStudents.add(student);
        });
    }

    private void openFilteredWindow(TableColumn<Student, String> column) {
        Stage stage = new Stage();
        stage.setTitle("Filtered Students");

        TableView<Student> filteredTableView = new TableView<>();
        filteredTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Student, String> filteredColumn = createTableColumn(column.getText(), column.getId());
        filteredTableView.getColumns().add(filteredColumn);
        filteredTableView.setItems(filteredStudents);

        TextField searchField = createSearchField(filteredColumn);

        VBox root = new VBox(10, searchField, filteredTableView);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}


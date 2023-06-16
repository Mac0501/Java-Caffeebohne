package com.example.baum.student;


import com.example.baum.*;
import com.example.baum.company.Company;
import com.example.baum.company.CompanyData;
import com.example.baum.course.Course;
import com.example.baum.course.CourseData;
import com.example.baum.student.Student;
import com.example.baum.student.StudentData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentPane extends GridPane {

    private StudentData studentData;
    private CourseData courseData;
    private CompanyData companyData;

    private TableView<Student> table;
    private TextField searchField;
    private TextField nameField;
    private TextField surnameField;
    private Label errorLabel;
    private Slider javaSkillsSlider;
    private ComboBox<Course> courseComboBox;
    private ComboBox<Company> companyComboBox;
    private Button addButton;
    private Button removeButton;
    private Button editButton;

    private Student selectedStudent;

    public StudentPane(StudentData studentData, CourseData courseData, CompanyData companyData) {
        this.studentData = studentData;
        this.courseData = courseData;
        this.companyData = companyData;

        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);

        createComponents();
        layoutComponents();
        setupEventHandlers();
    }

    private void createComponents() {
        table = createStudentTable();
        searchField = createStudentSearchField(table);
        nameField = createStudentNameField();
        surnameField = createStudentSurnameField();
        errorLabel = createErrorLabel();
        javaSkillsSlider = createStudentJavaSkillsSlider();
        courseComboBox = createStudentCourseComboBox();
        companyComboBox = createStudentCompanyComboBox();
        addButton = createStudentAddButton(nameField, surnameField, javaSkillsSlider, courseComboBox, companyComboBox, errorLabel);
        removeButton = createStudentRemoveButton(table);
        editButton = createStudentEditButton(table);

        selectedStudent = null;
    }

    private void layoutComponents() {
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        GridPane.setHgrow(addButton, Priority.ALWAYS);
        GridPane.setVgrow(addButton, Priority.ALWAYS);

        GridPane.setHgrow(removeButton, Priority.ALWAYS);
        GridPane.setVgrow(removeButton, Priority.ALWAYS);

        GridPane.setHgrow(editButton, Priority.ALWAYS);
        GridPane.setVgrow(editButton, Priority.ALWAYS);

        add(table, 0, 0, 1, 3);

        VBox vBox = new VBox(10, searchField, nameField, surnameField, courseComboBox, companyComboBox, javaSkillsSlider, addButton, removeButton, editButton, errorLabel);
        vBox.setAlignment(Pos.TOP_CENTER);
        add(vBox, 1, 0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(70);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(30);
        getColumnConstraints().addAll(col1, col2);
    }

    private void setupEventHandlers() {
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectStudent(newValue);
            } else {
                deselectStudent();
            }
        });
    }

    private void selectStudent(Student student) {
        selectedStudent = student;
        nameField.setText(student.getName());
        surnameField.setText(student.getSurname());
        javaSkillsSlider.setValue(student.getJavaskills());
        courseComboBox.getSelectionModel().select(student.getCourse());
        companyComboBox.getSelectionModel().select(student.getCompany());
        addButton.setDisable(true);
        removeButton.setDisable(true);
        editButton.setText("Save Changes");
    }

    private void deselectStudent() {
        selectedStudent = null;
        nameField.clear();
        surnameField.clear();
        javaSkillsSlider.setValue(0);
        courseComboBox.getSelectionModel().clearSelection();
        companyComboBox.getSelectionModel().clearSelection();
        addButton.setDisable(false);
        removeButton.setDisable(false);
        editButton.setText("Edit Student");
    }

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

        List<TableColumn<Student, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(surnameColumn);
        columns.add(courseColumn);
        columns.add(companyColumn);
        columns.add(javaSkillsColumn);

        table.getColumns().addAll(columns);

        return table;
    }

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

    private TableColumn<Student, Course> createCourseColumn() {
        TableColumn<Student, Course> courseColumn = new TableColumn<>("Course");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        return courseColumn;
    }

    private TableColumn<Student, Company> createCompanyColumn() {
        TableColumn<Student, Company> companyColumn = new TableColumn<>("Company");
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));

        return companyColumn;
    }

    private TextField createStudentSearchField(TableView<Student> table) {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Students...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.trim().toLowerCase();
            table.setItems(studentData.searchStudentsByName(searchTerm));
        });

        return searchField;
    }

    private TextField createStudentNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        return nameField;
    }

    private TextField createStudentSurnameField() {
        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");

        return surnameField;
    }

    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setTextFill(Color.RED);

        return errorLabel;
    }

    private Slider createStudentJavaSkillsSlider() {
        Slider javaSkillsSlider = new Slider(0, 100, 0);
        javaSkillsSlider.setShowTickLabels(true);
        javaSkillsSlider.setShowTickMarks(true);
        javaSkillsSlider.setMajorTickUnit(25);
        javaSkillsSlider.setMinorTickCount(5);
        javaSkillsSlider.setBlockIncrement(10);

        return javaSkillsSlider;
    }

    private ComboBox<Course> createStudentCourseComboBox() {
        ComboBox<Course> courseComboBox = new ComboBox<>();
        courseComboBox.setItems(courseData.getCourseList());
        courseComboBox.setPromptText("Select Course");

        return courseComboBox;
    }

    private ComboBox<Company> createStudentCompanyComboBox() {
        ComboBox<Company> companyComboBox = new ComboBox<>();
        companyComboBox.setItems(companyData.getCompanyList());
        companyComboBox.setPromptText("Select Company");

        return companyComboBox;
    }

    private Button createStudentAddButton(TextField nameField, TextField surnameField, Slider javaSkillsSlider,
                                          ComboBox<Course> courseComboBox, ComboBox<Company> companyComboBox, Label errorLabel) {
        Button addButton = new Button("Add Student");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
            Company selectedCompany = companyComboBox.getSelectionModel().getSelectedItem();

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

            clearValidationError(errorLabel);

            studentData.addStudent(nameField.getText(), surnameField.getText(),
                    (int) javaSkillsSlider.getValue(), selectedCourse.getId(), selectedCompany.getId());
            nameField.clear();
            surnameField.clear();
            javaSkillsSlider.setValue(0);
        });

        return addButton;
    }

    private Button createStudentRemoveButton(TableView<Student> table) {
        Button removeButton = new Button("Remove Student");
        removeButton.setOnAction(event -> {
            Student selectedStudent = table.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                studentData.removeStudent(selectedStudent);
            }
        });

        return removeButton;
    }

    private Button createStudentEditButton(TableView<Student> table) {
        Button editButton = new Button("Edit Student");
        editButton.setOnAction(event -> {
            if (selectedStudent != null) {
                if (editButton.getText().equals("Edit Student")) {
                    enableEditMode();
                } else if (editButton.getText().equals("Save Changes")) {
                    saveChanges();
                }
            }
        });

        return editButton;
    }

    private void enableEditMode() {
        nameField.setDisable(false);
        surnameField.setDisable(false);
        javaSkillsSlider.setDisable(false);
        courseComboBox.setDisable(false);
        companyComboBox.setDisable(false);
        addButton.setDisable(true);
        removeButton.setDisable(true);
        searchField.setDisable(true);
        table.setDisable(true);
        editButton.setText("Cancel");
    }

    private void saveChanges() {
        String name = nameField.getText();
        String surname = surnameField.getText();
        int javaskills = (int) javaSkillsSlider.getValue();
        Course course = courseComboBox.getSelectionModel().getSelectedItem();
        Company company = companyComboBox.getSelectionModel().getSelectedItem();

        if (name.isEmpty()) {
            displayValidationError(errorLabel, "Please enter a name.");
            return;
        }

        if (surname.isEmpty()) {
            displayValidationError(errorLabel, "Please enter a surname.");
            return;
        }

        if (course == null) {
            displayValidationError(errorLabel, "Please select a course.");
            return;
        }

        if (company == null) {
            displayValidationError(errorLabel, "Please select a company.");
            return;
        }

        clearValidationError(errorLabel);

        selectedStudent.setName(name);
        selectedStudent.setSurname(surname);
        selectedStudent.setJavaskills(javaskills);
        selectedStudent.setCourse(course);
        selectedStudent.setCompany(company);

        table.refresh();
        deselectStudent();
        nameField.clear();
        surnameField.clear();
        javaSkillsSlider.setValue(0);
    }

    private void displayValidationError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void clearValidationError(Label errorLabel) {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}

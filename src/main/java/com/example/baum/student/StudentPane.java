package com.example.baum.student;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import com.example.baum.company.Company;
import com.example.baum.company.CompanyData;
import com.example.baum.course.Course;
import com.example.baum.course.CourseData;

/**
 * A custom GridPane that represents the Student pane in the application.
 * It allows adding, removing, and searching for student.
 */
public class StudentPane extends GridPane {

    private StudentData studentData;
    private CourseData courseData;
    private CompanyData companyData;

    /**
     * Creates a new instance of the StudentPane.
     *
     * @param studentData The data manager for students.
     * @param courseData  The data manager for courses.
     * @param companyData The data manager for companies.
     */
    public StudentPane(StudentData studentData, CourseData courseData, CompanyData companyData) {
        this.studentData = studentData;
        this.courseData = courseData;
        this.companyData = companyData;

        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);

        TableView<Student> table = createStudentTable();
        TextField searchField = createStudentSearchField(table);
        TextField nameField = createStudentNameField();
        TextField surnameField = createStudentSurnameField();
        Label errorLabel = createErrorLabel();
        Slider javaSkillsSlider = createStudentJavaSkillsSlider();
        ComboBox<Course> courseComboBox = createStudentCourseComboBox();
        ComboBox<Company> companyComboBox = createStudentCompanyComboBox();
        Button addButton = createStudentAddButton(nameField, surnameField, javaSkillsSlider, courseComboBox, companyComboBox, errorLabel);
        Button removeButton = createStudentRemoveButton(table);
        HBox skillsBox = createStudentSkillsBox(javaSkillsSlider);

        GridPane addStudentGridPane = new GridPane();
        addStudentGridPane.add(nameField, 0, 0);
        addStudentGridPane.add(surnameField, 1, 0);
        addStudentGridPane.add(courseComboBox, 0, 1);
        addStudentGridPane.add(companyComboBox, 1, 1);
        addStudentGridPane.add(skillsBox, 0, 2, 2, 1);
        addStudentGridPane.add(addButton, 0, 3);
        addStudentGridPane.add(removeButton, 1, 3);
        addStudentGridPane.add(errorLabel, 0, 4, 2, 1);
        addStudentGridPane.add(searchField, 0, 5, 2, 1);

        add(table, 0, 0);
        add(addStudentGridPane, 1, 0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        getColumnConstraints().addAll(col1, col2);

        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        GridPane.setHgrow(addStudentGridPane, Priority.ALWAYS);
        GridPane.setVgrow(addStudentGridPane, Priority.ALWAYS);
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

        List<TableColumn<Student, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(surnameColumn);
        columns.add(courseColumn);
        columns.add(companyColumn);
        columns.add(javaSkillsColumn);

        table.getColumns().addAll(columns);

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
    private TextField createStudentSearchField(TableView<Student> table) {
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
    private TextField createStudentNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        return nameField;
    }

    /**
     * Creates the TextField for entering the student's surname.
     *
     * @return The TextField for the student's surname.
     */
    private TextField createStudentSurnameField() {
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
        errorLabel.setTextFill(Color.RED);

        return errorLabel;
    }

    /**
     * Creates the Slider for selecting the student's Java skills.
     *
     * @return The Slider for Java skills.
     */
    private Slider createStudentJavaSkillsSlider() {
        Slider javaSkillsSlider = new Slider(0, 100, 0);
        javaSkillsSlider.setShowTickLabels(true);
        javaSkillsSlider.setShowTickMarks(true);
        javaSkillsSlider.setMajorTickUnit(25);
        javaSkillsSlider.setMinorTickCount(5);
        javaSkillsSlider.setBlockIncrement(10);

        return javaSkillsSlider;
    }

    /**
     * Creates the ComboBox for selecting the student's course.
     *
     * @return The ComboBox for selecting the course.
     */
    private ComboBox<Course> createStudentCourseComboBox() {
        ComboBox<Course> courseComboBox = new ComboBox<>();
        courseComboBox.setItems(courseData.getCourseList());
        courseComboBox.setPromptText("Select Course");

        return courseComboBox;
    }

    /**
     * Creates the ComboBox for selecting the student's company.
     *
     * @return The ComboBox for selecting the company.
     */
    private ComboBox<Company> createStudentCompanyComboBox() {
        ComboBox<Company> companyComboBox = new ComboBox<>();
        companyComboBox.setItems(companyData.getCompanyList());
        companyComboBox.setPromptText("Select Company");

        return companyComboBox;
    }

    /**
     * Creates the Button for adding a new student.
     *
     * @param nameField           The TextField for the student's name.
     * @param surnameField        The TextField for the student's surname.
     * @param javaSkillsSlider    The Slider for the student's Java skills.
     * @param courseComboBox      The ComboBox for selecting the student's course.
     * @param companyComboBox     The ComboBox for selecting the student's company.
     * @param errorLabel          The Label for validation errors.
     * @return The Button for adding a new student.
     */
    private Button createStudentAddButton(TextField nameField, TextField surnameField, Slider javaSkillsSlider,
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

    /**
     * Creates the HBox for the Java skills slider.
     *
     * @param javaSkillsSlider The Slider for Java skills.
     * @return The HBox for the Java skills slider.
     */
    private HBox createStudentSkillsBox(Slider javaSkillsSlider) {
        Label skillsLabel = new Label("Java Skills:");
        skillsLabel.getStyleClass().add("skills-label");

        HBox skillsBox = new HBox(10, skillsLabel, javaSkillsSlider);
        skillsBox.setAlignment(Pos.CENTER_LEFT);

        return skillsBox;
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

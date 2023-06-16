package com.example.baum.student;

import com.example.baum.company.Company;
import com.example.baum.company.CompanyData;
import com.example.baum.course.Course;
import com.example.baum.course.CourseData;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Optional;

public class StudentPane extends GridPane {
    private TableView<Student> studentTable;
    private TextField nameField;
    private TextField surnameField;
    private TextField searchField;
    private Label errorLabel;
    private Slider javaSkillsSlider;
    private ComboBox<Course> courseComboBox;
    private ComboBox<Company> companyComboBox;
    private Button addEditButton;
    private Button removeButton;
    private Button deselectButton;
    private final StudentData studentData;
    private final CourseData courseData;
    private final CompanyData companyData;
    private SimpleObjectProperty<Student> selectedStudent = new SimpleObjectProperty<>(null);
    private boolean doNotShowAgain = false;

    public StudentPane(StudentData studentData, CourseData courseData, CompanyData companyData) {
        this.studentData = studentData;
        this.courseData = courseData;
        this.companyData = companyData;

        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);

        createAndLayoutComponents();
        setupEventHandlers();
    }

    private void createAndLayoutComponents() {
        studentTable = createTableView();
        searchField = createTextField("Search Students, Courses or Companies...");
        nameField = createTextField("Name");
        surnameField = createTextField("Surname");
        javaSkillsSlider = createSlider();
        Label javaSkillsLabel = new Label("Java Skills");
        courseComboBox = createComboBox(courseData.getCourseList(), "Select Course");
        companyComboBox = createComboBox(companyData.getCompanyList(), "Select Company");
        errorLabel = new Label();
        addEditButton = new Button("Add Student");
        removeButton = new Button("Remove Student");
        deselectButton = new Button("Deselect");

        configureTableColumns();
        configureFormFields();
        configureButtons();

        VBox vBox = createVBox(searchField, studentTable);
        VBox formBox = createFormBox(nameField, surnameField, courseComboBox, companyComboBox,
                javaSkillsLabel, javaSkillsSlider, addEditButton, removeButton, deselectButton, errorLabel);

        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);
        setColumnConstraints();
        setVgrow(vBox, Priority.ALWAYS);
        setVgrow(formBox, Priority.ALWAYS);
        add(vBox, 0, 0);
        add(formBox, 1, 0);
    }

    private TableView<Student> createTableView() {
        TableView<Student> tableView = new TableView<>();
        tableView.setItems(studentData.getStudentList());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return tableView;
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    private Slider createSlider() {
        Slider slider = new Slider(0, 100, 0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(50);
        slider.setMinorTickCount(5);
        slider.setBlockIncrement(10);
        return slider;
    }

    private <T> ComboBox<T> createComboBox(ObservableList<T> items, String promptText) {
        ComboBox<T> comboBox = new ComboBox<>(items);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setPromptText(promptText);
        return comboBox;
    }

    private VBox createVBox(Node... nodes) {
        VBox vBox = new VBox(10, nodes);
        vBox.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(studentTable, Priority.ALWAYS);
        vBox.setFillWidth(true);
        return vBox;
    }

    private VBox createFormBox(Node... nodes) {
        VBox formBox = new VBox(10, nodes);
        formBox.setAlignment(Pos.TOP_CENTER);
        formBox.setPadding(new Insets(10));
        formBox.setMaxWidth(Double.MAX_VALUE);
        formBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        return formBox;
    }

    private void configureTableColumns() {
        TableColumn<Student, String> nameColumn = createColumn("Name", "name");
        TableColumn<Student, String> surnameColumn = createColumn("Surname", "surname");
        TableColumn<Student, Integer> javaSkillsColumn = createColumn("Java Skills", "javaSkills");
        TableColumn<Student, Course> courseColumn = createColumn("Course", "course");
        TableColumn<Student, Company> companyColumn = createColumn("Company", "company");

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

        javaSkillsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            studentTable.refresh();
        });

        studentTable.getColumns().addAll(nameColumn, surnameColumn, courseColumn, companyColumn, javaSkillsColumn);
    }

    private <T> TableColumn<Student, T> createColumn(String title, String property) {
        TableColumn<Student, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private void configureFormFields() {
        nameField.setPromptText("Enter Name");
        surnameField.setPromptText("Enter Surname");

        studentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            ObservableList<Student> selectedStudents = studentTable.getSelectionModel().getSelectedItems();
            if (!selectedStudents.isEmpty()) {
                selectedStudent.set(selectedStudents.get(0));
                fillFormWithStudentData(selectedStudent.get());
                addEditButton.setText("Update Student");
                removeButton.setDisable(false);
                deselectButton.setDisable(false);
            } else {
                selectedStudent.set(null);
                clearForm();
                removeButton.setDisable(true);
                deselectButton.setDisable(true);
            }
        });
    }

    private void configureButtons() {
        removeButton.setDisable(true);
        deselectButton.setDisable(true);

        addEditButton.disableProperty().bind(
                Bindings.isEmpty(nameField.textProperty())
                        .or(Bindings.isEmpty(surnameField.textProperty()))
                        .or(Bindings.isNull(companyComboBox.valueProperty()))
                        .or(Bindings.createBooleanBinding(() -> selectedStudent.get() != null && !isFormChanged(),
                                nameField.textProperty(),
                                surnameField.textProperty(),
                                javaSkillsSlider.valueProperty(),
                                companyComboBox.valueProperty()
                        ))
        );
    }

    private void setColumnConstraints() {
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(70);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(30);
        getColumnConstraints().addAll(column1, column2);
    }

    private void setupEventHandlers() {
        searchField.textProperty().addListener((observable, oldText, newText) -> {
            searchStudents(newText);
        });

        addEditButton.setOnAction(event -> {
            if (selectedStudent.get() == null) {
                addStudent();
            } else {
                updateStudent(selectedStudent.get());
            }
        });

        removeButton.setOnAction(event -> {
            ObservableList<Student> selectedStudents = studentTable.getSelectionModel().getSelectedItems();
            if (selectedStudents.size() > 1) {
                CheckBox doNotShowAgainCheckbox = new CheckBox("Do not show this again");
                doNotShowAgainCheckbox.setSelected(false);

                if (!doNotShowAgain) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Remove Multiple Students");
                    alert.setContentText("Are you sure you want to remove " + selectedStudents.size() + " students?");
                    alert.getDialogPane().setContent(doNotShowAgainCheckbox);
                    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(okButton, cancelButton);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == okButton) {
                        if (doNotShowAgainCheckbox.isSelected()) {
                            doNotShowAgain = true;
                        }

                        studentData.removeStudents(selectedStudents);
                        clearForm();
                    }
                } else {
                    studentData.removeStudents(selectedStudents);
                    clearForm();
                }
            } else if (selectedStudents.size() == 1) {
                studentData.removeStudent(selectedStudents.get(0));
                clearForm();
            }
        });

        deselectButton.setOnAction(event -> {
            deselect();
        });
    }

    private void fillFormWithStudentData(Student student) {
        nameField.setText(student.getName());
        surnameField.setText(student.getSurname());
        javaSkillsSlider.setValue(student.getJavaSkills());
        courseComboBox.setValue(student.getCourse());
        companyComboBox.setValue(student.getCompany());
    }

    private void addStudent() {
        if (validateForm()) {
            String name = nameField.getText();
            String surname = surnameField.getText();
            int javaSkills = (int) javaSkillsSlider.getValue();
            Course course = courseComboBox.getValue();
            Company company = companyComboBox.getValue();

            studentData.addStudent(name, surname, javaSkills, course.getId(), company.getId());
            clearForm();
            deselect();
        }
    }

    private void updateStudent(Student student) {
        if (validateForm() && isFormChanged()) {
            String newName = nameField.getText();
            String newSurname = surnameField.getText();
            int newJavaSkills = (int) javaSkillsSlider.getValue();
            Course newCourse = courseComboBox.getValue();
            Company newCompany = companyComboBox.getValue();

            student.setName(newName);
            student.setSurname(newSurname);
            student.setJavaSkills(newJavaSkills);
            student.setCourse(newCourse);
            student.setCompany(newCompany);

            studentData.updateStudent(student);

            studentTable.refresh();

            clearForm();
            deselect();

            studentTable.getSelectionModel().clearSelection();
            studentTable.setStyle("");
        }
    }

    private void deselect() {
        studentTable.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        String errorMessage = "";

        if (nameField.getText().isEmpty()) {
            errorMessage += "No valid name!\n";
        }
        if (surnameField.getText().isEmpty()) {
            errorMessage += "No valid surname!\n";
        }
        if (courseComboBox.getValue() == null) {
            errorMessage += "No course selected!\n";
        }
        if (companyComboBox.getValue() == null) {
            errorMessage += "No company selected!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            errorLabel.setText(errorMessage);
            errorLabel.getStyleClass().add("error-label");
            return false;
        }
    }

    private void clearForm() {
        nameField.clear();
        surnameField.clear();
        javaSkillsSlider.setValue(0);
        selectedStudent.set(null);
        addEditButton.setText("Add Student");
        courseComboBox.setPromptText("Select Course");
        companyComboBox.setPromptText("Select Company");
        removeButton.setDisable(true);
        deselectButton.setDisable(true);
        errorLabel.setText("");
        errorLabel.getStyleClass().remove("error-label");
    }

    private void searchStudents(String searchText) {
        String searchTerm = searchText.toLowerCase();
        ObservableList<Student> filteredList = FXCollections.observableArrayList();
        for (Student student : studentData.getStudentList()) {
            if (student.getName().toLowerCase().contains(searchTerm) ||
                    student.getSurname().toLowerCase().contains(searchTerm) ||
                    student.getCourse().getName().toLowerCase().contains(searchTerm) ||
                    student.getCompany().getName().toLowerCase().contains(searchTerm)) {
                filteredList.add(student);
            }
        }
        studentTable.setItems(filteredList);
    }

    private boolean isFormChanged() {
        Student student = selectedStudent.get();
        if (student != null) {
            String name = nameField.getText();
            String surname = surnameField.getText();
            int javaSkills = (int) javaSkillsSlider.getValue();
            Course course = courseComboBox.getValue();
            Company company = companyComboBox.getValue();

            return !student.getName().equals(name) ||
                    !student.getSurname().equals(surname) ||
                    student.getJavaSkills() != javaSkills ||
                    !student.getCourse().equals(course) ||
                    !student.getCompany().equals(company);
        }
        return false;
    }
}

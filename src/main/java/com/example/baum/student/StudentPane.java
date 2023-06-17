package com.example.baum.student;

import com.example.baum.company.Company;
import com.example.baum.company.CompanyData;
import com.example.baum.course.Course;
import com.example.baum.course.CourseData;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
    private Button batchChangeButton;
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
        batchChangeButton = new Button("Batch Change");

        configureTableColumns();
        configureFormFields();
        configureButtons();

        VBox vBox = createVBox(searchField, studentTable);
        VBox formBox = createFormBox(nameField, surnameField, courseComboBox, companyComboBox, javaSkillsLabel,
                javaSkillsSlider, addEditButton, batchChangeButton, removeButton, deselectButton, errorLabel);

        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);
        setColumnConstraints();
        setVgrow(vBox, Priority.ALWAYS);
        setVgrow(formBox, Priority.ALWAYS);
        add(vBox, 0, 0);
        add(formBox, 1, 0);
    }

    /**
     * Updates the TableView with the latest room list.
     */
    private void updateRoomTableView() {
        studentTable.setItems(studentData.getStudentList());
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
                if (selectedStudents.size() == 1) {
                    selectedStudent.set(selectedStudents.get(0));
                    fillFormWithStudentData(selectedStudent.get());
                    addEditButton.setText("Update Student");
                    removeButton.setDisable(false);
                    deselectButton.setDisable(false);
                    enableFormFields(true);
                } else {
                    selectedStudent.set(null);
                    clearForm();
                    removeButton.setDisable(false);
                    deselectButton.setDisable(false);
                    enableFormFields(false);
                }
                batchChangeButton.disableProperty().unbind();
                batchChangeButton.setDisable(false);
            } else {
                selectedStudent.set(null);
                clearForm();
                removeButton.setDisable(true);
                deselectButton.setDisable(true);
                enableFormFields(true);
                batchChangeButton.setDisable(true);
            }
        });
    }

    private void enableFormFields(boolean enable) {
        boolean disableFormFields = !enable || studentTable.getSelectionModel().getSelectedItems().size() > 1;
        nameField.setDisable(disableFormFields);
        surnameField.setDisable(disableFormFields);
        javaSkillsSlider.setDisable(disableFormFields);
        courseComboBox.setDisable(disableFormFields);
        companyComboBox.setDisable(disableFormFields);
    }

    private void configureButtons() {
        removeButton.getStyleClass().add("remove-button"); // Add the "remove-button" style class
        deselectButton.getStyleClass().add("gray-button"); // Add the "gray-button" style class

        addEditButton.setOnAction(event -> {
            if (selectedStudent.get() == null) {
                addStudent();
            } else {
                updateStudent(selectedStudent.get());
            }
        updateRoomTableView();
        });

        removeButton.setOnAction(event -> {
            ObservableList<Student> selectedStudents = studentTable.getSelectionModel().getSelectedItems();
            if (selectedStudents.size() > 1) {
                if (!doNotShowAgain) {
                    CheckBox doNotShowAgainCheckbox = new CheckBox("Do not show this again");
                    doNotShowAgainCheckbox.setSelected(false);
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
            updateRoomTableView();
        });

        deselectButton.setOnAction(event -> {
            deselect();
            updateRoomTableView();
        });

        batchChangeButton.setOnAction(event -> {
            batchChangeStudents();
            updateRoomTableView();
        });

        // Disable the Batch Change button initially
        batchChangeButton.setDisable(true);

        // Enable the Batch Change button if multiple students are selected
        studentTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Student> change) -> {
            int selectedCount = studentTable.getSelectionModel().getSelectedItems().size();
            batchChangeButton.setDisable(selectedCount <= 2);
            addEditButton.setDisable(selectedCount > 1);

            if (selectedCount > 1) {
                removeButton.setText("Remove Students");
            } else {
                removeButton.setText("Remove Student");
            }
        });
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
            updateRoomTableView();
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
            updateRoomTableView();
        });

        deselectButton.setOnAction(event -> {
            deselect();
            updateRoomTableView();
        });

        batchChangeButton.setOnAction(event -> {
            batchChangeStudents();
            updateRoomTableView();
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
            if (student.getName().toLowerCase().contains(searchTerm)
                    || student.getSurname().toLowerCase().contains(searchTerm)
                    || student.getCourse().getName().toLowerCase().contains(searchTerm)
                    || student.getCompany().getName().toLowerCase().contains(searchTerm)) {
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

            return !student.getName().equals(name)
                    || !student.getSurname().equals(surname)
                    || student.getJavaSkills() != javaSkills
                    || !student.getCourse().equals(course)
                    || !student.getCompany().equals(company);
        }

        return false;
    }

    private void batchChangeStudents() {
        ObservableList<Student> selectedStudents = studentTable.getSelectionModel().getSelectedItems();

        if (!selectedStudents.isEmpty()) {
            Dialog<BatchChangeResult> dialog = new Dialog<>();
            dialog.setTitle("Batch Change");
            dialog.setHeaderText("Enter New Values for Batch Changes");

            // Apply modern dialog styling
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/baum/style.css").toExternalForm());
            dialog.getDialogPane().getStyleClass().add("batch-change-dialog");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));

            ComboBox<Course> newCourseComboBox = createComboBox(courseData.getCourseList(), "Select New Course");
            ComboBox<Company> newCompanyComboBox = createComboBox(companyData.getCompanyList(), "Select New Company");

            grid.add(new Label("New Course:"), 0, 0);
            grid.add(newCourseComboBox, 1, 0);
            grid.add(new Label("New Company:"), 0, 1);
            grid.add(newCompanyComboBox, 1, 1);

            dialog.getDialogPane().setContent(grid);
            ButtonType batchChangeButtonType = new ButtonType("Batch Change", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(batchChangeButtonType, cancelButtonType);

            Node batchChangeButton = dialog.getDialogPane().lookupButton(batchChangeButtonType);
            batchChangeButton.setDisable(true);
            batchChangeButton.disableProperty().bind(Bindings.createBooleanBinding(
                    () -> newCourseComboBox.getValue() == null && newCompanyComboBox.getValue() == null,
                    newCourseComboBox.valueProperty(), newCompanyComboBox.valueProperty()));

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == batchChangeButtonType) {
                    Course newCourse = newCourseComboBox.getValue();
                    Company newCompany = newCompanyComboBox.getValue();
                    return new BatchChangeResult(newCourse, newCompany);
                }
                return null;
            });

            Optional<BatchChangeResult> result = dialog.showAndWait();
            result.ifPresent(batchChangeResult -> {
                for (Student student : selectedStudents) {
                    if (batchChangeResult.newCourse != null) {
                        student.setCourse(batchChangeResult.newCourse);
                    }
                    if (batchChangeResult.newCompany != null) {
                        student.setCompany(batchChangeResult.newCompany);
                    }
                    // Update the student in the database
                    studentData.updateStudent(student);
                }
                studentTable.refresh();
                deselect();
            });
        }
    }


    // Helper class to store the batch change result
    private static class BatchChangeResult {
        private final Course newCourse;
        private final Company newCompany;

        public BatchChangeResult(Course newCourse, Company newCompany) {
            this.newCourse = newCourse;
            this.newCompany = newCompany;
        }
    }



}

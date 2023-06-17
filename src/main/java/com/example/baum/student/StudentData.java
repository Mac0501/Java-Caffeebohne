package com.example.baum.student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import com.example.baum.DatabaseManager;
import com.example.baum.company.Company;
import com.example.baum.company.CompanyData;
import com.example.baum.course.Course;
import com.example.baum.course.CourseData;

/**
 * A class that manages student data, including fetching from a database,
 * adding, removing, searching, and updating students.
 */
public class StudentData {
    private final ObservableList<Student> studentList;
    private final DatabaseManager databaseManager;
    private final CourseData courseData;
    private final CompanyData companyData;

    /**
     * Constructs a StudentData object with the specified dependencies.
     *
     * @param databaseManager The DatabaseManager object used for database
     *                        operations.
     * @param courseData      The CourseData object used for accessing course data.
     * @param companyData     The CompanyData object used for accessing company
     *                        data.
     */
    public StudentData(DatabaseManager databaseManager, CourseData courseData, CompanyData companyData) {
        this.databaseManager = databaseManager;
        this.courseData = courseData;
        this.companyData = companyData;
        studentList = FXCollections.observableArrayList();
    }

    /**
     * Retrieves the observable list of students.
     *
     * @return The observable list of students.
     */
    public ObservableList<Student> getStudentList() {
        return studentList;
    }

    /**
     * Fetches students from the database and populates the student list.
     */
    public void fetchStudentsFromDatabase() {
        studentList.clear();
        String selectQuery = "SELECT * FROM student";
        try {
            Statement statement = databaseManager.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");
                int javaskills = resultSet.getInt("javaskills");
                int courseId = resultSet.getInt("course_id");
                int companyId = resultSet.getInt("company_id");

                Course course = courseData.getCourseById(courseId);
                Company company = companyData.getCompanyById(companyId);

                Student student = new Student(name, surname, javaskills, course, company);
                student.setId(id); // Set the ID of the student
                studentList.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a student with the specified details to the database and the student
     * list.
     *
     * @param name       The name of the student.
     * @param surname    The surname of the student.
     * @param javaskills The Java skills level of the student.
     * @param courseId   The ID of the course associated with the student.
     * @param companyId  The ID of the company associated with the student.
     */
    public void addStudent(String name, String surname, int javaskills, int courseId, int companyId) {
        String insertQuery = "INSERT INTO student (name, surname, javaskills, course_id, company_id) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(insertQuery);
            statement.setString(1, name);
            statement.setString(2, surname);
            statement.setInt(3, javaskills);
            statement.setInt(4, courseId);
            statement.setInt(5, companyId);
            statement.executeUpdate();

            int lastInsertedId = getLastInsertedId();
            Course course = courseData.getCourseById(courseId);
            Company company = companyData.getCompanyById(companyId);
            Student newStudent = new Student(name, surname, javaskills, course, company);
            newStudent.setId(lastInsertedId); // Set the ID of the new student
            studentList.add(newStudent);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes the specified student from the database and the student list.
     *
     * @param student The student to be removed.
     */
    public void removeStudent(Student student) {
        if (student != null) {
            String deleteQuery = "DELETE FROM student WHERE id = ?";
            try {
                PreparedStatement statement = databaseManager.getConnection().prepareStatement(deleteQuery);
                statement.setInt(1, student.getId());
                statement.executeUpdate();
                studentList.remove(student);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Searches for students in the student list by name.
     *
     * @param name The name to search for.
     * @return The observable list of students matching the search criteria.
     */
    public ObservableList<Student> searchStudentsByName(String name) {
        String searchTerm = name.toLowerCase();
        return FXCollections.observableArrayList(
                studentList.stream()
                        .filter(student -> student.getName().toLowerCase().contains(searchTerm) ||
                                student.getSurname().toLowerCase().contains(searchTerm))
                        .collect(Collectors.toList()));
    }

    /**
     * Retrieves the ID of the last inserted student from the database.
     *
     * @return The ID of the last inserted student.
     * @throws SQLException If an SQL exception occurs.
     */
    private int getLastInsertedId() throws SQLException {
        Statement statement = databaseManager.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    /**
     * Clears the input fields after adding a student.
     * This method can be extended to update the GUI with a notification or feedback
     * to the user.
     */
    private void clearFields() {
        // Clear text fields after adding a student
        // You can also update the GUI with a notification or feedback to the user
        // indicating that the operation was successful
        // nameField.clear();
        // courseIdField.clear();
        // companyIdField.clear();
    }

    /**
     * Updates the details of the specified student in the database.
     *
     * @param student The student to be updated.
     */
    public void updateStudent(Student student) {
        if (student != null) {
            String updateQuery = "UPDATE student SET name = ?, surname = ?, javaskills = ?, course_id = ?, company_id = ? WHERE id = ?";
            try {
                PreparedStatement statement = databaseManager.getConnection().prepareStatement(updateQuery);
                statement.setString(1, student.getName());
                statement.setString(2, student.getSurname());
                statement.setInt(3, student.getJavaSkills());
                statement.setInt(4, student.getCourse().getId());
                statement.setInt(5, student.getCompany().getId());
                statement.setInt(6, student.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Removes multiple students from the database and the student list.
     *
     * @param selectedStudents The list of students to be removed.
     */
    public void removeStudents(ObservableList<Student> selectedStudents) {
        String deleteQuery = "DELETE FROM student WHERE id = ?";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(deleteQuery);
            for (Student student : selectedStudents) {
                statement.setInt(1, student.getId());
                statement.addBatch();
            }
            statement.executeBatch();
            studentList.removeAll(selectedStudents);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

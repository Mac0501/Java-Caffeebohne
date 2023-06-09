package com.example.baum;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentData {
    private final ObservableList<Student> studentList;
    private final DatabaseManager databaseManager;
    private final CourseData courseData;
    private final CompanyData companyData;

    public StudentData(DatabaseManager databaseManager, CourseData courseData, CompanyData companyData) {
        this.databaseManager = databaseManager;
        this.courseData = courseData;
        this.companyData = companyData;
        studentList = FXCollections.observableArrayList();
    }

    public ObservableList<Student> getStudentList() {
        return studentList;
    }

    public void fetchStudentsFromDatabase() {
        String selectQuery = "SELECT * FROM student";
        try {
            Statement statement = databaseManager.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int courseId = resultSet.getInt("course_id");
                int companyId = resultSet.getInt("company_id");

                Course course = courseData.getCourseById(courseId);
                Company company = companyData.getCompanyById(companyId);

                Student student = new Student(id, name, course, company);
                studentList.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStudent(String name, int courseId, int companyId) {
        String insertQuery = "INSERT INTO student (name, course_id, company_id) VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(insertQuery);
            statement.setString(1, name);
            statement.setInt(2, courseId);
            statement.setInt(3, companyId);
            statement.executeUpdate();

            int lastInsertedId = getLastInsertedId();
            Course course = courseData.getCourseById(courseId);
            Company company = companyData.getCompanyById(companyId);
            Student newStudent = new Student(lastInsertedId, name, course, company);
            studentList.add(newStudent);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    private int getLastInsertedId() throws SQLException {
        Statement statement = databaseManager.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    private void clearFields() {
        // Clear text fields after adding a student
        // You can also update the GUI with a notification or feedback to the user
        // indicating that the operation was successful
        // nameField.clear();
        // courseIdField.clear();
        // companyIdField.clear();
    }
}

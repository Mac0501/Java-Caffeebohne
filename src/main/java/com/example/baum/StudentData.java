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

    public StudentData(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
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
                String className = resultSet.getString("class");
                String companyName = resultSet.getString("company");
                Course course = new Course(0, className, null); // Modify with actual Course data
                Company company = new Company(0, companyName); // Modify with actual Company data
                Student student = new Student(id, name, course, company);
                studentList.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void addStudent(String name, String className, String companyName) {
        String insertQuery = "INSERT INTO student (name, class, company) VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(insertQuery);
            statement.setString(1, name);
            statement.setString(2, className);
            statement.setString(3, companyName);
            statement.executeUpdate();
    
            int lastInsertedId = getLastInsertedId(); // Modify with actual implementation
            Course course = new Course(0, className, null); // Modify with actual Course data
            Company company = new Company(0, companyName); // Modify with actual Company data
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
        // classField.clear();
        // companyField.clear();
    }
}


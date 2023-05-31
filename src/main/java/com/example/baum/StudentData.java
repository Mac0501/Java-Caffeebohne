package com.example.baum;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
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
        String selectQuery = "SELECT * FROM students";
        try {
            Statement statement = databaseManager.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String className = resultSet.getString("class");
                String company = resultSet.getString("company");
                Student student = new Student(id, name, className, company);
                studentList.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStudent(String name, String className, String company) {
        String insertQuery = "INSERT INTO students (name, class, company) VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(insertQuery);
            statement.setString(1, name);
            statement.setString(2, className);
            statement.setString(3, company);
            statement.executeUpdate();

            Student newStudent = new Student(getLastInsertedId(), name, className, company);
            studentList.add(newStudent);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeStudent(Student student) {
        if (student != null) {
            String deleteQuery = "DELETE FROM students WHERE id = ?";
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


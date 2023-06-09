
package com.example.baum;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CourseData {
    private final ObservableList<Course> courseList;
    private final DatabaseManager databaseManager;
    private final RoomData roomData;

    public CourseData(DatabaseManager databaseManager, RoomData roomData) {
        this.databaseManager = databaseManager;
        this.roomData = roomData;
        courseList = FXCollections.observableArrayList();
    }

    public ObservableList<Course> getCourseList() {
        return courseList;
    }

    public void fetchCoursesFromDatabase() {
        String selectQuery = "SELECT * FROM course";
        try {
            Statement statement = databaseManager.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int roomId = resultSet.getInt("room_id");

                Room room = roomData.getRoomById(roomId);

                Course course = new Course(id, name, room);
                courseList.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCourse(String name, int roomId) {
        String insertQuery = "INSERT INTO course (name, room_id) VALUES (?, ?)";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(insertQuery);
            statement.setString(1, name);
            statement.setInt(2, roomId);
            statement.executeUpdate();

            int lastInsertedId = getLastInsertedId();
            Room room = roomData.getRoomById(roomId);
            Course newCourse = new Course(lastInsertedId, name, room);
            courseList.add(newCourse);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeCourse(Course course) {
        if (course != null) {
            String deleteQuery = "DELETE FROM course WHERE id = ?";
            try {
                PreparedStatement statement = databaseManager.getConnection().prepareStatement(deleteQuery);
                statement.setInt(1, course.getId());
                statement.executeUpdate();
                courseList.remove(course);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Course getCourseById(int courseId) {
        for (Course course : courseList) {
            if (course.getId() == courseId) {
                return course;
            }
        }
        return null;
    }

    public Course getCourseByName(String courseName) {
        for (Course course : courseList) {
            if (course.getName().equals(courseName)) {
                return course;
            }
        }
        return null;
    }

    public int getCourseIdByName(String courseName) {
        for (Course course : courseList) {
            if (course.getName().equals(courseName)) {
                return course.getId();
            }
        }
        return -1; // Return -1 if the course with the specified name is not found
    }


    private int getLastInsertedId() throws SQLException {
        Statement statement = databaseManager.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    public ObservableList<Room> getRoomList() {
        return roomData.getRoomList();
    }
}

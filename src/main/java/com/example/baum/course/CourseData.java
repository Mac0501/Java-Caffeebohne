package com.example.baum.course;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.baum.DatabaseManager;
import com.example.baum.company.Company;
import com.example.baum.company.CompanyData;
import com.example.baum.room.RoomData;
import com.example.baum.student.Student;
import com.example.baum.room.Room;

/**
 * The CourseData class manages the data and operations related to courses.
 */
public class CourseData {
    private final ObservableList<Course> courseList;
    private final DatabaseManager databaseManager;
    private final RoomData roomData;
    private final CompanyData companyData;

    /**
     * Constructs a new CourseData object with the specified DatabaseManager and
     * RoomData.
     *
     * @param databaseManager The DatabaseManager object used for database
     *                        operations.
     * @param roomData        The RoomData object used for accessing room
     *                        information.
     */
    public CourseData(DatabaseManager databaseManager, RoomData roomData, CompanyData companyData) {
        this.databaseManager = databaseManager;
        this.roomData = roomData;
        this.companyData = companyData;
        courseList = FXCollections.observableArrayList();
    }

    /**
     * Retrieves the list of students enrolled in a course.
     *
     * @param course The course to retrieve the student list for.
     * @return The ObservableList of students enrolled in the course.
     */
    public ObservableList<Student> getCourseStudentList(Course course) {
        ObservableList<Student> studentList = FXCollections.observableArrayList();
        if (course != null) {
            int courseId = course.getId();
            String selectQuery = "SELECT * FROM student WHERE course_id = ?";
            try {
                PreparedStatement statement = databaseManager.getConnection().prepareStatement(selectQuery);
                statement.setInt(1, courseId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");
                    int javaskills = resultSet.getInt("javaskills");
                    int companyId = resultSet.getInt("Company_id");
                    Company company = companyData.getCompanyById(companyId);

                    // Create a new Student object
                    Student student = new Student(name, surname, javaskills, course, company);
                    student.setId(id);
                    // Add the student to the list
                    studentList.add(student);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return studentList;
    }

    /**
     * Returns the list of courses.
     *
     * @return The ObservableList of courses.
     */
    public ObservableList<Course> getCourseList() {
        return courseList;
    }

    /**
     * Fetches courses from the database and populates the course list.
     */
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

    /**
     * Updates the details of a course in the database and the course list.
     *
     * @param course The Course object to be updated.
     */
    public void updateCourse(Course course) {
        if (course != null) {
            try {
                String updateQuery = "UPDATE course SET name = ?, room_id = ? WHERE id = ?";
                PreparedStatement statement = databaseManager.getConnection().prepareStatement(updateQuery);
                statement.setString(1, course.getName());
                statement.setInt(2, course.getRoom().getId());
                statement.setInt(3, course.getId());
                statement.executeUpdate();
                int index = -1;
                for (int i = 0; i < courseList.size(); i++) {
                    if (courseList.get(i).getId() == course.getId()) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    courseList.set(index, course);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a new course to the database and the course list.
     *
     * @param name   The name of the course.
     * @param roomId The ID of the assigned room for the course.
     */
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

    /**
     * Removes a course from the database and the course list.
     *
     * @param course The Course object to be removed.
     */
    public void removeCourse(Course course) {
        if (course != null) {
            try {
                String deleteQuery = "DELETE FROM course WHERE id = ?";
                PreparedStatement statement = databaseManager.getConnection().prepareStatement(deleteQuery);
                statement.setInt(1, course.getId());
                statement.executeUpdate();
                courseList.remove(course);
            } catch (SQLException e) {
                // Display an error alert
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error: Delete Course");
                alert.setHeaderText("Failed to delete the course.");
                alert.setContentText("The course has associated students.");

                alert.showAndWait();
            }
        }
    }

    /**
     * Retrieves a course from the course list based on the course ID.
     *
     * @param courseId The ID of the course to retrieve.
     * @return The Course object with the specified ID, or null if not found.
     */
    public Course getCourseById(int courseId) {
        for (Course course : courseList) {
            if (course.getId() == courseId) {
                return course;
            }
        }
        return null;
    }

    /**
     * Retrieves a course from the course list based on the course name.
     *
     * @param courseName The name of the course to retrieve.
     * @return The Course object with the specified name, or null if not found.
     */
    public Course getCourseByName(String courseName) {
        for (Course course : courseList) {
            if (course.getName().equals(courseName)) {
                return course;
            }
        }
        return null;
    }

    /**
     * Retrieves the ID of a course based on the course name.
     *
     * @param courseName The name of the course to retrieve the ID for.
     * @return The ID of the course with the specified name, or -1 if not found.
     */
    public int getCourseIdByName(String courseName) {
        for (Course course : courseList) {
            if (course.getName().equals(courseName)) {
                return course.getId();
            }
        }
        return -1; // Return -1 if the course with the specified name is not found
    }

    /**
     * Retrieves the last inserted ID from the database.
     *
     * @return The last inserted ID, or -1 if not found.
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
     * Returns the list of rooms.
     *
     * @return The ObservableList of rooms.
     */
    public ObservableList<Room> getRoomList() {
        return roomData.getRoomList();
    }

    /**
     * Searches for courses with names matching the specified search term.
     *
     * @param searchTerm The search term to match against course names.
     * @return The ObservableList of search results.
     */
    public ObservableList<Course> searchCoursesByName(String searchTerm) {
        ObservableList<Course> searchResults = FXCollections.observableArrayList();
        String searchQuery = "SELECT * FROM course WHERE LOWER(name) LIKE ?";
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(searchQuery);
            statement.setString(1, "%" + searchTerm.toLowerCase() + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int roomId = resultSet.getInt("room_id");

                Room room = roomData.getRoomById(roomId);

                Course course = new Course(id, name, room);
                searchResults.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }
}
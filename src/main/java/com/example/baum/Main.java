package com.example.baum;

import com.example.baum.DatabaseManager;
import com.example.baum.student.StudentPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Objects;

import com.example.baum.company.CompanyData;
import com.example.baum.company.CompanyPane;
import com.example.baum.course.CourseData;
import com.example.baum.course.CoursePane;
import com.example.baum.room.RoomData;
import com.example.baum.room.RoomPane;
import com.example.baum.student.StudentData;

/**
 * The main class for the Student Manager application.
 */
public class Main extends Application {

    private DatabaseManager databaseManager;
    private StudentData studentData;
    private CourseData courseData;
    private CompanyData companyData;
    private RoomData roomData;

    /**
     * Starts the Student Manager application.
     *
     * @param primaryStage The primary stage for the application.
     */
    @Override
    public void start(Stage primaryStage) {
        initializeDatabaseAndData();

        primaryStage.setTitle("Student Manager");

        TabPane tabPane = new TabPane();

        Tab studentTab = new Tab("Students", createStudentPane());
        Tab courseTab = new Tab("Courses", createCoursePane());
        Tab companyTab = new Tab("Company", createCompanyPane());
        Tab roomTab = new Tab("Room", createRoomPane());

        tabPane.getTabs().add(studentTab);
        tabPane.getTabs().add(courseTab);
        tabPane.getTabs().add(companyTab);
        tabPane.getTabs().add(roomTab);

        studentTab.setClosable(false);
        courseTab.setClosable(false);
        companyTab.setClosable(false);
        roomTab.setClosable(false);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/com/example/baum/style.css")).toExternalForm());

        primaryStage.show();
    }

    /**
     * Initializes the database and data objects.
     */
    private void initializeDatabaseAndData() {
        databaseManager = new DatabaseManager();
        databaseManager.connect();
        databaseManager.createTablesIfNotExists();

        roomData = new RoomData(databaseManager);
        courseData = new CourseData(databaseManager, roomData);
        companyData = new CompanyData(databaseManager);
        studentData = new StudentData(databaseManager, courseData, companyData);

        roomData.fetchRoomsFromDatabase();
        courseData.fetchCoursesFromDatabase();
        companyData.fetchCompaniesFromDatabase();
        studentData.fetchStudentsFromDatabase();
    }

    /**
     * Creates the student pane.
     *
     * @return The created student pane.
     */
    private Pane createStudentPane() {
        StudentPane studentPane = new StudentPane(studentData, courseData, companyData);
        studentPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return studentPane;
    }

    /**
     * Creates the course pane.
     *
     * @return The created course pane.
     */
    private Pane createCoursePane() {
        CoursePane coursePane = new CoursePane(courseData, roomData, studentData);
        coursePane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return coursePane;
    }

    /**
     * Creates the company pane.
     *
     * @return The created company pane.
     */
    private Pane createCompanyPane() {
        CompanyPane companyPane = new CompanyPane(companyData);
        companyPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return companyPane;
    }

    /**
     * Creates the room pane.
     *
     * @return The created room pane.
     */
    private Pane createRoomPane() {
        RoomPane roomPane = new RoomPane(roomData);
        roomPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return roomPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

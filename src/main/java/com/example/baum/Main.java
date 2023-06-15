package com.example.baum;

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
import com.example.baum.student.StudentPane;

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

        primaryStage.setScene(new Scene(tabPane, 800, 600));
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
        return new StudentPane(studentData, courseData, companyData);
    }

    /**
     * Creates the course pane.
     *
     * @return The created course pane.
     */
    private Pane createCoursePane() {
        return new CoursePane(courseData, roomData);
    }

    /**
     * Creates the company pane.
     *
     * @return The created company pane.
     */
    private Pane createCompanyPane() {
        return new CompanyPane(companyData);
    }

    /**
     * Creates the room pane.
     *
     * @return The created room pane.
     */
    private Pane createRoomPane() {
        return new RoomPane(roomData);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
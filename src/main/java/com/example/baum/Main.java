package com.example.baum;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Objects;
import java.util.prefs.Preferences;

import com.example.baum.company.CompanyData;
import com.example.baum.company.CompanyPane;
import com.example.baum.course.CourseData;
import com.example.baum.course.CoursePane;
import com.example.baum.room.RoomData;
import com.example.baum.room.RoomPane;
import com.example.baum.student.StudentData;
import com.example.baum.student.StudentPane;

/**
 * The main class that initializes and runs the Student Manager application.
 */
public class Main extends Application {

    private DatabaseManager databaseManager;
    private StudentData studentData;
    private CourseData courseData;
    private CompanyData companyData;
    private RoomData roomData;

    private TextField dbLinkField;
    private TextField usernameField;
    private PasswordField passwordField;

    private Preferences preferences;

    @Override
    /**
     * The entry point of the JavaFX application. It is called when the application
     * is launched.
     *
     * @param primaryStage the primary stage for the application
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Manager");

        preferences = Preferences.userRoot().node(getClass().getName());

        dbLinkField = new TextField();
        dbLinkField.setPromptText("MySQL Database Link");
        dbLinkField.setText(preferences.get("dbLink", ""));

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setText(preferences.get("username", ""));

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setText(preferences.get("password", ""));

        Button connectButton = new Button("Connect");
        connectButton.setOnAction(e -> connectToDatabase());

        VBox connectionSettingsBox = new VBox(10, dbLinkField, usernameField, passwordField, connectButton);
        connectionSettingsBox.setAlignment(Pos.CENTER);
        connectionSettingsBox.setPadding(new Insets(10));

        primaryStage.setScene(new Scene(connectionSettingsBox, 400, 200));
        primaryStage.show();

        connectToDatabase();
    }

    /**
     * Connects to the database using the provided connection settings.
     */
    private void connectToDatabase() {
        String dbLink = dbLinkField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        preferences.put("dbLink", dbLink);
        preferences.put("username", username);
        preferences.put("password", password);

        databaseManager = new DatabaseManager(dbLink, username, password);

        try {
            databaseManager.connect();
            databaseManager.createTablesIfNotExists();
            initializeData();
            showMainApplication();
        } catch (Exception e) {
            displayErrorAlert("Database Connection Error", "Failed to connect to the database.", e.getMessage());
        }
    }

    /**
     * Displays an error alert dialog with the specified title, header, and content.
     *
     * @param title   the title of the alert
     * @param header  the header text of the alert
     * @param content the content text of the alert
     */
    private void displayErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Initializes the data objects and fetches data from the database.
     */
    private void initializeData() {
        roomData = new RoomData(databaseManager);
        companyData = new CompanyData(databaseManager);
        courseData = new CourseData(databaseManager, roomData, companyData);
        studentData = new StudentData(databaseManager, courseData, companyData);

        roomData.fetchRoomsFromDatabase();
        courseData.fetchCoursesFromDatabase();
        companyData.fetchCompaniesFromDatabase();
        studentData.fetchStudentsFromDatabase();
    }

    /**
     * Shows the main application window with tabs for different functionality.
     */
    private void showMainApplication() {
        Stage mainStage = new Stage();
        mainStage.setTitle("Student Manager");

        TabPane tabPane = new TabPane();

        Tab studentTab = new Tab("Students", createStudentPane());
        Tab courseTab = new Tab("Courses", createCoursePane());
        Tab companyTab = new Tab("Company", createCompanyPane());
        Tab roomTab = new Tab("Room", createRoomPane());

        tabPane.getTabs().addAll(studentTab, courseTab, companyTab, roomTab);

        studentTab.setClosable(false);
        courseTab.setClosable(false);
        companyTab.setClosable(false);
        roomTab.setClosable(false);

        mainStage.setScene(new Scene(tabPane, 800, 600));
        mainStage.getScene().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/com/example/baum/style.css")).toExternalForm());

        mainStage.show();

        Stage primaryStage = (Stage) dbLinkField.getScene().getWindow();
        primaryStage.close();
    }

    /**
     * Creates the pane for the "Students" tab.
     *
     * @return the created pane
     */
    private Pane createStudentPane() {
        return new StudentPane(studentData, courseData, companyData);
    }

    /**
     * Creates the pane for the "Courses" tab.
     *
     * @return the created pane
     */
    private Pane createCoursePane() {
        return new CoursePane(courseData, roomData);
    }

    /**
     * Creates the pane for the "Company" tab.
     *
     * @return the created pane
     */
    private Pane createCompanyPane() {
        return new CompanyPane(companyData);
    }

    /**
     * Creates the pane for the "Room" tab.
     *
     * @return the created pane
     */
    private Pane createRoomPane() {
        return new RoomPane(roomData);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

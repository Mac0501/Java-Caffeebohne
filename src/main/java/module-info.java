module com.example.baum {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires org.controlsfx.controls;
    requires transitive java.sql;
    requires java.prefs;

    opens com.example.baum to javafx.fxml;
    opens com.example.baum.room to javafx.base;
    opens com.example.baum.company to javafx.base;
    opens com.example.baum.student to javafx.base;
    opens com.example.baum.course to javafx.base;

    exports com.example.baum;
    exports com.example.baum.room;
    exports com.example.baum.company;
    exports com.example.baum.student;
    exports com.example.baum.course;
}
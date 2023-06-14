module com.example.baum {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires org.controlsfx.controls;
    requires transitive java.sql;

    opens com.example.baum to javafx.fxml;
    exports com.example.baum;





}
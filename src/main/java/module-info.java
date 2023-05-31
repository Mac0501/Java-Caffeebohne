module com.example.baum {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
    requires java.sql;

    opens com.example.baum to javafx.fxml;
    exports com.example.baum;
}
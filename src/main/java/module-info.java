module com.example.baum {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
    requires java.sql;

    opens com.example.baum to javafx.fxml;
    exports com.example.baum;




    exports com.example.baum.test; // Export the necessary package(s) to javafx.graphics


    opens com.example.baum.test to javafx.fxml;



}
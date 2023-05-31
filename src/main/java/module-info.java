module com.example.baum {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
                            
    opens com.example.baum to javafx.fxml;
    exports com.example.baum;
}
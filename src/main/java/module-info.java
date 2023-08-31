module com.mycompany.csc311.homework4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson; 
    requires java.sql;
    requires java.base;
    opens com.mycompany.csc311.homework4 to javafx.fxml, com.google.gson;
    exports com.mycompany.csc311.homework4;
}

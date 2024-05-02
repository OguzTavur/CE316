module com.edeapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.edeapp to javafx.fxml;
    exports com.edeapp;
}
module com.edeapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.desktop;


    opens com.edeapp to javafx.fxml;
    exports com.edeapp;
}
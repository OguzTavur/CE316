package com.edeapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();
        Controller controller = new Controller();
        File file = new File("img.png");

        controller.setPrimaryStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 750);
        messageExchangePoint.setController(fxmlLoader.getController());
        stage.setTitle("EDE App");
        stage.getIcons().add(new Image(new FileInputStream(file.getAbsolutePath())));
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}
package com.edeapp;

import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class Controller {
    private File _InitialDirectory;
    private ArrayList<String> _acceptedExtensions = new ArrayList<>(Arrays.asList("txt", "java", "c", "cpp", "py", "json"));
    public TreeView<String> treeView;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onOpenButtonClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Project Directory");
        directoryChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath() + "/src/main/resources/ProjectFiles"));
        File selectedDirectory = directoryChooser.showDialog(new Popup());
        _InitialDirectory = selectedDirectory.getAbsoluteFile();

        TreeItem<String> root = new TreeItem<>(selectedDirectory.getAbsoluteFile().getName());
        root.setExpanded(true);
        treeView.setRoot(root);

        populateTreeView(selectedDirectory.getAbsoluteFile(), root);

        treeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    System.out.println("Double-clicked on: " + selectedItem.getValue());
                    // Add your double click handling code here
                }
            }
        });
    }

    // Recursive function to populate the TreeView with subfiles and subdirectories
    private void populateTreeView(File directory, TreeItem<String> parentItem) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<String> newItem = new TreeItem<>(file.getName());
                parentItem.getChildren().add(newItem);

                //String extension = file.getName().split("\\.")[1];
                //if (file.isFile() && _acceptedExtensions.contains(extension)) {

                //}

                if (file.isDirectory()) {
                    // If the file is a directory, recursively populate its subfiles and subdirectories
                    populateTreeView(file, newItem);
                }
            }
        }
    }
}
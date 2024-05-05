package com.edeapp;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Controller {
    @FXML
    private TabPane tabPane;
    private File _InitialDirectory;
    private ArrayList<String> _acceptedExtensions = new ArrayList<>(Arrays.asList("txt", "java", "c", "cpp", "py", "json"));
    public TreeView<FileItem> treeView;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onOpenButtonClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser(); // To chose only Directories
        directoryChooser.setTitle("Choose Project Directory");
        directoryChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath() + "/src/main/resources/ProjectFiles")); // Initial Path
        File selectedDirectory = directoryChooser.showDialog(new Popup()); // Popup is used to show Dialog
        _InitialDirectory = selectedDirectory.getAbsoluteFile(); // To store selected root directory

        TreeItem<FileItem> root = new TreeItem<>(new FileItem(selectedDirectory.getAbsoluteFile()));
        root.setExpanded(true);
        treeView.setRoot(root);

        populateTreeView(root);

        // To detect double-click on TreeView
        // Other functionalities like is clicked element is a file or folder, handled in redFile() function
        // TODO: Selected Files shouldn't be able to open at multiple tabs
        // TODO: If a file is selected and even though the user does not click on it double times the file opens because it is selected
        treeView.setOnMouseClicked(event -> {
            if (event.isPrimaryButtonDown()) {

            }
            if (event.getClickCount() == 2) {
                TreeItem<FileItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue() != null) {
                    System.out.println("Double-clicked on: " + selectedItem.getValue());
                    if (readFile(selectedItem.getValue().file()))
                        openTabWithFileData(selectedItem.getValue().toString());
                    // Add your double click handling code here
                }
            }
        });

    }

    // Recursive function to populate the TreeView with subfiles and subdirectories
    private void populateTreeView( TreeItem<FileItem> parentItem) {

        FileItem parentFileItem = parentItem.getValue();
        File parentFile = parentFileItem.file();

        if (parentFile.isDirectory()) {
            // List all files and directories within the parent directory
            File[] files = parentFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    FileItem fileItem = new FileItem(file);
                    TreeItem<FileItem> newItem = new TreeItem<>(fileItem);
                    parentItem.getChildren().add(newItem);
                    populateTreeView(newItem); // Recursive call to populate children
                }
            }
        }
    }

    /*
    This record is used for keeping the File but when we need to display this
     object in a TreeItem object we are able to pass the name of the file by
     overriding soString() method.
     */
    record FileItem(File file) {

        @Override
            public String toString() {
                return file.getName(); // Display file name in the TreeItem
            }
    }

    protected ArrayList<String> fileData = new ArrayList<>();

    // Reads the given File and stores all data in a ArrayList
    // It's return type is boolean it because we are determine are we going to open a new tab or not
    private boolean readFile(File rFile) {

        fileData.clear(); // To clear all data inside the array
        String extension;
        try {
            extension = rFile.getName().split("\\.")[1]; // Get the extension of the file
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Selected path has not an extension!");
            return false;
        }
        if (rFile.isFile() && _acceptedExtensions.contains(extension)) { // Check if the selected path is a File or a Directory
            try{
                FileReader fileReader = new FileReader(rFile);
                Scanner reader = new Scanner(fileReader);

                while (reader.hasNextLine()) // Read each line in the File and add to ArrayList
                    fileData.add(reader.nextLine()); // Later this data is going to shown on the Tab

                return true;

            } catch (FileNotFoundException e) {
                System.out.println("File" + rFile.getName() + " could not found");
                return false;
            }
        }
        else {
            System.out.println("Selected path is not a File or has not an accepted extension!");
            return false;
        }
    }

    // Creates a Tab with a TextArea with given data and puts it into TabPane
    private void openTabWithFileData(String tabHeader) {

        TextArea textArea = new TextArea();
        textArea.setEditable(false); // We don't want the TextArea be editable
        for (String row: fileData)
            textArea.appendText(row + "\n");

        Tab newTab = new Tab(tabHeader,textArea);
        tabPane.getTabs().add(newTab);
    }

    //Requirement 8: Comparing expected and student's result. Writing comparison result to CSV file
    private boolean compareResult(String studentOutputPath, String expectedOutputPath, String csvFilePath) {
        try {
            // Read student output and expected output files
            String studentOutput = new String(Files.readAllBytes(Paths.get(studentOutputPath)));
            String expectedOutput = new String(Files.readAllBytes(Paths.get(expectedOutputPath)));

            // Compare outputs
            boolean match = studentOutput.trim().equals(expectedOutput.trim());

            // Write comparison result to CSV file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, true))) {
                writer.append(studentOutputPath + "," + expectedOutputPath + "," + (match ? "Match" : "Mismatch") + "\n");
            }

            return match;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        /*
        String studentOutputPath = "src\\main\\resources\\ProjectFiles\\project1\\result.txt"; // Path to student output file
        String expectedOutputPath = "src\\main\\resources\\ProjectFiles\\project1\\expectedResult.txt"; // Path to expected output file
        String csvFilePath = "src\\main\\resources\\ProjectFiles\\project1\\comparison_results.csv"; // Path to CSV file to store results

        boolean match = compareResult(studentOutputPath, expectedOutputPath, csvFilePath);
        System.out.println("Comparison result: " + (match ? "Match" : "Mismatch"));
        */
    }
}
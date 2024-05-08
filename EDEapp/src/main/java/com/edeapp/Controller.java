package com.edeapp;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

public class Controller {
    @FXML
    public TableView tableView;
    private Stage popup;
    private Stage primaryStage;
    @FXML
    private TabPane tabPane;
    private File _InitialDirectory;
    private ArrayList<String> _acceptedExtensions = new ArrayList<>(Arrays.asList("txt", "java", "c", "cpp", "py", "json", "csv"));
    public TreeView<FileItem> treeView;
    @FXML
    private Label welcomeText;


    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onNewProjectButtonClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("createProject.fxml"));
        fxmlLoader.setController(new PopupController());
        // Scene
        setPopup(new Stage());
        popup.initOwner(getPrimaryStage());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("New Project");
        popup.setResizable(false);
        popup.setScene(fxmlLoader.load());
        popup.showAndWait();
    }



    @FXML
    protected void onOpenButtonClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser(); // To chose only Directories
        directoryChooser.setTitle("Choose Project Directory");
        directoryChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath() + "/src/main/resources/ProjectFiles")); // Initial Path
        File selectedDirectory = directoryChooser.showDialog(new Popup()); // Popup is used to show Dialog
        if (selectedDirectory == null)// Check if any directory is selected
            return;
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
        /* TableView of students it will be moved to its method
        tableView.getColumns().clear();

        ObservableList<Student> data =
                FXCollections.observableArrayList(
                        new Student("20200602013", "❌"),
                        new Student("2", "✔"),
                        new Student("3", "✔")
                );


        TableColumn<Student, String> idColumn = new TableColumn<>("Student ID");
        idColumn.setMinWidth(120);
        idColumn.setCellValueFactory(
                new PropertyValueFactory<Student,String>("id")
        );

        TableColumn<Student, Boolean> resultColumn = new TableColumn<>("Result");
        resultColumn.setMinWidth(50);
        resultColumn.setCellValueFactory(
                new PropertyValueFactory<Student,Boolean>("result")
        );

        tableView.getColumns().addAll(idColumn, resultColumn);

        // Sample data
        tableView.setItems(data);

         */

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
     overriding toString() method.
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

                fileData.add(System.getenv("JAVA_HOME"));


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

        /* Will be put to the method that will run this method
        String studentOutputPath = "src\\main\\resources\\ProjectFiles\\project1\\result.txt"; // Path to student output file
        String expectedOutputPath = "src\\main\\resources\\ProjectFiles\\project1\\expectedResult.txt"; // Path to expected output file
        String csvFilePath = "src\\main\\resources\\ProjectFiles\\project1\\comparison_results.csv"; // Path to CSV file to store results

        boolean match = compareResult(studentOutputPath, expectedOutputPath, csvFilePath);
        System.out.println("Comparison result: " + (match ? "Match" : "Mismatch"));
        */
    }

    public static File createJsonConfiguration(String language, String inputCodePath, String expectedOutputPath) throws IOException {
        String compileCommand = "javac {sourceFile}";
        String runCommand = "java {mainClass}";
        String testInput = "input.txt";

        String json = "{\n" +
                "    \"compilerConfig\": {\n" +
                "        \"language\": \"" + language + "\",\n" +
                "        \"compileCommand\": \"" + compileCommand + "\",\n" +
                "        \"runCommand\": \"" + runCommand + "\"\n" +
                "    },\n" +
                "    \"testConfig\": {\n" +
                "        \"testInput\": \"" + testInput + "\",\n" +
                "        \"expectedOutput\": \"" + expectedOutputPath + "\"\n" +
                "    }\n" +
                "}";

        String pathWithoutFile = inputCodePath.substring(0, inputCodePath.lastIndexOf("\\"));
        File configFile = new File(pathWithoutFile + "/config.json");
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(json);
        }

        return configFile;

        /* Will be put to the method that will run this method
        String language = "Java";
        String inputCodePath = "src\\main\\resources\\ProjectFiles\\project1\\result.txt";
        String expectedOutputPath = "output.txt";

        try {
            File configFile = createJsonConfiguration(language, inputCodePath, expectedOutputPath);
            System.out.println("JSON configuration file created: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
    @FXML
    protected void den1() throws Exception {
        System.out.println(runSourceCode("EDEapp\\build\\resources\\main\\ProjectFiles\\project1\\config.json",
                "EDEapp\\build\\resources\\main\\ProjectFiles\\project1\\main.c","EDEapp\\build\\resources\\main\\ProjectFiles\\project1\\main.c"));
    }
    public String runSourceCode(String configFilePath, String sourceFile, String mainClass) throws Exception {
        // Read the JSON file
        String jsonText = new String(Files.readAllBytes(Path.of(configFilePath)));

        // Parse the JSON
        JSONObject json = new JSONObject(jsonText);

        // Get the language and commands
        System.out.println(json);
        JSONObject compilerConfig = json.getJSONObject("compilerConfig");
        String language = compilerConfig.getString("language");
        String compileCommand = compilerConfig.getString("compileCommand");
        String runCommand = compilerConfig.getString("runCommand");

        // Get the compiler path from the environment variable
        // TODO: 8.05.2024 path should be checked
        /*String compilerPath;
        if ("Java".equalsIgnoreCase(language)) {
            compilerPath = System.getenv("JAVA_HOME");
            if (compilerPath == null) {
                return "JAVA_HOME is not set";
            }
            compilerPath += File.separator + "bin" + File.separator + "javac";
        } else if ("C".equalsIgnoreCase(language)) {
            compilerPath = System.getenv("GCC"); // Replace with the correct environment variable for C compiler
            if (compilerPath == null) {
                return "GCC is not set"; // Replace with the correct error message for C compiler
            }
        } else {
            return "Unsupported language: " + language;
        }

         */

        // Replace {sourceFile} and {mainClass} in the commands with the actual values
        compileCommand = compileCommand.replace("{sourceFile}", sourceFile);
        runCommand = runCommand.replace("{mainClass}", mainClass);

        // Compile the source
        ProcessBuilder compileProcessBuilder = new ProcessBuilder((compileCommand).split(" "));
        Process compileProcess = compileProcessBuilder.start();
        compileProcess.waitFor();

        // Check if the compilation was successful
        if (compileProcess.exitValue() != 0) {
            return "Compilation failed";
        }

        // Run the compiled code
        ProcessBuilder runProcessBuilder = new ProcessBuilder(runCommand.split(" "));
        Process runProcess = runProcessBuilder.start();
        runProcess.waitFor();

        // Check if the run was successful
        if (runProcess.exitValue() != 0) {
            return "Run failed";
        }

        // Get the output of the run
        BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Return the output as a string
        return output.toString();
    }


    public Stage getPopup() {
        return popup;
    }
    public void setPopup(Stage popup) {
        this.popup = popup;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
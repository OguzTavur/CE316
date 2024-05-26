package com.edeapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.skin.TreeViewSkin;
import javafx.scene.input.MouseButton;
import javafx.stage.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import org.json.JSONArray;
import org.json.JSONObject;

public class Controller {
    @FXML
    public TableView tableView;
    public SplitPane splitPane;
    private Stage popup;
    private Stage primaryStage;
    @FXML
    private TabPane tabPane;
    private File _InitialDirectory;
    private final ArrayList<String> _acceptedExtensions = new ArrayList<>(Arrays.asList("txt", "java", "c", "cpp", "py", "json", "csv"));
    public TreeView<FileItem> treeView;
    private File openWithFilePath;


    @FXML
    protected void onNewProjectButtonClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("createProject.fxml"));
        MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();

        // Scene
        setPopup(new Stage());
        popup.initOwner(getPrimaryStage());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("New Project");
        popup.setResizable(false);
        popup.setScene(fxmlLoader.load());
        messageExchangePoint.setPopupController(fxmlLoader.getController());
        popup.showAndWait();
    }
    @FXML
    protected void onEditConfigButtonClicked() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("editConfig.fxml"));
        MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();
        if (messageExchangePoint.getPopupController() != null) {
            messageExchangePoint.setPopupController(null);
        }

        // Scene
        setPopup(new Stage());
        popup.initOwner(getPrimaryStage());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Edit Config File");
        popup.setResizable(false);
        popup.setScene(fxmlLoader.load());
        // This comes after load() function. The reason behind of this, if we set the controller before load it the PopupController will store null
        messageExchangePoint.setPopupController(fxmlLoader.getController());

        if (openWithFilePath != null) {
            messageExchangePoint.getPopupController().configFilePath.setText(openWithFilePath.getAbsolutePath());
            messageExchangePoint.getPopupController().extractJson(openWithFilePath);
        }
        popup.showAndWait();
    }

    @FXML
    protected void onCreateConfigButtonClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("createConfig.fxml"));
        MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();

        // Scene
        setPopup(new Stage());
        popup.initOwner(getPrimaryStage());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Create Configuration File");
        popup.setResizable(false);
        popup.setScene(fxmlLoader.load());
        // This comes after load() function. The reason behind of this, if we set the controller before load it the PopupController will store null
        messageExchangePoint.setPopupController(fxmlLoader.getController());
        popup.showAndWait();
    }

    @FXML
    protected void onDeleteConfigButtonClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("deleteConfig.fxml"));
        MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();

        // Scene
        setPopup(new Stage());
        popup.initOwner(getPrimaryStage());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Delete Configuration File");
        popup.setResizable(false);
        popup.setScene(fxmlLoader.load());
        // This comes after load() function. The reason behind of this, if we set the controller before load it the PopupController will store null
        messageExchangePoint.setPopupController(fxmlLoader.getController());
        popup.showAndWait();
    }

    @FXML
    protected void onCloseButtonClicked(){
        treeView.setRoot(null);
        tabPane.getTabs().clear();
        tableView.getColumns().clear();
        tableView.getItems().clear();
    }
    @FXML
    protected void onQuitButtonClicked(){
        System.exit(0);
    }



    @FXML
    protected void closePopUp(){
        popup.close();// Closes the current active popup
        MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();
        messageExchangePoint.setPopupController(null); // To avoid any possible conflict
    }

    protected void createNewProject(String projectDirectory, String projectName, boolean importConfig, String customConfigName, String language, String directoryThatContainsProjectZips, String configFilePath, String arguments, String expectedOutput) throws IOException {
        // Create the destination directory if it doesn't exist
        File createNewProjectDirectory = new File(projectDirectory + "\\" + projectName);
        if (!createNewProjectDirectory.exists()) {
            if(createNewProjectDirectory.mkdirs()){
                System.out.println("Directory is created!");
                System.out.println(createNewProjectDirectory.getAbsolutePath());
                _InitialDirectory = createNewProjectDirectory;
            }
        }


        // If we have already a JSON Config File then we don't need to create one.
        if (!importConfig) {
            saveFileToGivenDirectory(createJsonConfiguration(customConfigName,language,arguments,expectedOutput),projectDirectory + "\\" + projectName);
        }
        else {
            File configFile = new File(configFilePath);
            Path sourcePath = Path.of(configFilePath);
            Path destinationPath = Path.of(projectDirectory + "\\" + projectName + "\\" + configFile.getName());
            try {
                // Perform the copy operation
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File copied successfully from " + configFilePath + " to " + projectDirectory + "\\" + projectName);
            } catch (IOException e) {
                System.out.println("Failed to copy file: " + e.getMessage());
            }
        }

        File relocateFolderThatContainsZipFiles = new File(directoryThatContainsProjectZips);
        File[] files = relocateFolderThatContainsZipFiles.listFiles();
        assert files != null;
        for (File file: files){
            if (file.renameTo(new File(projectDirectory + "\\" + projectName + "\\" + file.getName()))) {
                System.out.println("File move to " + file.getAbsolutePath());
            }
        }
//        if(relocateFolderThatContainsZipFiles.renameTo(new File(projectDirectory + "\\" + projectName + "\\" + relocateFolderThatContainsZipFiles.getName())))
//            System.out.println("File move to " + relocateFolderThatContainsZipFiles.getAbsolutePath());

        TreeItem<FileItem> root = new TreeItem<>(new FileItem(createNewProjectDirectory.getAbsoluteFile()));
        root.setExpanded(true);
        treeView.setRoot(root);

        populateTreeView(root);// Adding all other Sub-Items to the TreeView
        addFunctionalityToTreeItems();// Adds the functionality to the TreeItems
    }



    @FXML
    protected void onOpenButtonClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser(); // To chose only Directories
        directoryChooser.setTitle("Choose Project Directory");
        directoryChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath() + "/ProjectFiles")); // Initial Path
        File selectedDirectory = directoryChooser.showDialog(new Popup()); // Popup is used to show Dialog
        if (selectedDirectory == null)// Check if any directory is selected
            return;
        _InitialDirectory = selectedDirectory.getAbsoluteFile(); // To store selected root directory

        tableView.getColumns().clear();
        tableView.getItems().clear();

        TreeItem<FileItem> root = new TreeItem<>(new FileItem(selectedDirectory.getAbsoluteFile()));
        root.setExpanded(true);
        treeView.setRoot(root);

        populateTreeView(root);// Adding all other Sub-Items to the TreeView

        addFunctionalityToTreeItems();// Adds the functionality to the TreeItems

    }

    //Requirement 8: Comparing expected and student's result. Writing comparison result to CSV file
    @FXML
    protected void onCheckButtonClicked() throws IOException {
        if (treeView == null)
            return;
        if (_InitialDirectory == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Project Not Selected", "Please open or create a project before check.");
            return;
        }
        checkOutputsOfStudents(_InitialDirectory.getAbsolutePath());
        String csvFilePath = _InitialDirectory.getAbsolutePath() + "/StudentResults.csv";


        //TableView of students it will be moved to its method
        tableView.getItems().clear();
        tableView.getColumns().clear();
        TableColumn<Student, String> idColumn = new TableColumn<>("Student ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, Boolean> resultColumn = new TableColumn<>("Result");
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        resultColumn.setCellFactory(column -> new TextFieldTableCell<>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "✔" : "❌");
                }
            }
        });

        tableView.getColumns().addAll(idColumn, resultColumn);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    boolean result = (parts[1].equals("Match"));
                    tableView.getItems().add(new Student(parts[0], result));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshTreeView();
    }

    // ContextMenu that will contain options
    private ContextMenu treeViewContextMenu;
    private void addFunctionalityToTreeItems(){

        // Other functionalities like is clicked element is a file or folder, handled in redFile() function
        // TODO: Selected Files shouldn't be able to open at multiple tabs
        treeView.setOnMouseClicked(event -> {

            // Define the regex pattern to match content inside single quotes
            Pattern pattern = Pattern.compile("'null'");

            // Create a matcher object
            Matcher matcher = pattern.matcher(event.getTarget().toString());

            if (matcher.find()) {
                treeView.getSelectionModel().clearSelection();
                return;
            }

            if (treeViewContextMenu != null) {
                treeViewContextMenu.hide();
            }
            // To detect double-click on TreeView
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                TreeItem<FileItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue() != null) {
                    //System.out.println("Double-clicked on: " + selectedItem.getValue());
                    if (readFile(selectedItem.getValue().file()))
                        openTabWithFileData(selectedItem.getValue().toString());
                }
            }

            // This if statement is used for create option view when the user clicks secondary mouse click
            if (event.getButton() == MouseButton.SECONDARY) {
                TreeItem<FileItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue().file().isFile()){
                    ContextMenu contextMenu = contextMenuBuilder(selectedItem.getValue().toString().split("\\.")[1],selectedItem.getValue().file().isFile(),selectedItem);
                    if (contextMenu == null) {
                        return;
                    }
                    treeViewContextMenu = contextMenu;
                    contextMenu.show(treeView, event.getScreenX(), event.getScreenY());
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
     overriding toString() method.
     */
    record FileItem(File file) {

        @Override
            public String toString() {
                return file.getName(); // Display file name in the TreeItem
            }


    }
    // This ArrayList is used for save file text that will be used after printing in GUI
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

    protected File createJsonConfiguration(String customFileName, String language, String arguments, String expectedOutput) throws IOException {
        String compileCommand;
        String runCommand;

        if ("Java".equalsIgnoreCase(language)) {
            compileCommand = "javac";
            runCommand = "java";
        } else if ("C".equalsIgnoreCase(language)) {
            compileCommand = "gcc";
            runCommand = "";
        } else if ("Python".equalsIgnoreCase(language)) {
            compileCommand = "";
            runCommand = "python";
        } else if ("C++".equalsIgnoreCase(language)) {
            compileCommand = "g++";
            runCommand = "";
        } else {
            compileCommand = "";
            runCommand = "";
        }


        String json = "{\n" +
                "    \"compilerConfig\": {\n" +
                "        \"language\": \"" + language + "\",\n" +
                "        \"compileCommand\": \"" + compileCommand + "\",\n" +
                "        \"runCommand\": \"" + runCommand + "\"\n" +
                "    },\n" +
                "    \"projectConfig\": {\n" +
                "        \"argument\": [" ;

        String[] argArray = arguments.split(",");
        if (!Objects.equals(argArray[0], "")) {
            for (String argument : argArray) {
                json += "\n           \"" + argument + "\",";
            }
            json = json.substring(0,json.length()-1);
        }
        json += "\n        ],\n        \"expectedOutput\": \"" + expectedOutput + "\"\n" +
                "    }\n" +
                "}";

        System.out.println(json);
        // Replace with the path where you want to save the config file
        String configFilePath = customFileName + ".json";
        File configFile = new File(configFilePath);
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(json);
        }

        return configFile;
    }

    protected void deleteFileOrDirectory(File file){
        if (file.delete()) {
            System.out.println("Deleted the file: " + file.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
        refreshTreeView();
    }

    @FXML
    protected void editJsonConfiguration(String configFilePath, String language, String arguments, String expectedOutput) throws IOException {
        String compCommand = "";
        String runCommand = "";

        JSONObject compilerConfig = new JSONObject();
        compilerConfig.put("language", language);

        if (language.equals("Java")){
            compCommand = "javac";
            runCommand = "java";
        } else if (language.equals("C")) {
            compCommand = "gcc";
            runCommand = "";
        } else if (language.equals("Python")) {
            compCommand = "";
            runCommand = "python";
        } else if (language.equals("C++")) {
            compCommand = "g++";
            runCommand = "";
        }
        compilerConfig.put("compileCommand", compCommand);
        compilerConfig.put("runCommand", runCommand);

        // Create the projectConfig object
        JSONObject projectConfig = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        String[] values = arguments.split(",");

        for (String value : values) {
            jsonArray.put(value.trim()); // Trim to remove leading/trailing spaces
        }

        projectConfig.put("argument", jsonArray);
        projectConfig.put("expectedOutput", expectedOutput);

        JSONObject json = new JSONObject();
        json.put("compilerConfig", compilerConfig);
        json.put("projectConfig", projectConfig);

        // Format the JSON string for better readability
        String formattedJson = json.toString(4); // Indent with 4 spaces

        Files.write(Paths.get(configFilePath), formattedJson.getBytes());
    }

    protected void saveFileToGivenDirectory(File file, String destinationPath){
        File relocateJSONFile = new File(file.getAbsolutePath());
        if(relocateJSONFile.renameTo(new File(destinationPath, relocateJSONFile.getName())))
            System.out.println("File Moved to " + relocateJSONFile.getAbsolutePath());
        else System.out.println("File could not move!");
    }

    protected ArrayList<Student> queryStudents(String filePath) throws Exception {
        File configFile = new File(getJsonFilePath(filePath));
        String configFilePath = configFile.getAbsolutePath();
        ArrayList<Student> students = new ArrayList<>();

        File directory =new File(filePath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File[] sourceFiles = file.listFiles();

                    assert sourceFiles != null;
                    for(File sourceFile: sourceFiles){
                        if (sourceFile.getName().endsWith(".java")){
                            Student student = javaRun(configFilePath,sourceFile.getAbsolutePath());
                            student.setId(file.getName());
                            students.add(student);
                        }else if (sourceFile.getName().endsWith(".c")){
                            Student student = cRun(configFilePath,sourceFile.getAbsolutePath());
                            student.setId(file.getName());
                            students.add(student);
                        }else if (sourceFile.getName().endsWith(".py")){
                            Student student = pythonRun(configFilePath,sourceFile.getAbsolutePath());
                            student.setId(file.getName());
                            students.add(student);
                        } else if (sourceFile.getName().endsWith(".cpp")) {
                            Student student = cppRun(configFilePath,sourceFile.getAbsolutePath());
                            student.setId(file.getName());
                            students.add(student);
                        }
                    }
                }
            }
        }
        return students;
    }

    protected void writeToCSV(FileWriter writer, String studentId, boolean result){
        try  {
            // Write CSV records
            writer.append(studentId);
            writer.append(",");
            if (result)
                writer.append("Match");
            else
                writer.append("MisMatch");
            writer.append("\n");

            writer.flush();
            System.out.println("CSV file created successfully.");
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
        }
    }

    protected void checkOutputsOfStudents(String projectPath) throws IOException {
        String configOfProject = getJsonFilePath(projectPath);
        JSONObject projectConfig = getObject(configOfProject, "projectConfig");
        String expOutput = projectConfig.getString("expectedOutput");
        String pathOfCSV = projectPath + "/StudentResults.csv";
        FileWriter writer = new FileWriter(pathOfCSV);

        try {
            ArrayList<Student> studentList = queryStudents(projectPath);
            for (Student student: studentList) {
                if (student.getOutput().equals(expOutput)) //student.getOutput().trim().equals(expOutput.trim())
                    student.setResult(true);
                else
                    student.setResult(false);

                writeToCSV(writer, student.getId(),student.getResult());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getObject(String configFilePath,String objectName) throws IOException {

        String jsonText = new String(Files.readAllBytes(Path.of(configFilePath)));


        JSONObject json = new JSONObject(jsonText);


        System.out.println(json);
        return json.getJSONObject(objectName);
    }

    public Student javaRun(String configFilePath, String sourceFile){

        JSONObject compilerConfig = null;
        JSONObject projectConfig = null;

        try {
            compilerConfig = getObject(configFilePath,"compilerConfig");
            projectConfig = getObject(configFilePath,"projectConfig");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String jCompile = compilerConfig.getString("compileCommand");
        String runCommand = compilerConfig.getString("runCommand");

        String[] compileCommand = {jCompile,sourceFile};


        JSONArray arguments = projectConfig.getJSONArray("argument");

        String[] executeCommand = new String[arguments.length()+2];
        executeCommand[0] = runCommand;
        executeCommand[1] = sourceFile;
        for (int i = 0; i < arguments.length(); i++) {
            executeCommand[i+2] = arguments.getString(i);
        }

        return runSourceCode(compileCommand,executeCommand);


    }

    public Student cRun(String configFilePath, String sourceFile){
        File cFile = new File(sourceFile);
        String fileName = cFile.getName();
        JSONObject compilerConfig = null;
        JSONObject projectConfig = null;
        try {
            compilerConfig = getObject(configFilePath,"compilerConfig");
            projectConfig = getObject(configFilePath,"projectConfig");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String substring = fileName.substring(0, fileName.length() - 2);
        String[] compileCommand = {compilerConfig.getString("compileCommand"),sourceFile,"-o", substring};
        JSONArray arguments = projectConfig.getJSONArray("argument");
        String[] executeCommand = new String[arguments.length()+1];
        executeCommand[0] = substring;
        for (int i = 0; i < arguments.length(); i++) {
            executeCommand[i+1] = arguments.getString(i);
        }

        return runSourceCode(compileCommand,executeCommand);

    }

    public Student pythonRun(String configFilePath, String sourceFile){
        //python -m py_compile
        JSONObject compilerConfig = null;
        JSONObject projectConfig = null;
        try {
            compilerConfig = getObject(configFilePath,"compilerConfig");
            projectConfig = getObject(configFilePath,"projectConfig");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONArray arguments = projectConfig.getJSONArray("argument");

        String[] compileCommand = {compilerConfig.getString("compileCommand")};
        String[] executeCommand = new String[arguments.length()+2];
        executeCommand[0] = compilerConfig.getString("runCommand");
        executeCommand[1] = sourceFile;
        for (int i = 0; i < arguments.length(); i++) {
            executeCommand[i+2] = arguments.getString(i);
        }

        return runSourceCode(compileCommand,executeCommand);


    }

    public Student cppRun(String configFilePath, String sourceFile){
        File cppFile = new File(sourceFile);
        String fileName = cppFile.getName();
        JSONObject compilerConfig;
        JSONObject projectConfig;

        try {
            compilerConfig = getObject(configFilePath, "compilerConfig");
            projectConfig = getObject(configFilePath, "projectConfig");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Extract the base name (without path) and remove .cpp extension
        String executableName = fileName.substring(0, fileName.length() - 4);

        String[] compileCommand = {
                compilerConfig.getString("compileCommand"),
                sourceFile,
                "-o",
                executableName
        };

        JSONArray arguments = projectConfig.getJSONArray("argument");
        String[] executeCommand = new String[arguments.length() + 1];
        executeCommand[0] = executableName; // Add ./ prefix
        for (int i = 0; i < arguments.length(); i++) {
            executeCommand[i + 1] = arguments.getString(i);
        }

        return runSourceCode(compileCommand,executeCommand);
    }

    public Student runSourceCode(String[] compilerCommand,String[] executeCommand) {
        
        Student student = new Student();
        boolean isCompiled = true;
        boolean isRan = true;
        try {
            if (!Objects.equals(executeCommand[0], "python")) {
                // Compile the source
                ProcessBuilder compileProcessBuilder = new ProcessBuilder(compilerCommand);
                Process compileProcess = compileProcessBuilder.start();
                compileProcess.waitFor();

                // Check if the compilation was successful
                if (compileProcess.exitValue() != 0) {
                    System.out.println("comp failed");
                    isCompiled = false;
                }
            }

            // Run the compiled code
            ProcessBuilder runProcessBuilder = new ProcessBuilder(executeCommand);
            Process runProcess = runProcessBuilder.start();
            runProcess.waitFor();

            // Check if the run was successful
            if (runProcess.exitValue() != 0) {
                System.out.println("run failed");
                isRan = false;
            }

            // Get the output of the run
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line1;
            while ((line1 = reader1.readLine()) != null) {
                output.append(line1).append("\n");
            }
            student.setCompiled(isCompiled);
            student.setRan(isRan);
            student.setOutput(output.toString());


            return student;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void unZipFile(File zipFile) throws IOException {
        String destinationDir = zipFile.getParent() + File.separator + zipFile.getName().replaceAll("\\.zip$", "");
        byte[] buffer = new byte[1024];

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(destinationDir + File.separator + fileName);

                // Create directories if necessary
                boolean isDirectoryCreated = new File(newFile.getParent()).mkdirs();

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        if (zipFile.delete()) {
            System.out.println("Zip is unzipped and deleted!");
        }else System.out.println("Zip could not be deleted!");
        refreshTreeView();
    }

    protected String getJsonFilePath(String dirPath) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dirPath))) {
            for (Path path : stream) {
                if (path.toString().endsWith(".json")) {
                    return path.toString();
                }
            }
        }
        return null;
    }

    public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private ContextMenu contextMenuBuilder(String fileExtension, boolean isFile, TreeItem<FileItem> selectedItem){

        if (isFile) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem openMenuItem = new MenuItem("Open");
            openMenuItem.setOnAction(event1 -> {
                System.out.println("Opening file...");
                if (readFile(selectedItem.getValue().file()))
                    openTabWithFileData(selectedItem.getValue().toString());
            });
            MenuItem deleteMenuItem = new MenuItem("Delete");
            deleteMenuItem.setOnAction(event1 -> {
                System.out.println("Deleting file...");
                deleteFileOrDirectory(selectedItem.getValue().file());
            });
            MenuItem editMenuItem = new MenuItem("Edit");
            editMenuItem.setOnAction(event1 -> {
                try {
                    openWithFilePath = selectedItem.getValue().file();
                    onEditConfigButtonClicked();
                    openWithFilePath = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Editing file...");
            });
            MenuItem unzipMenuItem = new MenuItem("Unzip");
            unzipMenuItem.setOnAction(event1 -> {
                System.out.println("Unzipping file...");
                try {
                    unZipFile(selectedItem.getValue().file());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            if (fileExtension.equalsIgnoreCase("json")) {
                contextMenu.getItems().addAll(openMenuItem, editMenuItem, deleteMenuItem);
            } else if (fileExtension.equalsIgnoreCase("zip")) {
                contextMenu.getItems().addAll(unzipMenuItem, deleteMenuItem);
            } else {
                contextMenu.getItems().addAll(openMenuItem, deleteMenuItem);
            }
            return contextMenu;
        }

        return null;
    }

    protected void refreshTreeView(){
        if (_InitialDirectory == null)
            return;
        TreeItem<FileItem> root = new TreeItem<>(new FileItem(_InitialDirectory));
        root.setExpanded(true);
        treeView.setRoot(root);

        populateTreeView(root);// Adding all other Sub-Items to the TreeView
        addFunctionalityToTreeItems();// Adds the functionality to the TreeItems

    }

    @FXML
    protected void onAboutClicked() {
        String contentText = "- Harun Onur\n- Ege Deniz Yasar\n- Ali Boztepe\n- Oguz Kaan Tavur" +
                "\n\nThis application is in development in the scope of CE 316 - Programming Paradigms as the  course project.";
        showAlert(Alert.AlertType.INFORMATION, "About", "Software Development Team", contentText);
    }

    /*
    This is the alert popup that will be given if there will be warning, required place to be filled, information etc.
    These are the alertTypes:
    1 - Alert.AlertType.NONE: Represents an alert with no predefined type.
    2 - Alert.AlertType.INFORMATION: Represents an information alert.
    3 - Alert.AlertType.WARNING: Represents a warning alert.
    4 - Alert.AlertType.ERROR: Represents an error alert.
    5 - Alert.AlertType.CONFIRMATION: Represents a confirmation alert, typically used to confirm an action with the user.
     */

    protected void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText){

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
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
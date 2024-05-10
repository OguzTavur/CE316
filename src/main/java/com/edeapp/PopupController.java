package com.edeapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PopupController {

    public RadioButton radioNew;
    public RadioButton radioImport;
    public VBox importConfigInfo;
    public VBox newConfigInfo;
    public TextField configFilePath;
    public TextField projectName;
    public TextField zipFilePath;
    public ChoiceBox languageChoice;
    public TextField projectArguments;
    public TextField projectDestinationPath;
    public Button configFilePathButton;
    public Button projectDestinationPathButton;
    public Button zipFilePathButton;
    public TextField projectDestinationPathImport;
    public Button projectDestinationPathImportButton;
    public TextArea expectedOutput;
    public TextField configFileName;
    public TextField destinationPath;
    public Button destinationPathButton;
    public VBox editConfigVBox;
    public TextField compCommand;
    public TextField runCommand;
    public Button configFilePathEditButton;

    @FXML
    protected void onRadioButtonClicked(ActionEvent event){
        if (event.getSource().equals(radioNew)) {
            radioNew.setSelected(true);
            radioImport.setSelected(false);
            importConfigInfo.setVisible(false);
            newConfigInfo.setVisible(true);
        }
        else {
            radioImport.setSelected(true);
            radioNew.setSelected(false);
            newConfigInfo.setVisible(false);
            importConfigInfo.setVisible(true);
        }
    }

    @FXML
    protected void onCreateButtonClicked() throws IOException {
        if (radioNew.isSelected()) {
            if (checkInputAreas(false)) {
                System.out.println("Project Name: " + projectName.getText());
                System.out.println("Language: " + languageChoice.getValue());
                System.out.println("Project Arguments: " + projectArguments.getText());
                System.out.println("Project Destination Path: " + projectDestinationPath.getText());
                System.out.println("Zip File Path: " + zipFilePath.getText());
                MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();
                messageExchangePoint.getController().closePopUp();
                messageExchangePoint.getController().createNewProject(projectDestinationPath.getText(),projectName.getText(),false,projectName.getText(),languageChoice.getValue().toString(),zipFilePath.getText(),null,projectArguments.getText(),expectedOutput.getText());
            }
        }
        else if (radioImport.isSelected()) {
            if (checkInputAreas(true)) {
                System.out.println("Project Name: " + projectName.getText());
                System.out.println("Config File Path: " + configFilePath.getText());
                System.out.println("Project Destination Path: " + projectDestinationPathImport.getText());
                System.out.println("Zip File Path: " + zipFilePath.getText());
                MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();
                messageExchangePoint.getController().createNewProject(projectDestinationPathImport.getText(),projectName.getText(),true,null,null,zipFilePath.getText(),configFilePath.getText(),null,null);
                messageExchangePoint.getController().closePopUp();
            }
        }
        // else TODO: Add here state information after
    }
    @FXML
    protected void onCreateButtonClickedNewConfig() throws IOException {
        if (checkInputAreasForCreateConfigFile()) {
            System.out.println("Configuration File Name: " + configFileName.getText());
            System.out.println("Program Language: " + languageChoice.getValue());
            System.out.println("Project Arguments: " + projectArguments.getText());
            System.out.println("Expected Output: " + expectedOutput.getText());
            System.out.println("Destination Path: " + destinationPath.getText());
            MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();
            messageExchangePoint.getController()
                    .saveFileToGivenDirectory(messageExchangePoint.getController()
                            .createJsonConfiguration(configFileName.getText(),languageChoice.getValue().toString(),projectArguments.getText(),expectedOutput.getText()),destinationPath.getText());
            messageExchangePoint.getController().closePopUp();
        }
        // TODO: Add something in here maybe later
    }

    @FXML
    protected void onSaveButtonClicked() throws IOException {
        if (checkInputAreasForEditConfigFile()) {
            System.out.println("Configuration File Path: " + configFilePath.getText());
            System.out.println("Program Language: " + languageChoice.getValue());
            System.out.println("Project Arguments: " + projectArguments.getText());
            System.out.println("Expected Output: " + expectedOutput.getText());


            MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();
            messageExchangePoint.getController().editJsonConfiguration(configFilePath.getText(),languageChoice.getValue().toString(),compCommand.getText(),runCommand.getText(),projectArguments.getText(),expectedOutput.getText());
            messageExchangePoint.getController().closePopUp();
        }
    }


    private boolean checkInputAreas(boolean importConfig) {
        if (importConfig) {
            return !projectName.getText().isEmpty() && !configFilePath.getText().isEmpty() && !expectedOutput.getText().isEmpty() && !zipFilePath.getText().isEmpty();
        }
        else return !projectName.getText().isEmpty() && !projectDestinationPath.getText().isEmpty() && !expectedOutput.getText().isEmpty() && !zipFilePath.getText().isEmpty();
    }

    private boolean checkInputAreasForCreateConfigFile() {
        return !configFileName.getText().isEmpty() && !expectedOutput.getText().isEmpty() && !destinationPath.getText().isEmpty();
    }

    private boolean checkInputAreasForEditConfigFile() {
        return !configFilePath.getText().isEmpty() && !expectedOutput.getText().isEmpty();
    }

    @FXML
    protected void onExploreButtonClicked(ActionEvent event){
        if (event.getSource() == configFilePathButton) {
            File file = get_JSONFilePath();
            if (file != null) {
                configFilePath.setText(file.getAbsolutePath());
            }
            else System.out.println("File not found!");
        } else if (event.getSource() == projectDestinationPathButton) {
            File file = get_InitialDirectory("/ProjectFiles");
            if (file != null) {
                projectDestinationPath.setText(file.getAbsolutePath());
            }
            else System.out.println("File not found!");
        } else if (event.getSource() == zipFilePathButton) {
            File file = get_ZipDirectory();
            if (file != null) {
                zipFilePath.setText(file.getAbsolutePath());
            }
            else System.out.println("File not found!");
        } else if (event.getSource() == projectDestinationPathImportButton) {
            File file = get_InitialDirectory("/ProjectFiles");
            if (file != null) {
                projectDestinationPathImport.setText(file.getAbsolutePath());
            } else System.out.println("File not found!");
        }
        else if (event.getSource() == destinationPathButton) {
            File file = get_InitialDirectory("/ConfigFiles");
            if (file != null) {
                destinationPath.setText(file.getAbsolutePath());
            } else System.out.println("File not found!");
        } else if (event.getSource() == configFilePathEditButton) {
            File file = get_JSONFilePath();
            if (file != null) {
                configFilePath.setText(file.getAbsolutePath());
                extractJson(file);

            }
            else System.out.println("File not found!");
        }
    }

    private File get_InitialDirectory(String folderName) {
        DirectoryChooser directoryChooser = new DirectoryChooser(); // To chose only Directories
        directoryChooser.setTitle("Choose Save Project Directory");
        directoryChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath() + folderName)); // Initial Path
        return directoryChooser.showDialog(new Popup());
    }

    private File get_JSONFilePath() {
        FileChooser fileChooser = new FileChooser(); // To chose only Directories
        fileChooser.setTitle("Choose Configuration File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath() + "/ConfigFiles")); // Initial Path
        return fileChooser.showOpenDialog(new Popup());
    }

    private File get_ZipDirectory() {
        FileChooser fileChooser = new FileChooser(); // To chose only Directories
        fileChooser.setTitle("Choose Zip File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files", "*.zip"));
        return fileChooser.showOpenDialog(new Popup());
    }

    private void extractJson(File file){
        if (configFilePath.getText().isEmpty()){
            return;
        }
        //It is getting json information to put them in text fields
        String jsonText ;
        try {
            jsonText = new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = new JSONObject(jsonText);

        JSONObject compilerConfig = jsonObject.getJSONObject("compilerConfig");
        String language = compilerConfig.getString("language");
        String compileCommand = compilerConfig.getString("compileCommand");
        String rCommand = compilerConfig.getString("runCommand");

        JSONObject projectConfig = jsonObject.getJSONObject("projectConfig");
        JSONArray arguments = projectConfig.getJSONArray("argument");
        String expectedOut = projectConfig.getString("expectedOutput");

        String argumentsToStr = "";
        for (int i = 0; i < arguments.length(); i++) {
            argumentsToStr += arguments.getString(i);
            if (i != arguments.length()-1)
                argumentsToStr+=",";
        }

        editConfigVBox.setVisible(true);
        languageChoice.setValue(language);
        compCommand.setText(compileCommand);
        runCommand.setText(rCommand);
        projectArguments.setText(argumentsToStr);
        expectedOutput.setText(expectedOut);

    }


}

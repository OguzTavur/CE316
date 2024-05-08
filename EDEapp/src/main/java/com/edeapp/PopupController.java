package com.edeapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import java.io.File;
import java.io.IOException;
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
                messageExchangePoint.getController().createNewProject(projectDestinationPath.getText(),projectName.getText(),false,languageChoice.getValue().toString(),zipFilePath.getText(),null,projectArguments.getText());
            }
        }
        else if (radioImport.isSelected()) {
            if (checkInputAreas(true)) {
                System.out.println("Project Name: " + projectName.getText());
                System.out.println("Config File Path: " + configFilePath.getText());
                System.out.println("Zip File Path: " + zipFilePath.getText());
                MessageExchangePoint messageExchangePoint = MessageExchangePoint.getInstance();
                messageExchangePoint.getController().closePopUp();
                // TODO: GUI ye destination path ekle
                messageExchangePoint.getController().createNewProject(projectDestinationPath.getText(),projectName.getText(),true,null,zipFilePath.getText(),configFilePath.getText(),null);
            }
        }
        // else TODO: Add here state information after
    }

    private boolean checkInputAreas(boolean importConfig) {
        if (importConfig) {
            return !projectName.getText().isEmpty() && !configFilePath.getText().isEmpty() && !zipFilePath.getText().isEmpty();
        }
        else return !projectName.getText().isEmpty() && !projectDestinationPath.getText().isEmpty() && !zipFilePath.getText().isEmpty();
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
            File file = get_InitialDirectory();
            if (file != null) {
                projectDestinationPath.setText(file.getAbsolutePath());
            }
            else System.out.println("File not found!");
        } else if (event.getSource() == zipFilePathButton) {
            File file = get_ZipDirectory();
            if (file != null) {
                projectDestinationPath.setText(file.getAbsolutePath());
            }
            else System.out.println("File not found!");
        }
    }

    private File get_InitialDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser(); // To chose only Directories
        directoryChooser.setTitle("Choose Save Project Directory");
        directoryChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath() + "/src/main/resources/ProjectFiles")); // Initial Path
        return directoryChooser.showDialog(new Popup());
    }

    private File get_JSONFilePath() {
        FileChooser fileChooser = new FileChooser(); // To chose only Directories
        fileChooser.setTitle("Choose Configuration File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath() + "/src/main/resources/ConfigFiles")); // Initial Path
        return fileChooser.showOpenDialog(new Popup());
    }

    private File get_ZipDirectory() {
        FileChooser fileChooser = new FileChooser(); // To chose only Directories
        fileChooser.setTitle("Choose Zip File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files", "*.zip"));
        return fileChooser.showOpenDialog(new Popup());
    }
}

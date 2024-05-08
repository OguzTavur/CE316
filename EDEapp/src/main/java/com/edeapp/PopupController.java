package com.edeapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
    protected void onCreateButtonClicked(){
        if (radioNew.isSelected()) {
            if (checkInputAreas(false)) {

            }
        }
        else if (radioImport.isSelected()) {
            if (checkInputAreas(true)) {
                System.out.println("Project Name: " + projectName);
                System.out.println("Config File Path: " + configFilePath);
                System.out.println("Zip File Path: " + zipFilePath);

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
}

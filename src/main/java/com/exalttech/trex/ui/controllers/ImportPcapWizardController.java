/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.views.importPcap.ImportedPacketProperties;
import com.exalttech.trex.ui.views.importPcap.ImportedPacketPropertiesView;
import com.exalttech.trex.ui.views.importPcap.ImportedPacketTableView;
import com.exalttech.trex.util.PreferencesManager;
import com.exalttech.trex.util.Util;
import com.exalttech.trex.util.files.FileManager;
import com.exalttech.trex.util.files.FileType;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author GeorgeKH
 */
public class ImportPcapWizardController extends DialogView implements Initializable {

    @FXML
    Button selectPcapBtn;

    @FXML
    Button importBtn;

    @FXML
    Button backBtn;

    @FXML
    Pane wizardViewContainer;

    List<Profile> profilesList;
    String yamlFileName;

    ImportedPacketPropertiesView importedPacketPropertiesView;
    ImportedPacketTableView importedPacketTableView;

    ImportedPacketProperties propertiesBinder;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    /**
     * Initialize wizard
     *
     * @param profilesList
     * @param yamlFileName
     */
    public void initWizard(List<Profile> profilesList, String yamlFileName) {
        this.profilesList = profilesList;
        this.yamlFileName = yamlFileName;

        importedPacketPropertiesView = new ImportedPacketPropertiesView();
        importedPacketTableView = new ImportedPacketTableView(profilesList, yamlFileName);

        wizardViewContainer.getChildren().add(importedPacketPropertiesView);

        initPropertiesBinder();
    }

    /**
     * Init properties binder model
     */
    private void initPropertiesBinder() {
        propertiesBinder = new ImportedPacketProperties();
        importedPacketPropertiesView.setPropertiesBinder(propertiesBinder);
        importedPacketTableView.setPropertiesBinder(propertiesBinder);
    }

    /**
     * Handle select pcap button click event
     * @param event 
     */
    @FXML
    public void handleSelectPcapBtnClicked(ActionEvent event) {
        if (importedPacketPropertiesView.isValidInputValues()) {
            String loadFolderPath = PreferencesManager.getInstance().getLoadLocation();
            Stage owner = (Stage) wizardViewContainer.getScene().getWindow();
            File pcapFile = FileManager.getSelectedFile("Open Pcap File", "", owner, FileType.PCAP, loadFolderPath, false);
            if (pcapFile != null) {
                if (importedPacketTableView.setPcapFile(pcapFile)) {
                    wizardViewContainer.getChildren().clear();
                    wizardViewContainer.getChildren().add(importedPacketTableView);
                    updateBtnState(false);
                } else {
                    Alert wrongPcapMsg = Util.getAlert(Alert.AlertType.ERROR);
                    wrongPcapMsg.setContentText("Invalid Pcap, it should be one flow with IPV4 packets");
                    wrongPcapMsg.showAndWait();
                }
            }
        }
    }

    /**
     * Handle cancel button click event
     * @param event 
     */
    @FXML
    public void handleCancelButtonClicked(ActionEvent event) {
        closeDialog();
    }

    /**
     * Handle import button click event
     * @param event 
     */
    @FXML
    public void handleImportBtnClicked(ActionEvent event) {
        if (importedPacketTableView.doImport()) {
            closeDialog();
        }
    }

    /**
     * Handle back button click event
     * @param event 
     */
    @FXML
    public void handleBackBtnClicked(ActionEvent event) {
        wizardViewContainer.getChildren().clear();
        wizardViewContainer.getChildren().add(importedPacketPropertiesView);
        updateBtnState(true);
    }

    /**
     * Close import dialog
     */
    private void closeDialog() {
        Stage currentStage = (Stage) wizardViewContainer.getScene().getWindow();
        currentStage.fireEvent(new WindowEvent(currentStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * 
     * @param stage 
     */
    @Override
    public void onEnterKeyPressed(Stage stage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Update button visibility state
     * @param isBack 
     */
    private void updateBtnState(boolean isBack) {
        importBtn.setVisible(!isBack);
        selectPcapBtn.setVisible(isBack);
        backBtn.setVisible(!isBack);
    }
}

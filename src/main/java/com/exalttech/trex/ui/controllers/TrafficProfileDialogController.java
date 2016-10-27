/**
 * *****************************************************************************
 * Copyright (c) 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************
 */
package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.views.PacketTableView;
import com.exalttech.trex.util.PreferencesManager;
import com.exalttech.trex.util.ProfileManager;
import com.exalttech.trex.util.TrafficProfile;
import com.exalttech.trex.util.Util;
import com.exalttech.trex.util.files.FileManager;
import com.exalttech.trex.util.files.FileType;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;

/**
 * Traffic profile FXM controller
 *
 * @author Georgekh
 */
public class TrafficProfileDialogController extends DialogView implements Initializable {

    private static final Logger LOG = Logger.getLogger(TrafficProfileDialogController.class.getName());
    private static final int TABLE_WIDTH = 450;

    @FXML
    FlowPane functionContainer;
    @FXML
    ListView profileListView;
    @FXML
    HBox profileTableOperationContainer;
    @FXML
    AnchorPane profileViewWrapper;
    @FXML
    Button loadProfileBtn;
    @FXML
    Button exportProfileBtn;

    @FXML
    Button createProfileBtn;
    @FXML
    Button exportToYamlBtn;

    PacketTableView tableView;
    private Profile[] currentLoadedProfilesList;
    private File selectedFile;
    private TrafficProfile trafficProfile;
    Stage currentStage;
    private String currentLoadedProfile;

    /**
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tableView = new PacketTableView(TABLE_WIDTH);
        initializeProfileBtn();
        initializeProfileList();
    }

    /**
     * Initialize current stage
     */
    public void init() {
        currentStage = (Stage) profileViewWrapper.getScene().getWindow();

        currentStage.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                currentStage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue && tableView.isStreamEditingWindowOpen()) {
                        Util.optimizeMemory();
                        loadStreamTable();
                        tableView.setStreamEditingWindowOpen(false);
                    }
                });
            }
        });

    }

    /**
     * Initialize profile buttons
     */
    private void initializeProfileBtn() {
        loadProfileBtn.setGraphic(new ImageView(new Image("/icons/load_profile.png")));
        exportProfileBtn.setGraphic(new ImageView(new Image("/icons/export_profile_icon.png")));

        createProfileBtn.setGraphic(new ImageView(new Image("/icons/add.png")));
        exportToYamlBtn.setGraphic(new ImageView(new Image("/icons/export_profile_icon.png")));
        trafficProfile = new TrafficProfile();
    }

    /**
     * Initialize profile list
     */
    private void initializeProfileList() {
        profileListView.setItems(FXCollections.observableArrayList(ProfileManager.getInstance().loadProfiles()));
        profileListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    currentLoadedProfile = newValue;
                    loadStreamTable();
                } catch (Exception ex) {
                    handleInvalidLoadedFile(newValue);
                    LOG.error("Invalid loaded file " + newValue, ex);
                }
            }
        });
        profileListView.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.DELETE) {
                handleDeleteProfile();
            }
        });
        if (Util.isNullOrEmpty(currentLoadedProfile)) {
            profileListView.getSelectionModel().select(0);
        } else {
            profileListView.getSelectionModel().select(currentLoadedProfile);
        }
        boolean disableBtn = false;
        if (profileListView.getItems().isEmpty()) {
            disableBtn = true;
        }
        disableProfileFunctionBtn(disableBtn);
    }

    /**
     * Handle load profile button click
     *
     * @param event
     */
    @FXML
    public void handleLoadProfileBtnClick(MouseEvent event) {

        String loadFileName = "";
        try {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("YAML Files (*.yaml)", "*.yaml");
            FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All files ", "*.*");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.getExtensionFilters().add(allFilesFilter);
            fileChooser.setTitle("Load Profile File");

            String loadFolderPath = PreferencesManager.getInstance().getLoadLocation();
            if (!Util.isNullOrEmpty(loadFolderPath) && new File(loadFolderPath).exists()) {
                fileChooser.setInitialDirectory(new File(loadFolderPath));
            }
            File loadedFile = fileChooser.showOpenDialog(((Button) (event.getSource())).getScene().getWindow());
            if (loadedFile != null) {
                loadFileName = loadedFile.getName();
                // check if exists in list or not
                if (!ProfileManager.getInstance().isFileExists(loadedFile.getName())) {
                    // Read Selected File.
                    Profile[] yamlTrafficProfile = trafficProfile.getTrafficProfile(loadedFile);

                    // make a copy of selected file
                    File localFile = trafficProfile.convertTrafficProfileToYamlFile(yamlTrafficProfile, loadedFile.getName());

                    // add it to list
                    profileListView.getItems().add(localFile.getName());

                    // save the new selected profile
                    ProfileManager.getInstance().updateProfilesList(localFile, true);

                }
                profileListView.getSelectionModel().select(loadedFile.getName());

                // enaprofileListView.getSelectionModel().select(localFile.getName());ble delete profile btn
                disableProfileFunctionBtn(false);
            }
        } catch (IOException ex) {
            Alert alert = Util.getAlert(AlertType.ERROR);
            alert.setContentText("Error loading file " + loadFileName);
            alert.showAndWait();
            LOG.error("Error loading the profile", ex);
        }
    }

    /**
     * Handle delete profile button clicked
     */
    public void handleDeleteProfile() {

        if (!profileListView.getItems().isEmpty()) {
            String fileName = profileListView.getSelectionModel().getSelectedItem().toString();
            if (Util.isConfirmed("Are you sure you want to delete profile " + fileName + "?")) {
                // update saved list
                ProfileManager.getInstance().updateProfilesList(selectedFile, false);

                // delete File from local dist
                FileManager.deleteFile(selectedFile);

                // remove profile from the list
                profileListView.getItems().remove(fileName);
                if (profileListView.getItems().isEmpty()) {
                    profileViewWrapper.getChildren().clear();
                    disableProfileFunctionBtn(true);
                }
            }
        }
    }

    /**
     * View stream in table
     *
     * @param fileToLoad
     */
    private void loadStreamTable() {
        try {
            if (currentLoadedProfile != null) {
                selectedFile = new File(ProfileManager.getInstance().getProfileFilePath(currentLoadedProfile));
                currentLoadedProfilesList = tableView.loadStreamTable(selectedFile);
                profileViewWrapper.getChildren().clear();
                profileViewWrapper.getChildren().add(tableView);
            }
        } catch (Exception ex) {
            LOG.error("Error loading stream table", ex);
        }
    }

    /**
     * Enable/Disable profile functional buttons
     *
     * @param enable
     */
    private void disableProfileFunctionBtn(boolean enable) {
        exportProfileBtn.setDisable(enable);
        exportToYamlBtn.setDisable(enable);
    }

    /**
     * Handle invalid loaded file
     *
     * @param fileName
     * @param oldSelection
     */
    private void handleInvalidLoadedFile(String fileName) {
        Alert errAlert = Util.getAlert(AlertType.ERROR);
        errAlert.setContentText("Invalid Yaml file " + fileName);
        profileViewWrapper.getChildren().clear();

        errAlert.showAndWait();
    }

    /**
     * Handle export to JSON button clicked
     *
     * @param event
     */
    @FXML
    public void hanldeExportToJSONBtnClicked(ActionEvent event) {
        try {
            String data = trafficProfile.convertTrafficProfileToJson(currentLoadedProfilesList, -1, "NONE");
            String fileName = selectedFile.getName().substring(0, selectedFile.getName().indexOf(".")) + ".json";
            Window owner = ((Button) (event.getSource())).getScene().getWindow();
            FileManager.exportFile("Save JSON File", fileName, data, owner, FileType.JSON);
        } catch (Exception ex) {
            LOG.error("Error during generate JSON file", ex);
        }
    }

    /**
     * Handle create profile button clicked
     *
     * @param event
     */
    @FXML
    public void handleCreateProfileBtnClicked(ActionEvent event) {
        try {
            String newProfileName = ProfileManager.getInstance().createNewProfile(currentStage);
            if (!Util.isNullOrEmpty(newProfileName)) {
                profileListView.getItems().add(newProfileName);
                profileListView.getSelectionModel().select(newProfileName);
                // enable export buttons
                disableProfileFunctionBtn(false);
            }
        } catch (IOException ex) {
            LOG.error("Error creating new profile", ex);
        }
    }

    /**
     * Handle export to YAML button clicked
     *
     * @param event
     */
    @FXML
    public void handleExportToYamlBtnClicked(ActionEvent event) {
        Window owner = ((Button) (event.getSource())).getScene().getWindow();
        trafficProfile.exportProfileToYaml(owner, currentLoadedProfilesList, selectedFile.getName());
    }

    /**
     * Handle Enter key pressed
     *
     * @param stage
     */
    @Override
    public void onEnterKeyPressed(Stage stage) {
        stage.hide();
    }
}

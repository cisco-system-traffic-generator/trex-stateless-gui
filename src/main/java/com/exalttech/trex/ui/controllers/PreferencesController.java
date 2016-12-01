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

import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.models.datastore.Preferences;
import com.exalttech.trex.util.PreferencesManager;
import com.exalttech.trex.util.Util;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.exalttech.trex.util.files.FileManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Georgekh
 */
public class PreferencesController extends DialogView implements Initializable {

    @FXML
    TextField loadLocation;
    @FXML
    TextField savedLocation;
    @FXML
    TextField templatesLocation;
    DirectoryChooser chooser;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chooser = new DirectoryChooser();
        // initialize locations
        loadPreferences();
    }

    /**
     * Handle OK button clicked
     *
     * @param event
     */
    @FXML
    public void handleOKBtnClicked(final MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        savePreferences(stage);
    }

    /**
     * Save preferences
     */
    private void savePreferences(Stage current) {
        // update prefernces file
        Preferences pref = new Preferences(loadLocation.getText(), savedLocation.getText(), templatesLocation.getText());
        PreferencesManager.getInstance().savePreferences(pref);

        current.hide();
    }

    /**
     * Load preferences Load preferences
     */
    private void loadPreferences() {
        Preferences pref = PreferencesManager.getInstance().getPreferences();
        if (pref != null) {
            loadLocation.setText(pref.getLoadLocation());
            savedLocation.setText(pref.getSavedLocation());
            templatesLocation.setText(pref.getTemplatesLocation());
            templatesLocation.setPromptText(FileManager.getTemplatesFilePath());
        }
    }

    /**
     * Select load location choose button click handler
     *
     * @param event
     */
    @FXML
    public void selectLoadLocation(ActionEvent event) {
        chooser.setTitle("Load Location Directory");
        File locationDirectory = new File(loadLocation.getText());
        if (!Util.isNullOrEmpty(loadLocation.getText()) && locationDirectory.exists()) {
            chooser.setInitialDirectory(new File(loadLocation.getText()));
        }
        File location = chooser.showDialog(((Button) (event.getSource())).getScene().getWindow());
        if (location != null) {
            loadLocation.setText(location.getAbsolutePath());
        }
    }

    /**
     * Select save location choose button click handler
     *
     * @param event
     */
    @FXML
    public void selectSavedLocation(ActionEvent event) {
        chooser.setTitle("Save Location Directory");
        File locationDirectory = new File(savedLocation.getText());
        if (!Util.isNullOrEmpty(savedLocation.getText()) && locationDirectory.exists()) {
            chooser.setInitialDirectory(new File(savedLocation.getText()));
        }
        File location = chooser.showDialog(((Button) (event.getSource())).getScene().getWindow());
        if (location != null) {
            savedLocation.setText(location.getAbsolutePath());
        }
    }

    /**
     * Select save location choose button click handler
     *
     * @param event
     */
    @FXML
    public void selectTemplatesLocation(ActionEvent event) {
        chooser.setTitle("Templates Directory");
        File templatesDirectory = new File(templatesLocation.getText());
        if (!Util.isNullOrEmpty(templatesLocation.getText()) && templatesDirectory.exists()) {
            chooser.setInitialDirectory(new File(templatesLocation.getText()));
        }
        File location = chooser.showDialog(((Button) (event.getSource())).getScene().getWindow());
        if (location != null) {
            templatesLocation.setText(location.getAbsolutePath());
        }
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        savePreferences(stage);
    }
}

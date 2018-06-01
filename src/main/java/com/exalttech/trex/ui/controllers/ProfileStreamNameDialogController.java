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
import com.exalttech.trex.ui.util.TrexAlertBuilder;
import com.exalttech.trex.util.Util;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Stream Name dialog FXML controller
 *
 * @author Georgekh
 */
public class ProfileStreamNameDialogController extends DialogView implements Initializable {

    @FXML
    TextField nameTF;
    @FXML
    AnchorPane profileWrapper;
    @FXML
    AnchorPane profileStreamView;
    @FXML
    Label namelabel;

    boolean dataAvailabe = false;
    boolean profileWindow = true;
    List<Profile> profileList;

    /**
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComponent();
    }

    /**
     * Handle OK button clicked
     *
     * @param event
     */
    @FXML
    public void handleOKBtnClicked(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        doCreating(stage);
    }

    /**
     * Save name
     *
     * @param stage
     */
    private void doCreating(Stage stage) {
        if (validInput()) {
            stage.hide();
        }
    }

    /**
     * Initialize component
     */
    private void initializeComponent() {
        dataAvailabe = false;
        if (!profileWindow) {
            nameTF.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
                if (nameTF.getText().length() > 15) {
                    event.consume();
                }
            });
        }
    }

    /**
     * Validate input
     *
     * @return
     */
    private boolean validInput() {
        TrexAlertBuilder errorBuilder = TrexAlertBuilder.build().setType(Alert.AlertType.ERROR);
        if (Util.isNullOrEmpty(nameTF.getText())) {
            errorBuilder.setContent("Please fill the empty fields");
            errorBuilder.getAlert().showAndWait();
            return false;
        } else if (profileList != null && !profileWindow) {
            for (Profile p : profileList) {
                if (p.getName().equals(nameTF.getText())) {
                    errorBuilder.setContent("Stream name already exists, please select a different Stream name");
                    errorBuilder.getAlert().showAndWait();
                    return false;
                }
            }
        }
        dataAvailabe = true;
        return true;
    }

    /**
     * Set profile list
     *
     * @param profileList
     */
    public void setProfileList(List<Profile> profileList) {
        this.profileList = profileList;
    }

    /**
     * Return name
     *
     * @return
     */
    public String getName() {
        return nameTF.getText();
    }

    /**
     * Return true if name is entered
     *
     * @return
     */
    public boolean isDataAvailable() {
        return dataAvailabe;
    }

    /**
     * Set field title
     *
     * @param title
     */
    private void setFieldTitle(String title) {
        namelabel.setText(title);
    }

    /**
     * Define if the window is profile or stream
     *
     * @param profileWindow
     */
    public void setProfileWindow(boolean profileWindow) {
        this.profileWindow = profileWindow;
        if (!profileWindow) {
            setFieldTitle("Stream Name");
        }
    }

    /**
     * Handle Enter key pressed
     *
     * @param stage
     */
    @Override
    public void onEnterKeyPressed(Stage stage) {
        doCreating(stage);
    }

}

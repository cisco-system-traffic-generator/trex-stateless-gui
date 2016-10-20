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

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.models.datastore.Connection;
import com.exalttech.trex.ui.models.datastore.ConnectionsWrapper;
import com.exalttech.trex.util.Util;
import com.exalttech.trex.util.files.XMLFileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.*;

/**
 * FXML Controller class for connect dialog
 *
 * @author Georgekh
 */
public class ConnectDialogController extends DialogView implements Initializable, ChangeListener<String> {

    private static final Logger LOG = Logger.getLogger(ConnectDialogController.class.getName());

    @FXML
    Label closeDialog;
    @FXML
    Button dialogCancelButton;
    @FXML
    Button connectButton;
    @FXML
    TextField rpcPort;
    @FXML
    TextField asyncPort;
    @FXML
    TextField scapyPort;
    @FXML
    TextField nameTF;
    @FXML
    ComboBox connectionsCB;
    @FXML
    TitledPane advanceTitledPan;
    @FXML
    AnchorPane mainViewContainer;
    @FXML
    RadioButton fullControlRB;

    Map<String, Connection> connectionMap = new HashMap<>();
    private Connection selectedConnection;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeConnections();
    }

    /**
     * Handle close button clicking
     *
     * @param event
     */
    @FXML
    public void handleCloseDialog(final MouseEvent event) {
        if (event.getSource() == closeDialog || event.getSource() == dialogCancelButton) {
            ConnectionManager.getInstance().setConnected(false);
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.hide();
        }
    }

    /**
     * Handle connect button clicking
     *
     * @param event
     */
    @FXML
    public void handleConnectButton(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        doConnect(stage);
    }

    /**
     * Do connect to server
     *
     * @param stage
     */
    private void doConnect(Stage stage) {
        try {
            if (validateInput() && isConnectionValid()) {
                Connection con = new Connection(connectionsCB.getEditor().getText(), rpcPort.getText(), asyncPort.getText(), scapyPort.getText(), nameTF.getText(), fullControlRB.isSelected());
                // remove it if connection already exists
                if (connectionMap.get(connectionsCB.getEditor().getText()) != null) {
                    connectionMap.remove(connectionsCB.getEditor().getText());
                }
                con.setLastUsed(true);
                connectionMap.put(connectionsCB.getEditor().getText(), con);
                updateConnectionsList();
                ConnectionManager.getInstance().setConnected(true);

                stage.hide();
            }
        } catch (Exception ex) {
            LOG.error("Error durring connection to TRex server", ex);
        }
    }

    /**
     * Validate connection
     *
     * @return
     */
    private boolean isConnectionValid() {
        Alert errMsg = Util.getAlert(Alert.AlertType.ERROR);
        boolean isValid = true;
        if (!ConnectionManager.getInstance().initializeConnection(connectionsCB.getEditor().getText(), rpcPort.getText(), asyncPort.getText(), scapyPort.getText(), nameTF.getText(), !fullControlRB.isSelected())) {
            errMsg.setContentText("TRex Hostname or IP address are not valid");
            isValid = false;
        } else if (!ConnectionManager.getInstance().testConnection(false) || !ConnectionManager.getInstance().testConnection(true)) {
            errMsg.setContentText("Failed to connect to TRex - make sure the server is up");
            isValid = false;
        }
        if (!isValid) {
            errMsg.show();
        }
        return isValid;

    }

    /**
     * Validate connect dialog input fields
     *
     * @return
     */
    private boolean validateInput() {
        boolean isValid = true;
        Alert errMsg = Util.getAlert(Alert.AlertType.ERROR);
        if (connectionsCB.getEditor().getText() == null || !Util.isValidIPAddress(connectionsCB.getEditor().getText())) {
            errMsg.setContentText("Invalid TRex Host Name or IP address");
            isValid = false;
        } else if (!Util.isValidPort(rpcPort.getText())) {
            errMsg.setContentText("Invalid TRex Sync Port Number(" + rpcPort.getText() + ")");
            isValid = false;
        } else if (!Util.isValidPort(asyncPort.getText())) {
            errMsg.setContentText("Invalid Async Port Number(" + asyncPort.getText() + ")");
            isValid = false;
        } else if (Util.isNullOrEmpty(nameTF.getText())) {
            errMsg.setContentText("Name should not be empty");
            isValid = false;
        }

        if (!isValid) {
            errMsg.show();
        }
        return isValid;
    }

    /**
     * Initialize and fill connections list from external connection file
     */
    private void initializeConnections() {
        connectionsCB.getItems().clear();
        connectionsCB.valueProperty().addListener(this);
        ConnectionsWrapper connection = (ConnectionsWrapper) XMLFileManager.loadXML("connections.xml", ConnectionsWrapper.class);
        if (connection != null && connection.getConnectionList() != null) {
            fillConnectionItem(connection.getConnectionList());
        }
    }

    /**
     * Save and update the connections list in external connection file
     */
    private void updateConnectionsList() {
        ConnectionsWrapper connections = new ConnectionsWrapper();
        connections.setConnectionList(new ArrayList<>(connectionMap.values()));
        XMLFileManager.saveXML("connections.xml", connections, ConnectionsWrapper.class);
    }

    /**
     * Change event handler
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!Util.isNullOrEmpty(newValue) && connectionMap.get(newValue) != null) {
            selectedConnection = connectionMap.get(newValue);
            rpcPort.setText(selectedConnection.getRpcPort());
            asyncPort.setText(selectedConnection.getAsyncPort());
            nameTF.setText(selectedConnection.getUser());
            fullControlRB.setSelected(selectedConnection.isFullControl());
        }
    }

    /**
     * Handle advance option clicking
     *
     * @param event
     */
    @FXML
    public void handleTitledPanelClicking(MouseEvent event) {
        if (advanceTitledPan.isExpanded()) {
            advanceTitledPan.setText("Hide advanced options");
            advanceTitledPan.setPrefHeight(190);
            mainViewContainer.setPrefHeight(327);
            mainViewContainer.getScene().getWindow().sizeToScene();
        } else {
            advanceTitledPan.setText("Show advanced options...");
            advanceTitledPan.setPrefHeight(30);
            mainViewContainer.setPrefHeight(195);
            mainViewContainer.getScene().getWindow().sizeToScene();
        }
    }

    /**
     * Handle delete connection
     *
     * @param event
     */
    @FXML
    public void handleDeleteConnections(MouseEvent event) {
        if (selectedConnection != null && Util.isConfirmed("Are you sure you want to delete the connection?")) {
            connectionMap.remove(selectedConnection.getIp());
            fillConnectionItem(new ArrayList<>(connectionMap.values()));
            updateConnectionsList();
        }
    }

    /**
     * Fill connections item
     *
     * @param connectionList
     */
    private void fillConnectionItem(List<Connection> connectionList) {
        connectionsCB.getItems().clear();
        connectionMap.clear();
        Connection lastUsed = null;
        for (Connection con : connectionList) {
            if(con.isLastUsed()){
                lastUsed = con;
            }
            con.setLastUsed(false);
            connectionsCB.getItems().add(con.getIp());
            connectionMap.put(con.getIp(), con);
        }
        if(lastUsed != null){
            connectionsCB.getSelectionModel().select(lastUsed.getIp());
        }
    }

    /**
     * Handle enter key pressed
     *
     * @param stage
     */
    @Override
    public void onEnterKeyPressed(Stage stage) {
        doConnect(stage);
    }
}

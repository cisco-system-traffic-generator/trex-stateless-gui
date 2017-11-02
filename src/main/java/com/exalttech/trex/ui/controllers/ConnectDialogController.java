/*
 * *****************************************************************************
 * Copyright (c) 2016
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */

package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.models.datastore.Connection;
import com.exalttech.trex.ui.models.datastore.ConnectionsWrapper;
import com.exalttech.trex.util.Util;
import com.exalttech.trex.util.files.XMLFileManager;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Consumer;


public class ConnectDialogController extends DialogView implements Initializable, ChangeListener<String> {
    private static class ValidateConnectionTask extends Task<Void> {
        private final String ip;
        private final String rpcPort;
        private final String asyncPort;
        private final String scapyPort;
        private final int timeout;
        private final String name;
        private final boolean isReadOnly;
        private final Consumer<String> callback;

        ValidateConnectionTask(
            final String ip,
            final String rpcPort,
            final String asyncPort,
            final String scapyPort,
            final int timeout,
            final String name,
            final boolean isReadOnly,
            final Consumer<String> callback
        ) {
            this.ip = ip;
            this.rpcPort = rpcPort;
            this.asyncPort = asyncPort;
            this.scapyPort = scapyPort;
            this.timeout = timeout;
            this.name = name;
            this.isReadOnly = isReadOnly;
            this.callback = callback;
        }

        public Void call() {
            final ConnectionManager connectionManager = ConnectionManager.getInstance();
            String error = null;
            boolean connected = false;
            final int ms = 1000;

            try {
                connected = connectionManager.initializeConnection(ip, rpcPort, asyncPort, scapyPort, timeout * ms, name, isReadOnly);
            } catch (Exception e) {
                error = e.getMessage();
            }

            if (connected && !connectionManager.testConnection(false) || !connectionManager.testConnection(true)) {
                error = "Failed to connect to TRex - make sure the server is up";
            }
            acceptCallback(error);
            return null;
        }

        private void acceptCallback(final String error) {
            Platform.runLater(() -> callback.accept(error));
        }
    }

    @FXML
    private TextField timeoutField;
    @FXML
    private TextField rpcPortTextField;
    @FXML
    private TextField asyncPortTextField;
    @FXML
    private TextField scapyPortTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<String> connectionsCB;
    @FXML
    private TitledPane advanceTitledPan;
    @FXML
    private AnchorPane mainViewContainer;
    @FXML
    private RadioButton fullControlRB;

    private final Map<String, Connection> connectionMap = new HashMap<>();
    private Connection selectedConnection;
    private BooleanProperty isConnectionInProgress;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeConnections();

        isConnectionInProgress = new SimpleBooleanProperty(false);
        isConnectionInProgress.addListener(
            (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue != null) {
                    mainViewContainer.setDisable(newValue);
                    mainViewContainer.getScene().setCursor(newValue ? Cursor.WAIT : Cursor.DEFAULT);
                }
            }
        );

        timeoutField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                timeoutField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @Override
    public void setupStage(final Stage stage) {
        super.setupStage(stage);
        stage.setOnCloseRequest((WindowEvent event) -> {
            if (isConnectionInProgress.get()) {
                event.consume();
            }
        });
    }

    @FXML
    public void handleCloseDialog(final MouseEvent event) {
        ConnectionManager.getInstance().setConnected(false);
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.hide();
    }

    @FXML
    public void handleConnectButton(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        doConnect(stage);
    }

    private void doConnect(final Stage stage) {
        if (isConnectionInProgress.get() || !validateInput()) {
            return;
        }

        isConnectionInProgress.set(true);
        final ValidateConnectionTask validateConnectionTask = new ValidateConnectionTask(
            connectionsCB.getEditor().getText(),
            rpcPortTextField.getText(),
            asyncPortTextField.getText(),
            scapyPortTextField.getText(),
            Integer.parseInt(timeoutField.getText()),
            nameTextField.getText(),
            !fullControlRB.isSelected(),
            (String error) -> connectionValidationFinished(stage, error)
        );
        new Thread(validateConnectionTask).start();
    }

    private void connectionValidationFinished(final Stage stage, final String error) {
        if (error != null) {
            final Alert errMsg = Util.getAlert(Alert.AlertType.ERROR);
            errMsg.setContentText(error);
            errMsg.show();
        } else {
            final String ip = connectionsCB.getEditor().getText();

            Connection con = new Connection(
                ip,
                rpcPortTextField.getText(),
                asyncPortTextField.getText(),
                scapyPortTextField.getText(),
                Integer.parseInt(timeoutField.getText()),
                nameTextField.getText(),
                fullControlRB.isSelected()
            );
            con.setLastUsed(true);
            connectionMap.put(ip, con);
            updateConnectionsList();
            ConnectionManager.getInstance().setConnected(true);

            stage.hide();
        }
        isConnectionInProgress.set(false);
    }

    private boolean validateInput() {
        boolean isValid = true;
        Alert errMsg = Util.getAlert(Alert.AlertType.ERROR);
        if (connectionsCB.getEditor().getText() == null || !Util.isValidAddress(connectionsCB.getEditor().getText())) {
            errMsg.setContentText("Invalid TRex Host Name or IP address");
            isValid = false;
        } else if (!Util.isValidPort(rpcPortTextField.getText())) {
            errMsg.setContentText("Invalid TRex Sync Port Number(" + rpcPortTextField.getText() + ")");
            isValid = false;
        } else if (!Util.isValidPort(asyncPortTextField.getText())) {
            errMsg.setContentText("Invalid Async Port Number(" + asyncPortTextField.getText() + ")");
            isValid = false;
        } else if (!Util.isValidPort(scapyPortTextField.getText())) {
            errMsg.setContentText("Invalid Scapy Port Number(" + scapyPortTextField.getText() + ")");
            isValid = false;
        } else if (Util.isNullOrEmpty(nameTextField.getText())) {
            try {
                InetAddress ip = InetAddress.getLocalHost();
                String username = System.getProperty("user.name");
                nameTextField.setText(username + "@" + ip.getHostAddress());
            } catch (UnknownHostException e) {
                errMsg.setContentText("Name should not be empty");
                isValid = false;
            }
        }

        if (!isValid) {
            errMsg.show();
        }
        return isValid;
    }

    private void initializeConnections() {
        connectionsCB.getItems().clear();
        connectionsCB.valueProperty().addListener(this);
        ConnectionsWrapper connection = (ConnectionsWrapper) XMLFileManager.loadXML("connections.xml", ConnectionsWrapper.class);
        if (connection != null && connection.getConnectionList() != null) {
            fillConnectionItem(connection.getConnectionList());
        }
        String username = System.getProperty("user.name");
        nameTextField.setText(username);
    }

    private void updateConnectionsList() {
        try {
            ConnectionsWrapper connections = new ConnectionsWrapper();
            connections.setConnectionList(new ArrayList<>(connectionMap.values()));
            XMLFileManager.saveXML("connections.xml", connections, ConnectionsWrapper.class);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!Util.isNullOrEmpty(newValue) && connectionMap.get(newValue) != null) {
            selectedConnection = connectionMap.get(newValue);
            final int timeout = selectedConnection.getTimeout();

            rpcPortTextField.setText(selectedConnection.getRpcPort());
            asyncPortTextField.setText(selectedConnection.getAsyncPort());
            scapyPortTextField.setText(Util.isNullOrEmpty(selectedConnection.getScapyPort()) ? "4507" : selectedConnection.getScapyPort());
            nameTextField.setText(selectedConnection.getUser());
            fullControlRB.setSelected(selectedConnection.isFullControl());

            if (timeout != 0) {
                timeoutField.setText(Integer.toString(timeout));
            }
        }
    }

    @FXML
    public void handleTitledPanelClicking() {
        if (advanceTitledPan.isExpanded()) {
            advanceTitledPan.setText("Hide advanced options");
            advanceTitledPan.setPrefHeight(220);
            mainViewContainer.setPrefHeight(357);
            mainViewContainer.getScene().getWindow().sizeToScene();
        } else {
            advanceTitledPan.setText("Show advanced options...");
            advanceTitledPan.setPrefHeight(30);
            mainViewContainer.setPrefHeight(195);
            mainViewContainer.getScene().getWindow().sizeToScene();
        }
    }

    @FXML
    public void handleDeleteConnections() {
        if (selectedConnection != null && Util.isConfirmed("Are you sure you want to delete the connection?")) {
            connectionMap.remove(selectedConnection.getIp());
            fillConnectionItem(new ArrayList<>(connectionMap.values()));
            updateConnectionsList();
        }
    }

    private void fillConnectionItem(List<Connection> connectionList) {
        connectionsCB.getItems().clear();
        connectionMap.clear();
        Connection lastUsed = null;
        for (Connection con : connectionList) {
            if (con.isLastUsed()) {
                lastUsed = con;
            }
            con.setLastUsed(false);
            connectionsCB.getItems().add(con.getIp());
            connectionMap.put(con.getIp(), con);
        }
        if (lastUsed != null) {
            connectionsCB.getSelectionModel().select(lastUsed.getIp());
        }
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        doConnect(stage);
    }
}

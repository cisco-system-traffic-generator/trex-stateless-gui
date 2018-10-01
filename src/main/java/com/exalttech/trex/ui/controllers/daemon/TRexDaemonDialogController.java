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

package com.exalttech.trex.ui.controllers.daemon;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.models.datastore.Connection;
import com.exalttech.trex.ui.models.datastore.ConnectionsWrapper;
import com.exalttech.trex.ui.views.logs.LogType;
import com.exalttech.trex.ui.views.logs.LogsView;
import com.exalttech.trex.util.files.XMLFileManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.arteam.simplejsonrpc.client.JsonRpcClient;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.google.common.base.Charsets;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

/**
 * TRex Daemon FXM controller
 *
 * @author Blagov
 */

public class TRexDaemonDialogController extends DialogView implements Initializable {
    static class JsonClientTransport implements Transport {
        private String hostname;
        private String port;
        CloseableHttpClient httpClient;

        public JsonClientTransport(String hostname, String port) {
            this.hostname = hostname;
            this.port = port;
            this.httpClient = HttpClients.createDefault();
        }

        @NotNull
        @Override
        public String pass(@NotNull String request) throws IOException {
            HttpPost post = new HttpPost("http://" + hostname + ":" + port);
            post.setEntity(new StringEntity(request, Charsets.UTF_8));
            post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
            try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                return EntityUtils.toString(httpResponse.getEntity(), Charsets.UTF_8);
            }
        }
    }

    @FXML
    private ComboBox<String> hostnamesComboBox;

    @FXML
    private TextField rpcPortTextField;

    @FXML
    private AnchorPane configContainer;

    @FXML
    private ConfigTreeView configTree;

    @FXML
    private LogsView logsView;

    @FXML
    private TitledPane configEditTitledPane;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private Button stopTRexButton;

    @FXML
    private Button startTRexButton;

    @FXML
    private Button loadDefaultConfigButton;

    @FXML
    public TextArea yamlPreviewTextfield;

    private JsonRpcClient client;

    private List<MetaField> metadata;
    private Map<String, InterfaceInfo> interfacesInfo;

    private UserConfigModel userConfigModel;
    private String configFilename;

    private List<Control> controlsDisabledOnDisconnected;
    private List<Control> controlsDisabledOnConnected;
    private List<Control> controlsDisabledOnMetadataNotExists;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initHostnamesComboBox();
        controlsDisabledOnConnected = Arrays.asList(hostnamesComboBox, rpcPortTextField, connectButton);
        controlsDisabledOnDisconnected = Arrays.asList(disconnectButton, startTRexButton, stopTRexButton);
        controlsDisabledOnMetadataNotExists = Arrays.asList(configEditTitledPane, loadDefaultConfigButton);
        configEditTitledPane.expandedProperty().bind(configEditTitledPane.disableProperty().not());
        this.configFilename = System.getProperty("user.name");
        refreshControlsAvailability();
    }

    private void initHostnamesComboBox() {
        hostnamesComboBox.getItems().clear();
        ConnectionsWrapper connection = (ConnectionsWrapper) XMLFileManager.loadXML("connections.xml", ConnectionsWrapper.class);
        if (connection != null && connection.getConnectionList() != null) {
            hostnamesComboBox.getItems().clear();
            Connection lastUsed = null;
            for (Connection con : connection.getConnectionList()) {
                if (con.isLastUsed()) {
                    lastUsed = con;
                }
                con.setLastUsed(false);
                hostnamesComboBox.getItems().add(con.getIp());
            }
            if (lastUsed != null) {
                hostnamesComboBox.getSelectionModel().select(lastUsed.getIp());
            }
        }
    }

    private void tryToConnect() {
        initJsonRpcClient();
        boolean connected = false;
        try {
            connected = client.createRequest()
                    .id(getId())
                    .method("connectivity_check")
                    .returnAs(Boolean.class)
                    .execute();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        if (connected) {
            log(LogType.INFO, MessageFormat.format("Connection to http://{0}:{1} established", getHostname(), getPort()));
            getMetadata();
        } else {
            this.client = null;
            log(LogType.ERROR, MessageFormat.format("Unable to access http://{0}:{1} requested host and port", getHostname(), getPort()));
        }
        refreshControlsAvailability();
    }

    private void refreshControlsAvailability() {
        for (Control control: controlsDisabledOnConnected) {
            control.setDisable(isConnected());
        }

        for (Control control: controlsDisabledOnDisconnected) {
            control.setDisable(!isConnected());
        }

        for (Control control: controlsDisabledOnMetadataNotExists) {
            control.setDisable(this.metadata == null);
        }
    }

    private boolean isConnected() {
        return client != null;
    }

    private void disconnect() {
        if (isConnected()) {
            client = null;
            metadata = null;

            interfacesInfo = null;
            TrexApp.injector.getInstance(InterfaceInfoProvider.class).setInterfacesInfo(null);

            configContainer.getChildren().clear();
            log(LogType.INFO, MessageFormat.format("Disconnected from http://{0}:{1}", getHostname(), getPort()));
            refreshControlsAvailability();
        }
    }


    private void loadDefaultConfig() {
        try {
            String defaultConfigB64 = client.createRequest()
                    .id(getId())
                    .method("get_trex_config")
                    .returnAs(String.class)
                    .execute();

            String decoded = new String(Base64.getDecoder().decode(defaultConfigB64.trim()), Charsets.US_ASCII);
            userConfigModel.fromYAMLString(decoded);
            initConfigTree();
            updateYAML();
        } catch (RuntimeException ex) {
            log(LogType.ERROR, MessageFormat.format("Unable to get default config from TRex Daemon: {0}", ex));
        } catch (IOException ex) {
            log(LogType.ERROR, MessageFormat.format("Unable to parse received default config YAML: {0}", ex));
        }
    }

    private void initJsonRpcClient() {
        this.client = new JsonRpcClient(new JsonClientTransport(getHostname(), getPort()));
    }

    private String getPort() {
        return rpcPortTextField.getText();
    }

    private String getHostname() {
        return hostnamesComboBox.getSelectionModel().getSelectedItem();
    }

    private void getMetadata() {
        try {
            metadata = client.createRequest()
                    .id(getId())
                    .method("get_trex_config_metadata")
                    .returnAs(new TypeReference<List<MetaField>>() {})
                    .execute();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            log(LogType.ERROR, "Unable to get TRex config Metadata, custom config usage will not be available: " + ex.getMessage());
            return;
        }

        try {
            interfacesInfo = client.createRequest()
                    .id(getId())
                    .method("get_devices_info")
                    .returnAs(new TypeReference<Map<String, InterfaceInfo>>() {})
                    .execute();
            TrexApp.injector.getInstance(InterfaceInfoProvider.class).setInterfacesInfo(interfacesInfo);
        } catch (Exception ex) {
            ex.printStackTrace();
            log(LogType.ERROR, "Unable to get TRex devices info, interface selection dialog will not be available: " + ex.getMessage());
        }

        initUserConfigModel();
        initConfigTree();
        updateYAML();
    }

    private void startTRex() {
        Boolean configUploaded = false;
        try {
            configUploaded = client.createRequest()
                    .id(getId())
                    .method("push_file")
                    .param("filename", configFilename)
                    .param("bin_data", Base64.getEncoder().encodeToString(
                            userConfigModel.getYAMLString().getBytes(Charsets.US_ASCII)))
                    .returnAs(Boolean.class)
                    .execute();

            if (!configUploaded) {
                log(LogType.ERROR, "Config upload to TRex host failed (TRex Daemon IO error)");
            }

            log(LogType.INFO, "Config uploaded successfully");
        } catch (RuntimeException ex) {
            log(LogType.ERROR, MessageFormat.format("Config upload to TRex host failed: {0}", ex));
        } catch (JsonProcessingException ex) {
            log(LogType.ERROR, MessageFormat.format("Config serialization failed: {0}", ex));
        }

        Map<String, Object> cmdParams = new HashMap<>();

        if (configUploaded) {
            try {
                String files_path = client.createRequest()
                        .id(getId())
                        .method("get_files_path")
                        .returnAs(String.class)
                        .execute();

                cmdParams.put("cfg", Paths.get(files_path, configFilename));
            } catch (RuntimeException ex) {
                log(LogType.ERROR, MessageFormat.format("Unable to get user configs path: {0}", ex));
            }
        }

        try {
            Integer trexSessionHandler = client.createRequest()
                    .id(getId())
                    .method("start_trex")
                    .param("trex_cmd_options", cmdParams)
                    .param("user", System.getProperty("user.name"))
                    .param("stateless", true)
                    .param("block_to_success", false)
                    .returnAs(Integer.class)
                    .execute();
            log(LogType.INFO, "TRex started successfully");
        } catch (RuntimeException ex) {
            log(LogType.ERROR, MessageFormat.format("Unable to start TRex: {0} ", ex));
        }
    }

    private void stopTRex() {
        try {
            Boolean stopped = client.createRequest()
                    .id(getId())
                    .method("force_trex_kill")
                    .returnAs(Boolean.class)
                    .execute();
            if (stopped) {
                log(LogType.INFO, "TRex stopped successfully");
            } else {
                log(LogType.INFO, "TRex is not running");
            }
        } catch (RuntimeException ex) {
            log(LogType.ERROR, MessageFormat.format("Unable to stop TRex: {0}", ex));
        }
    }

    private void initUserConfigModel() {
        userConfigModel = new UserConfigModel(metadata);
    }

    private void initConfigTree() {
        configContainer.getChildren().clear();

        if (userConfigModel != null) {
            configTree = new ConfigTreeView(userConfigModel, this::updateYAML);
            configContainer.getChildren().add(configTree);
        }
    }

    private void updateYAML() {
        try {
            yamlPreviewTextfield.setText(userConfigModel.getYAMLString());
        } catch (JsonProcessingException e) {
            log(LogType.ERROR, MessageFormat.format("Unable to get YAML conents: {0}", e));
        }
    }

    private String getId() {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<8; i++) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private void log(LogType type, String message) {
        logsView.append(type, message);
    }

    public void handleConnectClicked(ActionEvent actionEvent) {
        tryToConnect();
    }

    public void handleDisconnectClicked(ActionEvent actionEvent) {
        disconnect();
    }

    public void handleLoadDefaultConfigClicked(ActionEvent actionEvent) {
        loadDefaultConfig();
    }

    public void handleStartClicked(ActionEvent actionEvent) {
        startTRex();
    }

    public void handleStopClicked(ActionEvent actionEvent) {
        stopTRex();
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        //Do nothing
    }

    @Override
    public void closeHandler() {
        disconnect();
    }
}

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
import com.exalttech.trex.remote.models.profiles.Packet;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.remote.models.profiles.Stream;
import com.exalttech.trex.ui.StreamBuilderType;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.dialog.DialogWindow;
import com.exalttech.trex.ui.models.PacketInfo;
import com.exalttech.trex.ui.views.streams.binders.BuilderDataBinding;
import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.exalttech.trex.ui.views.streams.viewer.PacketHex;
import com.exalttech.trex.ui.views.streams.viewer.PacketParser;
import com.exalttech.trex.util.PreferencesManager;
import com.exalttech.trex.util.TrafficProfile;
import com.exalttech.trex.util.Util;
import com.exalttech.trex.util.files.FileManager;
import com.exalttech.trex.util.files.FileType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.xored.javafx.packeteditor.controllers.FieldEditorController;
import com.xored.javafx.packeteditor.events.ScapyClientNeedConnectEvent;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Packet builder FXML controller
 *
 * @author Georgekh
 */
public class PacketBuilderHomeController extends DialogView implements Initializable {

    private static final Logger LOG = Logger.getLogger(PacketBuilderHomeController.class.getName());

    @FXML
    AnchorPane details;
    @FXML
    AnchorPane hexPane;
    @FXML
    Button loadPcap;
    @FXML
    Button pcapProperties;
    @FXML
    Button savePacket;
    @FXML
    Button nextStreamBtn;
    @FXML
    Button streamEditorModeBtn;
    @FXML
    Button prevStreamBtn;
    @FXML
    Button resetPacket;
    @FXML
    AnchorPane windowContainer;

    // define sub FXML & controllers
    @FXML
    AnchorPane streamProperties;
    @FXML
    StreamPropertiesViewController streamPropertiesController;
    @FXML
    AnchorPane packetViewer;
    @FXML
    PacketViewerController packetViewerController;
    @FXML
    AnchorPane protocolSelection;
    @FXML
    ProtocolSelectionController protocolSelectionController;
    @FXML
    AnchorPane protocolData;
    @FXML
    ProtocolDataController protocolDataController;
    @FXML
    Tab packetViewerTab;
    @FXML
    Tab packetEditorTab;
    @FXML
    Tab fieldEngineTab;
    @FXML
    Tab packetViewerWithTreeTab;
    @FXML
    Tab protocolSelectionTab;
    @FXML
    Tab protocolDataTab;
    @FXML
    Tab advanceSettingsTab;
    @FXML
    AdvancedSettingsController advancedSettingsController;
    @FXML
    Tab streamPropertiesTab;
    @FXML
    TabPane streamTabPane;

    @FXML
    StackPane fieldEditorTopPane;

    @Inject
    FieldEditorController packetBuilderController;

    @Inject
    EventBus eventBus;
    
    PacketInfo packetInfo = null;
    private PacketParser parser;
    private PacketHex packetHex;

    private TextWindowController controller;
    private Profile selectedProfile;
    private boolean isBuildPacket = false;
    private List<Profile> profileList;
    private String yamlFileName;
    private int currentSelectedProfileIndex;
    BuilderDataBinding builderDataBinder;
    TrafficProfile trafficProfile;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        trafficProfile = new TrafficProfile();
        packetHex = new PacketHex(hexPane);
        loadPcap.visibleProperty().bind(packetViewerTab.selectedProperty());
        nextStreamBtn.setGraphic(new ImageView(new Image("/icons/next_stream.png")));
        prevStreamBtn.setGraphic(new ImageView(new Image("/icons/prev_stream.png")));
        packetInfo = new PacketInfo();
        parser = new PacketParser();
    }

    /**
     * Initialize stream builder view
     *
     * @param pcapFileBinary
     * @param profileList
     * @param selectedProfileIndex
     * @param yamlFileName
     * @param type
     */
    public boolean initStreamBuilder(String pcapFileBinary, List<Profile> profileList, int selectedProfileIndex, String yamlFileName, StreamBuilderType type) {
        this.selectedProfile = profileList.get(selectedProfileIndex);
        this.profileList = profileList;
        this.yamlFileName = yamlFileName;
        this.currentSelectedProfileIndex = selectedProfileIndex;

        if (selectedProfile.getStream().getAdvancedMode()
                && !ConnectionManager.getInstance().isScapyConnected()) {
            boolean loop = true;
            while (loop) {
                eventBus.post(new ScapyClientNeedConnectEvent());
                if (ConnectionManager.getInstance().isScapyConnected()) {
                    loop = false;
                }
                else {
                    loop = alertWarning("Can't open packet editor in Advanced mode",
                            "There is no connection to Scapy server."
                                    + "\nPlease refer to documentation about"
                                    + "\nScapy server and advanced mode.");
                }
            }
            if (!ConnectionManager.getInstance().isScapyConnected()) {
                return false;
            }
        }

        packetBuilderController.reset();
        streamPropertiesController.init(profileList, selectedProfileIndex);
        updateNextPrevButtonState();
        switch (type) {
            case BUILD_STREAM:
                initStreamBuilder(new BuilderDataBinding());
                showSimpleModeTabs();
                break;
            case EDIT_STREAM:
                initEditStream(pcapFileBinary);
                if(selectedProfile.getStream().getAdvancedMode()) {
                    showAdvancedModeTabs();
                } else {
                    showSimpleModeTabs();
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * Initialize Edit stream builder in case of edit
     *
     * @param pcapFileBinary
     */
    private void initEditStream(String pcapFileBinary) {
        Stream currentStream = selectedProfile.getStream();
        streamEditorModeBtn.setText(currentStream.getAdvancedMode() ? "Simple mode" : "Advanced mode");
        if (!Util.isNullOrEmpty(currentStream.getPacket().getMeta())) {
            BuilderDataBinding dataBinding = getDataBinding();
            if (dataBinding != null) {
                initStreamBuilder(dataBinding);
                return;
            }
        }
        hideStreamBuilderTab();
        if (pcapFileBinary != null) {
            try {
                isBuildPacket = false;
                File pcapFile = trafficProfile.decodePcapBinary(pcapFileBinary);
                parser.parseFile(pcapFile.getAbsolutePath(), packetInfo);
                packetHex.setData(packetInfo);
                packetBuilderController.loadPcapBinary(Base64.getDecoder().decode(pcapFileBinary.getBytes()));
            } catch (IOException ex) {
                LOG.error("Failed to load PCAP value", ex);
            }
        }
    }
    
    private BuilderDataBinding getDataBinding() {
        return (BuilderDataBinding) Util.deserializeStringToObject(selectedProfile.getStream().getPacket().getMeta());
    }

    /**
     * Initialize Build stream builder in case of edit
     *
     * @param builderDataBinder
     */
    private void initStreamBuilder(BuilderDataBinding builderDataBinder) {

        isBuildPacket = true;
        streamTabPane.getTabs().remove(packetViewerTab);
        String packetEditorModel = selectedProfile.getStream().getPacket().getModel();
        if (!Strings.isNullOrEmpty(packetEditorModel)) {
            packetBuilderController.loadUserModel(packetEditorModel);
        }
        this.builderDataBinder = builderDataBinder;
        // initialize builder tabs
        protocolSelectionController.bindSelections(builderDataBinder.getProtocolSelection());
        protocolDataController.bindSelection(builderDataBinder);
        advancedSettingsController.bindSelections(builderDataBinder.getAdvancedPropertiesDB());

        packetViewerWithTreeTab.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                try {
                    // handle it and send the result to packet viewer controller
                    packetViewerController.setPacketView(protocolDataController.getProtocolData());
                } catch (Exception ex) {
                    LOG.error("Error creating packet", ex);
                }
            }
        });
        Stream currentStream = selectedProfile.getStream();
        currentStream.getFlagsProperty().addListener(((observable, oldValue, newValue) -> {
            builderDataBinder.getStreamSelection().srcMacModePropertyProperty().setValue(((int) newValue & 1) == 1);
            builderDataBinder.getStreamSelection().dstMacModePropertyProperty().setValue(((int) newValue & 2) == 2);
            
        }));
    }

    /**
     * Hide stream builder related tabs
     */
    private void hideStreamBuilderTab() {
        streamTabPane.getTabs().remove(protocolDataTab);
        streamTabPane.getTabs().remove(protocolSelectionTab);
        streamTabPane.getTabs().remove(advanceSettingsTab);
        streamTabPane.getTabs().remove(packetViewerWithTreeTab);
    }

    private void showSimpleModeTabs() {
        streamTabPane.getTabs().clear();
        streamTabPane.getTabs().addAll(
                streamPropertiesTab,
                protocolSelectionTab,
                protocolDataTab,
                advanceSettingsTab,
                packetViewerWithTreeTab
        );
    }

    private void showAdvancedModeTabs() {
        streamTabPane.getTabs().clear();
        streamTabPane.getTabs().addAll(streamPropertiesTab, packetEditorTab, fieldEngineTab);
    }
    
    /**
     * Load Pcap button click handler
     *
     * @param event
     * @throws Exception
     */
    public void loadPcapFired(ActionEvent event) throws Exception {
        String loadLocation = PreferencesManager.getInstance().getLoadLocation();
        Window owner = ((Button) (event.getSource())).getScene().getWindow();
        File selectedFile = FileManager.getSelectedFile("Open Pcap File", "", owner, FileType.PCAP, loadLocation, false);

        if (selectedFile != null) {
            packetInfo = new PacketInfo();
            parser.parseFile(selectedFile.getAbsolutePath(), packetInfo);
            packetHex.setData(packetInfo);
            String encodedPcapFile = trafficProfile.encodePcapFile(selectedFile.getAbsolutePath());
            selectedProfile.getStream().getPacket().setBinary(encodedPcapFile);

        } else {
            LOG.info("file = null");
        }
    }

    /**
     * Close button click handler
     *
     * @param event
     */
    @FXML
    public void handleCloseDialog(final MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.hide();
    }

    /**
     * JSON window button click handler
     *
     * @param event
     */
    @FXML
    public void updateJSONWindow(ActionEvent event) {
        try {
            updateCurrentProfile();
            if (streamPropertiesController.isValidStreamPropertiesFields()) {
                if (controller != null && controller.isShown()) {
                    controller.setContent(Util.toPrettyFormat(new Gson().toJson(selectedProfile)));
                    controller.focus();
                } else {
                    viewStreamDetailWindow(selectedProfile);
                }
            }
        } catch (Exception ex) {
            LOG.error("Error in viewing object", ex);
        }
    }

    /**
     * View stream detail window
     *
     * @param profile
     */
    private void viewStreamDetailWindow(Profile profile) {
        try {
            Stage currentStage = (Stage) windowContainer.getScene().getWindow();
            DialogWindow window = new DialogWindow("TextWindow.fxml", "View stream", 300, 150, false, currentStage);
            controller = (TextWindowController) window.getController();

            if (selectedProfile != null) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(profile);
                controller.setContent(Util.toPrettyFormat(jsonString));
            }
            window.show(false);
        } catch (IOException ex) {
            LOG.error("Error in viewing object", ex);
        }
    }

    /**
     * Save button click handler
     *
     * @param event
     */
    @FXML
    public void saveProfileBtnClicked(ActionEvent event) {
        if (saveStream()) {
            // close the dialog
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.hide();
        }
    }

    /**
     * save stream Return true if stream saved successfully otherwise return
     * false
     *
     * @return
     */
    private boolean saveStream() {
        try {
            String fieldEngineError = packetBuilderController.getFieldEngineError();
            if (!Strings.isNullOrEmpty(fieldEngineError))  {
                streamTabPane.getSelectionModel().select(fieldEngineTab);
                LOG.error("Unable to save stream due to errors in Field Engine:" + fieldEngineError);
                return false;
            }
            updateCurrentProfile();
            if (streamPropertiesController.isValidStreamPropertiesFields()) {
                String yamlData = trafficProfile.convertTrafficProfileToYaml(profileList.toArray(new Profile[profileList.size()]));
                FileUtils.writeStringToFile(new File(yamlFileName), yamlData);
                Util.optimizeMemory();
                return true;
            }
        } catch (Exception ex) {
            LOG.error("Error Saving yaml file", ex);
        }
        return false;
    }

    /**
     * Next stream button click handler
     *
     * @param event
     */
    @FXML
    public void nextStreamBtnClicked(ActionEvent event) {
        nextStreamBtn.setDisable(true);
        loadProfile(true);
    }

    /**
     * Next stream button click handler
     *
     * @param event
     */
    @FXML
    public void switchEditorMode(ActionEvent event) throws Exception {
        Stream currentStream = streamPropertiesController.getUpdatedSelectedProfile().getStream();
        boolean advancedMode = currentStream.getAdvancedMode();

        try {
            if (!ConnectionManager.getInstance().isScapyConnected()) {
                eventBus.post(new ScapyClientNeedConnectEvent());
            }
            packetBuilderController.loadSimpleUserModel(builderDataBinder.serializeAsPacketModel());
            if (advancedMode) {
                streamEditorModeBtn.setText("Advanced mode");
                currentStream.setAdvancedMode(false);
                showSimpleModeTabs();
            } else {
                if (ConnectionManager.getInstance().isScapyConnected()) {
                    streamEditorModeBtn.setText("Simple mode");
                    currentStream.setAdvancedMode(true);
                    showAdvancedModeTabs();
                } else {
                    alertWarning("Can't open Advanced mode", "There is no connection to Scapy server."
                            + "\nPlease refer to documentation about"
                            + "\nScapy server and advanced configuration mode.");
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to open advanced mode due to: " + e.getMessage());
            alertWarning("Can't open Advanced mode", "Some errors occurred. See logs for more details.");
        }
    }

    /**
     * Previous stream button click handler
     *
     * @param event
     */
    @FXML
    public void prevStreamBtnClick(ActionEvent event) {
        prevStreamBtn.setDisable(true);
        loadProfile(false);
    }

    /**
     * Load profile
     *
     * @param isNext
     */
    private void loadProfile(boolean isNext) {
        try {
            Util.optimizeMemory();
            updateCurrentProfile();
            if (streamPropertiesController.isValidStreamPropertiesFields()) {
                if (isNext) {
                    this.currentSelectedProfileIndex += 1;
                } else {
                    this.currentSelectedProfileIndex -= 1;
                }
                loadStream();
            }
        } catch (Exception ex) {
            LOG.error("Invalid data", ex);
        }
        updateNextPrevButtonState();
    }

    /**
     * Reset tabs
     */
    private void resetTabs() {
        streamTabPane.getTabs().clear();
        streamTabPane.getTabs().add(streamPropertiesTab);
        streamTabPane.getTabs().add(packetViewerTab);
        streamTabPane.getTabs().add(protocolSelectionTab);
        streamTabPane.getTabs().add(protocolDataTab);
        streamTabPane.getTabs().add(advanceSettingsTab);
        streamTabPane.getTabs().add(packetViewerWithTreeTab);
        streamTabPane.getTabs().add(packetEditorTab);
        streamTabPane.getTabs().add(fieldEngineTab);
    }

    /**
     * Update next/previous stream button disable state
     */
    private void updateNextPrevButtonState() {
        nextStreamBtn.setDisable((currentSelectedProfileIndex >= profileList.size() - 1));
        prevStreamBtn.setDisable((currentSelectedProfileIndex == 0));
//        nextBtnCLicked = false;
//        prevBtnCLicked = false;
    }

    /**
     * Load current stream
     */
    private void loadStream() {
        resetTabs();
        streamTabPane.getSelectionModel().select(streamPropertiesTab);
        this.selectedProfile = profileList.get(currentSelectedProfileIndex);
        String windowTitle = "Edit Stream (" + selectedProfile.getName() + ")";
        // update window title
        Stage stage = (Stage) streamTabPane.getScene().getWindow();
        stage.setTitle(windowTitle);

        streamPropertiesController.init(profileList, currentSelectedProfileIndex);
        initEditStream(selectedProfile.getStream().getPacket().getBinary());
    }

    /**
     * Update current profile
     *
     * @throws Exception
     */
    private void updateCurrentProfile() throws Exception {
        selectedProfile = streamPropertiesController.getUpdatedSelectedProfile();
        String hexPacket = null;
        if (packetHex != null && !isBuildPacket) {
            hexPacket = packetHex.getPacketHexFromList();
        } else if (isBuildPacket) {
            hexPacket = PacketBuilderHelper.getPacketHex(protocolDataController.getProtocolData().getPacket().getRawData());
            selectedProfile.getStream().setAdditionalProperties(protocolDataController.getVm(advancedSettingsController.getCacheSize()));
            // save stream selected in stream property
            selectedProfile.getStream().getPacket().setMeta(Util.serializeObjectToString(builderDataBinder));
        }
        String encodedBinaryPacket = trafficProfile.encodeBinaryFromHexString(hexPacket);
        Packet packet = selectedProfile.getStream().getPacket();
        
        if (selectedProfile.getStream().getAdvancedMode()) {
            packet.setBinary(packetBuilderController.getModel().getPkt().binary);
            packet.setModel(packetBuilderController.getModel().serialize());
            selectedProfile.getStream().setAdditionalProperties(packetBuilderController.getPktVmInstructions());
        } else {
            packet.setBinary(encodedBinaryPacket);
            packet.setModel("");
        }
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        // ignore event
    }

    @Override
    public void onEscapKeyPressed() {
        // ignoring global escape
    }

    private boolean alertWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(content + "\nWould you like to change ip or port and try connection again ?");
        alert.setTitle("Warning");

        ButtonType buttonTypeOne = new ButtonType("Try", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        alert.showAndWait();
        ButtonType res = alert.getResult();
        if (res.getButtonData()== ButtonBar.ButtonData.CANCEL_CLOSE) {
            return false;
        }
        return true;
    }

}

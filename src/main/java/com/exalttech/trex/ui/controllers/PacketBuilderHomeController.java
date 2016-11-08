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

import com.exalttech.trex.remote.models.profiles.Packet;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.remote.models.profiles.Stream;
import com.exalttech.trex.ui.StreamBuilderType;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.dialog.DialogWindow;
import com.exalttech.trex.ui.models.PacketInfo;
import com.exalttech.trex.ui.views.models.TableProfileStream;
import com.exalttech.trex.ui.views.streams.binders.BuilderDataBinding;
import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.exalttech.trex.ui.views.streams.viewer.PacketHex;
import com.exalttech.trex.ui.views.streams.viewer.PacketParser;
import com.exalttech.trex.util.TrafficProfile;
import com.exalttech.trex.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.xored.javafx.packeteditor.controllers.FieldEditorController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


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
    Button pcapProperties;
    @FXML
    Button savePacket;
    @FXML
    Button nextStreamBtn;
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
    public void initStreamBuilder(TableProfileStream selectedStream, List<Profile> profileList, int selectedProfileIndex, String yamlFileName, StreamBuilderType type) {
        packetBuilderController.initAcceleratorsHandler(windowContainer.getScene());
        this.selectedProfile = profileList.get(selectedProfileIndex);
        this.profileList = profileList;
        this.yamlFileName = yamlFileName;
        this.currentSelectedProfileIndex = selectedProfileIndex;

        streamPropertiesController.init(profileList, selectedProfileIndex);
        updateNextPrevButtonState();
        packetBuilderController.reset();
        switch (type) {
            case ADD_STREAM:
                hideStreamBuilderTab();
                break;
            case BUILD_STREAM:
                initStreamBuilder(new BuilderDataBinding());
                break;
            case EDIT_STREAM:
                initEditStream(selectedStream);
                break;
            default:
                break;
        }
    }

    /**
     * Initialize Edit stream builder in case of edit
     *
     * @param tableProfileStream
     */
    private void initEditStream(TableProfileStream tableProfileStream) {
        String pcapFileBinary = tableProfileStream.getPcapBinary();
        String pktModel = tableProfileStream.getPktModel();
        try {
            if(!StringUtils.isEmpty(pktModel)) {
                packetBuilderController.loadUserModel(pktModel);
            } else if (!StringUtils.isEmpty(pcapFileBinary)) {
                packetBuilderController.loadPcapBinary(Base64.getDecoder().decode(pcapFileBinary.getBytes()));
            }
        } catch (IOException e) {
            LOG.error("Failed to load Packet Builder", e);
        }
        if (!Util.isNullOrEmpty(selectedProfile.getStream().getPacket().getMeta())) {
            BuilderDataBinding dataBinding = (BuilderDataBinding) Util.deserializeStringToObject(selectedProfile.getStream().getPacket().getMeta());
            if (dataBinding != null) {
                initStreamBuilder(dataBinding);
                return;
            }
        }
        hideStreamBuilderTab();
    }

    /**
     * Initialize Build stream builder in case of edit
     *
     * @param builderDataBinder
     */
    private void initStreamBuilder(BuilderDataBinding builderDataBinder) {

        isBuildPacket = true;
        this.builderDataBinder = builderDataBinder;
        // initialize builder tabs
        protocolSelectionController.bindSelections(builderDataBinder.getProtocolSelection());
        protocolDataController.bindSelection(builderDataBinder);
    }

    /**
     * Hide stream builder related tabs
     */
    private void hideStreamBuilderTab() {
        streamTabPane.getTabs().remove(protocolDataTab);
        streamTabPane.getTabs().remove(protocolSelectionTab);
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
        
        Stream stream = selectedProfile.getStream();
        Packet packet = stream.getPacket();
        
        selectedProfile = streamPropertiesController.getUpdatedSelectedProfile();
        String hexPacket = null;
        if (packetHex != null && !isBuildPacket) {
            hexPacket = packetHex.getPacketHexFromList();
        } else if (isBuildPacket) {
            hexPacket = PacketBuilderHelper.getPacketHex(protocolDataController.getProtocolData().getPacket().getRawData());
            stream.setAdditionalProperties(protocolDataController.getVm());
            stream.setFlags(protocolDataController.getFlagsValue());
            // save stream selected in stream property
            packet.setMeta(Util.serializeObjectToString(builderDataBinder));
        }
        packet.setBinary(packetBuilderController.getBinaryPkt());
        packet.setModel(packetBuilderController.getModel().serialize());
        stream.setVm(packetBuilderController.getPktVmInstructions());
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        // ignore event
    }

    @Override
    public void onEscapKeyPressed() {
        // ignoring global escape
    }
}

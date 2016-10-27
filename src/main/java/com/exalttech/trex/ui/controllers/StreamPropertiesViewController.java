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

import com.exalttech.trex.remote.models.profiles.Mode;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.util.Util;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

/**
 * FXML Controller class
 *
 * @author Georgekh
 */
public class StreamPropertiesViewController implements Initializable, EventHandler<KeyEvent> {

    // mode type
    @FXML
    ToggleGroup streamModeGroup;
    @FXML
    RadioButton continuousMode;
    @FXML
    RadioButton burstMode;
    @FXML
    RadioButton multiBurstMode;
    // Misc
    @FXML
    CheckBox enabledCB;
    @FXML
    CheckBox selfStartCB;
    //Numbers
    @FXML
    AnchorPane numbersContainer;
    @FXML
    TextField numOfPacketTB;
    @FXML
    TextField numOfBurstTB;
    @FXML
    TextField packetPBurstTB;
    @FXML
    Label numOfPacketLabel;
    @FXML
    Label packetPBurstTitle;
    @FXML
    Label numOfBurstLabel;
    // rate
    @FXML
    ToggleGroup rateGroup;
    @FXML
    RadioButton packetSecRG;
    @FXML
    RadioButton packetBitsSecRG;

    @FXML
    TextField packetSecTB;
    @FXML
    TextField packetBitsSecTB;
    @FXML
    TextField burstSecTB;
    // next stream
    @FXML
    AnchorPane afterStreamContainer;
    @FXML
    ToggleGroup nextStreamGroup;
    @FXML
    RadioButton stopRG;
    @FXML
    RadioButton gotoRG;
    @FXML
    ComboBox nextStreamCB;
    @FXML
    CheckBox timeInLoopCB;
    @FXML
    TextField timeInLoopTF;
    // gaps
    @FXML
    ImageView gapsImageContainer;
    @FXML
    TextField isgTF;
    @FXML
    TextField ibgTF;
    @FXML
    Label ibgTitle;
    @FXML
    TextField ipgTF;

    @FXML
    CheckBox rxEnableCB;
    @FXML
    TextField rxStreamID;
    @FXML
    Label rxStreamIDLabel;

    private List<Profile> profileList;
    private Profile selectedProfile;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initStreamPropertiesEvent();
    }

    /**
     * Initialize profile and stream properties
     *
     * @param profileList
     * @param selectedProfileIndex
     */
    public void init(List<Profile> profileList, int selectedProfileIndex) {
        this.profileList = profileList;
        this.selectedProfile = profileList.get(selectedProfileIndex);
        fillStreamProperties(selectedProfileIndex);
    }

    /**
     * Initialize events and properties binding
     */
    private void initStreamPropertiesEvent() {
        packetSecTB.disableProperty().bind(packetSecRG.selectedProperty().not());
        packetBitsSecTB.disableProperty().bind(packetBitsSecRG.selectedProperty().not());

        timeInLoopTF.disableProperty().bind(timeInLoopCB.selectedProperty().not());
        nextStreamCB.disableProperty().bind(gotoRG.selectedProperty().not());

        streamModeGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (newValue == continuousMode) {
                handleContinousModeSelection();
            } else if (newValue == burstMode) {
                handleBurstModeSelection();
            } else {
                handleMultiBurstModeSelection();
            }
        });

        nextStreamGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            boolean disableTimeToLoopCB = true;
            if (newValue == gotoRG) {
                disableTimeToLoopCB = false;
            }
            timeInLoopCB.setSelected(false);
            timeInLoopCB.setDisable(disableTimeToLoopCB);
        });

        timeInLoopCB.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                timeInLoopTF.setText("0");
            }
        });

        packetSecTB.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            double ipgValue = 0;
            if (!Util.isNullOrEmpty(newValue) && Double.parseDouble(newValue) > 0) {
                ipgValue = 1.0 / Double.parseDouble(newValue);
            }
            ipgTF.setText(Util.getFormatedFraction(ipgValue));
        });

        // bind RX fields with rx enable property
        rxStreamID.disableProperty().bind(rxEnableCB.selectedProperty().not());
        rxStreamIDLabel.disableProperty().bind(rxEnableCB.selectedProperty().not());

        // add key press event to allow digits only
        numOfPacketTB.addEventFilter(KeyEvent.KEY_TYPED, this);
        packetSecTB.addEventFilter(KeyEvent.KEY_TYPED, this);
        numOfBurstTB.addEventFilter(KeyEvent.KEY_TYPED, this);
        packetPBurstTB.addEventFilter(KeyEvent.KEY_TYPED, this);
        packetBitsSecTB.addEventFilter(KeyEvent.KEY_TYPED, this);
        timeInLoopTF.addEventFilter(KeyEvent.KEY_TYPED, this);
        rxStreamID.addEventFilter(KeyEvent.KEY_TYPED, this);

        // set input validation on ibg field
        final UnaryOperator<TextFormatter.Change> formatter = Util.getTextChangeFormatter(Util.getUnitRegex(true));
        ibgTF.setTextFormatter(new TextFormatter<>(formatter));
        isgTF.setTextFormatter(new TextFormatter<>(formatter));
    }

    /**
     * Handle continuous mode selection
     */
    private void handleContinousModeSelection() {
        streamModeGroup.selectToggle(continuousMode);
        streamModeGroup.setUserData(StreamMode.CONTINUOUS);

        // disable numbers 
        numbersContainer.setDisable(true);
        packetPBurstTB.setVisible(true);
        packetPBurstTitle.setVisible(true);
        numOfBurstTB.setText("1");
        packetBitsSecRG.setDisable(false);

        // disable next stream
        afterStreamContainer.setDisable(true);
        stopRG.setSelected(true);

        // disable ibg
        ibgTF.setDisable(true);
        ibgTitle.setDisable(true);

        // define gaps view
        gapsImageContainer.setImage(new Image("/icons/" + StreamMode.CONTINUOUS.getImageName()));
    }

    /**
     * Handle Burst mode selection
     */
    private void handleBurstModeSelection() {
        streamModeGroup.selectToggle(burstMode);
        streamModeGroup.setUserData(StreamMode.SINGLE_BURST);

        // disable/hide part of numbers 
        numbersContainer.setDisable(false);
        numOfPacketTB.setDisable(false);
        numOfPacketLabel.setDisable(false);
        numOfBurstTB.setDisable(true);
        numOfBurstTB.setText("1");
        numOfBurstLabel.setDisable(true);
        packetPBurstTB.setVisible(false);
        packetPBurstTitle.setVisible(false);
        packetSecRG.setSelected(true);
        packetBitsSecRG.setDisable(true);

        // enable next stream
        afterStreamContainer.setDisable(false);

        // disable ibg
        ibgTF.setDisable(true);
        ibgTitle.setDisable(true);

        // define gaps view
        gapsImageContainer.setImage(new Image("/icons/" + StreamMode.SINGLE_BURST.getImageName()));
    }

    /**
     * Handle multi burst mode selection
     */
    private void handleMultiBurstModeSelection() {
        streamModeGroup.selectToggle(multiBurstMode);
        streamModeGroup.setUserData(StreamMode.MULTI_BURST);

        // disable numbers
        numbersContainer.setDisable(false);
        numOfPacketTB.setDisable(true);
        numOfPacketLabel.setDisable(true);
        numOfBurstTB.setDisable(false);
        numOfBurstLabel.setDisable(false);
        packetPBurstTB.setDisable(false);
        packetPBurstTB.setVisible(true);
        packetPBurstTitle.setVisible(true);
        packetSecRG.setSelected(true);
        packetBitsSecRG.setDisable(true);

        // enable next stream
        afterStreamContainer.setDisable(false);

        // enable ibg
        ibgTF.setDisable(false);
        ibgTitle.setDisable(false);

        // define gaps view
        gapsImageContainer.setImage(new Image("/icons/" + StreamMode.MULTI_BURST.getImageName()));

        // set numOf Burst value
        Mode mode = selectedProfile.getStream().getMode();
        int numOfBurst = mode.getCount();
        if (numOfBurst < 2) {
            numOfBurst = 2;
        }
        numOfBurstTB.setText(String.valueOf(numOfBurst));
    }

    /**
     * key press event handler
     *
     * @param event
     */
    @Override
    public void handle(KeyEvent event) {
        if (!event.getCharacter().matches("[0-9]") && event.getCode() != KeyCode.BACK_SPACE) {
            event.consume();
        }
    }

    /**
     * Fills stream properties value
     *
     * @param currentSelectedIndex
     */
    private void fillStreamProperties(int currentSelectedIndex) {
        Mode mode = selectedProfile.getStream().getMode();
        enabledCB.setSelected(selectedProfile.getStream().isEnabled());
        selfStartCB.setSelected(selectedProfile.getStream().isSelfStart());
        numOfPacketTB.setText(String.valueOf(mode.getTotalPkts()));
        packetPBurstTB.setText(String.valueOf(mode.getPacketsPerBurst()));
        numOfBurstTB.setText(String.valueOf(mode.getCount()));
        packetSecTB.setText(String.valueOf(mode.getPps()));
        isgTF.setText(convertNumToUnit(selectedProfile.getStream().getIsg()));
        ibgTF.setText(convertNumToUnit(mode.getIbg()));

        rxStreamID.setText(String.valueOf(selectedProfile.getStream().getFlowStats().getStreamID()));
        rxEnableCB.setSelected(selectedProfile.getStream().getFlowStats().getEnabled());

        fillGotoStreamOption(currentSelectedIndex);
        stopRG.setSelected(true);
        if (!"-1".equals(selectedProfile.getNext())) {
            gotoRG.setSelected(true);
        }
        timeInLoopTF.setText(String.valueOf(selectedProfile.getStream().getActionCount()));
        timeInLoopCB.setSelected(selectedProfile.getStream().getActionCount() > 0);

        StreamMode streamMode = StreamMode.CONTINUOUS;
        if (!Util.isNullOrEmpty(mode.getType())) {
            streamMode = StreamMode.getMode(mode.getType());
        }
        switch (streamMode) {
            case CONTINUOUS:
                handleContinousModeSelection();
                break;
            case SINGLE_BURST:
                handleBurstModeSelection();
                break;
            case MULTI_BURST:
                handleMultiBurstModeSelection();
                break;
            default:
                break;
        }
    }

    /**
     * Fill goto Stream value
     *
     * @param currentSelectedIndex
     */
    private void fillGotoStreamOption(int currentSelectedIndex) {
        nextStreamCB.getItems().clear();
        if (currentSelectedIndex > 0) {
            nextStreamCB.getItems().add("First Stream");
        }
        for (Profile p : profileList) {
            if (!p.getName().equalsIgnoreCase(selectedProfile.getName())) {
                nextStreamCB.getItems().add(p.getName());
            }
            if (p.getName().equalsIgnoreCase(selectedProfile.getNext())) {
                nextStreamCB.getSelectionModel().select(p.getName());
            }
        }
    }

    /**
     * Return selected profile after update it according to selection
     *
     * @return
     * @throws Exception
     */
    public Profile getUpdatedSelectedProfile() throws Exception {
        // update Misc
        selectedProfile.getStream().setEnabled(enabledCB.isSelected());
        selectedProfile.getStream().setSelfStart(selfStartCB.isSelected());

        // update rx
        selectedProfile.getStream().getFlowStats().setEnabled(rxEnableCB.isSelected());
        selectedProfile.getStream().getFlowStats().setStreamID(Util.getIntFromString(rxStreamID.getText()));

        String ruleType = null;
        if (rxEnableCB.isSelected()) {
            ruleType = "latency";
        }
        selectedProfile.getStream().getFlowStats().setRuleType(ruleType);

        switch ((StreamMode) streamModeGroup.getUserData()) {
            case CONTINUOUS:
                updateContinuousProfile(selectedProfile);
                break;
            case SINGLE_BURST:
                updateSingleBurstProfile(selectedProfile);
                break;
            case MULTI_BURST:
                updateMultiBurstProfile(selectedProfile);
                break;
            default:
                break;
        }
        return selectedProfile;
    }

    /**
     * Update profile according to continuous mode selection
     *
     * @param profile
     */
    private void updateContinuousProfile(Profile profile) {
        // update mode
        profile.getStream().getMode().setType(StreamMode.CONTINUOUS.toString());

        // update rate
        if (rateGroup.getSelectedToggle() == packetSecRG) {
            profile.getStream().getMode().setPps(Double.parseDouble(packetSecTB.getText()));
        }

        // update next stream 
        updateNextStream(profile);
        // gaps
        profile.getStream().setIsg(convertUnitToNum(isgTF.getText()));
    }

    /**
     * Update profile according to single burst mode selection
     *
     * @param profile
     */
    private void updateSingleBurstProfile(Profile profile) {
        // update mode
        profile.getStream().getMode().setType(StreamMode.SINGLE_BURST.toString());

        // update numbers
        profile.getStream().getMode().setTotalPkts(getIntValue(numOfPacketTB.getText()));

        //no property for number of burst yet to update
        profile.getStream().getMode().setPacketsPerBurst(0);

        // update rate
        profile.getStream().getMode().setPps(Double.parseDouble(packetSecTB.getText()));

        // update next stream
        updateNextStream(profile);

        // gaps
        profile.getStream().setIsg(convertUnitToNum(isgTF.getText()));
    }

    /**
     * Update profile according to multi burst mode selection
     *
     * @param profile
     */
    private void updateMultiBurstProfile(Profile profile) {
        // update mode
        profile.getStream().getMode().setType(StreamMode.MULTI_BURST.toString());

        // update numbers
        profile.getStream().getMode().setPacketsPerBurst(getIntValue(packetPBurstTB.getText()));

        // update number of bursts
        profile.getStream().getMode().setCount(getIntValue(numOfBurstTB.getText()));

        // update rate
        profile.getStream().getMode().setPps(Double.parseDouble(packetSecTB.getText()));

        // update next stream
        updateNextStream(profile);

        // gaps
        profile.getStream().setIsg(convertUnitToNum(isgTF.getText()));
        String ibgValue = !Util.isNullOrEmpty(ibgTF.getText()) ? ibgTF.getText() : "0.0";
        profile.getStream().getMode().setIbg(convertUnitToNum(ibgValue));
    }

    /**
     * Return within the stream properties value is valid
     *
     * @return
     */
    public boolean isValidStreamPropertiesFields() {
        String errMsg = "";
        boolean valid = true;
        if (StringUtils.isEmpty(selectedProfile.getStream().getPacket().getBinary())) {
            errMsg = "Stream can not have an empty packet.";
            valid = false;
        } else {
            double timeInloop = Double.parseDouble(timeInLoopTF.getText());
            if (timeInLoopCB.isSelected() && (timeInloop <= 0 || timeInloop > 64000)) {
                errMsg = "Time in loop should be between > 0 and < 64K";
                valid = false;
            }
        }
        boolean validInputData = validInputData();
        if (!valid && validInputData) {
            Alert alert = Util.getAlert(Alert.AlertType.ERROR);
            alert.setContentText(errMsg);
            alert.showAndWait();
        }
        return valid && validInputData;
    }

    /**
     * Return within the input fields value is valid
     *
     * @return
     */
    private boolean validInputData() {
        String errMsg = "";
        boolean valid = true;
        if (Double.parseDouble(packetSecTB.getText()) <= 0) {
            errMsg = "Packet/sec should be > 0";
            valid = false;
        } else if ((Util.isNullOrEmpty(numOfPacketTB.getText()) || Double.parseDouble(numOfPacketTB.getText()) <= 0)
                && (StreamMode) streamModeGroup.getUserData() == StreamMode.SINGLE_BURST) {
            errMsg = "Number of packets should be > 0";
            valid = false;
        } else if ((StreamMode) streamModeGroup.getUserData() == StreamMode.MULTI_BURST) {
            if (Util.isNullOrEmpty(numOfBurstTB.getText()) || Double.parseDouble(numOfBurstTB.getText()) < 2) {
                errMsg = "Number of burst should be > 1";
                valid = false;
            } else if (Util.isNullOrEmpty(packetPBurstTB.getText()) || Double.parseDouble(packetPBurstTB.getText()) <= 0) {
                errMsg = "Packer per Burst should be > 0";
                valid = false;
            }
        }
        if (!valid) {
            Alert alert = Util.getAlert(Alert.AlertType.ERROR);
            alert.setContentText(errMsg);
            alert.showAndWait();
        }
        return valid;
    }

    /**
     * Update next stream
     *
     * @param profile
     */
    private void updateNextStream(Profile profile) {
        profile.setNext("-1");
        if (nextStreamGroup.getSelectedToggle() == gotoRG) {
            profile.setNext(String.valueOf(nextStreamCB.getValue()));
            if ("First Stream".equals(nextStreamCB.getValue())) {
                profile.setNext(profileList.get(0).getName());
            }

            profile.getStream().setActionCount(Integer.parseInt(timeInLoopTF.getText()));
        }
    }

    /**
     * Return integer value from number String value
     *
     * @param text
     * @return
     */
    private int getIntValue(String text) {
        return Util.isNullOrEmpty(text) ? 0 : Integer.parseInt(text);
    }

    /**
     * Convert unit to related number
     *
     * @param valueData
     * @return
     */
    public double convertUnitToNum(String valueData) {
        return Util.convertUnitToNum(valueData) * 1000000;
    }

    /**
     * Convert number to related unit format
     *
     * @param value
     * @return
     */
    public String convertNumToUnit(double value) {
        return Util.convertNumToUnit(value / 1000000);
    }

    /**
     * Enumerator that present stream mode type
     */
    private enum StreamMode {
        CONTINUOUS,
        SINGLE_BURST,
        MULTI_BURST;

        public String getImageName() {
            return name().toLowerCase() + ".png";
        }

        public static StreamMode getMode(String modeName) {
            return StreamMode.valueOf(modeName.toUpperCase());
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.trafficProfile;

import com.exalttech.trex.ui.UIBaseTest;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

/**
 * traffic profile test base class
 *
 * @author GeorgeKH
 */
public class TrafficProfileTestBase extends UIBaseTest {

    /**
     * Open traffic profile view
     */
    protected void openTrafficProfile() {
        clickOn("#trafficProfileMenu", MouseButton.PRIMARY);
        sleep(500);
        clickOn("#trafficProfileItem", MouseButton.PRIMARY);
    }

    /**
     * Add new profile/stream name field
     *
     * @param name
     */
    protected void addNewProfileStream(String name) {
        waitForNode("#nameTF");
        TextField nameTF = find("#nameTF");
        nameTF.setText(name);
        Button okButton = find("OK");
        clickOn(okButton);
    }

    /**
     * Add new stream
     * @param streamName 
     */
    protected void addNewStream(String streamName) {
        clickOn("#buildStreamBtn");
        addNewProfileStream(streamName);
        waitForNode("#protocolSelectionTab");
    }

    /**
     * Select profile
     * @param profileName 
     */
    protected void selectProfile(String profileName) {
        moveTo(profileName);
        clickOn(profileName);
        waitForNode("#buildStreamBtn");
    }

    /**
     * Add vlan
     */
    protected void addVlan() {
        clickOn("#protocolSelectionTab");
        waitForNode("#taggedVlanRB");
        clickOn("#taggedVlanRB");
    }

    /**
     * Add payload
     * @param type 
     */
    protected void addPayload(String type) {
        clickOn("#protocolDataTab");
        waitForNode("Payload Data");
        clickOn("Payload Data");
        waitForNode("#payloadType");
        ComboBox payloadType = find("#payloadType");
        payloadType.getSelectionModel().select(type);
    }

    /**
     * save stream
     */
    protected void saveStream() {
        clickOn("#savePacket");
        sleep(500);
    }

    /**
     * Verify stream created
     * @param profileName
     * @param streamName 
     */
    protected void verifyStreamCreated(String profileName, String streamName) {
        selectProfile(profileName);
        verifyTableHasElement("#streamTableView", streamName);
    }

    /**
     * Open stream properties
     * @param streamName 
     */
    protected void openStreamProperties(String streamName) {
        moveTo(streamName);
        clickOn(streamName);
        sleep(500);
        Button editBtn = find("#editStreanBtn");
        clickOn(editBtn);
        waitForNode("#protocolSelectionTab");
    }

    /**
     * verify vlan selection
     */
    protected void verifyVlanSelection() {
        clickOn("#protocolSelectionTab");
        waitForNode("#taggedVlanRB");
        verifRadioButtonSelected("#taggedVlanRB");
    }

    /**
     * verify payload
     * @param type 
     */
    protected void verifyPayload(String type) {
        clickOn("#protocolDataTab");
        waitForNode("Payload Data");
        clickOn("Payload Data");
        waitForNode("#payloadType");
        verifyComboBoxSelection("#payloadType", type);
    }

    /**
     * Close stream properties window
     */
    protected void closeStreamProperties() {
        clickOn("Cancel");
        sleep(500);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.trafficProfile;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.testng.annotations.Test;

/**
 * Ethernet stream test
 *
 * @author GeorgeKH
 */
public class EthernetStreamTest extends TrafficProfileTestBase {

    @Test(dependsOnGroups = {"createProfileGroup"})
    public void addEtherneWithoutVlanFixPayloadTest() {
        createEthernetStream("ethernetWithoutVlanFixPayload");
        addPayload("Fixed Word");
        saveStream();
        verifyStreamCreated("testProfile.yaml", "ethernetWithoutVlanFixPayload");
        verifyEthernetSelection("ethernetWithoutVlanFixPayload");
        verifyPayload("Fixed Word");
        closeStreamProperties();
    }

    @Test(dependsOnGroups = {"createProfileGroup"})
    public void addEtherneWithVlanFixPayloadTest() {
        createEthernetStream("ethernetWithVlanFixPayload");
        addVlan();
        addPayload("Fixed Word");
        saveStream();
        verifyStreamCreated("testProfile.yaml", "ethernetWithVlanFixPayload");
        verifyEthernetSelection("ethernetWithVlanFixPayload");
        verifyVlanSelection();
        verifyPayload("Fixed Word");
        closeStreamProperties();
    }
    
    /**
     * Create Ethernet stream
     * @param streamName 
     */
    private void createEthernetStream(String streamName){
        selectProfile("testProfile.yaml");

        // add stream
        addNewStream(streamName);

        // set l3 none
        setEthernetSelection();

        //Set protocol Data
        setEthernetMacInfo();
    }
    
    /**
     * Set ethernet selection
     */
    private void setEthernetSelection() {
        clickOn("#protocolSelectionTab");
        waitForNode("#l3NoneRB");
        clickOn("#l3NoneRB");
    }

    /**
     * Fill Ethernet mac information
     */
    private void setEthernetMacInfo() {
        clickOn("#protocolDataTab");
        waitForNode("Media Access Control");
        clickOn("Media Access Control");
        waitForNode("#macDstAddress");
        interact(() -> {
            TextField macDstAddress = find(("#macDstAddress"));
            macDstAddress.setText("12:00:00:00:00:22");

            ComboBox dstMode = find("#macDstMode");
            dstMode.getSelectionModel().select("Fixed");

            TextField macSrcAddress = find(("#macSrcAddress"));
            macSrcAddress.setText("22:00:00:00:00:00");

            ComboBox srcMode = find("#macsrcMode");
            srcMode.getSelectionModel().select("Increment");
        });
    }

    /**
     * Verify mac information
     */
    private void verifyMacInformation() {
        clickOn("#protocolDataTab");
        waitForNode("Media Access Control");
        clickOn("Media Access Control");
        waitForNode("#macDstAddress");
        verifyTextFieldValue("#macDstAddress", "12:00:00:00:00:22");
        verifyComboBoxSelection("#macDstMode", "Fixed");
        verifyTextFieldValue("#macSrcAddress", "22:00:00:00:00:00");
        verifyComboBoxSelection("#macsrcMode", "Increment");
    }

    /**
     * Verify Ethernet selections
     * @param streamName 
     */
    private void verifyEthernetSelection(String streamName) {
        
        // open stream properties window
        openStreamProperties(streamName);
        
        clickOn("#protocolSelectionTab");
        waitForNode("#l3NoneRB");
        verifRadioButtonSelected("#l3NoneRB");
        
        // verify mac info
        verifyMacInformation();
        
    }

}

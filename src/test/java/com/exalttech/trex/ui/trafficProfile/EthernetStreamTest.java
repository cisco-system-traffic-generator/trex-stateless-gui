/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.trafficProfile;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.testng.annotations.Test;

/**
 * Ethernet stream test
 * @author GeorgeKH
 */
public class EthernetStreamTest extends TrafficProfileTestBase{
    
    
    @Test(dependsOnGroups = {"createProfileGroup"})
    public void addEthernetStreamTest() throws Exception {

        ListView list = find("#profileListView");
        list.getSelectionModel().select("testProfile.yaml");
        // wait for build stream button
        waitForNode("#buildStreamBtn");
        clickOn("#buildStreamBtn");
        // add stream
        addNewProfileStream("stream");

        // define protocol selection
        waitForNode("#protocolSelectionTab");
        clickOn("#protocolSelectionTab");
        waitForNode("#l3NoneRB");
        clickOn("#l3NoneRB");

        interact(() -> {
            ComboBox frameLength = find("#lengthCB");
            frameLength.getSelectionModel().select("Increment");
            TextField minTF = find("#minTF");
            minTF.setText("200");

            TextField maxTF = find("#maxTF");
            maxTF.setText("1500");
        });

        //Set protocol Data
        clickOn("#protocolDataTab");
        waitForNode("Media Access Protocol");
        clickOn("Media Access Protocol");
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

        // save stream packet
        clickOn("#savePacket");

        moveTo("testProfile.yaml");
        clickOn("testProfile.yaml");
        sleep(500);
        verifyTableHasElement("#streamTableView", "stream");

        // verify created stream selections
        verifyData();
        
        // close stream properties windows
        clickOn("Cancel");
        
    }

    /**
     * Verify selections and inputs
     */
    private void verifyData() {
        moveTo("stream");
        clickOn("stream");
        sleep(500);
        Button editBtn = find("#editStreanBtn");
        clickOn(editBtn);
        sleep(500);
        // verify protocol selections
        waitForNode("#protocolSelectionTab");
        clickOn("#protocolSelectionTab");
        waitForNode("#l3NoneRB");
        verifRadioButtonSelected("#l3NoneRB");
        verifyComboBoxSelection("#lengthCB", "Increment");
        verifyTextFieldValue("#minTF", "200");
        verifyTextFieldValue("#maxTF", "1500");
        // verify protocol data
        clickOn("#protocolDataTab");
        waitForNode("Media Access Protocol");
        clickOn("Media Access Protocol");
        waitForNode("#macDstAddress");
        verifyTextFieldValue("#macDstAddress", "12:00:00:00:00:22");
        verifyComboBoxSelection("#macDstMode", "Fixed");
        verifyTextFieldValue("#macSrcAddress", "22:00:00:00:00:00");
        verifyComboBoxSelection("#macsrcMode", "Increment");
    }
}

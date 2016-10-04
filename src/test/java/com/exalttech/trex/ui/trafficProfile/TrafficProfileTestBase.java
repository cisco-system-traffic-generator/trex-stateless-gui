/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.trafficProfile;

import com.exalttech.trex.ui.UIBaseTest;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

/**
 * traffic profile test base class
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
     * @param name 
     */
    protected void addNewProfileStream(String name) {
        waitForNode("#nameTF");
        TextField nameTF = find("#nameTF");
        nameTF.setText(name);
        Button okButton = find("OK");
        clickOn(okButton);
        sleep(500);
        
    }
}

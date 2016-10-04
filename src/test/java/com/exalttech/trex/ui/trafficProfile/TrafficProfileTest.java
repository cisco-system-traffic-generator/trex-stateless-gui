/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.trafficProfile;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import org.testng.annotations.Test;

/**
 * Traffic profile test
 *
 * @author GeorgeKH
 */
public class TrafficProfileTest extends TrafficProfileTestBase {

    @Test(groups = "createProfileGroup")
    public void createProfileTest() {

        // open traffic profile from menu
        openTrafficProfile();

        // click on create profile button
        waitForNode("Create Profile");
        Button createProfileBtn = find("Create Profile");
        clickOn(createProfileBtn);

        // create profile
        addNewProfileStream("testProfile");
        verifyItemExistsInList("#profileListView", "testProfile.yaml");
    }

}

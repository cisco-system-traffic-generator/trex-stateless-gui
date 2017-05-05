package com.exalttech.trex;

import javafx.scene.control.ListView;

import org.junit.Assert;
import org.junit.Test;


public class TestTrafficProfiles extends TestBase {
    @Test
    public void testCreateProfile() {
        assertCall(
                () -> {
                    clickOn("#main-traffic-profiles-menu");
                    clickOn("#main-traffic-profiles-menu-traffic-profiles");
                },
                () -> lookup("#traffic-profile-dialog").query() != null
        );
        assertCall(
                () -> {
                    clickOn("#create-profile-button");
                },
                () -> lookup("#profile-stream-name-dialog").query() != null
        );
        setText("#profile-stream-name-dialog-name-text-field", "Test Profile");
        assertCall(
                () -> {
                    clickOn("#profile-stream-name-dialog-ok-button");
                },
                () -> lookup("#profile-stream-name-dialog").query() == null
        );
        final ListView profileList = lookup("#traffic-profile-dialog-profiles-list-view").query();
        Assert.assertTrue(profileList.getItems().contains("Test Profile.yaml"));
    }
}

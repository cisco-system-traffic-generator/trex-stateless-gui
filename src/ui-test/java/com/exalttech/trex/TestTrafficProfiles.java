package com.exalttech.trex;

import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

import java.io.File;

import com.exalttech.trex.util.FileChooserFactory;


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

    @Test
    public void testLoadProfile() throws Exception {
        assertCall(
                () -> {
                    clickOn("#main-traffic-profiles-menu");
                    clickOn("#main-traffic-profiles-menu-traffic-profiles");
                },
                () -> lookup("#traffic-profile-dialog").query() != null
        );

        final FileChooser fileChooser = Mockito.spy(FileChooser.class);
        FileChooserFactory.set(fileChooser);
        final File file = new File(getTestTrafficProfilesFolder() + "/profile.yaml");
        Mockito.doReturn(file).when(fileChooser).showOpenDialog(Mockito.any());

        assertCall(
                () -> {
                    clickOn("#load-profile-button");
                    FileChooserFactory.set(null);
                },
                () -> {
                    final ListView profileList = lookup("#traffic-profile-dialog-profiles-list-view").query();
                    return profileList.getItems().contains("profile.yaml");
                }
        );
    }
}

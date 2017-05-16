package com.exalttech.trex;

import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import org.apache.commons.io.FileUtils;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import com.exalttech.trex.util.FileChooserFactory;
import com.exalttech.trex.util.files.FileManager;


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

    @Test
    public void testDeleteProfile() throws Exception {
        prepareProfile();

        assertCall(
                () -> {
                    clickOn("#main-traffic-profiles-menu");
                    clickOn("#main-traffic-profiles-menu-traffic-profiles");
                },
                () -> lookup("#traffic-profile-dialog").query() != null
        );

        final ListView profileList = lookup("#traffic-profile-dialog-profiles-list-view").query();
        Assert.assertTrue(profileList.getItems().contains("profile.yaml"));

        assertCall(
                () -> {
                    clickOn("profile.yaml");
                    clickOn("#delete-profile-button");
                    type(KeyCode.ENTER);
                },
                () -> !profileList.getItems().contains("profile.yaml")
        );
    }
    @Test
    public void testExportProfileToJSON() throws Exception {
        prepareProfile();

        assertCall(
                () -> {
                    clickOn("#main-traffic-profiles-menu");
                    clickOn("#main-traffic-profiles-menu-traffic-profiles");
                },
                () -> lookup("#traffic-profile-dialog").query() != null
        );

        final ListView profileList = lookup("#traffic-profile-dialog-profiles-list-view").query();
        Assert.assertTrue(profileList.getItems().contains("profile.yaml"));

        final FileChooser fileChooser = Mockito.spy(FileChooser.class);
        FileChooserFactory.set(fileChooser);
        final File result = new File(FileManager.getLocalFilePath() + "/profile.json");
        Mockito.doReturn(result).when(fileChooser).showSaveDialog(Mockito.any());

        tryCall(
                () -> {
                    clickOn("profile.yaml");
                    clickOn("#export-profile-to-json-button");
                },
                () -> true
        );

        final File expected = new File(getResourcesFolder() + "/profile.json");
        Assert.assertEquals(FileUtils.checksumCRC32(expected), FileUtils.checksumCRC32(result));
    }

    private void prepareProfile() throws IOException {
        final File profile = new File(getTestTrafficProfilesFolder() + "/profile.yaml");
        final File profilesDir = new File(FileManager.getProfilesFilePath());
        FileUtils.copyFileToDirectory(profile, profilesDir);

        final File profiles = new File(getResourcesFolder() + "/profiles.xml");
        final File localDir = new File(FileManager.getLocalFilePath());
        FileUtils.copyFileToDirectory(profiles, localDir);
    }
}

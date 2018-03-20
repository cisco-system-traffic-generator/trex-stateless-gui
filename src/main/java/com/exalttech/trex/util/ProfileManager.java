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
package com.exalttech.trex.util;

import com.exalttech.trex.ui.controllers.ProfileStreamNameDialogController;
import com.exalttech.trex.ui.dialog.DialogWindow;
import com.exalttech.trex.ui.models.datastore.Profiles;
import com.exalttech.trex.ui.models.datastore.ProfilesWrapper;
import com.exalttech.trex.util.files.FileManager;
import com.exalttech.trex.util.files.XMLFileManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

/**
 *
 * @author GeorgeKh
 */
public class ProfileManager {

    private static ProfileManager instance = null;

    /**
     * Return instance of the saveLoadProfiles
     *
     * @return
     */
    public static ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }
    Map<String, Profiles> profilesMap = new HashMap<>();
    private BooleanProperty updatedProperty = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected ProfileManager() {

    }

    /**
     * load profiles from XML file
     *
     * @return
     */
    public List<String> loadProfiles() {
        List<String> profileNames = new ArrayList<>();
        profilesMap.clear();

        ProfilesWrapper profiles = (ProfilesWrapper) XMLFileManager.loadXML("profiles.xml", ProfilesWrapper.class);
        if (profiles != null && profiles.getProfiles() != null) {
            for (Profiles prof : profiles.getProfiles()) {
                profileNames.add(prof.getFileName());
                profilesMap.put(prof.getFileName(), new Profiles(prof.getFileName(), prof.getFilePath()));
            }
        }
        return profileNames;
    }

    /**
     * Check whether the new file is already loaded or not
     *
     * @param fileName
     * @return
     */
    public boolean isFileExists(String fileName) {
        return profilesMap.containsKey(fileName);
    }

    /**
     * Update saved file list
     *
     * @param fileToSave
     * @param isAdd
     */
    public void updateProfilesList(File fileToSave, boolean isAdd) {
        // add file to the list
        if (isAdd) {
            profilesMap.put(fileToSave.getName(), new Profiles(fileToSave.getName(), fileToSave.getAbsolutePath()));
        } else {
            profilesMap.remove(fileToSave.getName());
        }

        // update saved xml file
        List<Profiles> profilesList = new ArrayList<>(profilesMap.values());
        ProfilesWrapper wrapper = new ProfilesWrapper(profilesList);
        XMLFileManager.saveXML("profiles.xml", wrapper, ProfilesWrapper.class);
        updatedProperty.setValue(!updatedProperty.getValue());
    }

    /**
     * Return file path
     *
     * @param fileName
     * @return
     */
    public String getProfileFilePath(String fileName) {
        return profilesMap.get(fileName).getFilePath();
    }

    /**
     * Return booleanProperty
     *
     * @return
     */
    public BooleanProperty getUpdatedProperty() {
        return updatedProperty;
    }

    /**
     *
     * @param currentStage
     * @return
     * @throws IOException
     */
    public String createNewProfile(Stage currentStage) throws IOException {
        String profileName = getProfileNameDialogValue(currentStage, "Create Profile");
        if (Util.isNullOrEmpty(profileName)) {
            return "";
        }
        File newFile = FileManager.createNewFile(profileName);
        updateProfilesList(newFile, true);

        return profileName;
    }

    public String duplicateProfile(Stage currentStage, String sourceProfileName) throws IOException {
        if (!profilesMap.containsKey(sourceProfileName)) {
            throw new IllegalArgumentException(String.format("Cannot find profile: %s", sourceProfileName));
        }

        String targetFileName = getProfileNameDialogValue(currentStage, "Duplicate Profile");
        if (Util.isNullOrEmpty(targetFileName)) {
            return "";
        }

        if (profilesMap.containsKey(targetFileName)) {
            throw new IllegalArgumentException(String.format("Profile already exists: %s", targetFileName));
        }

        Profiles profileInfo = profilesMap.get(sourceProfileName);
        String sourceFileName = profileInfo.getFileName();

        File result = FileManager.duplicateFile(sourceFileName, targetFileName);

        updateProfilesList(result, true);

        return targetFileName;
    }

    private String getProfileNameDialogValue(Stage currentStage, String title) throws IOException {
        DialogWindow profileNameWindow = new DialogWindow(
                "ProfileStreamNameDialog.fxml",
                title,
                150,
                100,
                false,
                currentStage);
        ProfileStreamNameDialogController controller = (ProfileStreamNameDialogController) profileNameWindow.getController();
        controller.setProfileWindow(true);
        profileNameWindow.show(true);

        if (controller.isDataAvailable()) {
            return controller.getName() + ".yaml";
        }

        return "";
    }
}

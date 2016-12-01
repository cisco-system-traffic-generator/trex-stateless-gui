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

import com.exalttech.trex.ui.models.datastore.Preferences;
import com.exalttech.trex.ui.models.datastore.PreferencesWrapper;
import com.exalttech.trex.util.files.XMLFileManager;
import com.xored.javafx.packeteditor.service.ConfigurationService;

/**
 * Preferences manager class
 *
 * @author Georgekh
 */
public class PreferencesManager {

    private static PreferencesManager instance = null;

    private ConfigurationService packetEditorConfigurations = null;

    /**
     * Return instance of preferences manager
     *
     * @return
     */
    public static PreferencesManager getInstance() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }
    Preferences preferences;

    /**
     *
     */
    protected PreferencesManager() {
        loadPreferences();
    }

    /**
     * Return preferences
     *
     * @return
     */
    public Preferences getPreferences() {
        return preferences;
    }

    /**
     * Load preferences
     */
    private void loadPreferences() {
        PreferencesWrapper preferencesWrapper = (PreferencesWrapper) XMLFileManager.loadXML("preferences.xml", PreferencesWrapper.class);
        if (preferencesWrapper != null) {
            preferences = preferencesWrapper.getPreferences();
        }
    }

    /**
     * Update and save preferences
     *
     * @param preferences
     */
    public void savePreferences(Preferences preferences) {
        PreferencesWrapper preferencesWrapper = new PreferencesWrapper();
        preferencesWrapper.setPreferences(preferences);
        XMLFileManager.saveXML("preferences.xml", preferencesWrapper, PreferencesWrapper.class);

        // update current prefernces
        loadPreferences();

        // propagate locations to packet editor
        packetEditorConfigurations.setLoadLocation(getLoadLocation());
        packetEditorConfigurations.setSaveLocation(getSaveLocation());
        packetEditorConfigurations.setTemplatesLocation(getTemplatesLocation());
    }

    /**
     * Return load file location
     *
     * @return
     */
    public String getLoadLocation() {
        if (preferences != null && !Util.isNullOrEmpty(preferences.getLoadLocation())) {
            return preferences.getLoadLocation();
        } else {
            return "";
        }
    }

    /**
     * Return saving files location
     *
     * @return
     */
    public String getSaveLocation() {
        if (preferences != null && !Util.isNullOrEmpty(preferences.getSavedLocation())) {
            return preferences.getSavedLocation();
        } else {
            return "";
        }
    }

    public String getTemplatesLocation() {
        if (preferences != null && !Util.isNullOrEmpty(preferences.getTemplatesLocation())) {
            return preferences.getTemplatesLocation();
        } else {
            return "";
        }
    }

    public ConfigurationService getPacketEditorConfigurations() {
        return packetEditorConfigurations;
    }

    public void setPacketEditorConfigurations(ConfigurationService packetEditorConfigurations) {
        this.packetEditorConfigurations = packetEditorConfigurations;
        this.packetEditorConfigurations.setLoadLocation(getLoadLocation());
        this.packetEditorConfigurations.setSaveLocation(getSaveLocation());
        this.packetEditorConfigurations.setTemplatesLocation(getTemplatesLocation());
    }

}

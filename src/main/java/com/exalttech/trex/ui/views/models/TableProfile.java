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
/*



 */
package com.exalttech.trex.ui.views.models;

import com.exalttech.trex.remote.models.profiles.Profile;
import java.util.List;

/**
 *
 * @author GeorgeKh
 */
public class TableProfile {

    String yamlFileName;

    List<Profile> profiles;

    List<TableProfileStream> streamsList;

    /**
     *
     * @param profiles
     * @param streamsList
     * @param yamlFileName
     */
    public TableProfile(List<Profile> profiles, List<TableProfileStream> streamsList, String yamlFileName) {
        this.profiles = profiles;
        this.streamsList = streamsList;
        this.yamlFileName = yamlFileName;
    }

    /**
     *
     * @return
     */
    public List<Profile> getProfiles() {
        return profiles;
    }

    /**
     *
     * @param profiles
     */
    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    /**
     *
     * @return
     */
    public List<TableProfileStream> getStreamsList() {
        return streamsList;
    }

    /**
     *
     * @param streamsList
     */
    public void setStreamsList(List<TableProfileStream> streamsList) {
        this.streamsList = streamsList;
    }

    /**
     *
     * @return
     */
    public String getYamlFileName() {
        return yamlFileName;
    }

    /**
     *
     * @param yamlFileName
     */
    public void setYamlFileName(String yamlFileName) {
        this.yamlFileName = yamlFileName;
    }

}

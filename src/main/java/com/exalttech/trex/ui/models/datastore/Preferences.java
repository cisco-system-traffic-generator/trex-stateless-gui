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
package com.exalttech.trex.ui.models.datastore;

import javax.xml.bind.annotation.XmlElement;

/**
 * Preferences data model
 *
 * @author Georgekh
 */
public class Preferences {

    private String loadLocation = "";
    private String savedLocation = "";
    private String templatesLocation = "";

    /**
     *
     */
    public Preferences() {
        // default constructor
    }

    /**
     *
     * @param loadLocation
     * @param savedLocation
     * @param templatesLocation
     */
    public Preferences(String loadLocation, String savedLocation, String templatesLocation) {
        this.loadLocation = loadLocation;
        this.savedLocation = savedLocation;
        this.templatesLocation = templatesLocation;
    }

    /**
     * Return load location
     *
     * @return
     */
    @XmlElement(name = "load_location")
    public String getLoadLocation() {
        return loadLocation;
    }

    /**
     * Set load location
     *
     * @param loadLocation
     */
    public void setLoadLocation(String loadLocation) {
        this.loadLocation = loadLocation;
    }

    /**
     * Return saved location
     *
     * @return
     */
    @XmlElement(name = "saved_location")
    public String getSavedLocation() {
        return savedLocation;
    }

    /**
     * Set saved location
     *
     * @param savedLocation
     */
    public void setSavedLocation(String savedLocation) {
        this.savedLocation = savedLocation;
    }

    @XmlElement(name = "templates_location")
    public String getTemplatesLocation() {
        return templatesLocation;
    }

    public void setTemplatesLocation(String templatesLocation) {
        this.templatesLocation = templatesLocation;
    }

}

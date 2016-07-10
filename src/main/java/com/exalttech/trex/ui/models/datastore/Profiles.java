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
 * Save profile model
 *
 * @author GeorgeKh
 */
public class Profiles {

    String fileName;

    String filePath;

    /**
     *
     */
    public Profiles() {
        // default constructor
    }

    /**
     *
     * @param fileName
     * @param filePath
     */
    public Profiles(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    /**
     * Return profile name
     *
     * @return
     */
    @XmlElement(name = "fileName")
    public String getFileName() {
        return fileName;
    }

    /**
     * Set profile name
     *
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Return profile path
     *
     * @return
     */
    @XmlElement(name = "filePath")
    public String getFilePath() {
        return filePath;
    }

    /**
     * Set profile path
     *
     * @param filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}

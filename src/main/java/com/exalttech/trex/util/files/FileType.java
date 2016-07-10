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
package com.exalttech.trex.util.files;

/**
 * Enumerator that present save file type
 *
 * @author Georgekh
 */
public enum FileType {

    /**
     *
     */
    JSON("JSON File (*.json)", "*.json", ".json"),
    /**
     *
     */
    YAML("YAML File (*.yaml)", "*.yaml", ".yaml"),
    /**
     *
     */
    PCAP("PCAP File", "*.pcap", ".pcap");

    String filterDescription;
    String filterExtension;
    String extension;

    private FileType(String filterDescription, String filterExtension, String extension) {
        this.filterDescription = filterDescription;
        this.filterExtension = filterExtension;
        this.extension = extension;
    }

    /**
     * Return filter description
     *
     * @return
     */
    public String getFilterDescription() {
        return filterDescription;
    }

    /**
     * Return filter extension
     *
     * @return
     */
    public String getFilterExtension() {
        return filterExtension;
    }

    /**
     * Return extension
     *
     * @return
     */
    public String getExtension() {
        return extension;
    }

}

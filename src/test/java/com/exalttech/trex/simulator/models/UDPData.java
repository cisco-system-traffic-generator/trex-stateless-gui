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
package com.exalttech.trex.simulator.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * UDP data model
 *
 * @author Georgekh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UDPData {

    boolean enable;

    int srcPort;

    int dstPort;

    String checksum;

    int length;

    /**
     * Return source port
     *
     * @return
     */
    public int getSrcPort() {
        return srcPort;
    }

    /**
     * Set source port
     *
     * @param srcPort
     */
    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    /**
     * Return destination port
     *
     * @return
     */
    public int getDstPort() {
        return dstPort;
    }

    /**
     * Set destination port
     *
     * @param dstPort
     */
    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    /**
     * Return checksum
     *
     * @return
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Set checksum
     *
     * @param checksum
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * Return length
     *
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * Set length
     *
     * @param length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Return whether UDP is enabled or not
     *
     * @return
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Set enabling UDP value
     *
     * @param enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}

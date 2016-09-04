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
 * TCP data model
 *
 * @author Georgekh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TCPData {

    boolean enable;

    int srcPort;

    int dstPort;

    int sequenceNumber;

    int ackNumber;

    String window;

    String checksum;

    String urgetPointer;

    /**
     * Return whether TCP is enable or not
     *
     * @return
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Set enabling TCP value
     *
     * @param enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

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
     * Return sequence number
     *
     * @return
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Set sequence number
     *
     * @param sequenceNumber
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Return acknowledgment number
     *
     * @return
     */
    public int getAckNumber() {
        return ackNumber;
    }

    /**
     * Set acknowledgment number
     *
     * @param ackNumber
     */
    public void setAckNumber(int ackNumber) {
        this.ackNumber = ackNumber;
    }

    /**
     * Return window
     *
     * @return
     */
    public String getWindow() {
        return window;
    }

    /**
     * Set window
     *
     * @param window
     */
    public void setWindow(String window) {
        this.window = window;
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
     * Return urgent pointer
     *
     * @return
     */
    public String getUrgetPointer() {
        return urgetPointer;
    }

    /**
     * Set urgent pointer
     *
     * @param urgetPointer
     */
    public void setUrgetPointer(String urgetPointer) {
        this.urgetPointer = urgetPointer;
    }

}

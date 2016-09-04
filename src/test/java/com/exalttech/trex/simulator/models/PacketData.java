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
 * Packet data model
 *
 * @author Georgekh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PacketData {

    String testFileName;
    EthernetData ethernetData;
    IPV4Data ipv4Data;
    TCPData tcpData;
    UDPData udpData;
    PayloadData payload;
    PacketLength packetLength;
    boolean taggedVlan;

    /**
     * Set test file name
     *
     * @param testFileName
     */
    public void setTestFileName(String testFileName) {
        this.testFileName = testFileName;
    }

    /**
     * Return test file name
     *
     * @return
     */
    public String getTestFileName() {
        return testFileName;
    }

    /**
     * Return ethernet data info
     *
     * @return
     */
    public EthernetData getEthernetData() {
        return ethernetData;
    }

    /**
     * Set ethernet data info
     *
     * @param ethernetData
     */
    public void setEthernetData(EthernetData ethernetData) {
        this.ethernetData = ethernetData;
    }

    /**
     * Return ipv4 data info
     *
     * @return
     */
    public IPV4Data getIpv4Data() {
        return ipv4Data;
    }

    /**
     * Set ipv4 data info
     *
     * @param ipv4Data
     */
    public void setIpv4Data(IPV4Data ipv4Data) {
        this.ipv4Data = ipv4Data;
    }

    /**
     * Return tcp data info
     *
     * @return
     */
    public TCPData getTcpData() {
        return tcpData;
    }

    /**
     * Set tcp data info
     *
     * @param tcpData
     */
    public void setTcpData(TCPData tcpData) {
        this.tcpData = tcpData;
    }

    /**
     * Return udp data info
     *
     * @param udpData
     */
    public void setUdpData(UDPData udpData) {
        this.udpData = udpData;
    }

    /**
     * Set udp data info
     *
     * @return
     */
    public UDPData getUdpData() {
        return udpData;
    }

    /**
     * Return payload info
     *
     * @return
     */
    public PayloadData getPayload() {
        return payload;
    }

    /**
     * Set payload info
     *
     * @param payload
     */
    public void setPayload(PayloadData payload) {
        this.payload = payload;
    }

    /**
     * Return packet length info
     *
     * @return
     */
    public PacketLength getPacketLength() {
        return packetLength;
    }

    /**
     * Set packet length info
     *
     * @param packetLength
     */
    public void setPacketLength(PacketLength packetLength) {
        this.packetLength = packetLength;
    }

    /**
     * Return whether vlan is tagged or not
     *
     * @return
     */
    public boolean isTaggedVlan() {
        return taggedVlan;
    }

    /**
     * Set vlan tagged
     *
     * @param taggedVlan
     */
    public void setTaggedVlan(boolean taggedVlan) {
        this.taggedVlan = taggedVlan;
    }

}

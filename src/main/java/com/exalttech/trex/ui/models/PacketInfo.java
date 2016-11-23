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
package com.exalttech.trex.ui.models;

import org.pcap4j.packet.Packet;

/**
 *
 * @author GeorgeKh
 */
public class PacketInfo {

    private Packet packet = null;
    private String packetHex = null;
    private String packetRawData = null;
    private String packetPayLoad = null;

    private String ethernetHex = null;
    private String ethernetRawData = null;
    private String destMac = null;
    private String srcMac = null;

    private String ipv4Hex = null;
    private String ipv4RawData = null;
    private String destIpv4 = null;
    private String srcIpv4 = null;

    private String l4Name = null;
    private String l4Hex = null;
    private String l4RawData = null;

    private boolean vlanPacket = false;
    private long timeStamp;
    
    /**
     * @return the packet
     */
    public Packet getPacket() {
        return packet;
    }

    /**
     * @param packet the packet to set
     */
    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    /**
     * @return the packetHex
     */
    public String getPacketHex() {
        return packetHex;
    }

    /**
     * @param packetHex the packetHex to set
     */
    public void setPacketHex(String packetHex) {
        this.packetHex = packetHex;
    }

    /**
     * @return the packetRawData
     */
    public String getPacketRawData() {
        return packetRawData;
    }

    /**
     * @param packetRawData the packetRawData to set
     */
    public void setPacketRawData(String packetRawData) {
        this.packetRawData = packetRawData;
    }

    /**
     * @return the packetPayLoad
     */
    public String getPacketPayLoad() {
        return packetPayLoad;
    }

    /**
     * @param packetPayLoad the packetPayLoad to set
     */
    public void setPacketPayLoad(String packetPayLoad) {
        this.packetPayLoad = packetPayLoad;
    }

    /**
     * @return the ethernetHex
     */
    public String getEthernetHex() {
        return ethernetHex;
    }

    /**
     * @param ethernetHex the ethernetHex to set
     */
    public void setEthernetHex(String ethernetHex) {
        this.ethernetHex = ethernetHex;
    }

    /**
     * @return the ethernetRawData
     */
    public String getEthernetRawData() {
        return ethernetRawData;
    }

    /**
     * @param ethernetRawData the ethernetRawData to set
     */
    public void setEthernetRawData(String ethernetRawData) {
        this.ethernetRawData = ethernetRawData;
    }

    /**
     * @return the destMac
     */
    public String getDestMac() {
        return destMac;
    }

    /**
     * @param destMac the destMac to set
     */
    public void setDestMac(String destMac) {
        this.destMac = destMac;
    }

    /**
     * @return the srcMac
     */
    public String getSrcMac() {
        return srcMac;
    }

    /**
     * @param srcMac the srcMac to set
     */
    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    /**
     * @return the ipv4Hex
     */
    public String getIpv4Hex() {
        return ipv4Hex;
    }

    /**
     * @param ipv4Hex the ipv4Hex to set
     */
    public void setIpv4Hex(String ipv4Hex) {
        this.ipv4Hex = ipv4Hex;
    }

    /**
     * @return the ipv4RawData
     */
    public String getIpv4RawData() {
        return ipv4RawData;
    }

    /**
     * @param ipv4RawData the ipv4RawData to set
     */
    public void setIpv4RawData(String ipv4RawData) {
        this.ipv4RawData = ipv4RawData;
    }

    /**
     * @return the destIpv4
     */
    public String getDestIpv4() {
        return destIpv4;
    }

    /**
     * @param destIpv4 the destIpv4 to set
     */
    public void setDestIpv4(String destIpv4) {
        this.destIpv4 = destIpv4;
    }

    /**
     * @return the srcIpv4
     */
    public String getSrcIpv4() {
        return srcIpv4;
    }

    /**
     * @param srcIpv4 the srcIpv4 to set
     */
    public void setSrcIpv4(String srcIpv4) {
        this.srcIpv4 = srcIpv4;
    }

    /**
     * @return the l4Name
     */
    public String getL4Name() {
        return l4Name;
    }

    /**
     * @param l4Name the l4Name to set
     */
    public void setL4Name(String l4Name) {
        this.l4Name = l4Name;
    }

    /**
     * @return the l4Hex
     */
    public String getL4Hex() {
        return l4Hex;
    }

    /**
     * @param l4Hex the l4Hex to set
     */
    public void setL4Hex(String l4Hex) {
        this.l4Hex = l4Hex;
    }

    /**
     * @return the l4owData
     */
    public String getL4RawData() {
        return l4RawData;
    }

    /**
     * @param l4owData the l4owData to set
     */
    public void setL4RawData(String l4owData) {
        this.l4RawData = l4owData;
    }

    /**
     * Return whether packet has vlan or not
     * @return 
     */
    public boolean hasVlan(){
        return vlanPacket;
    }

    /**
     * Set packet has value
     * @param vlanPacket 
     */
    public void setVlanPacket(boolean vlanPacket) {
        this.vlanPacket = vlanPacket;
    }

    /**
     * Set timestamp value
     * @param timeStamp 
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Return timestamp value
     * @return 
     */
    public long getTimeStamp() {
        return timeStamp;
    }
    
    
}

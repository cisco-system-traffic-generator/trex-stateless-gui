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
package com.exalttech.trex.ui.views.streams.builder;

import org.apache.log4j.Logger;

/**
 * Packet builder helper class
 * @author Georgekh
 */
public class PacketBuilderHelper {

    private static final Logger LOG = Logger.getLogger(PacketBuilderHelper.class.getName());

     /**
     *
     */
    private PacketBuilderHelper() {
        // private constructor
    }
    
    /**
     *
     * @param rawData
     * @return
     */
    public static String getPacketHex(byte[] rawData) {
        String packetHex = "";
        try {
            StringBuilder myString = new StringBuilder();
            for (byte b : rawData) {
                myString.append(String.format("%02X", b));
            }
            packetHex = myString.toString();
        } catch (Exception ex) {
            LOG.error("Error generating packet hex", ex);
        }
        return packetHex;
    }

    /**
     * Return operation value from type
     */
    public static String getOperationFromType(String type) {
        if (type.startsWith("Inc")) {
            return "inc";
        } else if (type.startsWith("Dec")) {
            return "dec";
        } else if (type.startsWith("Rand")) {
            return "random";
        }
        return "Fixed";
    }

    /**
     * Return packet length
     * @param operation
     * @param defaultLength
     * @param maxLength
     * @return 
     */
    public static int getPacketLength(String operation, int defaultLength, int maxLength) {
        if (PacketLengthType.FIXED.getTitle().equals(getOperationFromType(operation))) {
            return defaultLength - 4;
        }
        return maxLength - 4;
    }

    /**
     * Return IPV4 packet length
     * @param taggedVlan
     * @param packetLength
     * @param totalLength
     * @return 
     */
    public static int getIPV4PacketLength(boolean taggedVlan, int packetLength, int totalLength) {
        if (taggedVlan) {
            return packetLength - totalLength - 8;
        }
        return packetLength - totalLength + 4;
    }

    /**
     * Return TCP/UDP packet length
     * @param taggedVlan
     * @param packetLength
     * @param totalLength
     * @return 
     */
    public static int getTcpUdpPacketLength(boolean taggedVlan, int packetLength, int totalLength) {
        if (taggedVlan) {
            return packetLength - totalLength - 4;
        }
        return packetLength - totalLength + 4;
    }

    /**
     * Return IPV4 total length
     * @param taggedVlan
     * @return 
     */
    public static int getIPV4TotalLength(boolean taggedVlan) {
        if (taggedVlan) {
            return 42;
        }
        return 46;
    }
}

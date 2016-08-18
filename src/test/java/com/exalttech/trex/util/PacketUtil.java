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

import com.exalttech.trex.ui.models.PacketInfo;
import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.exalttech.trex.ui.views.streams.viewer.PacketParser;
import java.io.File;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;

/**
 *
 * Utility class
 *
 * @author Georgekh
 */
public class PacketUtil {

    private TrafficProfile trafficProfile = new TrafficProfile();

    /**
     * @param packetRawData
     * @return encoded data
     */
    public String getEncodedPacket(byte[] packetRawData) {
        String hexDataString = PacketBuilderHelper.getPacketHex(packetRawData);
        return trafficProfile.encodeBinaryFromHexString(hexDataString);
    }

    /**
     * Decode string and return packet info data
     *
     * @param encodedBinaryPacket
     * @return packet info
     * @throws IOException
     */
    public PacketInfo getPacketInfoData(String encodedBinaryPacket) throws IOException {
        // decode binary data
        File pcapFile = trafficProfile.decodePcapBinary(encodedBinaryPacket);
        PacketInfo packetInfo = new PacketInfo();
        PacketParser parser = new PacketParser(pcapFile.getAbsolutePath(), packetInfo);
        return packetInfo;
    }

    /**
     * @return packet length
     */
    public int getPacketLength(Packet packet) throws IllegalRawDataException {
        return trafficProfile.getPacketTypeText(packet).getLength();
    }

    /**
     * Get packet from encoded string
     * @param encodedBinaryPacket
     * @return packet
     * @throws IllegalRawDataException 
     */
    public Packet getPacketFromEncodedString(String encodedBinaryPacket) throws IllegalRawDataException{
        byte[] pkt = Base64.decodeBase64(encodedBinaryPacket);
        return EthernetPacket.newPacket(pkt, 0, pkt.length);
    }
    
}

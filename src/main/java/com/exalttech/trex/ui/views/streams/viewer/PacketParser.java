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
package com.exalttech.trex.ui.views.streams.viewer;

import com.exalttech.trex.ui.models.PacketInfo;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

/**
 * Parse the packet
 *
 * @author GeorgeKh
 */
public class PacketParser {

    private static final Logger LOG = Logger.getLogger(PacketParser.class.getName());
    private static int total_octetes = 0;
    PacketInfo packetInfo = null;
    
    public PacketParser() {
        
    }
    
    /**
     * Format payload
     *
     * @param payLoad
     * @return
     */
    static String formatPayLoad(String payLoad) {
        int line = 0;
        String dim;
        StringBuilder finalHex = new StringBuilder();
        char[] arr = payLoad.toCharArray();

        for (int i = 0; i < arr.length; i++) {
            line++;
            dim = "";
            if (line == 16) {
                dim = "\n";
                line = 0;
            }
            int nVal = (int) arr[i];
            // Is character ISO control
            boolean bISO = Character.isISOControl(arr[i]);
            // Is Ignorable identifier
            boolean bIgnorable = Character.isIdentifierIgnorable(arr[i]);
            // Remove tab and other unwanted characters..
            if (nVal == 9 || bISO || bIgnorable) {
                arr[i] = '.';
            } else if (nVal > 255) {
                arr[i] = '.';
            }
            if (arr[i] == '\n' || arr[i] == '\r' || arr[i] == ' ' || arr[i] == '\0') {
                finalHex.append('.').append(dim);
            } else {
                finalHex.append(arr[i]).append(dim);
            }
        }
        return finalHex.toString();
    }
   

    /**
     *
     * @param fileName
     * @param packetInfo
     */
    public void parseFile(String fileName, PacketInfo packetInfo){
        Packet packet;
        try {
            this.packetInfo = packetInfo;
            total_octetes = 0;
            PcapHandle handle = Pcaps.openOffline(fileName);
            packet = handle.getNextPacketEx();
            packetInfo.setPacket(packet);
            extractPacketInfo(packet);
            handle.close();
        } catch (PcapNativeException | EOFException | TimeoutException | NotOpenException ex) {
            packet = null;
            LOG.warn("Failed to extract packet info from first try");
        }

        if (packet == null) {
            try {
                LOG.info("Attempting alternate method to read Pcap file");
                byte[] pkt = FileUtils.readFileToByteArray(new File(fileName));
                packet = EthernetPacket.newPacket(pkt, 0, pkt.length);
                extractPacketInfo(packet);
            } catch (IOException | IllegalRawDataException ex) {
                LOG.error("Failed to read Pcap file", ex);
            }

        }
    }
    
    /**
     *
     * @param packet
     * @param packetInfo
     */
    public void parsePacket(Packet packet, PacketInfo packetInfo) {
        total_octetes = 0;
        this.packetInfo = packetInfo;
        extractPacketInfo(packet);
    }

    /**
     * Extract packet info
     *
     * @param packet
     * @return
     */
    private PacketInfo extractPacketInfo(Packet packet) {

        if (packet != null) {
            packetInfo.setPacketHex(formatHex(DatatypeConverter.printHexBinary(packet.getRawData())));
            packetInfo.setPacketRawData(formatPayLoad(new String(packet.getRawData())));
        }

        // If the packet has Ethernet
        if (packet.get(EthernetPacket.class) != null) {
            packetInfo.setEthernetHex(getHeaderOffset(packet.get(EthernetPacket.class).getHeader().toHexString().toUpperCase()));
            packetInfo.setEthernetRawData(new String());
            packetInfo.setDestMac(packet.get(EthernetPacket.class).getHeader().getDstAddr().toString());
            packetInfo.setSrcMac(packet.get(EthernetPacket.class).getHeader().getSrcAddr().toString());
        }

        // if the packet has IPV4
        if (packet.get(IpV4Packet.class) != null) {
            packetInfo.setIpv4Hex(getHeaderOffset(packet.get(IpV4Packet.class).getHeader().toHexString().toUpperCase()));
            packetInfo.setIpv4RawData(new String());
            packetInfo.setDestIpv4(packet.get(IpV4Packet.class).getHeader().getDstAddr().getHostAddress());
            packetInfo.setSrcIpv4(packet.get(IpV4Packet.class).getHeader().getSrcAddr().getHostAddress());
        }

        // if the packet has TCP
        if (packet.get(TcpPacket.class) != null) {
            packetInfo.setL4Name("TCP");
            packetInfo.setL4Hex(getHeaderOffset(packet.get(TcpPacket.class).getHeader().toHexString().toUpperCase()));
            packetInfo.setL4RawData(new String());
            if (packet.get(TcpPacket.class).getPayload() != null) {
                packetInfo.setPacketPayLoad(getHeaderOffset(spaceHex(Hex.encodeHexString(packet.get(TcpPacket.class).getPayload().getRawData()))));
            } else {
                packetInfo.setPacketPayLoad(null);
            }
        }

        // if the packet has UDP
        if (packet.get(UdpPacket.class) != null) {
            packetInfo.setL4Name("UDP");
            packetInfo.setL4Hex(getHeaderOffset(packet.get(UdpPacket.class).getHeader().toHexString().toUpperCase()));
            packetInfo.setL4RawData(new String());

            if (packet.get(UdpPacket.class).getPayload() != null) {
                packetInfo.setPacketPayLoad(getHeaderOffset(spaceHex(Hex.encodeHexString(packet.get(UdpPacket.class).getPayload().getRawData()))));
            } else {
                packetInfo.setPacketPayLoad(null);
            }
        }
        return packetInfo;
    }

    /**
     * Spacing the hex
     *
     * @param hexString
     * @return
     */
    private String spaceHex(String hexString) {
        char[] hexChar = hexString.toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hexChar.length; i++) {
            result.append(hexChar[i]);
            if (i % 2 != 0) {
                result.append(' ');
            }
        }
        return result.toString().trim().toUpperCase();
    }

    /**
     * Format Hex
     *
     * @param hex
     * @return
     */
    private String formatHex(String hex) {
        int block = 0, line = 0, small = 0, octets = 0;
        String[] chare = hex.split("(?!^)");
        String dim;
        StringBuilder finalHex = new StringBuilder("0000 ");
        for (int i = 0; i < chare.length; i++) {
            block++;
            line++;
            small++;
            dim = "";
            if (small == 2) {
                dim = " ";
                small = 0;
                octets++;
            }
            if (block == 8) {
                dim = "  ";
                block = 0;
            }
            if (line == 32) {
                dim = "\n" + String.format("%04X", octets) + " ";
                line = 0;
            }
            finalHex.append(chare[i]).append(dim);
        }
        return finalHex.toString().toUpperCase();
    }

    /**
     * Return header offset
     *
     * @param header
     * @return
     */
    private String getHeaderOffset(String header) {
        int offset = total_octetes;
        String[] headerOctets = header.split(" ");
        total_octetes += headerOctets.length;

        return String.format("%04X", offset) + "-" + String.format("%04X", total_octetes) + " " + header;
    }
}

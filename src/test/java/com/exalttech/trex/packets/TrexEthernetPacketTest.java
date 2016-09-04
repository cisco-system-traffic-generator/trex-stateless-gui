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
package com.exalttech.trex.packets;

import com.exalttech.trex.ui.models.PacketInfo;
import com.exalttech.trex.util.PacketUtil;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.EtherType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 *
 * Ethernet packet tests class
 *
 * @author Georgekh
 */
public class TrexEthernetPacketTest {

    private static final Logger LOG = Logger.getLogger(TrexEthernetPacketTest.class.getName());
    PacketUtil packetUtil = new PacketUtil();

    public TrexEthernetPacketTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @BeforeClass
    public void setUp() {
    }

    @AfterClass
    public void tearDown() {
    }

    /**
     * Test of buildPacket method, of class TrexEthernetPacket without vlan
     */
    @Test
    @Parameters({"macSrcAddress", "macDstAddress", "packetLength", "expectedEthernetWithoutVlanHex"})
    public void testEthernetPacketWithoutVlan(String macSrcAddress, String macDstAddress, int packetLength, String expectedEthernetWithoutVlanHex) throws IOException, IllegalRawDataException {

        LOG.info("------------Testing Ethernet packet");
        // build ethernet packet

        LOG.info("Building Ethernet packet");
        TrexEthernetPacket instance = new TrexEthernetPacket();
        instance.setSrcAddr(macSrcAddress);
        instance.setDstAddr(macDstAddress);
        instance.setLength(packetLength);
        instance.buildPacket(null);

        LOG.info("Encoding packet data");

        // Encode packet data
        String encodedBinaryPacket = packetUtil.getEncodedPacket(instance.getPacket().getRawData());
        LOG.info("Decoding packets and returning packet data information");

        // Decode and return packet info
        PacketInfo packetInfo = packetUtil.getPacketInfoData(encodedBinaryPacket);
        LOG.info("Verifying packet data");

        // Assert mac src/destination address
        Assert.assertEquals(macSrcAddress, packetInfo.getSrcMac(), "Invalid MAC source address. ");
        Assert.assertEquals(macDstAddress, packetInfo.getDestMac(), "Invalid MAC destination address. ");

        // Verify packet length
        Packet packet = packetUtil.getPacketFromEncodedString(encodedBinaryPacket);
        Assert.assertEquals(packetLength, packetUtil.getPacketLength(packet), "Invalid Packet length. ");

        // Verify packet data
        String packetHex = DatatypeConverter.printHexBinary(packet.getRawData());
        Assert.assertEquals(expectedEthernetWithoutVlanHex.toLowerCase(), packetHex.toLowerCase(), "Invalid Packet hex. ");

    }

    /**
     * Test of buildPacket method, of class TrexEthernetPacket with VLAN
     */
    @Test
    @Parameters({"macSrcAddress", "macDstAddress", "packetLength", "expectedEthernetWithVlanHex"})
    public void testEthernetPacketWithVlan(String macSrcAddress, String macDstAddress, int packetLength, String expectedEthernetWithVlanHex) throws IOException, IllegalRawDataException {

        LOG.info("------------Testing Ethernet packet");
        // build ethernet packet

        LOG.info("Building Ethernet packet");
        TrexEthernetPacket instance = new TrexEthernetPacket();
        instance.setSrcAddr(macSrcAddress);
        instance.setDstAddr(macDstAddress);
        instance.setLength(packetLength);
        instance.setType(EtherType.DOT1Q_VLAN_TAGGED_FRAMES.value());

        // build VLAN packet
        TrexVlanPacket vlanPacket = new TrexVlanPacket();
        vlanPacket.setType((short) 0xFFFF);
        vlanPacket.buildPacket(null);

        LOG.info("Building VLAN packet");
        instance.buildPacket(vlanPacket.getBuilder());

        LOG.info("Encoding packet data");

        // Encode packet data
        String encodedBinaryPacket = packetUtil.getEncodedPacket(instance.getPacket().getRawData());
        LOG.info("Decoding packets and returning packet data information");

        // Decode and return packet info
        PacketInfo packetInfo = packetUtil.getPacketInfoData(encodedBinaryPacket);
        LOG.info("Verifying packet data");

        // Assert mac src/destination address
        Assert.assertEquals(macSrcAddress, packetInfo.getSrcMac(), "Invalid MAC source address. ");
        Assert.assertEquals(macDstAddress, packetInfo.getDestMac(), "Invalid MAC destination address. ");

        // Verify packet length
        Packet packet = packetUtil.getPacketFromEncodedString(encodedBinaryPacket);
        Assert.assertEquals(packetLength, packetUtil.getPacketLength(packet), "Invalid Packet length. ");

        // Verify packet data
        String packetHex = DatatypeConverter.printHexBinary(packet.getRawData());
        Assert.assertEquals(expectedEthernetWithVlanHex.toLowerCase(), packetHex.toLowerCase(), "Invalid Packet hex. ");

    }
}

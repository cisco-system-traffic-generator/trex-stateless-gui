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
import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
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
     * Test of buildPacket method, of class TrexEthernetPacket.
     */
    @Test
    @Parameters({"macSrcAddress", "macDstAddress", "packetLength", "expectedHex"})
    public void testEthernetPacketWithoutVlan(String macSrcAddress, String macDstAddress, int packetLength, String expectedHex) throws IOException, IllegalRawDataException {

        LOG.info("------------Testing Ethernet packet");
        // build ethernet packet

        LOG.info("Building Ethernet packet");
        AbstractPacket.AbstractBuilder builder = null;
        TrexEthernetPacket instance = new TrexEthernetPacket();
        instance.setSrcAddr(macSrcAddress);
        instance.setDstAddr(macDstAddress);
        instance.setLength(packetLength);
        instance.buildPacket(builder);

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
        Assert.assertEquals(expectedHex.toLowerCase(), packetHex.toLowerCase(), "Invalid Packet hex. ");

    }

}

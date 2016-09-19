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
package com.exalttech.trex.simulator.profiles;

import com.exalttech.trex.packets.TrexEthernetPacket;
import com.exalttech.trex.packets.TrexIpV4Packet;
import com.exalttech.trex.packets.TrexUdpPacket;
import static com.exalttech.trex.simulator.profiles.AbstractIPV4Test.LOG;
import com.exalttech.trex.simulator.models.PacketData;
import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.exalttech.trex.ui.views.streams.builder.Payload;
import com.exalttech.trex.util.Util;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.namednumber.IpNumber;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * UDP profile tests
 *
 * @author Georgekh
 */
public class UDPTest extends AbstractIPV4Test {

    @DataProvider(name = "packetData")
    public Object[][] packetData() throws IOException, URISyntaxException {
        return prepareTestData("UdpPcapData.json");
    }

    @Test(dataProvider = "packetData", dependsOnGroups = {"init"})
    public void testUDPProfile(PacketData packetData) throws IOException, InterruptedException, PcapNativeException, NotOpenException, URISyntaxException {
        TrexEthernetPacket ethernetPacket = buildIPV4Packet(packetData);

        // prepare and save yaml data
        LOG.info("Prepare and save Yaml file");
        packetUtil.prepareAndSaveYamlFile(ethernetPacket.getPacket().getRawData(), packetData);

        //Generate pcap files
        LOG.info("Generate Pcap file for " + packetData.getTestFileName() + ".yaml");
        packetUtil.generatePcapFile(packetData.getTestFileName());

        // compare pcaps
        boolean result = packetUtil.comparePcaps(packetData.getTestFileName(), "generated_" + packetData.getTestFileName());
        Assert.assertEquals(result, true, "Invalid generated " + packetData.getTestFileName() + " pcap. ");
    }

    @Override
    protected IpV4Packet.Builder getIPV4PacketBuilder(int totalLength, int packetLength, Payload payload, PacketData packetData) throws UnknownHostException {
        LOG.info("Create IPV4 Packet");
        TrexIpV4Packet ipV4Packet = prepareIPV4Packet(totalLength, packetLength, payload, packetData);

        int ipv4PacketLength = PacketBuilderHelper.getIPV4PacketLength(packetData.isTaggedVlan(), packetLength, totalLength);
        ipV4Packet.setPacketLength(ipv4PacketLength);
        int udpPacketLength = PacketBuilderHelper.getTcpUdpPacketLength(packetData.isTaggedVlan(), packetLength, totalLength);
        // build  packet
        ipV4Packet.buildPacket(getUdpPacket(payload, udpPacketLength, packetData).getBuilder(), IpNumber.UDP);

        return ipV4Packet.getBuilder();
    }

    /**
     * Create UDP packet
     *
     * @param payload
     * @param packetLength
     * @param packetData
     * @return
     * @throws UnknownHostException
     */
    private TrexUdpPacket getUdpPacket(Payload payload, int packetLength, PacketData packetData) throws UnknownHostException {
        LOG.info("Create UDP Packet");
        TrexUdpPacket udpPacket = new TrexUdpPacket();
        udpPacket.setSrcPort(packetData.getUdpData().getSrcPort());
        udpPacket.setDstPort(packetData.getUdpData().getDstPort());
        udpPacket.setChecksum(Util.getShortFromString(packetData.getUdpData().getChecksum(), true));
        udpPacket.setLength(packetData.getUdpData().getLength());
        udpPacket.setPacketLength(packetLength);
        udpPacket.setPayload(payload);
        udpPacket.buildPacket();
        return udpPacket;
    }
}

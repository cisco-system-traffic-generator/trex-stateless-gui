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
import com.exalttech.trex.packets.TrexTcpPacket;
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
 * TCP profile test
 *
 * @author Georgekh
 */
public class TCPTest extends AbstractIPV4Test {

    @DataProvider(name = "packetData")
    public Object[][] packetData() throws IOException, URISyntaxException {
        return prepareTestData("TcpPcapData.json");
    }

    @Test(dataProvider = "packetData", dependsOnGroups = {"init"})
    public void testTCPProfile(PacketData packetData) throws IOException, InterruptedException, PcapNativeException, NotOpenException, URISyntaxException {
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
        int tcpPacketLength = PacketBuilderHelper.getTcpUdpPacketLength(packetData.isTaggedVlan(), packetLength, totalLength);
        // build  packet
        ipV4Packet.buildPacket(getTcpPacket(payload, tcpPacketLength, packetData).getBuilder(), IpNumber.TCP);

        return ipV4Packet.getBuilder();
    }

    /**
     * Create TCP packet
     *
     * @param payload
     * @param packetLength
     * @return TCP packet
     */
    private TrexTcpPacket getTcpPacket(Payload payload, int packetLength, PacketData packetData) {
        LOG.info("Create TCP Packet");
        TrexTcpPacket tcpPacket = new TrexTcpPacket();
        tcpPacket.setSrcPort(packetData.getTcpData().getSrcPort());
        tcpPacket.setDstPort(packetData.getTcpData().getDstPort());
        tcpPacket.setSequenceNumber(packetData.getTcpData().getSequenceNumber());
        tcpPacket.setAcknowledgmentNumber(packetData.getTcpData().getAckNumber());
        tcpPacket.setWindow(Util.getShortFromString(packetData.getTcpData().getWindow(), false));
        tcpPacket.setChecksum(Util.getShortFromString(packetData.getTcpData().getChecksum(), true));
        tcpPacket.setUrgentPointer(Util.getShortFromString(packetData.getTcpData().getUrgetPointer(), false));
        tcpPacket.setPacketLength(packetLength);
        tcpPacket.setPayload(payload);
        tcpPacket.buildPacket();
        return tcpPacket;
    }
}

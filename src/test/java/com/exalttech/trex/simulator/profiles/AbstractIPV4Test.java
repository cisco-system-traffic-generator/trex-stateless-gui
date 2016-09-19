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
import com.exalttech.trex.simulator.models.PacketData;
import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.exalttech.trex.ui.views.streams.builder.Payload;
import com.exalttech.trex.util.PacketUtil;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.IpV4Packet;

/**
 * Abstract class for IPV4/TCP/UDP test
 *
 * @author Georgekh
 */
public abstract class AbstractIPV4Test {

    protected static final Logger LOG = Logger.getLogger(AbstractIPV4Test.class.getName());
    PacketUtil packetUtil = new PacketUtil();

    /**
     * Prepare test data
     *
     * @param dataFileName
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    protected Object[][] prepareTestData(String dataFileName) throws IOException, URISyntaxException {
        return packetUtil.parsePacketDataFile(dataFileName);
    }

    /**
     * Build IPV4 packet
     *
     * @param packetData
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws PcapNativeException
     * @throws NotOpenException
     * @throws java.net.URISyntaxException
     */
    public TrexEthernetPacket buildIPV4Packet(PacketData packetData) throws IOException, InterruptedException, PcapNativeException, NotOpenException, URISyntaxException {

        LOG.info("create ethernet packet");
        int packetLength = PacketBuilderHelper.getPacketLength(packetData.getPacketLength().getLengthType(), packetData.getPacketLength().getFrameLength(), packetData.getPacketLength().getMaxLength());
        Payload payload = packetUtil.getPayload(packetData.getPayload());
        TrexEthernetPacket ethernetPacket = packetUtil.prepareEthernetPacket(packetData, packetLength, payload);

        // define VLAN 
        IpV4Packet.Builder ipV4Packet = getIPV4PacketBuilder(PacketBuilderHelper.getIPV4TotalLength(packetData.isTaggedVlan()), packetLength, payload, packetData);
        if (!packetData.isTaggedVlan()) {
            LOG.info("Add IPV4 packet");
            ethernetPacket.buildPacket(ipV4Packet);
        } else {
            packetUtil.addVlanToPacket(ethernetPacket, ipV4Packet);
        }

        return ethernetPacket;
    }

    /**
     * Create IPV4 packet
     *
     * @param totalLength
     * @param packetLength
     * @param payload
     * @param packetData
     * @return IPV4 packet
     */
    protected abstract IpV4Packet.Builder getIPV4PacketBuilder(int totalLength, int packetLength, Payload payload, PacketData packetData) throws UnknownHostException;

    protected TrexIpV4Packet prepareIPV4Packet(int totalLength, int packetLength, Payload payload, PacketData packetData) throws UnknownHostException {
        TrexIpV4Packet ipV4Packet = new TrexIpV4Packet();
        ipV4Packet.setSrcAddr(packetData.getIpv4Data().getSrcAddress().getAddress());
        ipV4Packet.setDstAddr(packetData.getIpv4Data().getDstAddress().getAddress());
        ipV4Packet.setTotalLength((short) totalLength);
        ipV4Packet.setPacketLength(packetLength + 4);
        ipV4Packet.setPayload(payload);

        return ipV4Packet;
    }
}

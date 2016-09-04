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
import com.exalttech.trex.packets.TrexVlanPacket;
import com.exalttech.trex.simulator.models.PacketData;
import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.exalttech.trex.ui.views.streams.builder.Payload;
import com.exalttech.trex.util.PacketUtil;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.namednumber.EtherType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Ethernet packet test
 *
 * @author Georgekh
 */
public class EthernetTest {

    private static final Logger LOG = Logger.getLogger(EthernetTest.class.getName());
    PacketUtil packetUtil = new PacketUtil();

    @DataProvider(name = "packetData")
    public Object[][] packetData() throws IOException, URISyntaxException {
        return packetUtil.parsePacketDataFile("EthernetPcapData.json");
    }

    @Test(dataProvider = "packetData", dependsOnGroups = {"init"})
    public void testEthernetProfile(PacketData packetData) throws IOException, InterruptedException, PcapNativeException, NotOpenException, URISyntaxException {
        // create profile
        LOG.info("create ethernet packet");
        int packetLength = PacketBuilderHelper.getPacketLength(packetData.getPacketLength().getLengthType(), packetData.getPacketLength().getFrameLength(), packetData.getPacketLength().getMaxLength());
        Payload payload = packetUtil.getPayload(packetData.getPayload());
        TrexEthernetPacket ethernetPacket = packetUtil.prepareEthernetPacket(packetData, packetLength, payload);

        if (!packetData.isTaggedVlan()) {
            ethernetPacket.buildPacket(null);
        } else {
            addVLanToEthernetPacket(ethernetPacket);
        }

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

    /**
     *
     * @param isTaggedVlan
     * @param ethernetPacket
     */
    private void addVLanToEthernetPacket(TrexEthernetPacket ethernetPacket) {

        LOG.info("Add VLAN data");
        ethernetPacket.setType(EtherType.DOT1Q_VLAN_TAGGED_FRAMES.value());
        TrexVlanPacket vlanPacket = new TrexVlanPacket();

        ethernetPacket.setAddPad(true);
        vlanPacket.setType((short) 0xFFFF);
        // No IPV4 
        vlanPacket.buildPacket(null);
        ethernetPacket.buildPacket(vlanPacket.getBuilder());

    }
}

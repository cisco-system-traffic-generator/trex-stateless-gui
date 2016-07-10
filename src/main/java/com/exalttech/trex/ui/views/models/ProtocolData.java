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
package com.exalttech.trex.ui.views.models;

import com.exalttech.trex.packets.TrexEthernetPacket;
import com.exalttech.trex.packets.TrexIpV4Packet;
import com.exalttech.trex.packets.TrexTcpPacket;
import com.exalttech.trex.packets.TrexUdpPacket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.log4j.Logger;

/**
 * Model that present protocol data object
 *
 * @author GeorgeKh
 */
public class ProtocolData {

    private static final Logger LOG = Logger.getLogger(ProtocolData.class);
    TrexEthernetPacket ethernetPacket;
    TrexIpV4Packet ipv4Packet;
    TrexTcpPacket tcpPacket;
    TrexUdpPacket udpPacket;

    BooleanProperty ipv4PacketProperty = new SimpleBooleanProperty();
    BooleanProperty tcpPacketProperty = new SimpleBooleanProperty();
    BooleanProperty udpPacketProperty = new SimpleBooleanProperty();

    /**
     *
     */
    public ProtocolData() {
        init();
    }

    private void init() {
        try {
            ethernetPacket = new TrexEthernetPacket();
            ipv4Packet = new TrexIpV4Packet();
            tcpPacket = new TrexTcpPacket();
            udpPacket = new TrexUdpPacket();
        } catch (Exception ex) {
            LOG.error("Couldn't initialize protocol data", ex);
        }
    }

    /**
     * Return ethernet packet
     *
     * @return
     */
    public TrexEthernetPacket getEthernetPacket() {
        return ethernetPacket;
    }

    /**
     * Return IPV4 packet
     *
     * @return
     */
    public TrexIpV4Packet getIpv4Packet() {
        return ipv4Packet;
    }

    /**
     * Return TCP packet
     *
     * @return
     */
    public TrexTcpPacket getTcpPacket() {
        return tcpPacket;
    }

    /**
     * Return UDP packet
     *
     * @return
     */
    public TrexUdpPacket getUdpPacket() {
        return udpPacket;
    }

    /**
     * Return true if IPv4 is selected, otherwise return false
     *
     * @return
     */
    public boolean isIPV4Selected() {
        return ipv4PacketProperty.getValue();
    }

    /**
     * Return true if tcp is selected, otherwise return false
     *
     * @return
     */
    public boolean isTCPSelected() {
        return tcpPacketProperty.getValue();
    }

    /**
     * Return true if udp is selected, otherwise return false
     *
     * @return
     */
    public boolean isUDPSelected() {
        return udpPacketProperty.getValue();
    }

    /**
     * Return ipv4 property
     *
     * @return
     */
    public BooleanProperty getIpv4PacketProperty() {
        return ipv4PacketProperty;
    }

    /**
     * Return tcp property
     *
     * @return
     */
    public BooleanProperty getTcpPacketProperty() {
        return tcpPacketProperty;
    }

    /**
     * Return udp property
     *
     * @return
     */
    public BooleanProperty getUdpPacketProperty() {
        return udpPacketProperty;
    }

}

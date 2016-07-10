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

import com.exalttech.trex.ui.views.streams.builder.Payload;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV4Packet.IpV4Tos;
import org.pcap4j.packet.IpV4Rfc1349Tos;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;

/**
 *
 * @author Georgekh
 */
public class TrexIpV4Packet {

    private static final Logger LOG = Logger.getLogger(TrexIpV4Packet.class.getName());
    private static final int DEFAULT_PACKET_LENGTH = 64;
    private static final String DEFAULT_IP_ADDRESS = "0.0.0.0";
    private IpVersion version;
    private byte ihl;
    private IpV4Tos tos;
    private short totalLength;

    private int packetLength = DEFAULT_PACKET_LENGTH;

    private short identification;
    private boolean reservedFlag;
    private byte ttl;

    private short headerChecksum;
    private Inet4Address srcAddr;
    private Inet4Address dstAddr;

    private IpV4Packet packet1;
    private IpV4Packet.Builder ipV4Builder;
    private Payload payload;

    /**
     *
     */
    public TrexIpV4Packet() {
        try {
            this.srcAddr = (Inet4Address) InetAddress.getByName(DEFAULT_IP_ADDRESS);
            this.dstAddr = (Inet4Address) InetAddress.getByName(DEFAULT_IP_ADDRESS);
            this.version = IpVersion.IPV4;
            this.ihl = (byte) 5;
            this.tos = IpV4Rfc1349Tos.newInstance((byte) 0x00);
            this.totalLength = (short) 40;
            this.identification = (short) 0X04D2;
            this.reservedFlag = true;

            this.ttl = 127;
            this.headerChecksum = (short) 0xEEEE;

        } catch (UnknownHostException ex) {
            LOG.error("Error initializing parameter", ex);
        }
    }

    /**
     *
     * @param builder
     * @param protocol
     */
    public void buildPacket(AbstractPacket.AbstractBuilder builder, IpNumber protocol) {

        byte[] pad = new byte[1];
        String payloadPattern = payload.getPayloadPattern();
        String payloadString = payload.getPayloadType().getPadPayloadString(payloadPattern, (totalLength - 20) * 2);

        if (packetLength > DEFAULT_PACKET_LENGTH) {
            payloadString = payload.getPayloadType().getPadPayloadString(payloadPattern, payloadString.length() + ((packetLength - DEFAULT_PACKET_LENGTH) * 2));
            this.totalLength = (short) ((int) totalLength + (packetLength - DEFAULT_PACKET_LENGTH));
        }
        pad = payload.getPayloadPad(payloadString, totalLength - 20);

        ipV4Builder = new IpV4Packet.Builder();
        ipV4Builder.version(version)
                .ihl(ihl)
                .tos(tos)
                .totalLength(totalLength)
                .identification(identification)
                .reservedFlag(reservedFlag)
                .ttl(ttl)
                .fragmentOffset((short) 0)
                .protocol(protocol)
                .headerChecksum(headerChecksum)
                .srcAddr(srcAddr)
                .dstAddr(dstAddr)
                .protocol(protocol)
                .correctChecksumAtBuild(true)
                //  .correctLengthAtBuild(true)
                .payloadBuilder(builder);

        if (protocol != IpNumber.TCP && protocol != IpNumber.UDP) {
            ipV4Builder.paddingAtBuild(false)
                    .padding(pad);
        } else {
            ipV4Builder.paddingAtBuild(true).correctLengthAtBuild(true);
        }

        this.packet1 = ipV4Builder.build();
    }

    /**
     *
     * @return
     */
    protected Packet getPacket() {
        return packet1;
    }

    /**
     *
     * @return
     */
    public Inet4Address getSrcAddr() {
        return srcAddr;
    }

    /**
     *
     * @param srcAddr
     * @throws UnknownHostException
     */
    public void setSrcAddr(String srcAddr) throws UnknownHostException {

        this.srcAddr = (Inet4Address) InetAddress.getByName(srcAddr);
    }

    /**
     *
     * @return
     */
    public Inet4Address getDstAddr() {
        return dstAddr;
    }

    /**
     *
     * @param dstAddr
     * @throws UnknownHostException
     */
    public void setDstAddr(String dstAddr) throws UnknownHostException {
        this.dstAddr = (Inet4Address) InetAddress.getByName(dstAddr);
    }

    /**
     *
     * @return
     */
    public IpV4Packet.Builder getBuilder() {
        return ipV4Builder;
    }

    /**
     * Set payload
     *
     * @param payload
     */
    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    /**
     * Get payload
     *
     * @return
     */
    public Payload getPayload() {
        return payload;
    }

    /**
     *
     * @return
     */
    public int getPacketLength() {
        return packetLength;
    }

    /**
     *
     * @param packetLength
     */
    public void setPacketLength(int packetLength) {
        this.packetLength = packetLength;
    }

    /**
     *
     * @return
     */
    public short getTotalLength() {
        return totalLength;
    }

    /**
     *
     * @param totalLength
     */
    public void setTotalLength(short totalLength) {
        this.totalLength = totalLength;
    }

}

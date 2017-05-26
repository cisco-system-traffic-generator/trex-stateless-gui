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
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.packet.namednumber.UdpPort;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Georgekh
 */
@SuppressWarnings("javadoc")
public class TrexUdpPacket {

    private UdpPort srcPort;
    private UdpPort dstPort;
    private short length;
    private short checksum;

    private UdpPacket packet;
    private UdpPacket.Builder udpBuilder;
    private Payload payload;

    private int packetLength = 60;

    /**
     *
     */
    public TrexUdpPacket() {
        this.srcPort = UdpPort.getInstance((short) 0);
        this.dstPort = UdpPort.getInstance((short) 0);
        this.checksum = (short) 0x0;
        this.length = (short) 12;
    }

    /**
     * Build packet
     *
     * @throws java.net.UnknownHostException
     */
    public void buildPacket() throws UnknownHostException {

        int calculatedLength = getPacketLength();
        String payloadString = payload.getPayloadType().getPadPayloadString(payload.getPayloadPattern(), calculatedLength * 2);

        udpBuilder = new UdpPacket.Builder();
        UnknownPacket.Builder unknownb = new UnknownPacket.Builder();
        unknownb.rawData(payload.getPayloadPad(payloadString, calculatedLength));
        Inet4Address srcAddr = (Inet4Address) InetAddress.getByAddress(
                new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0}
        );
        Inet4Address dstAddr = (Inet4Address) InetAddress.getByAddress(
                new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0}
        );

        udpBuilder.srcPort(UdpPort.getInstance((short) srcPort.value()))
                .dstPort(UdpPort.getInstance((short) dstPort.value()))
                .srcAddr(srcAddr)
                .dstAddr(dstAddr)
                .length((short) calculatedLength)
                .payloadBuilder(unknownb)
                .checksum((short) 0)
                .correctLengthAtBuild(false);

        this.packet = udpBuilder.build();
    }

    /**
     *
     * @return
     */
    protected Packet getPacket() {
        return packet;
    }

    /**
     *
     * @return
     */
    public UdpPacket.Builder getBuilder() {
        return udpBuilder;
    }

    // PORTS
    /**
     *
     * @param srcPort
     */
    public void setSrcPort(int srcPort) {
        this.srcPort = UdpPort.getInstance((short) srcPort);

    }

    /**
     *
     * @param dstPort
     */
    public void setDstPort(int dstPort) {
        this.dstPort = UdpPort.getInstance((short) dstPort);
    }

    /**
     *
     * @return
     */
    public UdpPort getSrcPort() {
        return srcPort;
    }

    /**
     *
     * @return
     */
    public UdpPort getDstPort() {
        return dstPort;
    }

    /**
     *
     * @return
     */
    public short getLength() {
        return length;
    }

    /**
     *
     * @param length
     */
    public void setLength(int length) {
        this.length = (short) length;
    }

    /**
     *
     * @return
     */
    public short getChecksum() {
        return checksum;
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
     * Return payload
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

}

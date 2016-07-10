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

import com.exalttech.trex.ui.views.streams.buildstream.Payload;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.MacAddress;

/**
 *
 * @author Georgekh
 */
public class TrexEthernetPacket {

    private EthernetPacket packet;
    private MacAddress dstAddr;
    private MacAddress srcAddr;
    private EtherType type;

    private EthernetPacket.Builder ethernetBuilder;
    private boolean addPad = false;
    private Payload payload = null;
    private int length = 64;

    /**
     *
     */
    public TrexEthernetPacket() {

        this.srcAddr = MacAddress.getByName("00:00:00:00:00:00");
        this.dstAddr = MacAddress.getByName("00:00:00:00:00:00");
        this.type = EtherType.IPV4;
        this.addPad = false;
    }

    /**
     *
     * @param builder
     */
    public void buildPacket(AbstractPacket.AbstractBuilder builder) {

        if (builder == null) {

            this.type = EtherType.getInstance((short) 0xFFFF);
        }

        ethernetBuilder = new EthernetPacket.Builder();
        ethernetBuilder.dstAddr(dstAddr)
                .srcAddr(srcAddr)
                .type(type)
                .payloadBuilder(builder);

        byte[] pad;
        if (builder == null || addPad) {

            if (payload != null) {
                String payloadString = payload.getPayloadType().getPadPayloadString(payload.getPayloadPattern(), 100000);
                pad = new BigInteger(payloadString, 16).toByteArray();
            } else {
                pad = new byte[0];
            }

            ethernetBuilder.paddingAtBuild(false).pad(pad);
            this.packet = ethernetBuilder.build();
            fixPacketLength();
        } else {
            ethernetBuilder.paddingAtBuild(true);
            this.packet = ethernetBuilder.build();
        }
    }

    /**
     *
     * @return
     */
    public Packet getPacket() {
        return packet;
    }

    /**
     *
     * @param packet
     */
    public void setPacket(EthernetPacket packet) {
        this.packet = packet;
    }

    /**
     *
     * @return
     */
    public MacAddress getDstAddr() {
        return dstAddr;
    }

    /**
     *
     * @param dstAddr
     */
    public void setDstAddr(String dstAddr) {
        this.dstAddr = MacAddress.getByName(dstAddr);
    }

    /**
     *
     * @return
     */
    public MacAddress getSrcAddr() {
        return srcAddr;
    }

    /**
     *
     * @param srcAddr
     */
    public void setSrcAddr(String srcAddr) {
        this.srcAddr = MacAddress.getByName(srcAddr);
    }

    /**
     *
     * @param type
     */
    public void setType(short type) {
        this.type = EtherType.getInstance(type);
    }

    /**
     *
     * @return
     */
    public EthernetPacket.Builder getEthernetBuilder() {
        return ethernetBuilder;
    }

    /**
     *
     * @return
     */
    public EtherType getType() {
        return type;
    }

    /**
     *
     * @param addPad
     */
    public void setAddPad(boolean addPad) {
        this.addPad = addPad;
    }

    /**
     *
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     *
     * @param length
     */
    public void setLength(int length) {
        this.length = length;
    }

    private void fixPacketLength() {
        try {
            EthernetPacket newPacket = packet;
            if (packet.getRawData().length < getLength()) {
                byte[] pad = new byte[getLength() - packet.getRawData().length];
                newPacket = EthernetPacket.newPacket(ArrayUtils.addAll(packet.getRawData(), pad), 0, getLength());
            } else {
                newPacket = EthernetPacket.newPacket(packet.getRawData(), 0, getLength());
            }

            setPacket((EthernetPacket) newPacket);
        } catch (IllegalRawDataException ex) {
            Logger.getLogger(TrexEthernetPacket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param payload
     */
    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    /**
     *
     * @return
     */
    public Payload getPayload() {
        return payload;
    }

}

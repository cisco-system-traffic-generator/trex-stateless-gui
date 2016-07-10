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
import java.util.ArrayList;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpEndOfOptionList;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.TcpPacket.TcpOption;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.packet.namednumber.TcpPort;

/**
 *
 * @author Georgekh
 */
public class TrexTcpPacket {

    private TcpPort srcPort;
    private TcpPort dstPort;
    private int sequenceNumber;
    private int acknowledgmentNumber;
    private byte dataOffset;
    private byte reserved;

    private boolean urg;
    private boolean ack;
    private boolean psh;
    private boolean rst;
    private boolean syn;
    private boolean fin;

    private short window;
    private short checksum;
    private short urgentPointer;

    private TcpPacket packet;
    private TcpPacket.Builder tcpBuilder;
    private Payload payload;

    private int packetLength = 60;

    /**
     *
     */
    public TrexTcpPacket() {
        this.dataOffset = 5;
        this.reserved = (byte) 11;
        this.ack = false;
        this.urg = false;
        this.fin = false;
        this.psh = false;
        this.rst = false;
        this.syn = false;
        this.window = (short) 9999;
        this.checksum = (short) 0xABCD;
        this.urgentPointer = (short) 1111;

        this.srcPort = TcpPort.getInstance((short) 0);
        this.dstPort = TcpPort.getInstance((short) 0);

        this.sequenceNumber = 1234567;
        this.acknowledgmentNumber = 7654321;
    }

    /**
     * Build packet
     */
    public void buildPacket() {

        int calculatedLength = getPacketLength() - 14;

        String payloadString = payload.getPayloadType().getPadPayloadString(payload.getPayloadPattern(), calculatedLength * 2);

        UnknownPacket.Builder unknownb = null;
        if (calculatedLength != 0) {
            unknownb = new UnknownPacket.Builder();
            unknownb.rawData(payload.getPayloadPad(payloadString, calculatedLength));
        }
        ArrayList<TcpOption> options = new ArrayList<TcpOption>();

        options.add(TcpEndOfOptionList.getInstance());

        tcpBuilder = new TcpPacket.Builder();
        tcpBuilder.dstPort(dstPort)
                .srcPort(srcPort)
                .sequenceNumber(sequenceNumber)
                .acknowledgmentNumber(acknowledgmentNumber)
                .dataOffset(dataOffset)
                .reserved(reserved)
                .urg(urg)
                .ack(ack)
                .psh(psh)
                .rst(rst)
                .syn(syn)
                .fin(fin)
                .window(window)
                .checksum(checksum)
                .urgentPointer(urgentPointer)
                .options(options)
                .padding(new byte[]{(byte) 0xaa})
                .correctChecksumAtBuild(false)
                .correctLengthAtBuild(false)
                .paddingAtBuild(false);

        if (calculatedLength != 0) {
            tcpBuilder.payloadBuilder(unknownb);
        }
        this.packet = tcpBuilder.build();
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
    public TcpPacket.Builder getBuilder() {
        return tcpBuilder;
    }

    // Boolean Flags
    /**
     *
     * @param urg
     */
    public void setUrg(boolean urg) {
        this.urg = urg;
    }

    /**
     *
     * @param ack
     */
    public void setAck(boolean ack) {
        this.ack = ack;
    }

    /**
     *
     * @param psh
     */
    public void setPsh(boolean psh) {
        this.psh = psh;
    }

    /**
     *
     * @param rst
     */
    public void setRst(boolean rst) {
        this.rst = rst;
    }

    /**
     *
     * @param syn
     */
    public void setSyn(boolean syn) {
        this.syn = syn;
    }

    /**
     *
     * @param fin
     */
    public void setFin(boolean fin) {
        this.fin = fin;
    }

    // PORTS
    /**
     *
     * @param srcPort
     */
    public void setSrcPort(int srcPort) {
        this.srcPort = TcpPort.getInstance((short) srcPort);

    }

    /**
     *
     * @param dstPort
     */
    public void setDstPort(int dstPort) {
        this.dstPort = TcpPort.getInstance((short) dstPort);
    }

    // Sequence and Acknowledgment numbers
    /**
     *
     * @param sequenceNumber
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     *
     * @param acknowledgmentNumber
     */
    public void setAcknowledgmentNumber(int acknowledgmentNumber) {
        this.acknowledgmentNumber = acknowledgmentNumber;
    }

    /**
     *
     * @param window
     */
    public void setWindow(short window) {
        this.window = window;
    }

    /**
     *
     * @param checksum
     */
    public void setChecksum(short checksum) {
        this.checksum = checksum;
    }

    /**
     *
     * @param urgentPointer
     */
    public void setUrgentPointer(short urgentPointer) {
        this.urgentPointer = urgentPointer;
    }

    /**
     *
     * @return
     */
    public TcpPort getSrcPort() {
        return srcPort;
    }

    /**
     *
     * @return
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     *
     * @return
     */
    public boolean isUrg() {
        return urg;
    }

    /**
     *
     * @return
     */
    public boolean isAck() {
        return ack;
    }

    /**
     *
     * @return
     */
    public boolean isRst() {
        return rst;
    }

    /**
     *
     * @return
     */
    public boolean isSyn() {
        return syn;
    }

    /**
     *
     * @return
     */
    public short getWindow() {
        return window;
    }

    /**
     *
     * @return
     */
    public short getUrgentPointer() {
        return urgentPointer;
    }

    /**
     *
     * @return
     */
    public TcpPacket.Builder getTcpBuilder() {
        return tcpBuilder;
    }

    /**
     *
     * @return
     */
    public TcpPort getDstPort() {
        return dstPort;
    }

    /**
     *
     * @return
     */
    public byte getDataOffset() {
        return dataOffset;
    }

    /**
     *
     * @return
     */
    public boolean isPsh() {
        return psh;
    }

    /**
     *
     * @return
     */
    public boolean isFin() {
        return fin;
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

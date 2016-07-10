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
package com.exalttech.trex.ui.views.streams.builder;

import com.exalttech.trex.packets.TrexTcpPacket;
import com.exalttech.trex.ui.views.streams.binders.TCPProtocolDataBinding;
import com.exalttech.trex.util.Util;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * TCP protocol tab view implementation
 *
 * @author GeorgeKh
 */
public class TCPProtocolView extends AbstractProtocolView {

    TextField srcPort;
    TextField dstPort;
    TextField seqNumber;
    TextField ackNumber;
    TextField headerLength;
    TextField window;
    TextField checksum;
    TextField urgentPointer;

    CheckBox srcPortCB;
    CheckBox dstPortCB;
    CheckBox headerLengthCB;
    CheckBox checkSumCB;
    CheckBox urg;
    CheckBox ack;
    CheckBox psh;
    CheckBox rst;
    CheckBox syn;
    CheckBox fin;

    /**
     * Constructor
     *
     * @param dataBinding
     */
    public TCPProtocolView(TCPProtocolDataBinding dataBinding) {
        super("Transmission Control Protocol", 270, dataBinding);
    }

    /**
     * Build custom view
     */
    @Override
    protected void buildCustomProtocolView() {
        // source port
        srcPortCB = new CheckBox("Override source port");
        addCheckBox(srcPortCB, 8, 15);
        srcPort = new TextField();
        addInput(srcPort, 5, 220, 170);
        srcPort.disableProperty().bind(srcPortCB.selectedProperty().not());

        // Dst port
        dstPortCB = new CheckBox("Override destination port");
        addCheckBox(dstPortCB, 43, 15);
        dstPort = new TextField();
        addInput(dstPort, 40, 220, 170);
        dstPort.disableProperty().bind(dstPortCB.selectedProperty().not());

        // add seq number
        addLabel("Sequence number", 78, 38);
        seqNumber = new TextField();
        addInput(seqNumber, 75, 220, 170);

        // add ack number
        addLabel("Acknowledge number", 113, 38);
        ackNumber = new TextField();
        addInput(ackNumber, 110, 220, 170);

        // add window
        addLabel("Window", 148, 38);
        window = new TextField();
        addInput(window, 145, 220, 170);

        // add header length
        headerLengthCB = new CheckBox("Override header length(x4)");

        headerLength = new TextField();

        headerLength.disableProperty().bind(headerLengthCB.selectedProperty().not());

        // add checksum
        checkSumCB = new CheckBox("Override checksum");
        addCheckBox(checkSumCB, 8, 500);
        checksum = new TextField();
        addInput(checksum, 5, 660, 170);
        checksum.disableProperty().bind(checkSumCB.selectedProperty().not());

        // add urgent pointer
        addLabel("Urgent pointer", 43, 520);
        urgentPointer = new TextField();
        addInput(urgentPointer, 40, 660, 170);

        addSeparator(80, 490, 350);

        addLabel("Flags", 95, 500);
        urg = new CheckBox("URG");
        addCheckBox(urg, 125, 500);
        ack = new CheckBox("ACK");
        addCheckBox(ack, 125, 575);
        psh = new CheckBox("PSH");
        addCheckBox(psh, 125, 650);
        rst = new CheckBox("RST");
        addCheckBox(rst, 160, 500);
        syn = new CheckBox("SYN");
        addCheckBox(syn, 160, 575);
        fin = new CheckBox("FIN");
        addCheckBox(fin, 160, 650);

    }

    /**
     * Return TCP packet
     *
     * @param payload
     * @param packetLength
     * @return @throws Exception
     */
    public TrexTcpPacket getTcpPacket(Payload payload, int packetLength) throws Exception {
        TrexTcpPacket tcpPacket = new TrexTcpPacket();
        tcpPacket.setSrcPort(Util.getPortValue(srcPort.getText()));
        tcpPacket.setDstPort(Util.getPortValue(dstPort.getText()));
        tcpPacket.setSequenceNumber(Util.getIntFromString(seqNumber.getText()));
        tcpPacket.setAcknowledgmentNumber(Util.getIntFromString(ackNumber.getText()));
        //header length not set
        tcpPacket.setWindow(Util.getShortFromString(window.getText(), false));
        tcpPacket.setChecksum(Util.getShortFromString(checksum.getText(), true));
        tcpPacket.setUrgentPointer(Util.getShortFromString(urgentPointer.getText(), false));

        // set flags
        tcpPacket.setUrg(urg.isSelected());
        tcpPacket.setAck(ack.isSelected());
        tcpPacket.setPsh(psh.isSelected());
        tcpPacket.setRst(rst.isSelected());
        tcpPacket.setSyn(syn.isSelected());
        tcpPacket.setFin(fin.isSelected());

        tcpPacket.setPacketLength(packetLength);
        tcpPacket.setPayload(payload);
        // build packet

        tcpPacket.buildPacket();
        return tcpPacket;
    }

    /**
     * Add input fields validations
     */
    @Override
    protected void addInputValidation() {
        window.setTextFormatter(Util.getNumberFilter(4));
        checksum.setTextFormatter(Util.getHexFilter(4));
        urgentPointer.setTextFormatter(Util.getNumberFilter(4));
        srcPort.setTextFormatter(Util.getNumberFilter(5));
        dstPort.setTextFormatter(Util.getNumberFilter(5));
        seqNumber.setTextFormatter(Util.getNumberFilter(6));
        ackNumber.setTextFormatter(Util.getNumberFilter(4));
    }

    /**
     * Bind field with related properties
     */
    @Override
    protected void bindProperties() {
        TCPProtocolDataBinding tcpDB = (TCPProtocolDataBinding) dataBinding;
        srcPort.textProperty().bindBidirectional(tcpDB.getSrcPort());
        dstPort.textProperty().bindBidirectional(tcpDB.getDstPort());
        srcPortCB.selectedProperty().bindBidirectional(tcpDB.getOverrideSrcPort());
        dstPortCB.selectedProperty().bindBidirectional(tcpDB.getOverrideDstPort());
        seqNumber.textProperty().bindBidirectional(tcpDB.getSequeceNumber());
        ackNumber.textProperty().bindBidirectional(tcpDB.getAckNumber());
        window.textProperty().bindBidirectional(tcpDB.getWindow());
        checksum.textProperty().bindBidirectional(tcpDB.getChecksum());
        checkSumCB.selectedProperty().bindBidirectional(tcpDB.getOverrideChecksum());
        urgentPointer.textProperty().bindBidirectional(tcpDB.getUrgentPointer());
        urg.selectedProperty().bindBidirectional(tcpDB.getUrg());
        ack.selectedProperty().bindBidirectional(tcpDB.getAck());
        psh.selectedProperty().bindBidirectional(tcpDB.getPsh());
        rst.selectedProperty().bindBidirectional(tcpDB.getRst());
        syn.selectedProperty().bindBidirectional(tcpDB.getSyn());
        fin.selectedProperty().bindBidirectional(tcpDB.getFin());
    }
}

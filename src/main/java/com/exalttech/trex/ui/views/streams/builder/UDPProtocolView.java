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

import com.exalttech.trex.packets.TrexUdpPacket;
import com.exalttech.trex.ui.views.streams.binders.UDPProtocolDataBinding;
import com.exalttech.trex.util.Util;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * UDP protocol tab view implementation
 *
 * @author GeorgeKh
 */
public class UDPProtocolView extends AbstractProtocolView {

    CheckBox srcPortCB;
    CheckBox dstPortCB;
    CheckBox lengthCB;
    CheckBox checksumCB;

    TextField srcPort;
    TextField dstPort;
    TextField length;
    TextField checksum;

    /**
     * Constructor
     *
     * @param dataBinding
     */
    public UDPProtocolView(UDPProtocolDataBinding dataBinding) {
        super("User Datagram Protcol", 200, dataBinding);
    }

    /**
     * Build custom view
     */
    @Override
    protected void buildCustomProtocolView() {
        // add src port
        srcPortCB = new CheckBox("Override source port");
        addCheckBox(srcPortCB, 13, 10);
        srcPort = new TextField();
        addInput(srcPort, 10, 210, 220);
        srcPort.disableProperty().bind(srcPortCB.selectedProperty().not());

        // add dst port
        dstPortCB = new CheckBox("Override destination port");
        addCheckBox(dstPortCB, 48, 10);
        dstPort = new TextField();
        addInput(dstPort, 45, 210, 220);
        dstPort.disableProperty().bind(dstPortCB.selectedProperty().not());

        // add length
        lengthCB = new CheckBox("Override length");
        addCheckBox(lengthCB, 83, 10);
        length = new TextField();
        addInput(length, 80, 210, 220);
        length.disableProperty().bind(lengthCB.selectedProperty().not());

        // add checksum
        checksumCB = new CheckBox("Override checksum");
        addCheckBox(checksumCB, 118, 10);
        checksum = new TextField();
        addInput(checksum, 115, 210, 220);
        checksum.disableProperty().bind(checksumCB.selectedProperty().not());
    }

    /**
     * Return UDP packet
     *
     * @param payload
     * @param packetLength
     * @return @throws Exception
     */
    public TrexUdpPacket getUDPPacket(Payload payload, int packetLength) throws Exception {
        TrexUdpPacket udpPacket = new TrexUdpPacket();
        udpPacket.setSrcPort(Util.getPortValue(srcPort.getText()));
        udpPacket.setDstPort(Util.getPortValue(dstPort.getText()));
        udpPacket.setChecksum(Util.getShortFromString(checksum.getText(), true));
        udpPacket.setLength(Util.getIntFromString(length.getText()));
        udpPacket.setPacketLength(packetLength);
        udpPacket.setPayload(payload);
        // build packet

        udpPacket.buildPacket();
        return udpPacket;
    }

    /**
     * add input field validation
     */
    @Override
    protected void addInputValidation() {
        checksum.setTextFormatter(Util.getHexFilter(4));
        srcPort.setTextFormatter(Util.getNumberFilter(5));
        dstPort.setTextFormatter(Util.getNumberFilter(5));
        length.setTextFormatter(Util.getNumberFilter(5));
    }

    /**
     * Bind fields with related properties
     */
    @Override
    protected void bindProperties() {
        UDPProtocolDataBinding udpDB = (UDPProtocolDataBinding) dataBinding;
        srcPort.textProperty().bindBidirectional(udpDB.getSrcPortProperty());
        dstPort.textProperty().bindBidirectional(udpDB.getDstPortProperty());
        srcPortCB.selectedProperty().bindBidirectional(udpDB.getOverrideSrcPortProperty());
        dstPortCB.selectedProperty().bindBidirectional(udpDB.getOverrideDstPortProperty());
        length.textProperty().bindBidirectional(udpDB.getLengthProperty());
        lengthCB.selectedProperty().bindBidirectional(udpDB.getOverrideLengthProperty());
        checksum.textProperty().bindBidirectional(udpDB.getChecksumProperty());
        checksumCB.selectedProperty().bindBidirectional(udpDB.getOverrideChecksumProperty());
    }

}

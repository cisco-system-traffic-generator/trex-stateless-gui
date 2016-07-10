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
package com.exalttech.trex.ui.views.streams.buildstream;

import com.exalttech.trex.packets.TrexEthernetPacket;
import com.exalttech.trex.packets.TrexIpV4Packet;
import com.exalttech.trex.packets.TrexVlanPacket;
import com.exalttech.trex.ui.views.models.AddressProtocolData;
import com.exalttech.trex.ui.views.streams.BuilderBindingData;
import com.exalttech.trex.ui.views.streams.ProtocolSelection;
import com.exalttech.trex.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setRightAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;
import org.pcap4j.packet.IpV4Packet.Builder;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;

/**
 * Protocol data view implementation
 *
 * @author GeorgeKh
 */
public class ProtocolDataView extends Accordion {

    ProtocolSelection selections;
    TitledPane current;
    MacProtocolView macView;
    IPV4ProtocolView ipv4View;
    EthernetProtocolView ethernetView;
    TCPProtocolView tcpView;
    UDPProtocolView udpView;
    PayloadView payloadView;
    VLanProtocolView vlanView;
    private boolean isFixIPV4ChecksumAdded;
    private String splitByVar;
    private int vmCacheSize;

    /**
     *
     */
    public ProtocolDataView() {
        getStyleClass().add("protocolData");
        init();
    }

    /**
     * Initialize view
     */
    private void init() {
        setTopAnchor(this, 0d);
        setLeftAnchor(this, 0d);
        setRightAnchor(this, 0d);
        setPrefHeight(150);
    }

    /**
     * Do initialize tabs
     *
     * @param selections
     */
    public void doInitializingTabs(BuilderBindingData selections) {
        this.selections = selections.getProtocolSelection();
        macView = new MacProtocolView(selections.getMacDB());
        ethernetView = new EthernetProtocolView(selections.getEthernetDB());
        ipv4View = new IPV4ProtocolView(selections.getIpv4DB());
        tcpView = new TCPProtocolView(selections.getTcpProtocolDB());
        udpView = new UDPProtocolView(selections.getUdpProtocolDB());
        payloadView = new PayloadView(selections.getPayloadDB());
        vlanView = new VLanProtocolView(selections.getVlanDB());

        updateTabs();
        bindSelection();
    }

    /**
     * Bind properties
     */
    public void bindSelection() {

        this.selections.getIpv4Property().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            ethernetView.reset();
            ipv4View.reset();
            updateTabs();
        });

        this.selections.getTcpProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            tcpView.reset();
            updateTabs();
        });

        this.selections.getUdpProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            udpView.reset();
            updateTabs();
        });

        this.selections.getPatternProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            payloadView.reset();
            updateTabs();
        });

        this.selections.getTaggedVlanProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                vlanUpdated();
            }
        });
        this.selections.getStackedVlanProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                vlanUpdated();
            }
        });
    }

    /**
     * Enable/Disable stacked VLAN
     *
     * @param isStacked
     */
    private void vlanUpdated() {
        vlanView.reset();
        updateTabs();
    }

    /**
     * Update tabs view
     */
    private void updateTabs() {
        getPanes().clear();
        getPanes().add(macView);
        if (selections.isTaggedVlanSelected() || selections.isStackedVlanSelected()) {
            vlanView.setStacked(selections.isStackedVlanSelected());
            getPanes().add(vlanView);
        }
        if (selections.isIPV4Selected()) {
            getPanes().add(ethernetView);
            getPanes().add(ipv4View);
        }
        if (selections.isTCPSelected()) {
            getPanes().add(tcpView);
        } else if (selections.isUDPSelected()) {
            getPanes().add(udpView);
        }
        if (selections.isPatternSelected()) {
            getPanes().add(payloadView);
        }
    }

    /**
     * Return protocol data view
     *
     * @return
     * @throws Exception
     */
    public TrexEthernetPacket getProtocolData() throws Exception {
        int packetLength = 60;
        String operation = getOperationFromType(selections.getFrameLengthType());
        if (PacketLengthType.FIXED.getTitle().equals(operation)) {
            packetLength = Integer.parseInt(selections.getFrameLength()) - 4;
        } else {
            packetLength = Integer.parseInt(selections.getMaxLength()) - 4;
        }

        TrexEthernetPacket ethernetPacket = new TrexEthernetPacket();
        // set mac address
        ethernetPacket.setSrcAddr(macView.getSourceAddress().getAddress());
        ethernetPacket.setDstAddr(macView.getDestinationAddress().getAddress());
        ethernetPacket.setLength(packetLength);

        // set payload in ethernet
        ethernetPacket.setPayload(payloadView.getPayload());
        if (ethernetView.isOverrideType()) {
            ethernetPacket.setType(ethernetView.getType());
        }

        if (selections.isTaggedVlanSelected()) {
            if (!ethernetView.isOverrideType()) {
                ethernetPacket.setType(EtherType.DOT1Q_VLAN_TAGGED_FRAMES.value());
            }
            TrexVlanPacket vlanPacket = vlanView.getVlanList().get(0);
            /// IF IPV4 is selected
            if (!vlanPacket.isOverrideType()) {
                if (!selections.isIPV4Selected()) {
                    ethernetPacket.setAddPad(true);
                    vlanPacket.setType((short) 0xFFFF);
                } else {
                    vlanPacket.setType(EtherType.IPV4.value());

                }
            }
            Builder ipV4Packet = getIPV4Packet(42, packetLength);
            vlanPacket.buildPacket(ipV4Packet);

            ethernetPacket.buildPacket(vlanPacket.getBuilder());
        } else {
            ethernetPacket.buildPacket(getIPV4Packet(46, packetLength));
        }

        return ethernetPacket;
    }

    /**
     * Prepare and return IPV4 builder
     *
     * @return
     * @throws Exception
     */
    private Builder getIPV4Packet(int totalLength, int packetLength) throws Exception {
        if (selections.isIPV4Selected()) {
            TrexIpV4Packet ipV4Packet = new TrexIpV4Packet();
            ipV4Packet.setSrcAddr(ipv4View.getSourceAddress().getAddress());
            ipV4Packet.setDstAddr(ipv4View.getDestinationAddress().getAddress());
            ipV4Packet.setTotalLength((short) totalLength);
            ipV4Packet.setPacketLength(packetLength + 4);
            Payload payload = payloadView.getPayload();
            ipV4Packet.setPayload(payload);

            if (selections.isTCPSelected()) {

                if (totalLength == 46) {
                    ipV4Packet.setPacketLength(packetLength - totalLength + 4);
                    ipV4Packet.buildPacket(tcpView.getTcpPacket(payload, packetLength - totalLength + 4).getBuilder(), IpNumber.TCP);
                } else {//  VLAN
                    ipV4Packet.setPacketLength(packetLength - totalLength - 8);
                    ipV4Packet.buildPacket(tcpView.getTcpPacket(payload, packetLength - totalLength - 4).getBuilder(), IpNumber.TCP);
                }

            } else if (selections.isUDPSelected()) {
                if (totalLength == 46) {
                    ipV4Packet.setPacketLength(packetLength - totalLength + 4);
                    ipV4Packet.buildPacket(udpView.getUDPPacket(payload, packetLength - totalLength + 4).getBuilder(), IpNumber.UDP);
                } else { //  VLAN
                    ipV4Packet.setPacketLength(packetLength - totalLength - 8);
                    ipV4Packet.buildPacket(udpView.getUDPPacket(payload, packetLength - totalLength - 4).getBuilder(), IpNumber.UDP);
                }

            } else {
                ipV4Packet.buildPacket(null, IpNumber.getInstance((byte) 0));
            }
            return ipV4Packet.getBuilder();
        }
        return null;
    }

    private List<Object> getPacketLenVMInstruction(String name, String type, String minLength, String maxLength, boolean taggedVlanSelected) {
        ArrayList<Object> vmInstructionList = new ArrayList<>();
        String operation = getOperationFromType(type);
        if (PacketLengthType.FIXED.getTitle().equals(operation)) {
            return vmInstructionList;
        }

        LinkedHashMap<String, Object> firstVMInstruction = new LinkedHashMap<>();

        firstVMInstruction.put("init_value", Util.getIntFromString(minLength) - 4);
        firstVMInstruction.put("max_value", Util.getIntFromString(maxLength) - 4);
        firstVMInstruction.put("min_value", Util.getIntFromString(minLength) - 4);

        firstVMInstruction.put("name", name);
        firstVMInstruction.put("op", operation);
        firstVMInstruction.put("size", 2);
        firstVMInstruction.put("step", 1);
        firstVMInstruction.put("type", "flow_var");

        LinkedHashMap<String, Object> secondVMInstruction = new LinkedHashMap<>();
        secondVMInstruction.put("name", name);
        secondVMInstruction.put("type", "trim_pkt_size");

        LinkedHashMap<String, Object> thirdVMInstruction = new LinkedHashMap<>();
        thirdVMInstruction.put("add_value", taggedVlanSelected ? -18 : -14);
        thirdVMInstruction.put("is_big_endian", true);
        thirdVMInstruction.put("name", name);
        thirdVMInstruction.put("pkt_offset", taggedVlanSelected ? 20 : 16);
        thirdVMInstruction.put("type", "write_flow_var");

        LinkedHashMap<String, Object> forthVMInstruction = new LinkedHashMap<>();
        forthVMInstruction.put("pkt_offset", taggedVlanSelected ? 18 : 14);
        forthVMInstruction.put("type", "fix_checksum_ipv4");

        LinkedHashMap<String, Object> fifthVMInstruction = new LinkedHashMap<>();
        fifthVMInstruction.put("add_value", taggedVlanSelected ? -38 : -34);
        fifthVMInstruction.put("is_big_endian", true);
        fifthVMInstruction.put("name", name);
        fifthVMInstruction.put("pkt_offset", taggedVlanSelected ? 42 : 38);
        fifthVMInstruction.put("type", "write_flow_var");

        vmInstructionList.add(firstVMInstruction);
        vmInstructionList.add(secondVMInstruction);
        vmInstructionList.add(thirdVMInstruction);

        if (!isFixIPV4ChecksumAdded) {
            vmInstructionList.add(forthVMInstruction);
            isFixIPV4ChecksumAdded = true;
        }

        if (selections.isUDPSelected()) {
            vmInstructionList.add(fifthVMInstruction);
        }

        return vmInstructionList;
    }

    /**
     *
     * @param name
     * @param type
     * @param packetOffset
     * @param count
     * @param step
     * @return
     */
    public List<Object> getVMInstruction(String name, String type, int packetOffset, String count, String step) {
        ArrayList<Object> vmInstructionList = new ArrayList<>();

        String operation = getOperationFromType(type);
        if ("Fixed".equals(operation)) {
            return vmInstructionList;
        }

        LinkedHashMap<String, Object> firstVMInstruction = new LinkedHashMap<>();

        int size = getCalculatedSize(Util.convertUnitToNum(count));
        // set offset to byte 4 for the ip address
        if (name.contains("ip")) {
            packetOffset += 4 - size;
        }
        /**
         * "init_value": 1, "max_value": 1, "min_value": 1, "name": "mac_src",
         * "op": "inc", "size": 1, "step": 1, "type": "flow_var"
         */
        firstVMInstruction.put("init_value", 0);
        firstVMInstruction.put("min_value", 0);
        firstVMInstruction.put("max_value", Util.convertUnitToNum(count));
        firstVMInstruction.put("name", name);
        firstVMInstruction.put("op", operation);
        firstVMInstruction.put("size", size);
        firstVMInstruction.put("step", Util.getIntFromString(step));
        firstVMInstruction.put("type", "flow_var");

        /**
         * "add_value": 0, "is_big_endian": true, "name": "mac_src",
         * "pkt_offset": 11, "type": "write_flow_var"
         */
        LinkedHashMap<String, Object> secondVMInstruction = new LinkedHashMap<>();
        secondVMInstruction.put("add_value", 0);
        secondVMInstruction.put("is_big_endian", true);
        secondVMInstruction.put("name", name);
        secondVMInstruction.put("pkt_offset", packetOffset);
        secondVMInstruction.put("type", "write_flow_var");

        LinkedHashMap<String, Object> thirdVMInstruction = new LinkedHashMap<>();
        thirdVMInstruction.put("pkt_offset", selections.isTaggedVlanSelected() ? 18 : 14);
        thirdVMInstruction.put("type", "fix_checksum_ipv4");

        vmInstructionList.add(firstVMInstruction);
        vmInstructionList.add(secondVMInstruction);

        if (!isFixIPV4ChecksumAdded && name.contains("ip")) {
            vmInstructionList.add(thirdVMInstruction);
            isFixIPV4ChecksumAdded = true;
        }
        if (!"random".equals(operation)) {
            splitByVar = name;
        }
        if (Util.getIntFromString(count) < 5000 && vmCacheSize == 0) {
            vmCacheSize = 255;
        }
        return vmInstructionList;
    }

    /**
     * Calculate size according to count value return 4 if count greater than
     * 64K, 1 if count less than 256 or 2 if count greater than 256 and less
     * than 4G
     *
     * @param count
     * @return
     */
    private int getCalculatedSize(double count) {
        if (count > 65536) {
            return 4;
        } else if (count <= 256) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Return VM
     *
     * @return
     */
    public Map<String, Object> getVm() {

        // Reset Flag
        isFixIPV4ChecksumAdded = false;
        splitByVar = "";
        vmCacheSize = 0;
        // ipv4/mac selsetion data
        AddressProtocolData ipv4Src = ipv4View.getSourceAddress();
        AddressProtocolData ipv4Dst = ipv4View.getDestinationAddress();
        AddressProtocolData macSrc = macView.getSourceAddress();
        AddressProtocolData macDest = macView.getDestinationAddress();

        ArrayList<Object> instructionsList = new ArrayList<>();
        int offset = 0;
        if (selections.isTaggedVlanSelected()) {
            offset = 4;
        }
        // ADD 4 in case of VLAN
        instructionsList.addAll(getVMInstruction("mac_dest", macDest.getType(), 0, macDest.getCount(), macDest.getStep()));
        instructionsList.addAll(getVMInstruction("mac_src", macSrc.getType(), 6, macSrc.getCount(), macSrc.getStep()));

        instructionsList.addAll(getVMInstruction("ip_dest", ipv4Dst.getType(), 30 + offset, ipv4Dst.getCount(), "1"));
        instructionsList.addAll(getVMInstruction("ip_src", ipv4Src.getType(), 26 + offset, ipv4Src.getCount(), "1"));

        instructionsList.addAll(getPacketLenVMInstruction("pkt_len", selections.getFrameLengthType(), selections.getMinLength(), selections.getMaxLength(), selections.isTaggedVlanSelected()));

        Map<String, Object> additionalProperties = new HashMap<>();

        LinkedHashMap<String, Object> vmBody = new LinkedHashMap<>();

        vmBody.put("split_by_var", splitByVar);
        vmBody.put("instructions", instructionsList);

        if (vmCacheSize > 0) {
            vmBody.put("cache_size", vmCacheSize);
        }

        additionalProperties.put("vm", vmBody);

        return additionalProperties;

    }

    /**
     * Return operation value from type
     */
    private String getOperationFromType(String type) {
        if (type.startsWith("Inc")) {
            return "inc";
        } else if (type.startsWith("Dec")) {
            return "dec";
        } else if (type.startsWith("Rand")) {
            return "random";
        }
        return "Fixed";
    }

    /**
     * Return stream flag value
     *
     * @return
     */
    public int getFlagsValue() {
        if (macView.getSourceAddress().getType().contains("Fixed")) {
            return 1;
        } else if (macView.getDestinationAddress().getType().contains("Fixed")) {
            return 2;
        }
        return 0;
    }

}

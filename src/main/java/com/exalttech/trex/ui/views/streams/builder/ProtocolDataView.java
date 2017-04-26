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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Accordion;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.pcap4j.packet.IpV4Packet.Builder;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import com.xored.javafx.packeteditor.events.UpdateEtherLayerEvent;

import com.exalttech.trex.packets.TrexEthernetPacket;
import com.exalttech.trex.packets.TrexIpV4Packet;
import com.exalttech.trex.packets.TrexVlanPacket;
import com.exalttech.trex.ui.views.models.AddressProtocolData;
import com.exalttech.trex.ui.views.streams.binders.BuilderDataBinding;
import com.exalttech.trex.ui.views.streams.binders.ProtocolSelectionDataBinding;


public class ProtocolDataView extends Accordion {
    private ProtocolSelectionDataBinding selections;
    private MacProtocolView macView;
    private IPV4ProtocolView ipv4View;
    private EthernetProtocolView ethernetView;
    private TCPProtocolView tcpView;
    private UDPProtocolView udpView;
    private PayloadView payloadView;
    private VLanProtocolView vlanView;
    private EventBus eventBus;
    
    public ProtocolDataView() {
        getStyleClass().add("protocolData");
        init();
    }

    public ProtocolDataView(EventBus eventBus) {
        this.eventBus = eventBus;
        getStyleClass().add("protocolData");
        init();
        
    }

    private void init() {
        AnchorPane.setTopAnchor(this, 0d);
        AnchorPane.setLeftAnchor(this, 0d);
        AnchorPane.setRightAnchor(this, 0d);
        setPrefHeight(150);
        eventBus.register(this);
    }

    @Subscribe
    public void handleUpdateEtherLayerEvent(UpdateEtherLayerEvent event) {
        String modeValue;
        if(event.getMode().equals(UpdateEtherLayerEvent.MacMode.PACKET)) {
            modeValue = BuilderDataBinding.MODE_FIXED;
        } else {
            modeValue = BuilderDataBinding.MODE_TREX_CONFIG;
        }
        if (event.getFieldName().equals("src")) {
            macView.getSourceAddress().getTypeProperty().set(modeValue);
        } else {
            macView.getDestinationAddress().getTypeProperty().set(modeValue);
        }
    }
    
    public void doInitializingTabs(BuilderDataBinding selections) {
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
    }

    private void vlanUpdated() {
        vlanView.reset();
        updateTabs();
    }

    private void updateTabs() {
        getPanes().clear();
        getPanes().add(macView);
        if (selections.isTaggedVlanSelected()) {
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

        int packetLength = PacketBuilderHelper.getPacketLength(selections.getFrameLengthType(), Integer.parseInt(selections.getFrameLength()), Integer.parseInt(selections.getMaxLength()));

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

        Builder ipV4Packet = getIPV4Packet(PacketBuilderHelper.getIPV4TotalLength(selections.isTaggedVlanSelected()), packetLength);
        if (selections.isTaggedVlanSelected()) {
            if (!ethernetView.isOverrideType()) {
                ethernetPacket.setType(EtherType.DOT1Q_VLAN_TAGGED_FRAMES.value());
            }
            TrexVlanPacket vlanPacket = vlanView.getVlan();
            /// IF IPV4 is selected
            if (!vlanPacket.isOverrideType()) {
                if (!selections.isIPV4Selected()) {
                    ethernetPacket.setAddPad(true);
                    vlanPacket.setType((short) 0xFFFF);
                } else {
                    vlanPacket.setType(EtherType.IPV4.value());
                }
            }
            vlanPacket.buildPacket(ipV4Packet);
            ethernetPacket.buildPacket(vlanPacket.getBuilder());

        } else {
            ethernetPacket.buildPacket(ipV4Packet);
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

            int ipv4PacketLength = PacketBuilderHelper.getIPV4PacketLength(selections.isTaggedVlanSelected(), packetLength, totalLength);
            int tcpUdpPacketLength = PacketBuilderHelper.getTcpUdpPacketLength(selections.isTaggedVlanSelected(), packetLength, totalLength);

            if (selections.isTCPSelected()) {
                ipV4Packet.setPacketLength(ipv4PacketLength);
                ipV4Packet.buildPacket(tcpView.getTcpPacket(payload, tcpUdpPacketLength).getBuilder(), IpNumber.TCP);
            } else if (selections.isUDPSelected()) {
                ipV4Packet.setPacketLength(ipv4PacketLength);
                ipV4Packet.buildPacket(udpView.getUDPPacket(payload, tcpUdpPacketLength).getBuilder(), IpNumber.UDP);
            } else {
                ipV4Packet.buildPacket(null, IpNumber.getInstance((byte) 0));
            }
            return ipV4Packet.getBuilder();
        }
        return null;
    }

    /**
     * Return VM
     *
     * @param cacheSize
     * @return
     */
    public Map<String, Object> getVm(CacheSize cacheSize) {

        VMInstructionBuilder vmInstructionBuilder = new VMInstructionBuilder(selections.isTaggedVlanSelected(), selections.isUDPSelected());
        vmInstructionBuilder.setCacheSize(cacheSize);
        // ipv4/mac selsetion data
        AddressProtocolData ipv4Src = ipv4View.getSourceAddress();
        AddressProtocolData ipv4Dst = ipv4View.getDestinationAddress();
        AddressProtocolData macSrc = macView.getSourceAddress();
        AddressProtocolData macDest = macView.getDestinationAddress();

        ArrayList<Object> instructionsList = new ArrayList<>();

        // ADD 4 in case of VLAN
        instructionsList.addAll(vmInstructionBuilder.addVmInstruction(VMInstructionBuilder.InstructionType.MAC_DST, macDest.getType(), macDest.getCount(), macDest.getStep(), macDest.getAddress()));
        instructionsList.addAll(vmInstructionBuilder.addVmInstruction(VMInstructionBuilder.InstructionType.MAC_SRC, macSrc.getType(), macSrc.getCount(), macSrc.getStep(), macSrc.getAddress()));
        
        instructionsList.addAll(vmInstructionBuilder.addVmInstruction(VMInstructionBuilder.InstructionType.IP_DST, ipv4Dst.getType(), ipv4Dst.getCount(), ipv4Dst.getStep(), ipv4Dst.getAddress()));
        instructionsList.addAll(vmInstructionBuilder.addVmInstruction(VMInstructionBuilder.InstructionType.IP_SRC, ipv4Src.getType(), ipv4Src.getCount(), ipv4Src.getStep(), ipv4Src.getAddress()));
        // add ipv4 checksum instructions
        instructionsList.addAll(vmInstructionBuilder.getPacketLenVMInstruction("pkt_len", selections.getFrameLengthType(), selections.getMinLength(), selections.getMaxLength(), selections.isTaggedVlanSelected()));
        
        if (needFixIPChksm()) {
            instructionsList.addAll(vmInstructionBuilder.addChecksumInstruction());
        }

        Map<String, Object> additionalProperties = new HashMap<>();

        LinkedHashMap<String, Object> vmBody = new LinkedHashMap<>();

        vmBody.put("split_by_var", vmInstructionBuilder.getSplitByVar());
        vmBody.put("instructions", instructionsList);

        // add cache size
        vmInstructionBuilder.addCacheSize(vmBody);

        additionalProperties.put("vm", vmBody);

        return additionalProperties;

    }

    private boolean needFixIPChksm() {
        return !selections.getFrameLengthType().equalsIgnoreCase("fixed")
               || !ipv4View.getDestinationAddress().getType().equalsIgnoreCase("fixed")
               || !ipv4View.getSourceAddress().getType().equalsIgnoreCase("fixed");
    }
    
    /**
     * Return stream flag value
     *
     * @return
     */
    public int getFlagsValue() {
        int flags = 0;
        if (macView.getSourceAddress().getType().contains("Fixed")) {
            flags |= 1;
        }
        if (macView.getDestinationAddress().getType().contains("Fixed")) {
            flags |= 2;
        }
        return flags;
    }

}

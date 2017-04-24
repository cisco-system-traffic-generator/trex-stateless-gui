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
package com.exalttech.trex.ui.views.streams.binders;

import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.Serializable;
import java.util.*;

/**
 * Stream builder data binding model
 *
 * @author Georgekh
 */
public class BuilderDataBinding implements Serializable {

    public static final String MODE_TREX_CONFIG = "TRex Config";
    public static final String MODE_FIXED = "Fixed";
    ProtocolSelectionDataBinding protocolSelection = new ProtocolSelectionDataBinding();

    EthernetDataBinding ethernetDB = new EthernetDataBinding();

    MacAddressDataBinding macDB = new MacAddressDataBinding();

    IPV4AddressDataBinding ipv4DB = new IPV4AddressDataBinding();

    TCPProtocolDataBinding tcpProtocolDB = new TCPProtocolDataBinding();

    UDPProtocolDataBinding udpProtocolDB = new UDPProtocolDataBinding();

    PayloadDataBinding payloadDB = new PayloadDataBinding();

    List<VlanDataBinding> vlanDB = new ArrayList<>();

    AdvancedPropertiesDataBinding advancedPropertiesDB = new AdvancedPropertiesDataBinding();

    public String serializeAsPacketModel() {

        JsonObject model = new JsonObject();
        model.add("protocols", new JsonArray());
        
        JsonObject fieldEngine = new JsonObject();
        fieldEngine.add("instructions", new JsonArray());
        fieldEngine.add("global_parameters", new JsonObject());
        model.add("field_engine", fieldEngine);
        
        Map<String, AddressDataBinding> l3Binds = new HashMap<>();
        
        l3Binds.put("Ether", macDB);
        boolean isIPv4 = protocolSelection.getIpv4Property().get();
        if (isIPv4) {
            l3Binds.put("IP", ipv4DB);
        }

        l3Binds.entrySet().stream().forEach(entry -> {
            JsonObject proto = new JsonObject();
            String protoID = entry.getKey();
            proto.add("id", new JsonPrimitive(protoID));
            
            JsonArray fields = new JsonArray();
            
            AddressDataBinding binding = entry.getValue();
            String srcMode = entry.getValue().getDestination().getModeProperty().get();
            String dstMode = entry.getValue().getSource().getModeProperty().get();
            
            if(!MODE_TREX_CONFIG.equals(srcMode)) {
                fields.add(buildProtoField("src", binding.getSource().getAddressProperty().getValue()));
            }
            
            if(!MODE_TREX_CONFIG.equals(dstMode)) {
                fields.add(buildProtoField("dst", binding.getDestination().getAddressProperty().getValue()));
            }

            if (protoID.equals("Ether") && ethernetDB.getOverrideProperty().get()) {
                fields.add(buildProtoField("type", ethernetDB.getTypeProperty().getValue()));
            }
            proto.add("fields", fields);
            model.getAsJsonArray("protocols").add(proto);
            if (!MODE_FIXED.equals(binding.getSource().getModeProperty().get())
                && !MODE_TREX_CONFIG.equals(binding.getSource().getModeProperty().get())) {
                fieldEngine.getAsJsonArray("instructions").addAll(buildVMInstructions(protoID, "src", binding.getSource()));
            }
            if (!MODE_FIXED.equals(binding.getDestination().getModeProperty().get())
                && !MODE_TREX_CONFIG.equals(binding.getDestination().getModeProperty().get())) {
                fieldEngine.getAsJsonArray("instructions").addAll(buildVMInstructions(protoID, "dst", binding.getDestination()));
            }
            
        });

        boolean isVLAN = protocolSelection.getTaggedVlanProperty().get();
        String pktLenName = "pkt_len";
        String frameLenghtType = protocolSelection.getFrameLengthType();
        boolean pktSizeChanged = !frameLenghtType.equals("Fixed");
        if (pktSizeChanged) {
            LinkedHashMap<String, String> instructionParam = new LinkedHashMap<>();
            String operation = PacketBuilderHelper.getOperationFromType(frameLenghtType);
            Integer minLength = Integer.valueOf(protocolSelection.getMinLength()) - 4;
            Integer maxLength = Integer.valueOf(protocolSelection.getMaxLength()) - 4 ;

            instructionParam.put("init_value", minLength.toString());
            instructionParam.put("max_value", maxLength.toString());
            instructionParam.put("min_value", minLength.toString());

            instructionParam.put("name", pktLenName);
            instructionParam.put("op", operation);
            instructionParam.put("size", "2");
            instructionParam.put("step", "1");
            fieldEngine.getAsJsonArray("instructions").add(buildInstruction("STLVmFlowVar", instructionParam));

            instructionParam.clear();
            instructionParam.put("fv_name", pktLenName);
            fieldEngine.getAsJsonArray("instructions").add(buildInstruction("STLVmTrimPktSize", instructionParam));
            
            instructionParam.clear();
            instructionParam.put("add_val", isVLAN ? "-18" : "-14");
            instructionParam.put("is_big", "true");
            instructionParam.put("fv_name", pktLenName);
            instructionParam.put("pkt_offset", isVLAN ? "20" : "16");
            fieldEngine.getAsJsonArray("instructions").add(buildInstruction("STLVmWrFlowVar", instructionParam));
        }
        
        if (isVLAN) {
            JsonObject dot1QProto = new JsonObject();
            dot1QProto.add("id", new JsonPrimitive("Dot1Q"));
            Map<String, String> fieldsMap = new HashMap<>();

            fieldsMap.put("prio", vlanDB.get(0).getPriority().getValue());
            fieldsMap.put("id", vlanDB.get(0).getCfi().getValue());
            fieldsMap.put("vlan", vlanDB.get(0).getvID().getValue());
            
            dot1QProto.add("fields", buildProtoFieldsFromMap(fieldsMap));
            
            JsonArray protocols = model.getAsJsonArray("protocols");
            if (protocols.size() == 2) {
                JsonElement ipv4 = protocols.get(1);
                protocols.set(1, dot1QProto);
                protocols.add(ipv4);
            } else {
                model.getAsJsonArray("protocols").add(dot1QProto);
            }
            
            
            if (vlanDB.get(0).getOverrideTPID().getValue()) {
                JsonArray etherFields = ((JsonObject) model.getAsJsonArray("protocols").get(0)).get("fields").getAsJsonArray();
                if (etherFields.size() == 3) {
                    etherFields.remove(2);
                }
                etherFields.add(buildProtoField("type", vlanDB.get(0).getTpid().getValue()));
            }
        }
        
        boolean isTCP = protocolSelection.getTcpProperty().get();
        if (isTCP) {
            JsonObject tcpProto = new JsonObject();
            tcpProto.add("id", new JsonPrimitive("TCP"));
            
            Map<String, String> fieldsMap = new HashMap<>();
            fieldsMap.put("sport", tcpProtocolDB.getSrcPortProperty().getValue());
            fieldsMap.put("dport", tcpProtocolDB.getDstPortProperty().getValue());
            fieldsMap.put("chksum", "0x"+tcpProtocolDB.getChecksumProperty().getValue());
            fieldsMap.put("seq", tcpProtocolDB.getSequenceNumberProperty().getValue());
            fieldsMap.put("urgptr", tcpProtocolDB.getUrgentPointerProperty().getValue());
            fieldsMap.put("ack", tcpProtocolDB.getAckNumberProperty().getValue());

            int tcp_flags = 0;
            if (tcpProtocolDB.getUrgProperty().get()) {
                tcp_flags = tcp_flags | (1 << 5);
            }
            if (tcpProtocolDB.getAckProperty().get()) {
                tcp_flags = tcp_flags | (1 << 4);
            }
            if (tcpProtocolDB.getPshProperty().get()) {
                tcp_flags = tcp_flags | (1 << 3);
            }
            if (tcpProtocolDB.getRstProperty().get()) {
                tcp_flags = tcp_flags | (1 << 2);
            }
            if (tcpProtocolDB.getSynProperty().get()) {
                tcp_flags = tcp_flags | (1 << 1);
            }
            if (tcpProtocolDB.getFinProperty().get()) {
                tcp_flags = tcp_flags | 1;
            }
            fieldsMap.put("flags", String.valueOf(tcp_flags));

            tcpProto.add("fields", buildProtoFieldsFromMap(fieldsMap));
            model.getAsJsonArray("protocols").add(tcpProto);
        }

        // Field Engine instructions
        String cache_size = "5000";
        if("Enable".equals(advancedPropertiesDB.getCacheSizeType().getValue())) {
            cache_size = advancedPropertiesDB.getCacheValue().getValue();
        }
        fieldEngine.getAsJsonObject("global_parameters").add("cache_size", new JsonPrimitive(cache_size));
        
        boolean isUDP = protocolSelection.getUdpProperty().get();
        if (isUDP) {
            JsonObject udpProto = new JsonObject();
            udpProto.add("id", new JsonPrimitive("UDP"));
            
            Map<String, String> fieldsMap = new HashMap<>();
            fieldsMap.put("sport", udpProtocolDB.getSrcPort().getValue());
            fieldsMap.put("dport", udpProtocolDB.getDstPort().getValue());
            fieldsMap.put("len", udpProtocolDB.getLength().getValue());
            fieldsMap.put("chksum", "0x"+udpProtocolDB.getChecksum().getValue());
            
            udpProto.add("fields", buildProtoFieldsFromMap(fieldsMap));
            model.getAsJsonArray("protocols").add(udpProto);
            
            if (pktSizeChanged) {
                LinkedHashMap<String, String> instructionParam = new LinkedHashMap<>();
                instructionParam.put("add_val", isVLAN ? "-38" : "-34");
                instructionParam.put("is_big", "true");
                instructionParam.put("fv_name", pktLenName);
                instructionParam.put("pkt_offset", isVLAN ? "42" : "38");
                fieldEngine.getAsJsonArray("instructions").add(buildInstruction("STLVmWrFlowVar", instructionParam));
            }
        }

        if (ipv4DB.hasInstructions() || pktSizeChanged) {
            Map<String, String> flowWrVarParameters = new HashMap<>();
            flowWrVarParameters.put("offset", "IP");
            fieldEngine.getAsJsonArray("instructions").add(buildInstruction("STLVmFixIpv4", flowWrVarParameters));
        }

        return model.toString();
    }

    private JsonArray buildVMInstructions(String protoId, String fieldId, AddressDataBinding.AddressInfo binding) {
        JsonArray instructions = new JsonArray();

        Map<String, String> flowVarParameters = new HashMap<>();
        String varName = protoId + "_" + fieldId;
        flowVarParameters.put("name", varName);
        
        String initAndMinValue = "1";
        flowVarParameters.put("init_value", initAndMinValue);
        flowVarParameters.put("max_value", binding.getCountProperty().get());
        flowVarParameters.put("min_value", initAndMinValue);
        flowVarParameters.put("step", binding.getStepProperty().get());

        String operation;
        switch (binding.getModeProperty().get()) {
            case "Random Host":
                operation = "random";
                break;
            case "Decrement":
            case "Decrement Host":
                operation = "dec";
                break;
            case "Increment":
            case "Increment Host":
            default:
                operation = "inc";
                break;
        }
        flowVarParameters.put("op", operation);

        instructions.add(buildInstruction("STLVmFlowVar", flowVarParameters));

        Map<String, String> flowWrVarParameters = new HashMap<>();
        flowWrVarParameters.put("fv_name", varName);
        flowWrVarParameters.put("pkt_offset", protoId + "." + fieldId);
        
        instructions.add(buildInstruction("STLVmWrFlowVar", flowWrVarParameters));
        return instructions;
    }

    private JsonElement buildInstruction(String instructionId, Map<String, String> parameters) {
        JsonObject instruction = new JsonObject();
        instruction.add("id", new JsonPrimitive(instructionId));

        JsonObject params = new JsonObject();
        parameters.entrySet().stream().forEach(entry -> {
            params.add(entry.getKey(), new JsonPrimitive(entry.getValue()));
        });
        instruction.add("parameters", params);
        return instruction;
    }

    private JsonArray buildProtoFieldsFromMap(Map<String, String> fieldsMap) {
        JsonArray fields = new JsonArray();
        fieldsMap.entrySet().forEach(entry -> {
            fields.add(buildProtoField(entry.getKey(), entry.getValue()));
        });
        return fields;
    }
    
    private JsonObject buildProtoField(String id, String val) {
        JsonObject field = new JsonObject();
        field.add("id", new JsonPrimitive(id));
        field.add("value", new JsonPrimitive(val));
        return field;
    }
    
    /**
     * Constructor
     */
    public BuilderDataBinding() {
        // constructor
    }

    /**
     * Return Protocol selection data binding model
     *
     * @return
     */
    public ProtocolSelectionDataBinding getProtocolSelection() {
        return protocolSelection;
    }

    /**
     * Set Protocol selection data binding model
     *
     * @param protocolSelection
     */
    public void setProtocolSelection(ProtocolSelectionDataBinding protocolSelection) {
        this.protocolSelection = protocolSelection;
    }

    /**
     * Return Ethernet data binding model
     *
     * @return
     */
    public EthernetDataBinding getEthernetDB() {
        return ethernetDB;
    }

    /**
     * Set Ethernet data binding model
     *
     * @param ethernetDB
     */
    public void setEthernetDB(EthernetDataBinding ethernetDB) {
        this.ethernetDB = ethernetDB;
    }

    /**
     * Return Mac address data binding model
     *
     * @return
     */
    public MacAddressDataBinding getMacDB() {
        return macDB;
    }

    /**
     * Set Mac address data binding model
     *
     * @param macDB
     */
    public void setMacDB(MacAddressDataBinding macDB) {
        this.macDB = macDB;
    }

    /**
     * Return IPV4 data binding model
     *
     * @return
     */
    public IPV4AddressDataBinding getIpv4DB() {
        return ipv4DB;
    }

    /**
     * Set IPV4 data binding model
     *
     * @param ipv4DB
     */
    public void setIpv4DB(IPV4AddressDataBinding ipv4DB) {
        this.ipv4DB = ipv4DB;
    }

    /**
     * Return TCP protocol data binding model
     *
     * @return
     */
    public TCPProtocolDataBinding getTcpProtocolDB() {
        return tcpProtocolDB;
    }

    /**
     * Set TCP protocol data binding model
     *
     * @param tcpProtocolDB
     */
    public void setTcpProtocolDB(TCPProtocolDataBinding tcpProtocolDB) {
        this.tcpProtocolDB = tcpProtocolDB;
    }

    /**
     * Return UDP protocol data binding model
     *
     * @return
     */
    public UDPProtocolDataBinding getUdpProtocolDB() {
        return udpProtocolDB;
    }

    /**
     * Set UDP protocol data binding model
     *
     * @param udpProtocolDB
     */
    public void setUdpProtocolDB(UDPProtocolDataBinding udpProtocolDB) {
        this.udpProtocolDB = udpProtocolDB;
    }

    /**
     * Return payload data binding model
     *
     * @return
     */
    public PayloadDataBinding getPayloadDB() {
        return payloadDB;
    }

    /**
     * Set payload data binding model
     *
     * @param payloadDB
     */
    public void setPayloadDB(PayloadDataBinding payloadDB) {
        this.payloadDB = payloadDB;
    }

    /**
     * Return list of vlan data binding model
     *
     * @return
     */
    public List<VlanDataBinding> getVlanDB() {
        if (vlanDB.isEmpty()) {
            vlanDB.add(new VlanDataBinding());
            vlanDB.add(new VlanDataBinding());
        }
        return vlanDB;
    }

    /**
     * Set list of vlan data binding model
     *
     * @param vlanDB
     */
    public void setVlanDB(List<VlanDataBinding> vlanDB) {
        this.vlanDB = vlanDB;
    }

    /**
     * Return cahce size
     * @return 
     */
    public AdvancedPropertiesDataBinding getAdvancedPropertiesDB() {
        return advancedPropertiesDB;
    }

    /**
     * Set cache size
     * @param advancedPropertiesDB 
     */
    public void setAdvancedPropertiesDB(AdvancedPropertiesDataBinding advancedPropertiesDB) {
        this.advancedPropertiesDB = advancedPropertiesDB;
    }
}

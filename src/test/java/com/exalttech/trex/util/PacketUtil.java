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
package com.exalttech.trex.util;

import com.exalttech.trex.packets.TrexEthernetPacket;
import com.exalttech.trex.packets.TrexVlanPacket;
import com.exalttech.trex.simulator.models.EthernetData;
import com.exalttech.trex.simulator.models.IPV4Data;
import com.exalttech.trex.simulator.models.PacketData;
import com.exalttech.trex.simulator.models.PacketLength;
import com.exalttech.trex.simulator.models.PayloadData;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.models.PacketInfo;
import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.exalttech.trex.ui.views.streams.builder.Payload;
import com.exalttech.trex.ui.views.streams.builder.PayloadType;
import com.exalttech.trex.ui.views.streams.builder.VMInstructionBuilder;
import com.exalttech.trex.ui.views.streams.viewer.PacketParser;
import com.exalttech.trex.util.files.FileManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.EtherType;

/**
 *
 * Utility class
 *
 * @author Georgekh
 */
public class PacketUtil {

    private static final Logger LOG = Logger.getLogger(PacketUtil.class.getName());
    private final TrafficProfile trafficProfile = new TrafficProfile();
    private final String filePath;
    private final String STL_SIM_COMMAND ;
    
    public PacketUtil(){
        filePath = FileManager.getProfilesFilePath();
        STL_SIM_COMMAND = "./stl-sim -f "+filePath+"[FILE_NAME].yaml -o "+filePath+"generated_[FILE_NAME].pcap -l 10 ";
    }
    
    /**
     * @param packetRawData
     * @return encoded data
     */
    public String getEncodedPacket(byte[] packetRawData) {
        String hexDataString = PacketBuilderHelper.getPacketHex(packetRawData);
        return trafficProfile.encodeBinaryFromHexString(hexDataString);
    }

    /**
     * Decode string and return packet info data
     *
     * @param encodedBinaryPacket
     * @return packet info
     * @throws IOException
     */
    public PacketInfo getPacketInfoData(String encodedBinaryPacket) throws IOException {
        // decode binary data
        File pcapFile = trafficProfile.decodePcapBinary(encodedBinaryPacket);
        PacketInfo packetInfo = new PacketInfo();
        PacketParser parser = new PacketParser(pcapFile.getAbsolutePath(), packetInfo);
        return packetInfo;
    }

    /**
     * @param packet
     * @return packet length
     * @throws org.pcap4j.packet.IllegalRawDataException
     */
    public int getPacketLength(Packet packet) throws IllegalRawDataException {
        return trafficProfile.getPacketTypeText(packet).getLength();
    }

    /**
     * Get packet from encoded string
     *
     * @param encodedBinaryPacket
     * @return packet
     * @throws IllegalRawDataException
     */
    public Packet getPacketFromEncodedString(String encodedBinaryPacket) throws IllegalRawDataException {
        byte[] pkt = Base64.decodeBase64(encodedBinaryPacket);
        return EthernetPacket.newPacket(pkt, 0, pkt.length);
    }

    /**
     * Read file as String
     *
     * @param fileName
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public String getFileContent(String fileName) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getResource("/files/" + fileName).toURI())));
    }

    /**
     * Run stl-sim command to generates pcap file
     *
     * @param fileName
     * @throws IOException
     * @throws InterruptedException
     */
    public void generatePcapFile(String fileName) throws IOException, InterruptedException {
        String line;
        String command = STL_SIM_COMMAND.replace("[FILE_NAME]", fileName);
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = bufferReader.readLine()) != null) {
            LOG.info("line: " + line);
        }
        process.waitFor();
        process.destroy();
    }

    /**
     * Compare 2 pcap files, return true if equal, otherwise return false
     *
     * @param firstPCAP
     * @param secondPCAP
     * @return
     * @throws PcapNativeException
     * @throws NotOpenException
     */
    public boolean comparePcaps(String firstPCAP, String secondPCAP) throws PcapNativeException, NotOpenException, URISyntaxException {
        LOG.info("Comparing pcap files");
        List<String> firstPcapPacketList = getpcapPacketList(getClass().getResource("/pcaps/" + firstPCAP + ".pcap").toURI().getPath());
        List<String> secondPcapPacketList = getpcapPacketList(filePath + secondPCAP + ".pcap");
        if (firstPcapPacketList.size() != secondPcapPacketList.size()) {
            return false;
        }
        boolean equal = true;
        for (int index = 0; index < firstPcapPacketList.size(); index++) {
            equal = equal && (firstPcapPacketList.get(index).equals(secondPcapPacketList.get(index)));
        }
        return equal;
    }

    /**
     * Read pcap file to get all includes packet
     *
     * @param pcapFile
     * @return
     * @throws PcapNativeException
     * @throws NotOpenException
     */
    private List<String> getpcapPacketList(String pcapFile) throws PcapNativeException, NotOpenException {
        PcapHandle handler = Pcaps.openOffline(pcapFile);
        Packet packet = null;
        List<String> packetList = new ArrayList<>();
        while ((packet = handler.getNextPacket()) != null) {
            packetList.add(Hex.encodeHexString(packet.getRawData()));
        }
        return packetList;
    }

    /**
     * Create vm instruction
     *
     * @param packetData
     * @return vm instruction
     */
    public Map<String, Object> getVm(PacketData packetData) {
        VMInstructionBuilder vmInstructionBuilder = new VMInstructionBuilder(packetData.isTaggedVlan(), packetData.getUdpData().isEnable());
        ArrayList<Object> instructionsList = new ArrayList<>();

        // add VM instructions
        EthernetData ethernetData = packetData.getEthernetData();
        instructionsList.addAll(vmInstructionBuilder.addVmInstruction(VMInstructionBuilder.InstructionType.MAC_DST, ethernetData.getDstAddress().getType(), ethernetData.getDstAddress().getCount(), ethernetData.getDstAddress().getStep(), ethernetData.getDstAddress().getAddress()));
        instructionsList.addAll(vmInstructionBuilder.addVmInstruction(VMInstructionBuilder.InstructionType.MAC_SRC, ethernetData.getSrcAddress().getType(), ethernetData.getSrcAddress().getCount(), ethernetData.getSrcAddress().getStep(), ethernetData.getSrcAddress().getAddress()));
        
        IPV4Data ipv4Data = packetData.getIpv4Data();
        instructionsList.addAll(vmInstructionBuilder.addVmInstruction(VMInstructionBuilder.InstructionType.IP_DST, ipv4Data.getDstAddress().getType(), ipv4Data.getDstAddress().getCount(), "1", ipv4Data.getDstAddress().getAddress()));
        instructionsList.addAll(vmInstructionBuilder.addVmInstruction(VMInstructionBuilder.InstructionType.IP_SRC, ipv4Data.getSrcAddress().getType(), ipv4Data.getSrcAddress().getCount(), "1", ipv4Data.getSrcAddress().getAddress()));
        // add ipv4 checksum instructions
        instructionsList.addAll(vmInstructionBuilder.addChecksumInstruction());
        // add packet length instruction
        PacketLength packetLength = packetData.getPacketLength();
        instructionsList.addAll(vmInstructionBuilder.getPacketLenVMInstruction("pkt_len", packetLength.getLengthType(), String.valueOf(packetLength.getMinLength()), String.valueOf(packetLength.getMaxLength()), packetData.isTaggedVlan()));

        Map<String, Object> additionalProperties = new HashMap<>();

        LinkedHashMap<String, Object> vmBody = new LinkedHashMap<>();
        vmBody.put("split_by_var", vmInstructionBuilder.getSplitByVar());
        vmBody.put("instructions", instructionsList);

        // add cache size
        vmInstructionBuilder.addCacheSize(vmBody);

        additionalProperties.put("vm", vmBody);

        return additionalProperties;
    }

    /**
     * Parse data file to extract test data info
     *
     * @param fileName
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public Object[][] parsePacketDataFile(String fileName) throws IOException, URISyntaxException {
        String fileData = getFileContent(fileName);
        List<PacketData> data = new ObjectMapper().readValue(fileData, new TypeReference<List<PacketData>>() {
        });
        Object[][] dataList = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            dataList[i][0] = data.get(i);
        }
        return dataList;
    }

    /**
     * Create payload
     *
     * @param payloadData
     * @return
     */
    public Payload getPayload(PayloadData payloadData) {
        Payload payload = new Payload();
        payload.setPayloadPattern(payloadData.getPattern());
        payload.setPayloadType(PayloadType.getPayloadType(payloadData.getType()));
        return payload;
    }

    /**
     * Prepare and save yaml file
     *
     * @param packetRawData
     * @param packetData
     * @throws JsonProcessingException
     * @throws IOException
     */
    public void prepareAndSaveYamlFile(byte[] packetRawData, PacketData packetData) throws JsonProcessingException, IOException {

        String encodedBinaryPacket = getEncodedPacket(packetRawData);
        // save pqacket in the profile
        Profile profile = new Profile();
        profile.setName("stream0");
        profile.getStream().getMode().setType("continuous");
        profile.getStream().getPacket().setBinary(encodedBinaryPacket);
        profile.getStream().setAdditionalProperties(getVm(packetData));

        // save data to yaml file
        List<Profile> profileList = new ArrayList();
        profileList.add(profile);
        String yamlData = trafficProfile.getProfileYamlContent(profileList.toArray(new Profile[profileList.size()]));

        File newFile = FileManager.createNewFile(packetData.getTestFileName() + ".yaml");
        FileUtils.writeStringToFile(newFile, yamlData);
    }

    /**
     * Prepare Ethernet packet
     *
     * @param packetData
     * @param packetLength
     * @param payload
     * @return
     */
    public TrexEthernetPacket prepareEthernetPacket(PacketData packetData, int packetLength, Payload payload) {
        TrexEthernetPacket ethernetPacket = new TrexEthernetPacket();
        ethernetPacket.setSrcAddr(packetData.getEthernetData().getSrcAddress().getAddress());
        ethernetPacket.setDstAddr(packetData.getEthernetData().getDstAddress().getAddress());

        ethernetPacket.setLength(packetLength);

        ethernetPacket.setPayload(payload);

        return ethernetPacket;
    }

    /**
     * Add VLan to the generated packet
     *
     * @param ethernetPacket
     * @param ipV4Packet
     */
    public void addVlanToPacket(TrexEthernetPacket ethernetPacket, IpV4Packet.Builder ipV4Packet) {
        LOG.info("Add VLAN data");
        ethernetPacket.setType(EtherType.DOT1Q_VLAN_TAGGED_FRAMES.value());
        TrexVlanPacket vlanPacket = new TrexVlanPacket();
        if (ipV4Packet == null) {
            ethernetPacket.setAddPad(true);
            vlanPacket.setType((short) 0xFFFF);
        } else {
            vlanPacket.setType(EtherType.IPV4.value());
        }
        vlanPacket.buildPacket(ipV4Packet);
        ethernetPacket.buildPacket(vlanPacket.getBuilder());
    }
}

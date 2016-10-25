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

import com.exalttech.trex.remote.models.profiles.Mode;
import com.exalttech.trex.remote.models.profiles.PacketInfo;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.views.models.TableProfileStream;
import com.exalttech.trex.util.files.FileManager;
import com.exalttech.trex.util.files.FileType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.stage.Window;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.*;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author GeorgeKh
 */
public class TrafficProfile {

    private static final Logger LOG = Logger.getLogger(TrafficProfile.class.getName());

    /**
     *
     */
    public TrafficProfile() {
        super();

    }

    /**
     *
     * @param packet
     * @return
     */
    public PacketInfo getPacketTypeText(Packet packet) {
        PacketInfo packetInfo = new PacketInfo();

        String packetType = "";
        // Default values for packet info
        packetInfo.setType("Unknown");
        packetInfo.setLength(packet.length());
        if (packet != null) {
            // EthernetPacket
            if (packet.get(EthernetPacket.class) != null) {
                packetType += "Ethernet/";
            }
            // IPPacket
            if (packet.get(IpV4Packet.class) != null) {
                packetType += "IPV4/";
            }
            // TCPPacket
            if (packet.get(TcpPacket.class) != null) {
                packetType += "TCP/";
            }
            // UDPPacket
            if (packet.get(UdpPacket.class) != null) {
                packetType += "UDP";
            }
            if (packetType.endsWith("/")) {
                packetType = packetType.substring(0, packetType.length() - 1);
            }
            packetInfo.setType(packetType);
        }
        return packetInfo;
    }

    /**
     *
     * @param packetBinary
     * @return
     */
    public PacketInfo getPacketInfo(String packetBinary) {

        PacketInfo packetInfo = null;

        try {
            byte[] pkt = Base64.decodeBase64(packetBinary);
            Packet packet = EthernetPacket.newPacket(pkt, 0, pkt.length);
            packetInfo = getPacketTypeText(packet);
        } catch (IllegalRawDataException ex) {
            LOG.error("Error reading packet info", ex);
        }

        return packetInfo;

    }

    /**
     * @param binaryFile
     * @return Encodes the bytes array of a PCAP File using Base64
     */
    public String encodePcapFile(String binaryFile) {

        try {
            PcapHandle handle = Pcaps.openOffline(binaryFile);
            Packet packet = handle.getNextPacketEx();
            handle.close();
            byte[] pkt = packet.getRawData();
            byte[] bytesEncoded = Base64.encodeBase64(pkt);

            return new String(bytesEncoded);
        } catch (IOException | PcapNativeException | TimeoutException | NotOpenException ex) {
            LOG.error("Error encoding pcap file", ex);
            return binaryFile;
        }

    }

    /**
     * @param binaryPacket
     * @throws java.io.IOException
     * @
     * @return decodes the bytes array of a PCAP File using Base64
     */
    public File decodePcapBinary(String binaryPacket) throws IOException {

        byte[] decodedBytes = Base64.decodeBase64(binaryPacket);
        File pcapFile = File.createTempFile("temp-file-name", ".pcap");
        FileUtils.writeByteArrayToFile(pcapFile, decodedBytes);
        return pcapFile;
    }

    /**
     *
     * @param hexPacket
     * @return Converts a Hex packet to a base64 encoded packet.
     */
    public String encodeBinaryFromHexString(String hexPacket) {
        if (!Util.isNullOrEmpty(hexPacket)) {
            byte[] rawData = DatatypeConverter.parseHexBinary(hexPacket);
            return new String(Base64.encodeBase64(rawData));
        }
        return null;
    }

    /**
     * @param yamlFile File containing the Traffic Profile Yaml.
     * @return parsed Yaml file
     * @throws java.io.IOException
     */
    public Profile[] getTrafficProfile(File yamlFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Profile[] trafficProfileArray = mapper.readValue(yamlFile, Profile[].class);
        int i = 0;
        for (Profile profile : trafficProfileArray) {
            Map<String, Object> streamAdditionalProperties = profile.getStream().getAdditionalProperties();
            if (streamAdditionalProperties.containsKey("vm")) {
                profile.getStream().setVmRaw(streamAdditionalProperties.get("vm").toString());
            }
            if (streamAdditionalProperties.containsKey("rx_stats")) {
                profile.getStream().setRxStatsRaw(streamAdditionalProperties.get("rx_stats").toString());
            }
            // Check the Binary is in the Yaml File
            if (profile.getStream().getPacket().getBinary() == null) {
                String absolutePath = yamlFile.getAbsolutePath();
                String filePath = absolutePath.
                        substring(0, absolutePath.lastIndexOf(File.separator));
                String pacpFile = profile.getStream().getPacket().getPcap();
                String encodedPcap = encodePcapFile(filePath + File.separator + pacpFile);
                profile.getStream().getPacket().setBinary(encodedPcap);
            }
            profile.getStream().getPacket().setPcap(null);

            if (profile.getName() == null) {
                profile.setName("Stream" + i++);
            }

        }
        return trafficProfileArray;
    }

    /**
     * Convert profiles to equivalent tableProfile data
     *
     * @param profilesList
     * @return
     */
    public List<TableProfileStream> convertProfilesToTableData(Profile[] profilesList) {
        List<TableProfileStream> tableData = new ArrayList<>();
        for (int index = 0; index < profilesList.length; index++) {
            Profile p = profilesList[index];
            TableProfileStream stream = new TableProfileStream();
            Mode modeYaml = p.getStream().getMode();
            stream.setIndex(String.valueOf(index + 1));
            stream.setEnabled(p.getStream().isEnabled());
            stream.setName(p.getName());
            stream.setMode(modeYaml.getType());
            stream.setRate(String.valueOf(modeYaml.getPps()));
            stream.setNextStream(getNextStreamValue(p.getNext()));
            String packetBinary = p.getStream().getPacket().getBinary();
            String packetModel = p.getStream().getPacket().getModel();
            stream.setPcapBinary(packetBinary);
            stream.setPktModel(packetModel);
            PacketInfo packetInfo = getPacketInfo(packetBinary);
            stream.setLength(String.valueOf(packetInfo.getLength() + Constants.EXTRA_BYTE));
            stream.setPacketType(packetInfo.getType());
            tableData.add(stream);
        }

        return tableData;
    }

    /**
     * @param trafficProfileArray
     * @param portID
     * @param handler
     * @return returns a formatted JSON Array of Streams for a given port
     * @throws Exception
     *
     */
    public String convertTrafficProfileToJson(Profile[] trafficProfileArray, int portID, String handler) throws Exception {
        Profile[] preparedTrafficProfile = prepareTrafficProfile(trafficProfileArray, portID, handler);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(preparedTrafficProfile);
        return Util.toPrettyFormat(jsonString);

    }

    /**
     * @param trafficProfileArray
     *
     * @return Converts Traffic Profile to Yaml String
     * @throws JsonProcessingException
     *
     */
    public String convertTrafficProfileToYaml(Profile[] trafficProfileArray) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.writeValueAsString(trafficProfileArray);
    }

    /**
     * @param trafficProfileArray
     * @param fileName
     *
     * @return Converts Traffic Profile to Yaml String
     * @throws JsonProcessingException
     *
     */
    public File convertTrafficProfileToYamlFile(Profile[] trafficProfileArray, String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String localFileName = FileManager.getProfilesFilePath() + fileName;
        File yamlFile = new File(localFileName);
        mapper.writeValue(yamlFile, trafficProfileArray);
        return yamlFile;
    }

    private String getNextStreamValue(String nextStream) {

        if (nextStream == null || "-1".equals(nextStream)) {
            return "None";
        }
        return nextStream;
    }

    /**
     *
     * @param trafficProfileArray
     * @param portID
     * @param handler
     * @return
     * @throws java.lang.Exception
     */
    public Profile[] prepareTrafficProfile(Profile[] trafficProfileArray, int portID, String handler) throws Exception {
        Map<String, Integer> mapStreamToInteger = convertStreamNameToInteger(trafficProfileArray);

        ObjectMapper mapper = new ObjectMapper();
        String profileJsonString = mapper.writeValueAsString(trafficProfileArray);
        Profile[] updatedProfileArray = mapper.readValue(profileJsonString, Profile[].class);
        for (int i = 0; i < updatedProfileArray.length; i++) {

            Profile trafficProfile = updatedProfileArray[i];
            Profile originalProfile = trafficProfileArray[i];
            trafficProfile.setStreamId(mapStreamToInteger.get(trafficProfile.getName()));
            if (!"-1".equals(trafficProfile.getNext())) {
                trafficProfile.getStream().setNextStreamId(mapStreamToInteger.get(trafficProfile.getNext()));
            }

            trafficProfile.setName(null);
            trafficProfile.setNext(null);
            trafficProfile.getStream().getPacket().setPcap(null);
            String vm = (!originalProfile.getStream().getVmRaw().isEmpty() && !"[]".equals(originalProfile.getStream().getVmRaw())) ? originalProfile.getStream().getVmRaw() : "{\n"
                    + "                    \"instructions\": [],\n"
                    + "                    \"split_by_var\": \"\"\n"
                    + "                }";
            String rx = (!originalProfile.getStream().getRxStatsRaw().isEmpty() && !"[]".equals(originalProfile.getStream().getRxStatsRaw())) ? originalProfile.getStream().getRxStatsRaw() : "{\n"
                    + "                    \"enabled\": false\n"
                    + "                }";
            trafficProfile.getStream().setVmRaw(vm);
            trafficProfile.getStream().setRxStatsRaw(rx);

            trafficProfile.setHandler(handler);
            trafficProfile.setPortId(portID);
            trafficProfile.getStream().getMode().getRate().setValue((int) trafficProfile.getStream().getMode().getPps());
            updatedProfileArray[i] = trafficProfile;

        }
        return updatedProfileArray;
    }

    /**
     * Convert stream name to integer
     *
     * @param trafficProfileArray
     * @return
     */
    public Map<String, Integer> convertStreamNameToInteger(Profile[] trafficProfileArray) {
        HashMap<String, Integer> streamNameMap = new HashMap<>();
        int streamID = 0;
        for (Profile profile : trafficProfileArray) {
            if (!streamNameMap.containsKey(profile.getName())) {
                streamNameMap.put(profile.getName(), streamID++);
            }
        }
        return streamNameMap;
    }

    /**
     *
     * @param owner
     * @param profiles
     * @param fileName
     */
    public void exportProfileToYaml(Window owner, Profile[] profiles, String fileName) {
        try {
            String data = getProfileYamlContent(profiles);
            FileManager.exportFile("Save Yaml File", fileName, data, owner, FileType.YAML);
        } catch (IOException ex) {
            LOG.error("Error during generate YAML file", ex);
        }
    }

    /**
     * Return cleaned profile yaml content
     * @param profiles
     * @return
     * @throws JsonProcessingException 
     */
    public String getProfileYamlContent(Profile[] profiles) throws JsonProcessingException {
        String data = convertTrafficProfileToYaml(profiles);

        // Clean up Yaml file
        data = data.replace("next: \"-1\"", "");
        data = data.replace("next_stream_id: -1", "");
        return data;
    }
}

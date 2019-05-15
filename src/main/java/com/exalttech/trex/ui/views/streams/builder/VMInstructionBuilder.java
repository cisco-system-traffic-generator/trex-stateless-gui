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
package com.exalttech.trex.ui.views.streams.builder;

import com.exalttech.trex.util.Util;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * VM instruction builder
 *
 * @author Georgekh
 */
public class VMInstructionBuilder {
    public enum ChecksumFixHwCs {
        L4_TYPE_UDP(11),
        L4_TYPE_TCP(13),
        L4_TYPE_IP(17);

        private int code;
        ChecksumFixHwCs(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }
    boolean isAddFixIPV4Checksum = false;
    String splitByVar = "";
    int vmCacheSize = 0;
    boolean isTaggedVlan = false;
    boolean isUDPSelected = false;
    int offset = 0;
    long convertedAddress = 0;
    CacheSize cacheSize;

    /**
     *
     * @param isTaggedVlan
     * @param isUDPSelected
     */
    public VMInstructionBuilder(boolean isTaggedVlan, boolean isUDPSelected) {
        this.isTaggedVlan = isTaggedVlan;
        this.isUDPSelected = isUDPSelected;
        this.offset = getOffset(isTaggedVlan);
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
    private List<Object> getVMInstruction(String name, String type, int packetOffset, String count, String step, String address) {
        ArrayList<Object> vmInstructionList = new ArrayList<>();

        String operation = PacketBuilderHelper.getOperationFromType(type);
        if ("Fixed".equals(operation)) {
            return vmInstructionList;
        }

        LinkedHashMap<String, Object> firstVMInstruction = new LinkedHashMap<>();

        int size = getCalculatedSize(Util.convertUnitToNum(count), address);

        long initValue = this.convertedAddress;
        long convertedCount = (long) Util.convertUnitToNum(count);
        long minValue = initValue;
        long maxValue = initValue + convertedCount - 1;

        // set offset to byte 4 for the ip address
        if (name.contains("ip")) {
            packetOffset += 4 - size;
        } else {
            packetOffset += 6 - size;
        }

        /**
         * "init_value": 1, "max_value": 1, "min_value": 1, "name": "mac_src",
         * "op": "inc", "size": 1, "step": 1, "type": "flow_var"
         */
        firstVMInstruction.put("init_value", initValue);
        firstVMInstruction.put("min_value", minValue);
        firstVMInstruction.put("max_value", maxValue);
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

        vmInstructionList.add(firstVMInstruction);
        vmInstructionList.add(secondVMInstruction);

        if (name.contains("ip")) {
            isAddFixIPV4Checksum = true;
        }

        if (!"random".equals(operation)) {
            splitByVar = name;
        }

        updateVMCacheSize(count);

        return vmInstructionList;
    }

    /**
     * Update vm cache size calculated value
     *
     * @param count
     */
    private void updateVMCacheSize(String count) {
        long size = (long)Util.convertUnitToNum(count);
        CacheSize.CacheSizeType type = getCacheSize().getType();
        if (type != CacheSize.CacheSizeType.DISABLE && size < getCacheSize().getCacheValue() && vmCacheSize == 0) {
            vmCacheSize = 255;
        }
    }

    /**
     * Add checksum instructions
     *
     * @return
     */
    public List<Object> addChecksumInstruction() {
        ArrayList<Object> vmInstructionList = new ArrayList<>();
        LinkedHashMap<String, Object> checksumInstruction = new LinkedHashMap<>();
        int l3Offset = isTaggedVlan ? 18 : 14;
        int l4Offset = l3Offset + 20;
        checksumInstruction.put("type", "fix_checksum_hw");
        checksumInstruction.put("l2_len", l3Offset);
        checksumInstruction.put("l3_len", l4Offset);
        checksumInstruction.put("l4_type", ChecksumFixHwCs.L4_TYPE_IP.getCode());
        vmInstructionList.add(checksumInstruction);

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
    private int getCalculatedSize(double count, String address) {

        // mac address 
        if (address.contains(":")) {
            return calculateSizeFromMacAddress(address, count);
        }
        return calculateSizeFromIPAddress(address, count);
    }

    /**
     * calculate size from mac address
     *
     * @param address
     * @param count
     * @return
     */
    private int calculateSizeFromMacAddress(String address, double count) {
        convertedAddress = Long.parseLong(address.substring(address.length() - 2, address.length()), 16);
        if (convertedAddress + count < 256) {
            return 1;
        } else {
            String last2Byte = address.substring(address.length() - 5, address.length()).replaceAll(":", "");
            convertedAddress = Long.parseLong(last2Byte, 16);
            if (convertedAddress + count < 65536) {
                return 2;
            }
        }
        String last4Byte = address.substring(7, address.length()).replaceAll(":", "");
        convertedAddress = Long.parseLong(last4Byte, 16);
        return 4;
    }

    /**
     * Calculate size from IP address
     *
     * @param address
     * @param count
     * @return
     */
    private int calculateSizeFromIPAddress(String address, double count) {

        String[] ipBytesStrings = address.split("\\.");
        convertedAddress = Long.parseLong(ipBytesStrings[3]);
        if (convertedAddress + count < 256) {
            return 1;
        } else {
            String newAddress = "0.0." + ipBytesStrings[2] + "." + ipBytesStrings[3];
            convertedAddress = convertIPToInt(newAddress);
            if (convertedAddress + count < 65536) {
                return 2;
            }
        }
        convertedAddress = convertIPToInt(address);
        return 4;
    }

    /**
     *
     * @return
     */
    public String getSplitByVar() {
        return splitByVar;
    }

    /**
     *
     * @return
     */
    public int getVmCacheSize() {
        return vmCacheSize;
    }

    /**
     *
     * @param name
     * @param type
     * @param minLength
     * @param maxLength
     * @param taggedVlanSelected
     * @return
     */
    public List<Object> getPacketLenVMInstruction(String name, String type, String minLength, String maxLength, boolean taggedVlanSelected) {
        ArrayList<Object> vmInstructionList = new ArrayList<>();
        String operation = PacketBuilderHelper.getOperationFromType(type);
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

        LinkedHashMap<String, Object> fifthVMInstruction = new LinkedHashMap<>();
        fifthVMInstruction.put("add_value", taggedVlanSelected ? -38 : -34);
        fifthVMInstruction.put("is_big_endian", true);
        fifthVMInstruction.put("name", name);
        fifthVMInstruction.put("pkt_offset", taggedVlanSelected ? 42 : 38);
        fifthVMInstruction.put("type", "write_flow_var");

        vmInstructionList.add(firstVMInstruction);
        vmInstructionList.add(secondVMInstruction);
        vmInstructionList.add(thirdVMInstruction);

        if (isUDPSelected) {
            vmInstructionList.add(fifthVMInstruction);
        }
        return vmInstructionList;
    }

    /**
     * Return offset
     *
     * @param isTaggedVlan
     * @return
     */
    private int getOffset(boolean isTaggedVlan) {
        if (isTaggedVlan) {
            return 4;
        }
        return 0;
    }

    /**
     * Add cache size
     *
     * @param vmBody
     */
    public void addCacheSize(LinkedHashMap<String, Object> vmBody) {
        if (getVmCacheSize() > 0) {
            vmBody.put("cache_size", getVmCacheSize());
        }
    }

    /**
     * add Vm instruction
     *
     * @param instructionType
     * @param type
     * @param count
     * @param step
     * @param address
     * @return
     */
    public List<Object> addVmInstruction(InstructionType instructionType, String type, String count, String step, String address) {
        return getVMInstruction(instructionType.getType(), type, instructionType.getOffset() + this.offset, count, step, address);
    }

    /**
     * Convert IP to the equivalent integer value
     *
     * @param ipAddress
     * @return
     */
    public long convertIPToInt(String ipAddress) {
        String[] addrArray = ipAddress.split("\\.");
        long convertedIPValue = 0;
        for (int i = 0; i < addrArray.length; i++) {
            int power = 3 - i;
            convertedIPValue += Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power);
        }
        return convertedIPValue;
    }

    public long convertMacAddressToDecimal(String macAddress) {
        return Long.parseLong(macAddress.replaceAll(":", ""), 16);
    }

    /**
     * Set cache size
     * @param cacheSize 
     */
    public void setCacheSize(CacheSize cacheSize) {
        this.cacheSize = cacheSize;
        vmCacheSize = cacheSize.getCacheValue();
    }

    /**
     * Get cache size
     * @return 
     */
    public CacheSize getCacheSize() {
        if(cacheSize == null){
            // initialize it to auto
            this.cacheSize = new CacheSize();
        }
        return cacheSize;
    }

    /**
     * Enumerator present
     */
    public enum InstructionType {
        MAC_DST("mac_dest", 0),
        MAC_SRC("mac_src", 6),
        IP_DST("ip_dest", 30),
        IP_SRC("ip_src", 26);

        String type;
        int offset;

        private InstructionType(String type, int offset) {
            this.type = type;
            this.offset = offset;
        }

        /**
         *
         * @return
         */
        public String getType() {
            return type;
        }

        /**
         *
         * @return
         */
        public int getOffset() {
            return offset;
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.importPcap;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

/**
 * Packet source destination updater
 *
 * @author GeorgeKH
 */
public class PacketUpdater {

    private static final Logger LOG = Logger.getLogger(PacketUpdater.class.getName());
    private static PacketUpdater instance = null;
    private String defaultSrcAddress = null;
    private String defaultDstAddress = null;
    ImportedPacketProperties importedProperties;
    String defaultSrcAddressPort;
    String defaultDstAddressPort;
    boolean validPacket = true;
    String protocolType;

    String currentSrcAddressPort;
    String currentDstAddressPort;

    private PacketUpdater() {

    }

    /**
     * Return instance of packet updater
     *
     * @return
     */
    public static PacketUpdater getInstance() {
        if (instance == null) {
            instance = new PacketUpdater();
        }

        return instance;
    }

    /**
     * Set imported packet properties
     *
     * @param importedProperties
     */
    public void setImportedProperties(ImportedPacketProperties importedProperties) {
        this.importedProperties = importedProperties;
    }

    /**
     * Update packet source/destination address
     *
     * @param packet
     * @return
     */
    public Packet updatePacketSrcDst(Packet packet) {
        // initialze default src/dst address and protocol type
        if (packet.get(IpV4Packet.class) != null) {
            if (defaultSrcAddress == null && defaultDstAddress == null) {
                initializeUpdater(packet);
            }
            packet = updateSrcAddress(packet);
            packet = updateDstAddress(packet);
        }

        return packet;
    }

    /**
     * Initialize default source/destination address according to first packet
     *
     * @param firstPacket
     */
    private void initializeUpdater(Packet firstPacket) {
        defaultSrcAddress = firstPacket.get(IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();
        defaultDstAddress = firstPacket.get(IpV4Packet.class).getHeader().getDstAddr().getHostAddress();
        protocolType = firstPacket.get(IpV4Packet.class).getHeader().getProtocol().name();
        defaultSrcAddressPort = defaultSrcAddress + ":" + getSrcPort(firstPacket);
        defaultDstAddressPort = defaultDstAddress + ":" + getDstPort(firstPacket);
    }

    /**
     * Validate packet
     *
     * @param packet
     * @return
     */
    public boolean validatePacket(Packet packet) {
        validPacket = true;
        if(packet.get(IpV4Packet.class) == null){
            validPacket = false;
        }else if (defaultSrcAddress == null) {
            return true;
        } else if (!packet.get(IpV4Packet.class).getHeader().getProtocol().name().equals(protocolType)) {
            validPacket = false;
        } else {
            // check src/dst port combination
            currentSrcAddressPort = packet.get(IpV4Packet.class).getHeader().getSrcAddr().getHostAddress() + ":" + getSrcPort(packet);
            currentDstAddressPort = packet.get(IpV4Packet.class).getHeader().getDstAddr().getHostAddress() + ":" + getDstPort(packet);
            if ((!currentSrcAddressPort.equals(defaultSrcAddressPort) && !currentSrcAddressPort.equals(defaultDstAddressPort))
                    || (!currentDstAddressPort.equals(defaultSrcAddressPort) && !currentDstAddressPort.equals(defaultDstAddressPort))) {
                validPacket = false;
            }
        }
        return validPacket;
    }

    /**
     * Return source port
     *
     * @param packet
     * @return
     */
    private String getSrcPort(Packet packet) {
        if (protocolType.equals("TCP")) {
            return packet.get(TcpPacket.class).getHeader().getSrcPort().toString();

        } else if (protocolType.equals("UDP")) {
            return packet.get(UdpPacket.class).getHeader().getSrcPort().toString();
        }

        return "80";
    }

    /**
     * Return destination port
     *
     * @param packet
     * @return
     */
    private String getDstPort(Packet packet) {
        if (protocolType.equals("TCP")) {
            return packet.get(TcpPacket.class).getHeader().getDstPort().toString();

        } else if (protocolType.equals("UDP")) {
            return packet.get(UdpPacket.class).getHeader().getDstPort().toString();
        }

        return "80";
    }

    /**
     * Update source IP address
     */
    private Packet updateSrcAddress(Packet packet) {
        try {
            if (importedProperties.isSourceEnabled()) {
                Inet4Address modifiedAddress = (Inet4Address) Inet4Address.getByAddress(convertIPToByte(importedProperties.getSrcAddress()));
                Packet.Builder builder = packet.getBuilder();
                if (packet.get(IpV4Packet.class).getHeader().getSrcAddr().getHostAddress().equals(defaultSrcAddress)) {
                    builder.get(IpV4Packet.Builder.class).srcAddr(modifiedAddress);
                } else {
                    builder.get(IpV4Packet.Builder.class).dstAddr(modifiedAddress);
                }
                packet = builder.build();
            }

        } catch (Exception ex) {
            LOG.error("Error updating source IP", ex);
        }
        return packet;
    }

    /**
     * Update destination IP address
     */
    private Packet updateDstAddress(Packet packet) {
        try {
            if (importedProperties.isDestinationEnabled()) {
                Inet4Address modifiedAddress = (Inet4Address) Inet4Address.getByAddress(convertIPToByte(importedProperties.getDstAddress()));
                Packet.Builder builder = packet.getBuilder();
                if (packet.get(IpV4Packet.class).getHeader().getDstAddr().getHostAddress().equals(defaultDstAddress)) {
                    builder.get(IpV4Packet.Builder.class).dstAddr(modifiedAddress);
                } else {
                    builder.get(IpV4Packet.Builder.class).srcAddr(modifiedAddress);
                }
                packet = builder.build();
            }
        } catch (Exception ex) {
            LOG.error("Error updating destination IP", ex);
        }
        return packet;
    }

    /**
     * Convert IP address to presenting byte array
     * @param ipAddress
     * @return
     * @throws UnknownHostException 
     */
    private byte[] convertIPToByte(String ipAddress) throws UnknownHostException {
        String[] splittedIP = ipAddress.split("\\.");
        return new byte[]{(byte) Integer.parseInt(splittedIP[0]), (byte) Integer.parseInt(splittedIP[1]), (byte) Integer.parseInt(splittedIP[2]), (byte) Integer.parseInt(splittedIP[3])};
    }

    /**
     * Return true if packet is valid, otherwise return false
     * @return 
     */
    public boolean isValidPacket() {
        return validPacket;
    }

    /**
     * Reset to default values
     */
    public void reset() {
        defaultDstAddress = null;
        defaultSrcAddress = null;
    }
}

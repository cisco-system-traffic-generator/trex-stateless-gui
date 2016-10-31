/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.pcap4j.packet.Packet;

/**
 * Import pcap table data model
 * @author GeorgeKH
 */
public class ImportPcapTableData {
    
    private BooleanProperty selected = new SimpleBooleanProperty(true);
    private SimpleStringProperty name = new SimpleStringProperty("");
    private SimpleIntegerProperty index = new SimpleIntegerProperty();
    private SimpleIntegerProperty length = new SimpleIntegerProperty();
    private SimpleStringProperty macSrc = new SimpleStringProperty("");
    private SimpleStringProperty macDst = new SimpleStringProperty("");
    private SimpleStringProperty ipSrc = new SimpleStringProperty("");
    private SimpleStringProperty ipDst = new SimpleStringProperty("");
    private SimpleStringProperty packetType = new SimpleStringProperty("");

    private Packet packet;
    
    /**
     * Return enable property
     *
     * @return
     */
    public BooleanProperty getSelected() {
        return selected;
    }

    /**
     * Set selected
     * @param selected 
     */
    public void setSelected(Boolean selected){
        this.selected.setValue(selected);
    }
    
    /**
     * Return true if selected otherwise return false
     * @return 
     */
    public boolean isSelected(){
        return selected.getValue();
    }
    
    /**
     * Return enable property value
     *
     * @return
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Return name
     * @return 
     */
    public String getName() {
        return name.getValue();
    }

    /**
     * Set name
     * @param name 
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Return name property
     * @return 
     */
    public StringProperty nameProperty(){
        return name;
    }
    
    /**
     * Return index
     * @return 
     */
    public int getIndex() {
        return index.getValue();
    }

    /**
     * Set Index
     * @param index 
     */
    public void setIndex(int index) {
        this.index.setValue(index);
    }

    /**
     * Return length
     * @return 
     */
    public int getLength() {
        return length.getValue();
    }

    /**
     * Set length
     * @param length 
     */
    public void setLength(int length) {
        this.length.setValue(length);
    }

    /**
     * Return mac source address
     * @return 
     */
    public String getMacSrc() {
        return macSrc.getValue();
    }

    /**
     * Set mac source address
     * @param macSrc 
     */
    public void setMacSrc(String macSrc) {
        this.macSrc.setValue(macSrc);
    }

    /**
     * Return mac destination address
     * @return 
     */
    public String getMacDst() {
        return macDst.getValue();
    }

    /**
     * Set mac destination address
     * @param macDst 
     */
    public void setMacDst(String macDst) {
        this.macDst.setValue(macDst);
    }

    /**
     * Return ip source address
     * @return 
     */
    public String getIpSrc() {
        return ipSrc.getValue();
    }

    /**
     * Set ip source address
     * @param ipSrc 
     */
    public void setIpSrc(String ipSrc) {
        this.ipSrc.setValue(ipSrc);
    }

    /**
     * Return ip destination address
     * @return 
     */
    public String getIpDst() {
        return ipDst.getValue();
    }

    /**
     * Set ip destination address
     * @param ipDst 
     */
    public void setIpDst(String ipDst) {
        this.ipDst.setValue(ipDst);
    }

    /**
     * Return packet type
     * @return 
     */
    public String getPacketType() {
        return packetType.getValue();
    }

    /**
     * Set packet type
     * @param packetType 
     */
    public void setPacketType(String packetType) {
        this.packetType.setValue(packetType);
    }

    /**
     * Return packet
     * @return 
     */
    public Packet getPacket() {
        return packet;
    }
    
    /**
     * Set packet
     * @param packet 
     */
    public void setPacket(Packet packet) {
        this.packet = packet;
    }
    
}

package com.cisco.trex.stl.gui.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Recorder {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty packets = new SimpleStringProperty(); 
    private IntegerProperty bytes = new SimpleIntegerProperty();
    private StringProperty rxFilter = new SimpleStringProperty(); 
    private StringProperty txFilter = new SimpleStringProperty();

    public Recorder(Integer id, String status, String packets, Integer bytes, String rxFilter, String txFilter) {
        this.id.set(id);
        this.status.set(status);
        this.packets.set(packets);
        this.bytes.set(bytes);
        this.rxFilter.set(rxFilter);
        this.txFilter.set(txFilter);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getPackets() {
        return packets.get();
    }

    public StringProperty packetsProperty() {
        return packets;
    }

    public void setPackets(String packets) {
        this.packets.set(packets);
    }

    public int getBytes() {
        return bytes.get();
    }

    public IntegerProperty bytesProperty() {
        return bytes;
    }

    public void setBytes(int bytes) {
        this.bytes.set(bytes);
    }

    public String getRxFilter() {
        return rxFilter.get();
    }

    public StringProperty rxFilterProperty() {
        return rxFilter;
    }

    public void setRxFilter(String rxFilter) {
        this.rxFilter.set(rxFilter);
    }

    public String getTxFilter() {
        return txFilter.get();
    }

    public StringProperty txFilterProperty() {
        return txFilter;
    }

    public void setTxFilter(String txFilter) {
        this.txFilter.set(txFilter);
    }
}

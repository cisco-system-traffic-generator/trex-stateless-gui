package com.cisco.trex.stl.gui.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CapturedPkt {
    private StringProperty type = new SimpleStringProperty();
    private IntegerProperty length = new SimpleIntegerProperty();
    private StringProperty hwSrc = new SimpleStringProperty(); 
    private StringProperty hwDst = new SimpleStringProperty();
    private StringProperty ipSrc = new SimpleStringProperty(); 
    private StringProperty ipDst = new SimpleStringProperty();

    public CapturedPkt(String type, Integer length, String hwSrc, String hwDst, String ipSrc, String ipDst) {
        this.type.set(type);
        this.length.set(length);
        this.hwSrc.set(hwSrc);
        this.hwDst.set(hwDst);
        this.ipSrc.set(ipSrc);
        this.ipDst.set(ipDst);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public int getLength() {
        return length.get();
    }

    public IntegerProperty lengthProperty() {
        return length;
    }

    public void setLength(int length) {
        this.length.set(length);
    }

    public String getHwSrc() {
        return hwSrc.get();
    }

    public StringProperty hwSrcProperty() {
        return hwSrc;
    }

    public void setHwSrc(String hwSrc) {
        this.hwSrc.set(hwSrc);
    }

    public String getHwDst() {
        return hwDst.get();
    }

    public StringProperty hwDstProperty() {
        return hwDst;
    }

    public void setHwDst(String hwDst) {
        this.hwDst.set(hwDst);
    }

    public String getIpSrc() {
        return ipSrc.get();
    }

    public StringProperty ipSrcProperty() {
        return ipSrc;
    }

    public void setIpSrc(String ipSrc) {
        this.ipSrc.set(ipSrc);
    }

    public String getIpDst() {
        return ipDst.get();
    }

    public StringProperty ipDstProperty() {
        return ipDst;
    }

    public void setIpDst(String ipDst) {
        this.ipDst.set(ipDst);
    }
}

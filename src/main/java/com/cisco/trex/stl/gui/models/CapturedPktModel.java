package com.cisco.trex.stl.gui.models;

import javafx.beans.property.*;

public class CapturedPktModel {
    private IntegerProperty number = new SimpleIntegerProperty();
    private IntegerProperty port = new SimpleIntegerProperty();
    private StringProperty mode = new SimpleStringProperty();
    private StringProperty time = new SimpleStringProperty();
    private StringProperty ipDst = new SimpleStringProperty();
    private StringProperty ipSrc = new SimpleStringProperty();
    private StringProperty type = new SimpleStringProperty();
    private IntegerProperty length = new SimpleIntegerProperty();
    private StringProperty info = new SimpleStringProperty();

    public CapturedPktModel(Integer number, Integer port, String mode, Double time, String ipDst, String ipSrc, String type, Integer length, String info) {
        this.number.set(number);
        this.port.set(port);
        this.mode.set(mode);
        this.time.set(String.format("%f", time));
        this.ipDst.set(ipDst);
        this.ipSrc.set(ipSrc);
        this.type.set(type);
        this.length.set(length);
        this.info.set(info);
    }

    public int getNumber() {
        return number.get();
    }

    public IntegerProperty numberProperty() {
        return number;
    }

    public void setNumber(int number) {
        this.number.set(number);
    }

    public int getPort() {
        return port.get();
    }

    public IntegerProperty portProperty() {
        return port;
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public String getMode() {
        return mode.get();
    }

    public StringProperty modeProperty() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode.set(mode);
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(double time) {
        this.time.set(String.format("%f", time));
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

    public String getIpSrc() {
        return ipSrc.get();
    }

    public StringProperty ipSrcProperty() {
        return ipSrc;
    }

    public void setIpSrc(String ipSrc) {
        this.ipSrc.set(ipSrc);
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

    public String getInfo() {
        return info.get();
    }

    public StringProperty infoProperty() {
        return info;
    }

    public void setInfo(String info) {
        this.info.set(info);
    }
}

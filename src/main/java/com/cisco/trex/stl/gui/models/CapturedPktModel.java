package com.cisco.trex.stl.gui.models;

import javafx.beans.property.*;

public class CapturedPktModel {
    private IntegerProperty number = new SimpleIntegerProperty();
    private IntegerProperty port = new SimpleIntegerProperty();
    private StringProperty mode = new SimpleStringProperty();
    private StringProperty time = new SimpleStringProperty();
    private StringProperty dst = new SimpleStringProperty();
    private StringProperty src = new SimpleStringProperty();
    private StringProperty type = new SimpleStringProperty();
    private IntegerProperty length = new SimpleIntegerProperty();
    private StringProperty info = new SimpleStringProperty();

    public CapturedPktModel(Integer number, Integer port, String mode, Double time, String dst, String src, String type, Integer length, String info) {
        this.number.set(number);
        this.port.set(port);
        this.mode.set(mode);
        this.time.set(String.format("%f", time));
        this.dst.set(dst);
        this.src.set(src);
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

    public String getDst() {
        return dst.get();
    }

    public StringProperty dstProperty() {
        return dst;
    }

    public void setdst(String dst) {
        this.dst.set(dst);
    }

    public String getsrc() {
        return src.get();
    }

    public StringProperty srcProperty() {
        return src;
    }

    public void setSrc(String src) {
        this.src.set(src);
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

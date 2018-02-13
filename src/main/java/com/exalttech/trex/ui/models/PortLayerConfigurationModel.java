package com.exalttech.trex.ui.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PortLayerConfigurationModel {
    private ConfigurationMode type;
    
    private StringProperty src = new SimpleStringProperty();
    private StringProperty dst = new SimpleStringProperty();

    private String src6 = "";
    private String dst6 = "";

    private StringProperty state = new SimpleStringProperty();

    public PortLayerConfigurationModel(ConfigurationMode type, String src, String dst, String state) {
        this.type = type;
        this.src.setValue(src);
        this.dst.setValue(dst);
        this.state.setValue(state);
    }

    public ConfigurationMode getType() {
        return type;
    }

    public String getSrc() {
        return src.get();
    }

    public StringProperty srcProperty() {
        return src;
    }

    public String getDst() {
        return dst.get();
    }

    public StringProperty dstProperty() {
        return dst;
    }

    public String getState() {
        return state.get();
    }

    public StringProperty stateProperty() {
        return state;
    }

    public String getSrc6() {
        return src6;
    }

    public void setSrc6(String src6) {
        this.src6 = src6;
    }

    public String getDst6() {
        return dst6;
    }

    public void setDst6(String dst6) {
        this.dst6 = dst6;
    }
}

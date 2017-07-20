package com.cisco.trex.stl.gui.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class IPv6Host {
    private StringProperty macAddress = new SimpleStringProperty();
    private StringProperty ipAddress = new SimpleStringProperty();

    public IPv6Host(String macAddress, String ipAddress) {
        this.macAddress.set(macAddress);
        this.ipAddress.set(ipAddress);
    }

    public String getMacAddress() {
        return macAddress.get();
    }

    public StringProperty macAddressProperty() {
        return macAddress;
    }

    public String getIpAddress() {
        return ipAddress.get();
    }

    public StringProperty ipAddressProperty() {
        return ipAddress;
    }
}

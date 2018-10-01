package com.exalttech.trex.ui.controllers.daemon;

import java.util.Map;

public class InterfaceInfoProvider {
    private Map<String, InterfaceInfo> interfacesInfo;

    public void setInterfacesInfo(Map<String, InterfaceInfo> info) {
        this.interfacesInfo = info;
    }

    public Map<String, InterfaceInfo> getInterfacesInfo() {
        return this.interfacesInfo;
    }

    public boolean hasInfo() {
        return this.interfacesInfo != null;
    }
}

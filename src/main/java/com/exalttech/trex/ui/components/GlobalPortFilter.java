package com.exalttech.trex.ui.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class GlobalPortFilter {
    private BooleanProperty onlyOwnedPorts;

    public GlobalPortFilter() {
        onlyOwnedPorts = new SimpleBooleanProperty(false);
    }

    public BooleanProperty onlyOwnedPortsProperty() {
        return onlyOwnedPorts;
    }
}

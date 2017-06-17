package com.cisco.trex.stl.gui.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class RunningConfiguration {
    private BooleanProperty flowStatsEnabledProperty = new SimpleBooleanProperty(false);
    private BooleanProperty latencyEnabledProperty = new SimpleBooleanProperty(false);

    public boolean isFlowStatsEnabledProperty() {
        return flowStatsEnabledProperty.get();
    }

    public BooleanProperty flowStatsEnabledProperty() {
        return flowStatsEnabledProperty;
    }

    public boolean isLatencyEnabledProperty() {
        return latencyEnabledProperty.get();
    }

    public BooleanProperty latencyEnabledProperty() {
        return latencyEnabledProperty;
    }
}

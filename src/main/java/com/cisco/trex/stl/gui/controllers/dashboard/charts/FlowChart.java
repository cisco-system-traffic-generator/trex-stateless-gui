package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.scene.Node;
import javafx.stage.WindowEvent;

import com.cisco.trex.stl.gui.controllers.dashboard.FlowStatsAnchorPane;

import com.exalttech.trex.util.Initialization;


public abstract class FlowChart extends FlowStatsAnchorPane {
    public FlowChart() {
        Initialization.initializeFXML(this, getResourceName());
        Initialization.initializeCloseEvent(getRoot(), this::onWindowCloseRequest);
    }

    protected abstract String getResourceName();

    protected abstract Node getRoot();

    private void onWindowCloseRequest(final WindowEvent window) {
        setActive(false);
    }
}

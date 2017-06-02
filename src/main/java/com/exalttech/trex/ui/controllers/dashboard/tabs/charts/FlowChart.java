package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.scene.Node;
import javafx.stage.WindowEvent;

import com.exalttech.trex.ui.controllers.dashboard.FlowStatsAnchorPane;
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

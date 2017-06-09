package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.scene.Node;
import javafx.stage.WindowEvent;

import com.cisco.trex.stl.gui.controllers.dashboard.FlowStatsBaseController;

import com.exalttech.trex.util.Initialization;


public abstract class FlowChartController extends FlowStatsBaseController {
    public FlowChartController() {
        Initialization.initializeFXML(this, getResourceName());
        Initialization.initializeCloseEvent(getRoot(), this::onWindowCloseRequest);
    }

    protected abstract String getResourceName();

    protected abstract Node getRoot();

    private void onWindowCloseRequest(final WindowEvent window) {
        setActive(false);
    }
}

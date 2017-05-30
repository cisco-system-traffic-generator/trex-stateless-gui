package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

import com.exalttech.trex.ui.views.storages.PGIDStatsStorage;
import com.exalttech.trex.ui.views.storages.StatsStorage;
import com.exalttech.trex.util.Initialization;


public abstract class FlowChart extends AnchorPane {
    private boolean isActive = false;
    private PGIDStatsStorage.StatsChangedListener statsChangedListener = this::render;

    public FlowChart() {
        Initialization.initializeFXML(this, getResourceName());
        Initialization.initializeCloseEvent(getRoot(), this::onWindowCloseRequest);
    }

    public void setActive(final boolean isActive) {
        if (this.isActive == isActive) {
            return;
        }

        this.isActive = isActive;
        if (this.isActive) {
            StatsStorage.getInstance().getPGIDStatsStorage().addStatsChangeListener(statsChangedListener);
            render();
        } else {
            StatsStorage.getInstance().getPGIDStatsStorage().removeStatsChangeListener(statsChangedListener);
        }
    }

    protected abstract void render();

    protected abstract String getResourceName();

    protected abstract Node getRoot();

    private void onWindowCloseRequest(final WindowEvent window) {
        setActive(false);
    }
}

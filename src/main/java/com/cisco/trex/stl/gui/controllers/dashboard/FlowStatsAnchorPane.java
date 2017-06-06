package com.cisco.trex.stl.gui.controllers.dashboard;

import javafx.scene.layout.AnchorPane;

import com.cisco.trex.stl.gui.storages.PGIDStatsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;


public abstract class FlowStatsAnchorPane extends AnchorPane {
    private boolean isActive = false;
    private PGIDStatsStorage.StatsChangedListener statsChangedListener = this::render;

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
}

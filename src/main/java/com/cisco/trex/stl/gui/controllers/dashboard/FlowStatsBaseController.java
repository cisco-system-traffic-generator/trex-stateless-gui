package com.cisco.trex.stl.gui.controllers.dashboard;

import com.exalttech.trex.application.TrexApp;
import javafx.scene.layout.AnchorPane;

import com.cisco.trex.stl.gui.storages.PGIDStatsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;


public abstract class FlowStatsBaseController extends AnchorPane {
    private boolean isActive = false;
    private PGIDStatsStorage.StatsChangedListener statsChangedListener = this::render;

    StatsStorage statsStorage = TrexApp.injector.getInstance(StatsStorage.class);

    public void setActive(final boolean isActive) {
        if (this.isActive == isActive) {
            return;
        }

        this.isActive = isActive;
        if (this.isActive) {
            statsStorage.getPGIDStatsStorage().addStatsChangeListener(statsChangedListener);
            render();
        } else {
            statsStorage.getPGIDStatsStorage().removeStatsChangeListener(statsChangedListener);
        }
    }

    protected abstract void render();
}

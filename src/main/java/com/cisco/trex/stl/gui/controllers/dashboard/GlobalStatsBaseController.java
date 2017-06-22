package com.cisco.trex.stl.gui.controllers.dashboard;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;

import com.exalttech.trex.ui.views.statistics.StatsLoader;


public abstract class GlobalStatsBaseController extends AnchorPane {
    private boolean isActive = false;
    private StatsLoader.GlobalStatsChangedListener statsChangedListener = this::handleUpdate;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(final boolean isActive) {
        if (this.isActive == isActive) {
            return;
        }

        this.isActive = isActive;
        if (this.isActive) {
            StatsLoader.getInstance().addGlobalStatsChangedListener(statsChangedListener);
            render();
        } else {
            StatsLoader.getInstance().removeGlobalStatsChangedListener(statsChangedListener);
        }
    }

    private void handleUpdate() {
        Platform.runLater(this::render);
    }

    protected abstract void render();
}

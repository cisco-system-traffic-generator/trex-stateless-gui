package com.cisco.trex.stl.gui.controllers.dashboard.utilization;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;

import com.cisco.trex.stl.gui.storages.UtilizationStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;

import com.exalttech.trex.util.Initialization;


public class UtilizationController extends AnchorPane {
    @FXML
    private AnchorPane root;
    @FXML
    private ToggleGroup toggleGroupMode;
    @FXML
    private GridPane table;

    private boolean isActive = false;
    private UtilizationStorage.UtilizationChangedListener utilizationChangedListener = this::render;

    public UtilizationController() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/utilization/Utilization.fxml");
        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);

        toggleGroupMode.selectedToggleProperty().addListener(this::typeChanged);
    }

    public void setActive(final boolean isActive) {
        if (this.isActive == isActive) {
            return;
        }

        this.isActive = isActive;
        final UtilizationStorage utilizationStorage = StatsStorage.getInstance().getUtilizationStorage();
        if (this.isActive) {
            utilizationStorage.addUtilizationChangedListener(utilizationChangedListener);
            render();
        } else {
            utilizationStorage.removeUtilizationChangedListener(utilizationChangedListener);
        }
    }

    private void onWindowCloseRequest(final WindowEvent window) {
        setActive(false);
    }

    private void typeChanged(
            final ObservableValue<? extends Toggle> observable,
            final Toggle oldValue,
            final Toggle newValue
    ) {
        if (newValue == null) {
            oldValue.setSelected(true);
        } else {
            render();
        }
    }

    private void render() {
        if (((ToggleButton)toggleGroupMode.getSelectedToggle()).getText().equals("CPU")) {
            renderCPU();
        } else {
            renderMbuf();
        }
    }

    private void renderCPU() {
        table.getChildren().clear();
    }

    private void renderMbuf() {
        table.getChildren().clear();
    }
}

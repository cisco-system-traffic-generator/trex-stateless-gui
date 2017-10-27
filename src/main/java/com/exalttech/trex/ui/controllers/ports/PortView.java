package com.exalttech.trex.ui.controllers.ports;

import com.exalttech.trex.ui.controllers.ports.tabs.PortAttributes;
import com.exalttech.trex.ui.controllers.ports.tabs.PortHardwareCounters;
import com.exalttech.trex.ui.controllers.ports.tabs.PortLayerConfiguration;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.util.Initialization;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class PortView extends TabPane {
    
    @FXML
    private PortAttributes portAttributes;
    
    @FXML
    private PortLayerConfiguration layerConfig;
    
    @FXML
    PortHardwareCounters hardwareCounters;
    
    public PortView() {
        Initialization.initializeFXML(this, "/fxml/ports/Port.fxml");
        getSelectionModel().selectedItemProperty().addListener((observable, prevTab, currentTab) -> {
            if (currentTab.getId().equals("hardwareCountersTab")) {
                hardwareCounters.startPolling();
            } else {
                hardwareCounters.stopPolling();
            }
        });
    }

    public void loadModel(PortModel model) {
        portAttributes.bindModel(model);
        layerConfig.bindModel(model);
        boolean runPolling = getSelectionModel().selectedItemProperty().get().getId().equals("hardwareCountersTab");
        hardwareCounters.bindModel(model, runPolling);

        layerConfig.disableProperty().unbind();
        layerConfig.setDisable(!model.isOwnedProperty().get());
        layerConfig.disableProperty().bind(Bindings.or(
            model.isOwnedProperty().not(),
            model.transmitStateProperty())
        );
    }

    public void stopPolling() {
        hardwareCounters.stopPolling();
    }
}

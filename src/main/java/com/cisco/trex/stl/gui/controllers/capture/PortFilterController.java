package com.cisco.trex.stl.gui.controllers.capture;

import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckComboBox;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class PortFilterController extends HBox {
    
    @FXML
    private CheckComboBox<String> rxFilter;
    
    @FXML
    private CheckComboBox<String> txFilter;

    public PortFilterController() {
        Initialization.initializeFXML(this, "/fxml/pkt_capture/PortFilter.fxml");

        List<String> choices = PortsManager.getInstance().getPortList().stream().map(port -> String.format("Port %s", port.getIndex())).collect(toList());
        
        rxFilter.getItems().addAll(choices);
        txFilter.getItems().addAll(choices);
    }

    public List<Integer> getRxPorts() {
        return rxFilter.getCheckModel().getCheckedIndices();
    }
    public List<Integer> getTxPorts() {
        return txFilter.getCheckModel().getCheckedIndices();
    }
}

package com.cisco.trex.stl.gui.controllers.capture;

import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.util.Initialization;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PortFilterController extends HBox {
    
    @FXML
    private CheckComboBox<String> rxFilter;
    
    @FXML
    private CheckComboBox<String> txFilter;

    @FXML
    private Button applyBtn;

    private List<UpdateFilterHandler> onUpdateHandlers = new ArrayList<>();

    public interface UpdateFilterHandler {
        void onFilterUpdate();
    }

    public PortFilterController() {
        Initialization.initializeFXML(this, "/fxml/pkt_capture/PortFilter.fxml");
        PortsManager.getInstance().addPortServiceModeChangedListener(this::updateFilters);
        updateFilters();
        applyBtn.setOnAction(event -> {
            onUpdateHandlers.forEach(UpdateFilterHandler::onFilterUpdate);
            applyBtn.setDisable(true);
        });

        rxFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> applyBtn.setDisable(false));

        txFilter.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> applyBtn.setDisable(false));
    }

    public void addOnFilterUpdateHandler(UpdateFilterHandler handler) {
        onUpdateHandlers.add(handler);
    }

    private void updateFilters() {
        rxFilter.getCheckModel().clearChecks();
        rxFilter.getItems().clear();

        txFilter.getCheckModel().clearChecks();
        txFilter.getItems().clear();
        List<String> choices = getAvailablePorts();

        rxFilter.getItems().addAll(choices);
        txFilter.getItems().addAll(choices);

        rxFilter.getCheckModel().checkAll();
        txFilter.getCheckModel().checkAll();
    }

    public List<Integer> getRxPorts() {
        return getSelectedPortIndexes(rxFilter);
    }
    public List<Integer> getTxPorts() {
        return getSelectedPortIndexes(txFilter);
    }

    public static List<Integer> getSelectedPortIndexes(CheckComboBox<String> portFilter) {
        return portFilter.getCheckModel().getCheckedItems()
                                         .stream()
                                         .map(portName -> Integer.valueOf(portName.split(" ")[1]))
                                         .collect(toList());
    }

    public static List<String> getAvailablePorts() {
        return PortsManager.getInstance().getPortList()
                                         .stream()
                                         .filter(Port::getServiceMode)
                                         .map(port -> String.format("Port %s", port.getIndex()))
                                         .collect(toList());
    }
}

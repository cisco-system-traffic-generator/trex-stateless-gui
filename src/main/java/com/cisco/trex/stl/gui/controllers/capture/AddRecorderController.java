package com.cisco.trex.stl.gui.controllers.capture;

import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.CheckComboBox;

import java.util.List;

public class AddRecorderController extends BorderPane {

    @FXML
    private CheckComboBox<String> rxFilter;

    @FXML
    private CheckComboBox<String> txFilter;

    @FXML
    private TextField filter;

    @FXML
    private TextField limit;

    public AddRecorderController() {
        Initialization.initializeFXML(this, "/fxml/pkt_capture/AddRecord.fxml");

        List<String> availablePorts = FilterController.getAvailablePorts();
        rxFilter.getItems().addAll(availablePorts);
        txFilter.getItems().addAll(availablePorts);
    }

    public AddRecordPojo getData() {
        AddRecordPojo data = new AddRecordPojo();
        data.rxPorts = FilterController.getSelectedPortIndexes(rxFilter);
        data.txPorts = FilterController.getSelectedPortIndexes(txFilter);
        data.pktLimit = Integer.valueOf(limit.getText());
        data.filter = filter.getText();
        return data;
    }
}

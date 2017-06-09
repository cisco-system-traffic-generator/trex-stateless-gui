package com.cisco.trex.stl.gui.controllers.dashboard.selectors.ports;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import com.exalttech.trex.util.Initialization;


public class PortCheckboxController extends HBox {
    @FXML
    private CheckBox checkBox;
    @FXML
    private Label label;

    private int portNumber;
    private EventHandler<Event> onSelectionChanged;

    public PortCheckboxController(int portNumber, EventHandler<Event> onSelectionChanged) {
        Initialization.initializeFXML(this, "/fxml/dashboard/selectors/ports/PortCheckBox.fxml");

        this.portNumber = portNumber;
        this.label.setText(String.format("Port %d", portNumber));

        this.onSelectionChanged = onSelectionChanged;
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setOnSelectionChanged(EventHandler<Event> onSelectionChanged) {
        this.onSelectionChanged = onSelectionChanged;
    }

    @FXML
    public void handleCheckBoxAction(ActionEvent event) {
        if (onSelectionChanged != null) {
            onSelectionChanged.handle(new Event(this, null, null));
        }
    }
}

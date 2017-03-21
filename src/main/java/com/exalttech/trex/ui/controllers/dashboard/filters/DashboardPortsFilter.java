package com.exalttech.trex.ui.controllers.dashboard.filters;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;

import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.util.Initialization;


public class DashboardPortsFilter extends VBox {
    @FXML
    private ComboBox<String> portFilterSelector;
    @FXML
    private VBox portCheckBoxListContainer;

    private EventHandler<Event> onSelectedPortIndexesChanged;
    private Set<Integer> selectedPortIndexes = new HashSet<>();

    public DashboardPortsFilter() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/filters/DashboardPortsFilter.fxml");

        buildPortsList();
    }

    public EventHandler<Event> getOnSelectedPortIndexesChanged() {
        return onSelectedPortIndexesChanged;
    }

    public void setOnSelectedPortIndexesChanged(EventHandler<Event> onSelectedPortIndexesChanged) {
        this.onSelectedPortIndexesChanged = onSelectedPortIndexesChanged;
    }

    public Set<Integer> getSelectedPortIndexes() {
        return selectedPortIndexes;
    }

    @FXML
    public void handlePortFilterSelectorUpdated(Event event) {
        buildPortsList();
    }

    private void buildPortsList() {
        selectedPortIndexes.clear();
        portCheckBoxListContainer.getChildren().clear();

        final Set<Integer> portIndexes = getPortIndexes();
        portIndexes.forEach((Integer portIndex) -> {
            selectedPortIndexes.add(portIndex);
            final DashboardPortCheckbox portCheckbox = new DashboardPortCheckbox(
                    portIndex,
                    this::handlePortSelectionChanged
            );
            portCheckBoxListContainer.getChildren().add(portCheckbox);
        });

        if (onSelectedPortIndexesChanged != null) {
            onSelectedPortIndexesChanged.handle(new Event(this, null, null));
        }
    }

    private void handlePortSelectionChanged(Event event) {
        final DashboardPortCheckbox checkBox = (DashboardPortCheckbox) event.getSource();
        if (checkBox.isSelected()) {
            selectedPortIndexes.add(checkBox.getPortNumber());
        } else {
            selectedPortIndexes.remove(checkBox.getPortNumber());
        }

        if (onSelectedPortIndexesChanged != null) {
            onSelectedPortIndexesChanged.handle(new Event(this, null, null));
        }
    }

    private Set<Integer> getPortIndexes() {
        final String val = portFilterSelector.getSelectionModel().getSelectedItem();
        final PortsManager portsManager = PortsManager.getInstance();
        if (val.equals("All")) {
            return new HashSet<>(portsManager.getPortIndexes());
        }
        return new HashSet<>(PortsManager.getInstance().getOwnedPortIndexes());
    }
}

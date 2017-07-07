package com.cisco.trex.stl.gui.models;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class UtilizationCPUModel {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty thread = new SimpleStringProperty();
    
    // Active ports per core. By default 2 ports per core.
    private StringProperty activePorts = new SimpleStringProperty();
    private IntegerProperty avg = new SimpleIntegerProperty();
    private List<IntegerProperty> history = new ArrayList<>(14);

    public UtilizationCPUModel(int coreIdx, List<Integer> activePorts, int avg, List<Integer> history) {
        this.id.set(coreIdx);
        this.avg.set(avg);
        String activePortsStr = activePorts.stream().filter(port -> port >= 0).map(Objects::toString).collect(joining(", "));
        activePortsStr = activePortsStr.isEmpty() ? "IDLE" : activePortsStr;
        this.activePorts.set(activePortsStr);
        this.thread.set(coreIdx + " ("+activePortsStr+")");
        this.history.addAll(history.stream().map(SimpleIntegerProperty::new).collect(toList()));
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty threadProperty() {
        return thread;
    }

    public String getActivePorts() {
        return activePorts.get();
    }

    public StringProperty activePortsProperty() {
        return activePorts;
    }

    public double getAvg() {
        return avg.get();
    }

    public IntegerProperty avgProperty() {
        return avg;
    }

    public List<IntegerProperty> getHistory() {
        return history;
    }
    public IntegerProperty getHistory(int idx) {
        return history.get(idx);
    }
}

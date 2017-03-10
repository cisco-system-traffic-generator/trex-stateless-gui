package com.exalttech.trex.ui.controllers.dashboard.tabs.latency;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

import com.exalttech.trex.util.Initialization;


public class DashboardTabLatency extends AnchorPane {
    private static final List<String> chartTypes = new ArrayList<String>() {{
        add("Histogram");
        add("Chart");
    }};
    private static final List<String> latencyIntervals = new ArrayList<String>() {{
        add("60");
        add("90");
        add("120");
        add("300");
    }};

    @FXML
    private DashboardTabLatencyHistogram histogram;
    @FXML
    private DashboardTabLatencyChart chart;
    @FXML
    private ComboBox chartTypeComboBox;
    @FXML
    private ComboBox intervalComboBox;

    public DashboardTabLatency() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/latency/DashboardTabLatency.fxml");
        initializeChartTypeComboBox();
        initializeIntervalComboBox();
    }

    private void initializeChartTypeComboBox() {
        chartTypeComboBox.getItems().addAll(FXCollections.observableArrayList(chartTypes));
        chartTypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                histogram.setVisible(newValue.equals("Histogram"));
                setChartVisibility(newValue.equals("Chart"));
            }
        });
        chartTypeComboBox.setValue("Histogram");
    }

    private void setChartVisibility(boolean isVisible) {
        chart.setVisible(isVisible);
        intervalComboBox.setDisable(!isVisible);
    }

    private void initializeIntervalComboBox() {
        intervalComboBox.getItems().addAll(FXCollections.observableArrayList(latencyIntervals));
        intervalComboBox.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                chart.setInterval(Integer.parseInt(newValue));
            }
        });
        intervalComboBox.setValue("60");
    }
}

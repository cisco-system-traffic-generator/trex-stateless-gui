package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import com.exalttech.trex.util.Initialization;


public abstract class DashboardTabChartsLine extends AnchorPane implements DashboardTabChartsUpdatable {
    @FXML
    private LineChart<Number, Number> chart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    public DashboardTabChartsLine(IntegerProperty interval) {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/charts/DashboardTabChartsLine.fxml");
        xAxis.lowerBoundProperty().bind(interval.negate());
        xAxis.setLabel(getXChartLabel());
        yAxis.setLabel(getYChartLabel());
    }

    protected LineChart getChart() {
        return chart;
    }
    protected NumberAxis getYAxis() {
        return yAxis;
    }

    protected abstract String getXChartLabel();

    protected abstract String getYChartLabel();

    protected void setSeriesColor(final XYChart.Series<Number, Number> series, final String color) {
        series.nodeProperty().addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            if (oldValue == null && newValue != null) {
                series.getNode().setStyle(String.format("-fx-stroke: %s;", color));
            }
        });
    }
}

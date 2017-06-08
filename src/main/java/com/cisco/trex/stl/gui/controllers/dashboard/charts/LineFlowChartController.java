package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;


public abstract class LineFlowChartController extends FlowChartController {
    @FXML
    private AnchorPane root;
    @FXML
    private javafx.scene.chart.LineChart<Number, Number> chart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    public LineFlowChartController(final IntegerProperty interval) {
        xAxis.lowerBoundProperty().bind(interval.negate());
    }

    @Override
    protected String getResourceName() {
        return "/fxml/dashboard/charts/LineChart.fxml";
    }

    @Override
    protected Node getRoot() {
        return root;
    }

    protected javafx.scene.chart.LineChart getChart() {
        return chart;
    }

    protected NumberAxis getYAxis() {
        return yAxis;
    }

    protected void setSeriesColor(final XYChart.Series<?, ?> series, final String color) {
        series.nodeProperty().addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            if (oldValue == null && newValue != null) {
                series.getNode().setStyle(String.format("-fx-stroke: %s;", color));
            }
        });
    }
}

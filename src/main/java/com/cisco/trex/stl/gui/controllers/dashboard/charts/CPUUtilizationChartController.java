package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import com.cisco.trex.stl.gui.models.CpuUtilStatPoint;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Initialization;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CPUUtilizationChartController extends AnchorPane {

    @FXML
    private AnchorPane root;
    
    @FXML
    private javafx.scene.chart.LineChart<Double, Number> chart;
    
    @FXML
    private NumberAxis xAxis;
    
    @FXML
    private NumberAxis yAxis;
    
    private static String[] colors = new String[] {"#f3622d", "#fba71b", "#57b757", "#41a9c9", "#4258c9", "#9a42c8", "#c84164", "#888888"};
    
    public CPUUtilizationChartController() {
        Initialization.initializeFXML(this, "/fxml/dashboard/charts/LineChart.fxml");
        xAxis.lowerBoundProperty().bind(new SimpleIntegerProperty(300).negate());
        yAxis.setLabel("CPU Load (%)");
        chart.setLegendVisible(true);
    }
    
    public void render(Map<String, ArrayHistory<CpuUtilStatPoint>> cpuUtilizationHistoryMap) {
        chart.getData().clear();

        final List<XYChart.Series<Double, Number>> seriesList = new LinkedList<>();
        final AtomicInteger index = new AtomicInteger(0);
        cpuUtilizationHistoryMap.entrySet()
                                .stream()
                                .limit(colors.length)
                                .forEach(entry -> {
                                    String core = entry.getKey();
                                    ArrayHistory<CpuUtilStatPoint> history = entry.getValue();
                                    XYChart.Series<Double, Number> series = new XYChart.Series<>();
                                    series.setName(core);
                        
                                    double lastTime = history.last().getTime();
                                    
                                    int size = history.size();
                                    for (int i = 0; i < size; ++i) {
                                        final CpuUtilStatPoint point = history.get(i);
                                        final double time = point.getTime();
                                        series.getData().add(new XYChart.Data<>(time - lastTime, point.getValue()));
                                    }
                                    setSeriesColor(series, colors[index.getAndIncrement()]);
                                    seriesList.add(series);
        });
        chart.getData().addAll(seriesList);
    }

    private void setSeriesColor(final XYChart.Series<?, ?> series, final String color) {
        series.nodeProperty().addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            if (oldValue == null && newValue != null) {
                series.getNode().setStyle(String.format("-fx-stroke: %s; -fx-stroke-line-join: round;", color));
            }
        });
    }
}

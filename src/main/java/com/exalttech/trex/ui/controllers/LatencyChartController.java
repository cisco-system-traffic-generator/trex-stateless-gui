/**
 * *****************************************************************************
 * Copyright (c) 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************
 */
package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.ui.models.ChartSeries;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Util;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import org.apache.log4j.Logger;

/**
 * Latency chart controller
 *
 * @author Georgekh
 */
public class LatencyChartController implements Initializable {

    private static final Logger LOG = Logger.getLogger(LatencyChartController.class.getName());
    private static final int NUMBER_DISPLAYED_SERIES = 4;
    private static int AXIS_INTERVAL = 30;
    @FXML
    private LineChart latencyChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    Map<String, ChartSeries> latencySeriesList = new HashMap();
    Map<String, LiveSeries> liveSeriesMap = new HashMap();
    BooleanProperty chartSeriesUpdatedProperty = new SimpleBooleanProperty();
    StringProperty intervalProperty = new SimpleStringProperty();

    private int displayedSeriesCount = 0;

    private RefreshingService updateChartService = null;

    /**
     * Initializer
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDynamicChart();
    }

    /**
     * Initialize chart
     */
    private void initializeDynamicChart() {
        updateAxisBound();
        xAxis.setUpperBound(0);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setMinorTickVisible(true);
        yAxis.setAutoRanging(true);
        yAxis.setLabel("(μsec)");
        latencyChart.setCreateSymbols(false);
        latencyChart.setLegendVisible(true);
        latencyChart.setLegendSide(Side.BOTTOM);

        latencyChart.setAnimated(false);
        latencyChart.setHorizontalGridLinesVisible(true);

        chartSeriesUpdatedProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        latencyChart.getData().clear();
                        displayedSeriesCount = 0;
                        for (ChartSeries chartSeries : latencySeriesList.values()) {
                            if (chartSeries.showSeries()) {
                                latencyChart.getData().add(chartSeries.getLatencySeries());
                                displayedSeriesCount++;
                            }
                            if (displayedSeriesCount == NUMBER_DISPLAYED_SERIES) {
                                break;
                            }
                        }
                    }
                });
            }
        });

        intervalProperty.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            AXIS_INTERVAL = Integer.parseInt(newValue);
            updateAxisBound();
        });
    }

    /**
     * Update axis bound and tick unit
     */
    private void updateAxisBound() {
        xAxis.setLowerBound(-1 * AXIS_INTERVAL);
        int tickUnit = AXIS_INTERVAL / 6;
        xAxis.setTickUnit(tickUnit);
    }

    /**
     * Start drawing chart and update his points
     */
    public void runChart() {
        updateChartService = new RefreshingService();
        updateChartService.setPeriod(Duration.millis(500));
        updateChartService.setOnSucceeded((WorkerStateEvent event) -> {
            updateChartSeries();
            addDataToSeries();
        });
        updateChartService.start();
    }

    /**
     * Stop rendering chart thread
     */
    public void stopRenderingChart() {
        for (ChartSeries series : latencySeriesList.values()) {
            series.getLatencySeries().getData().clear();
        }
        if (updateChartService.isRunning()) {
            updateChartService.cancel();
        }
    }

    /**
     * Add point to series
     */
    private void addDataToSeries() {

        for (ChartSeries chartSeries : latencySeriesList.values()) {
            if (!chartSeries.getLatencyQueue().isEmpty()) {
                chartSeries.getLatencySeries().getData().clear();
                chartSeries.getLatencySeries().getData().addAll(getSeriesData(chartSeries.getLatencyQueue()));
            }

        }
    }

    /**
     * Return chart series data
     *
     * @param seriesPoints
     * @return
     */
    private List<XYChart.Data<Number, Number>> getSeriesData(List<Number> seriesPoints) {

        int startPt = -1 * AXIS_INTERVAL;
        List<XYChart.Data<Number, Number>> chartData = new ArrayList<>();
        int xAxisIndex = 0 - (seriesPoints.size() - 1) > startPt ? 0 - (seriesPoints.size() - 1) : startPt;
        int index = 0;
        for (; xAxisIndex <= 0; xAxisIndex++) {
            chartData.add(new XYChart.Data<>(xAxisIndex, seriesPoints.get(index)));
            index++;
        }
        if (seriesPoints.size() > AXIS_INTERVAL) {
            seriesPoints.remove(0);
        }
        return chartData;
    }

    /**
     * Return list of chart series
     *
     * @return
     */
    public List<ChartSeries> getChartSeriesList() {
        return new ArrayList<>(latencySeriesList.values());

    }

    /**
     * Return chart series updated property
     *
     * @return
     */
    public BooleanProperty getChartSeriesUpdatedProperty() {
        return chartSeriesUpdatedProperty;
    }

    /**
     * Return interval property
     *
     * @return
     */
    public StringProperty getIntervalProperty() {
        return intervalProperty;
    }

    /**
     * Update chart series
     */
    private void updateChartSeries() {
        try {
            List<String> liveSeries = new ArrayList<>();
            // add a item of random data to queue
            for (String key : StatsLoader.getInstance().getLatencyStatsMap().keySet()) {
                String seriesLatencyString = StatsLoader.getInstance().getLatencyStatsMap().get(key);
                if (seriesLatencyString.contains("latency")) {
                    String latencyString = Util.fromJSONResult(seriesLatencyString, "latency");
                    if (!latencySeriesList.keySet().contains(key)) {
                        ChartSeries newSeries = new ChartSeries(++displayedSeriesCount <= NUMBER_DISPLAYED_SERIES);
                        newSeries.setName(key);
                        latencySeriesList.put(key, newSeries);
                        chartSeriesUpdatedProperty.set(!chartSeriesUpdatedProperty.get());
                    }

                    Map streamLatency = Util.getStatsFromJSONString(latencyString);
                    int lastMaxValue = Integer.parseInt(streamLatency.get("last_max").toString());
                    if (isLiveSeries(key, lastMaxValue)) {
                        liveSeries.add(key);
                    }
                    ChartSeries series = latencySeriesList.get(key);

                    series.getLatencyQueue().add(lastMaxValue);
                }
            }
            // remove dead series
            Map<String, ChartSeries> clonedSeries = new HashMap<>();
            clonedSeries.putAll(latencySeriesList);

            // remove all live stream from cloned
            clonedSeries.keySet().removeAll(liveSeries);

            // remove remaining key -- dead series -- from latency series
            latencySeriesList.keySet().removeAll(clonedSeries.keySet());
            if (clonedSeries.size() > 0) {
                chartSeriesUpdatedProperty.set(!chartSeriesUpdatedProperty.get());
            }
        } catch (Exception ex) {
            LOG.error("Error during rendering chart");
        }
    }

    /**
     * Check and return whether series is live
     *
     * @param key
     * @param value
     * @return
     */
    private boolean isLiveSeries(String key, int value) {
        if (!liveSeriesMap.containsKey(key)) {
            LiveSeries liveSeries = new LiveSeries();
            liveSeries.setDrawnValue(value);
            liveSeriesMap.put(key, liveSeries);
        }
        LiveSeries liveSeries = liveSeriesMap.get(key);
        liveSeries.updateValue(value);
        // 10 = 5sec
        return liveSeries.getCount() <= 10;
    }

    /**
     * Model present live series data
     */
    private class LiveSeries {

        int count = 0;
        int drawnValue;

        /**
         * Return count
         *
         * @return
         */
        public int getCount() {
            return count;
        }

        /**
         * Set count
         *
         * @param count
         */
        public void setCount(int count) {
            this.count = count;
        }

        /**
         * Return drawn value
         *
         * @return
         */
        public int getDrawnValue() {
            return drawnValue;
        }

        /**
         * Set drawn value
         *
         * @param drawnValue
         */
        public void setDrawnValue(int drawnValue) {
            this.drawnValue = drawnValue;
        }

        /**
         * Update drawn value and counter
         *
         * @param value
         */
        public void updateValue(int value) {

            if (drawnValue == value) {
                count++;
            } else {
                count = 0;
                setDrawnValue(value);
            }
        }

    }
}

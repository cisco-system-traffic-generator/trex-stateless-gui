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
package com.exalttech.trex.ui.models;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.chart.XYChart;

/**
 * Chart series model
 *
 * @author Georgekh
 */
public class ChartSeries {

    XYChart.Series<Number, Number> latencySeries = new XYChart.Series<>();
    List<Number> latencyQueue = new ArrayList<>();
    BooleanProperty showSeriesProperty = new SimpleBooleanProperty();

    /**
     *
     * @param showSeries
     */
    public ChartSeries(boolean showSeries) {
        showSeriesProperty = new SimpleBooleanProperty(showSeries);
    }

    /**
     * Set chart series name
     *
     * @param name
     */
    public void setName(String name) {
        latencySeries.setName(name);
    }

    /**
     * Return series name
     *
     * @return
     */
    public String getName() {
        return latencySeries.getName();
    }

    /**
     * Return latency series
     *
     * @return
     */
    public XYChart.Series<Number, Number> getLatencySeries() {
        return latencySeries;
    }

    /**
     * Set latency series
     *
     * @param latencySeries
     */
    public void setLatencySeries(XYChart.Series<Number, Number> latencySeries) {
        this.latencySeries = latencySeries;
    }

    /**
     * Return latency queue
     *
     * @return
     */
    public List<Number> getLatencyQueue() {
        return latencyQueue;
    }

    /**
     * Set latency queue
     *
     * @param latencyQueue
     */
    public void setLatencyQueue(List<Number> latencyQueue) {
        this.latencyQueue = latencyQueue;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getShowSeriesProperty() {
        return showSeriesProperty;
    }

    /**
     *
     * @return
     */
    public boolean showSeries() {
        return showSeriesProperty.get();
    }
}

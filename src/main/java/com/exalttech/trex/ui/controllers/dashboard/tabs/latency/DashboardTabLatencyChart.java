package com.exalttech.trex.ui.controllers.dashboard.tabs.latency;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.exalttech.trex.ui.models.json.stats.latency.JSONStatsLatency;
import com.exalttech.trex.ui.models.json.stats.latency.JSONStatsStream;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.StatsUtils;
import com.exalttech.trex.util.Util;


public class DashboardTabLatencyChart extends AnchorPane {
    private static int historySize = 300;

    @FXML
    private AnchorPane root;
    @FXML
    private LineChart chart;
    @FXML
    private NumberAxis xAxis;

    private Map<String, ArrayHistory<Integer>> latencyHistory;
    private int interval;

    private RefreshingService refreshingService;
    Set<Integer> visiblePorts;

    public DashboardTabLatencyChart() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/latency/DashboardTabLatencyChart.fxml");

        latencyHistory = new HashMap<String, ArrayHistory<Integer>>();
        interval = 60;

        refreshingService = new RefreshingService();
        refreshingService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        refreshingService.setOnSucceeded(this::onRefreshSucceeded);
        refreshingService.start();

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    public void setInterval(int interval) {
        this.interval = interval;
        xAxis.setLowerBound(-interval);
    }

    public void setVisiblePorts(Set<Integer> visiblePorts) {
        this.visiblePorts = visiblePorts;
        renderChart();
    }

    private void onRefreshSucceeded(WorkerStateEvent event) {
        Map<String, String> latencyStatsByStreams = StatsLoader.getInstance().getLatencyStatsMap();

        Set<String> unvisitedStreams = new HashSet<String>(latencyHistory.keySet());
        latencyStatsByStreams.forEach((String stream, String jsonLatencyStats) -> {
            unvisitedStreams.remove(stream);

            JSONStatsStream latencyStats = (JSONStatsStream) Util.fromJSONString(
                    jsonLatencyStats,
                    JSONStatsStream.class
            );
            if (latencyStats == null) {
                return;
            }

            JSONStatsLatency latency = latencyStats.getLatency();
            if (latency == null) {
                return;
            }

            ArrayHistory<Integer> history = latencyHistory.get(stream);
            if (history == null) {
                history = new ArrayHistory<Integer>(historySize);
                latencyHistory.put(stream, history);
            }

            history.add(latency.getLast_max());
        });

        cleanHistory(unvisitedStreams);
        renderChart();
    }

    private void onWindowCloseRequest(WindowEvent window) {
        if (refreshingService.isRunning()) {
            refreshingService.cancel();
        }
    }

    private void cleanHistory(Set<String> unvisitedStreams) {
        unvisitedStreams.forEach((String stream) -> {
            latencyHistory.remove(stream);
        });
    }

    private void renderChart() {
        List<XYChart.Series> seriesList = new LinkedList<XYChart.Series>();
        Set<String> visibleStreams = StatsUtils.getVisibleStream(visiblePorts);
        latencyHistory.forEach((String stream, ArrayHistory<Integer> history) -> {
            if (visibleStreams != null && !visibleStreams.contains(stream)) {
                return;
            }
            XYChart.Series series = new XYChart.Series();
            series.setName(stream);
            AtomicInteger x = new AtomicInteger(-Math.min(historySize, history.size()) + 1);
            history.forEach((Integer value) -> {
                series.getData().add(new XYChart.Data<Number, Number>(x.getAndIncrement(), value));
            });
            seriesList.add(series);
        });
        chart.getData().clear();
        chart.getData().addAll(seriesList);
    }
}

package com.exalttech.trex.ui.controllers.dashboard.tabs.streams;

import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.StatsUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.exalttech.trex.ui.models.json.stats.streams.JSONFlowStatsStream;
import com.exalttech.trex.ui.models.json.stats.streams.JSONFlowStatsTimeStamp;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.cells.CellType;
import com.exalttech.trex.ui.views.statistics.cells.HeaderCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class DashboardTabStreams extends AnchorPane {
    private static class FlowStatsData {
        private Map<Integer, Long> txPkts;
        private Map<Integer, Long> txBytes;
        private Map<Integer, Long> rxPkts;
        private Map<Integer, Long> rxBytes;
        private double time;

        public FlowStatsData(
                Map<Integer, Long> txPkts,
                Map<Integer, Long> txBytes,
                Map<Integer, Long> rxPkts,
                Map<Integer, Long> rxBytes,
                double time
        ) {
            this.txPkts = txPkts;
            this.txBytes = txBytes;
            this.rxPkts = rxPkts;
            this.rxBytes = rxBytes;
            this.time = time;
        }

        public long calcTotalTxPkts(Set<Integer> visiblePorts) {
            return calcTotal(txPkts, visiblePorts);
        }

        public long calcTotalTxBytes(Set<Integer> visiblePorts) {
            return calcTotal(txBytes, visiblePorts);
        }

        public long calcTotalRxPkts(Set<Integer> visiblePorts) {
            return calcTotal(rxPkts, visiblePorts);
        }

        public long calcTotalRxBytes(Set<Integer> visiblePorts) {
            return calcTotal(rxBytes, visiblePorts);
        }

        public double getTime() { return time; }

        public double calcTxPps(FlowStatsData prev, Set<Integer> visiblePorts) {
            long totalCurr = calcTotalTxPkts(visiblePorts);
            long totalPrev = prev.calcTotalTxPkts(visiblePorts);
            return round((totalCurr - totalPrev)/(time - prev.time));
        }

        public Double calcTxBpsL2(FlowStatsData prev, Set<Integer> visiblePorts) {
            long totalCurr = calcTotalTxBytes(visiblePorts);
            long totalPrev = prev.calcTotalTxBytes(visiblePorts);
            return round((totalCurr - totalPrev)/(time - prev.time));
        }

        public Double calcTxBpsL1(FlowStatsData prev, Set<Integer> visiblePorts) {
            Double bps = calcTxBpsL2(prev, visiblePorts);
            Double pps = calcTxPps(prev, visiblePorts);
            Double factor = bps*pps;
            return round(bps*(1 + (20/factor)));
        }

        public Double calcRxPps(FlowStatsData prev, Set<Integer> visiblePorts) {
            long totalCurr = calcTotalRxPkts(visiblePorts);
            long totalPrev = prev.calcTotalRxPkts(visiblePorts);
            return round((totalCurr - totalPrev)/(time - prev.time));
        }

        public Double calcRxBps(FlowStatsData prev, Set<Integer> visiblePorts) {
            long totalCurr = calcTotalRxBytes(visiblePorts);
            long totalPrev = prev.calcTotalRxBytes(visiblePorts);
            return round((totalCurr - totalPrev)/(time - prev.time));
        }

        private static long calcTotal(Map<Integer, Long> data, Set<Integer> visiblePorts) {
            AtomicLong total = new AtomicLong(0);
            data.forEach((Integer port, Long value) -> {
                if (visiblePorts == null || visiblePorts.contains(port)) {
                    total.getAndAdd(value);
                }
            });
            return total.get();
        }

        private static double round(double value) { return ((int)(value*100.0))/100.0; }
    }

    private static final List<String> statisticTypes = new ArrayList<String>() {{
        add("Tx (pkt/s)");
        add("Tx (B/s) L2");
        add("Tx (B/s) L1");
        add("Rx (pkt/s)");
        add("Rx (B/s)");
        add("Tx (pkt)");
        add("Rx (pkt)");
        add("Tx (B)");
        add("Rx (B)");
    }};
    private static final Integer historySize = 300;
    private static final List<String> streamsCountValues = new ArrayList<String>() {{
        add("5");
        add("10");
        add("15");
        add("20");
    }};
    private static final List<String> latencyIntervals = new ArrayList<String>() {{
        add("60");
        add("90");
        add("120");
        add("300");
    }};

    @FXML
    private AnchorPane root;
    @FXML
    private LineChart chart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ComboBox statisticsComboBox;
    @FXML
    private ComboBox streamsCountComboBox;
    @FXML
    private ComboBox intervalComboBox;
    @FXML
    private GridPane table;

    private RefreshingService refreshingService;
    private String selectedStatistics;
    private Map<String, List<FlowStatsData>> streamsHistory;
    private Integer maxStreamsCount;
    private Set<Integer> visiblePorts;

    public DashboardTabStreams() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/streams/DashboardTabStreams.fxml");

        streamsHistory = new HashMap<>();
        refreshingService = new RefreshingService();
        refreshingService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        refreshingService.setOnSucceeded(this::onRefreshSucceeded);
        refreshingService.start();

        statisticsComboBox.getItems().addAll(FXCollections.observableArrayList(statisticTypes));
        statisticsComboBox.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                selectedStatistics = newValue;
                yAxis.setLabel(newValue);
            }
        });
        statisticsComboBox.setValue("Tx (pkt/s)");

        streamsCountComboBox.getItems().addAll(FXCollections.observableArrayList(streamsCountValues));
        streamsCountComboBox.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                if(!newValue.matches("[0-9]*")){
                    streamsCountComboBox.setValue(oldValue);
                }
                maxStreamsCount = Integer.parseInt(newValue);
                cleanHistory(null);
                render();
            }
        });
        streamsCountComboBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && ((String) streamsCountComboBox.getValue()).isEmpty()) {
                    streamsCountComboBox.setValue("5");
                }
            }
        });
        streamsCountComboBox.setValue("10");

        initializeIntervalComboBox();

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    public void setVisiblePorts(Set<Integer> visiblePorts) {
        this.visiblePorts = visiblePorts;
        render();
    }

    private void initializeIntervalComboBox() {
        intervalComboBox.getItems().addAll(FXCollections.observableArrayList(latencyIntervals));
        intervalComboBox.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                xAxis.setLowerBound(-Integer.parseInt(newValue));
            }
        });
        intervalComboBox.setValue("60");
    }

    private void onRefreshSucceeded(WorkerStateEvent event) {
        Map<String, String> flowStats = StatsLoader.getInstance().getLoadedFlowStatsMap();
        JSONFlowStatsTimeStamp timeStamp = (JSONFlowStatsTimeStamp) Util.fromJSONString(
                flowStats.get("ts"),
                JSONFlowStatsTimeStamp.class
        );

        Double time = timeStamp.getValue()*1.0/timeStamp.getFreq();

        Set<String> visitedStreams = new HashSet<>();
        flowStats.forEach((String key, String value) -> {
            visitedStreams.add(key);

            if (key.equals("ts") || Util.isNullOrEmpty(value) || value.equals("{}")) {
                return;
            }

            JSONFlowStatsStream rawData = (JSONFlowStatsStream) Util.fromJSONString(value, JSONFlowStatsStream.class);
            if (rawData == null) {
                return;
            }

            List<FlowStatsData> streamHistory = streamsHistory.get(key);
            if (streamHistory == null) {
                if (streamsHistory.keySet().size() >= maxStreamsCount) {
                    return;
                }
                streamHistory = new ArrayList<FlowStatsData>();
                streamsHistory.put(key, streamHistory);
            } else if (!streamHistory.isEmpty() && streamHistory.get(streamHistory.size() - 1).getTime() == time) {
                return;
            }

            FlowStatsData data = new FlowStatsData(
                rawData.getTx_pkts(),
                rawData.getTx_bytes(),
                rawData.getRx_pkts(),
                rawData.getRx_bytes(),
                time
            );

            streamHistory.add(data);
        });

        cleanHistory(visitedStreams);

        render(time);
    }

    private void onWindowCloseRequest(WindowEvent window) {
        if (refreshingService.isRunning()) {
            refreshingService.cancel();
        }
    }

    private void cleanHistory(Set<String> visitedStreams) {
        String[] streams = new String[streamsHistory.keySet().size()];
        streamsHistory.keySet().toArray(streams);
        for (String stream : streams) {
            if (visitedStreams != null && !visitedStreams.contains(stream)) {
                streamsHistory.remove(stream);
                continue;
            }

            List<FlowStatsData> streamHistory = streamsHistory.get(stream);
            int size = streamHistory.size();
            int skippedIndexes = Math.max(0, streamHistory.size() - historySize);
            if (skippedIndexes == 0) {
                continue;
            }
            streamsHistory.put(stream, streamHistory.subList(skippedIndexes, size));
        }
        while (streamsHistory.keySet().size() > maxStreamsCount) {
            streamsHistory.keySet().toArray(streams);
            for (int i = maxStreamsCount; i < streams.length; ++i) {
                streamsHistory.remove(streams[i]);
            }
        }
    }

    private void render() {
        Set<String> visibleStreams = StatsUtils.getVisibleStream(visiblePorts);
        renderChart(visibleStreams);
        renderTable(visibleStreams);
    }

    private void renderChart(Set<String> visibleStreams) {
        String[] streams = new String[streamsHistory.keySet().size()];
        streamsHistory.keySet().toArray();
        Double maxTime = 0.0;
        for (String stream : streams) {
            List<FlowStatsData> streamHistory = streamsHistory.get(stream);
            if (streamHistory.isEmpty()) {
                continue;
            }
            Double currMaxTime = streamHistory.get(streamHistory.size() - 1).getTime();
            if (currMaxTime > maxTime) {
                maxTime = currMaxTime;
            }
        }

        renderChart(visibleStreams, maxTime);
    }

    private void render(Double time) {
        Set<String> visibleStreams = StatsUtils.getVisibleStream(visiblePorts);
        renderChart(visibleStreams, time);
        renderTable(visibleStreams);
    }

    private void renderChart(Set<String> visibleStreams, Double time) {
        chart.getData().clear();

        switch (selectedStatistics) {
            case "Tx (pkt)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    if (visibleStreams != null && !visibleStreams.contains(key)) {
                        return;
                    }
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxPktsStreamChartDataList(data, time))));
                });
                break;
            case "Tx (B)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    if (visibleStreams != null && !visibleStreams.contains(key)) {
                        return;
                    }
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxBytesStreamChartDataList(data, time))));
                });
                break;
            case "Rx (pkt)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    if (visibleStreams != null && !visibleStreams.contains(key)) {
                        return;
                    }
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getRxPktsStreamChartDataList(data, time))));
                });
                break;
            case "Rx (B)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    if (visibleStreams != null && !visibleStreams.contains(key)) {
                        return;
                    }
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getRxBytesStreamChartDataList(data, time))));
                });
                break;
            case "Tx (pkt/s)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    if (visibleStreams != null && !visibleStreams.contains(key)) {
                        return;
                    }
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxPpsStreamChartDataList(data, time))));
                });
                break;
            case "Rx (pkt/s)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    if (visibleStreams != null && !visibleStreams.contains(key)) {
                        return;
                    }
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getRxPpsStreamChartDataList(data, time))));
                });
                break;
            case "Tx (B/s) L1":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    if (visibleStreams != null && !visibleStreams.contains(key)) {
                        return;
                    }
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxBpsL1StreamChartDataList(data, time))));
                });
                break;
            case "Tx (B/s) L2":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    if (visibleStreams != null && !visibleStreams.contains(key)) {
                        return;
                    }
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxBpsL2StreamChartDataList(data, time))));
                });
                break;
            case "Rx (B/s)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    if (visibleStreams != null && !visibleStreams.contains(key)) {
                        return;
                    }
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getRxBpsStreamChartDataList(data, time))));
                });
                break;
        }
    }

    private void renderTable(Set<String> visibleStreams) {
        int firstColumnWidth = 99;
        int secondHeaderWidth = 128;

        table.getChildren().clear();

        table.add(new HeaderCell(firstColumnWidth, "PG ID"), 0, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (pkt/s)"), 1, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (B/s) L2"), 2, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (B/s) L1"), 3, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Rx (pkt/s)"), 4, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Rx (B/s)"), 5, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (pkt)"), 6, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Rx (pkt)"), 7, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (B)"), 8, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Rx (B)"), 9, 0);

        AtomicInteger rowIndex = new AtomicInteger(1);
        AtomicBoolean odd = new AtomicBoolean(false);

        streamsHistory.forEach((String stream, List<FlowStatsData> data) -> {
            if ((visibleStreams != null && !visibleStreams.contains(stream)) || data.size() < 2) {
                return;
            }

            FlowStatsData prev = data.get(data.size() - 2);
            FlowStatsData last = data.get(data.size() - 1);

            table.add(new StatisticLabelCell(stream, firstColumnWidth, odd.get(), CellType.DEFAULT_CELL, true), 0, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTxPps(prev, visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 1, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTxBpsL2(prev, visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 2, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTxBpsL1(prev, visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 3, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcRxPps(prev, visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 4, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcRxBps(prev, visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 5, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalTxPkts(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 6, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalRxPkts(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 7, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalTxBytes(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 8, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalRxBytes(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 9, rowIndex.get());

            rowIndex.addAndGet(1);
            odd.getAndSet(!odd.get());
        });
    }

    private List<XYChart.Data<Number, Number>> getTxPktsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.isEmpty()) {
            return res;
        }

        dataList.forEach((FlowStatsData data) -> {
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTotalTxPkts(visiblePorts)));
        });
        return res;
    }

    private List<XYChart.Data<Number, Number>> getTxBytesStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.isEmpty()) {
            return res;
        }

        dataList.forEach((FlowStatsData data) -> {
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTotalTxBytes(visiblePorts)));
        });
        return res;
    }

    private List<XYChart.Data<Number, Number>> getRxPktsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.isEmpty()) {
            return res;
        }

        dataList.forEach((FlowStatsData data) -> {
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTotalRxPkts(visiblePorts)));
        });
        return res;
    }

    private List<XYChart.Data<Number, Number>> getRxBytesStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.isEmpty()) {
            return res;
        }

        dataList.forEach((FlowStatsData data) -> {
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTotalRxBytes(visiblePorts)));
        });
        return res;
    }

    private List<XYChart.Data<Number, Number>> getTxPpsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTxPps(prev, visiblePorts)));
            prev = data;
        }

        return res;
    }

    private List<XYChart.Data<Number, Number>> getRxPpsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcRxPps(prev, visiblePorts)));
            prev = data;
        }

        return res;
    }

    private List<XYChart.Data<Number, Number>> getTxBpsL1StreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTxBpsL1(prev, visiblePorts)));
            prev = data;
        }

        return res;
    }

    private List<XYChart.Data<Number, Number>> getTxBpsL2StreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTxBpsL2(prev, visiblePorts)));
            prev = data;
        }

        return res;
    }

    private List<XYChart.Data<Number, Number>> getRxBpsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcRxBps(prev, visiblePorts)));
            prev = data;
        }

        return res;
    }
}

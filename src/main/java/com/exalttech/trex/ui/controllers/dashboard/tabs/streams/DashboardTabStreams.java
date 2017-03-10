package com.exalttech.trex.ui.controllers.dashboard.tabs.streams;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        private Integer txPkts;
        private Integer txBytes;
        private Integer rxPkts;
        private Integer rxBytes;
        private Double time;

        private static Double round(Double value) {
            return ((int)(value*100.0))/100.0;
        }

        public void setTxPkts(Integer txPkts) { this.txPkts = txPkts; }
        public Integer getTxPkts() { return txPkts; }

        public void setTxBytes(Integer txBytes) { this.txBytes = txBytes; }
        public Integer getTxBytes() { return txBytes; }

        public void setRxPkts(Integer rxPkts) { this.rxPkts = rxPkts; }
        public Integer getRxPkts() { return rxPkts; }

        public void setRxBytes(Integer rxBytes) { this.rxBytes = rxBytes; }
        public Integer getRxBytes() { return rxBytes; }

        public void setTime(Double time) { this.time = time; }
        public Double getTime() { return time; }

        public Double calcTxPps() {
            return round(txPkts/time);
        }
        public Double calcTxPps(FlowStatsData prevData) {
            return round((txPkts - prevData.txPkts)/(time - prevData.time));
        }

        public Double calcTxBpsL2() {
            return round(txBytes/time);
        }
        public Double calcTxBpsL2(FlowStatsData prevData) {
            return round((txBytes - prevData.txBytes)/(time - prevData.time));
        }

        public Double calcTxBpsL1() {
            Double bps = txBytes/time;
            Double pps = txPkts/time;
            Double factor = bps*pps;
            return round(bps*(1 + (20/factor)));
        }
        public Double calcTxBpsL1(FlowStatsData prevData) {
            Double bps = (txBytes - prevData.txBytes)/(time - prevData.time);
            Double pps = (txPkts - prevData.txPkts)/(time - prevData.time);
            Double factor = bps*pps;
            return round(bps*(1 + (20/factor)));
        }

        public Double calcRxPps() {
            return round(rxPkts/time);
        }
        public Double calcRxPps(FlowStatsData prevData) {
            return round((rxPkts - prevData.rxPkts)/(time - prevData.time));
        }

        public Double calcRxBps() {
            return round(rxBytes/time);
        }
        public Double calcRxBps(FlowStatsData prevData) {
            return round((rxBytes - prevData.rxBytes)/(time - prevData.time));
        }
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
    private static final Integer historySize = 60;
    private static final List<String> streamsCountValues = new ArrayList<String>() {{
        add("5");
        add("10");
        add("15");
        add("20");
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
    private Spinner intervalSpinner;
    @FXML
    private GridPane table;

    private RefreshingService refreshingService;
    private String selectedStatistics;
    private Map<String, List<FlowStatsData>> streamsHistory;
    private Integer maxStreamsCount;
    private Integer chartInterval;
    private Double chartRange;

    public DashboardTabStreams() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/streams/DashboardTabStreams.fxml");

        chartInterval = 1;
        chartRange = chartInterval*(historySize - 10)*1.0;
        streamsHistory = new HashMap<>();
        refreshingService = new RefreshingService();
        refreshingService.setPeriod(Duration.seconds(chartInterval));
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
                renderChart();
                renderTable();
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

        xAxis.setLowerBound(-chartRange);

        intervalSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, chartInterval)
        );
        intervalSpinner.getEditor().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("[0-9]*")){
                    intervalSpinner.getEditor().setText(oldValue);
                } else {
                    Integer value = Integer.parseInt(intervalSpinner.getEditor().getText());
                    if (value > 100) {
                        intervalSpinner.getEditor().setText("100");
                    }
                }
                Integer value = Integer.parseInt(intervalSpinner.getEditor().getText());
                if (!value.equals(chartInterval)) {
                    chartInterval = value;
                    chartRange = chartInterval*(historySize - 10)*1.0;
                    xAxis.setLowerBound(-chartRange);
                    refreshingService.setPeriod(Duration.seconds(chartInterval));
                }
            }
        });
        intervalSpinner.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && intervalSpinner.getEditor().getText().isEmpty()) {
                    intervalSpinner.getEditor().setText("1");
                }
            }
        });

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
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
            } else if (!streamHistory.isEmpty() && streamHistory.get(streamHistory.size() - 1).getTime().equals(time)) {
                return;
            }

            FlowStatsData data = new FlowStatsData();
            data.setTxPkts(calcTotalValue(rawData.getTx_pkts()));
            data.setTxBytes(calcTotalValue(rawData.getTx_bytes()));
            data.setRxPkts(calcTotalValue(rawData.getRx_pkts()));
            data.setRxBytes(calcTotalValue(rawData.getRx_bytes()));
            data.setTime(time);

            streamHistory.add(data);
        });

        cleanHistory(visitedStreams);

        renderChart(time);
        renderTable();
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

    private void renderChart() {
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

        renderChart(maxTime);
    }

    private void renderChart(Double time) {
        chart.getData().clear();

        switch (selectedStatistics) {
            case "Tx (pkt)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxPktsStreamChartDataList(data, time))));
                });
                break;
            case "Tx (B)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxBytesStreamChartDataList(data, time))));
                });
                break;
            case "Rx (pkt)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getRxPktsStreamChartDataList(data, time))));
                });
                break;
            case "Rx (B)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getRxBytesStreamChartDataList(data, time))));
                });
                break;
            case "Tx (pkt/s)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxPpsStreamChartDataList(data, time))));
                });
                break;
            case "Rx (pkt/s)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getRxPpsStreamChartDataList(data, time))));
                });
                break;
            case "Tx (B/s) L1":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxBpsL1StreamChartDataList(data, time))));
                });
                break;
            case "Tx (B/s) L2":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getTxBpsL2StreamChartDataList(data, time))));
                });
                break;
            case "Rx (B/s)":
                streamsHistory.forEach((String key, List<FlowStatsData> data) -> {
                    chart.getData().add(new XYChart.Series<>(key, FXCollections.observableArrayList(getRxBpsStreamChartDataList(data, time))));
                });
                break;
        }
    }

    private void renderTable() {
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
            if (data.size() < 2) {
                return;
            }

            FlowStatsData prev = data.get(data.size() - 2);
            FlowStatsData last = data.get(data.size() - 1);

            table.add(new StatisticLabelCell(stream, firstColumnWidth, odd.get(), CellType.DEFAULT_CELL, true), 0, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTxPps(prev)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 1, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTxBpsL2(prev)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 2, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTxBpsL1(prev)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 3, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcRxPps(prev)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 4, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcRxBps(prev)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 5, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.getTxPkts()), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 6, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.getRxPkts()), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 7, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.getTxBytes()), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 8, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.getRxBytes()), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 9, rowIndex.get());

            rowIndex.addAndGet(1);
            odd.getAndSet(!odd.get());
        });
    }

    private static List<XYChart.Data<Number, Number>> getTxPktsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.isEmpty()) {
            return res;
        }

        dataList.forEach((FlowStatsData data) -> {
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.getTxPkts()));
        });
        return res;
    }

    private static List<XYChart.Data<Number, Number>> getTxBytesStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.isEmpty()) {
            return res;
        }

        dataList.forEach((FlowStatsData data) -> {
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.getTxBytes()));
        });
        return res;
    }

    private static List<XYChart.Data<Number, Number>> getRxPktsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.isEmpty()) {
            return res;
        }

        dataList.forEach((FlowStatsData data) -> {
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.getRxPkts()));
        });
        return res;
    }

    private static List<XYChart.Data<Number, Number>> getRxBytesStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.isEmpty()) {
            return res;
        }

        dataList.forEach((FlowStatsData data) -> {
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.getRxBytes()));
        });
        return res;
    }

    private static List<XYChart.Data<Number, Number>> getTxPpsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTxPps(prev)));
            prev = data;
        }

        return res;
    }

    private static List<XYChart.Data<Number, Number>> getRxPpsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcRxPps(prev)));
            prev = data;
        }

        return res;
    }

    private static List<XYChart.Data<Number, Number>> getTxBpsL1StreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTxBpsL1(prev)));
            prev = data;
        }

        return res;
    }

    private static List<XYChart.Data<Number, Number>> getTxBpsL2StreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcTxBpsL2(prev)));
            prev = data;
        }

        return res;
    }

    private static List<XYChart.Data<Number, Number>> getRxBpsStreamChartDataList(List<FlowStatsData> dataList, Double currTime) {
        List<XYChart.Data<Number, Number>> res = new ArrayList<>();
        if (dataList.size() < 2) {
            return res;
        }

        FlowStatsData prev = dataList.get(0);
        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcRxBps(prev)));
            prev = data;
        }

        return res;
    }

    private static Integer calcTotalValue(Map<Integer, Integer> valuesByPorts) {
        if (valuesByPorts == null) {
            return 0;
        }
        Integer total = 0;
        for (Integer value : valuesByPorts.values()) {
            total += value;
        }
        return total;
    }
}

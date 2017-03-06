package com.exalttech.trex.ui.views;

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
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.*;

import org.apache.log4j.Logger;

import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.RawFlowStatsData;
import com.exalttech.trex.ui.models.RawFlowStatsTimeStamp;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class DashboardTabStreams extends AnchorPane {
    private static class FlowStatsData {
        private Integer txPkts;
        private Integer txBytes;
        private Integer rxPkts;
        private Integer rxBytes;
        private Double time;

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
            return txPkts/time;
        }
        public Double calcTxPps(FlowStatsData prevData) {
            return (txPkts - prevData.txPkts)/(time - prevData.time);
        }

        public Double calcTxBpsL2() {
            return txBytes/time;
        }
        public Double calcTxBpsL2(FlowStatsData prevData) {
            return (txBytes - prevData.txBytes)/(time - prevData.time);
        }

        public Double calcTxBpsL1() {
            return calcTxBpsL2()/time + calcTxPps()*16;
        }
        public Double calcTxBpsL1(FlowStatsData prevData) {
            return calcTxBpsL2(prevData)/time + calcTxPps(prevData)*16;
        }

        public Double calcRxPps() {
            return rxPkts/time;
        }
        public Double calcRxPps(FlowStatsData prevData) {
            return (rxPkts - prevData.rxPkts)/(time - prevData.time);
        }

        public Double calcRxBps() {
            return rxBytes/time;
        }
        public Double calcRxBps(FlowStatsData prevData) {
            return (rxBytes - prevData.rxBytes)/(time - prevData.time);
        }
    }

    private static final Logger LOG = Logger.getLogger(MainViewController.class.getName());
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

    private RefreshingService refreshingService;
    private String selectedStatistics;
    private Map<String, List<FlowStatsData>> streamsHistory;
    private Integer maxStreamsCount;
    private Integer chartInterval;
    private Double chartRange;

    public DashboardTabStreams() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/DashboardTabStreams.fxml");

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
        RawFlowStatsTimeStamp timeStamp = (RawFlowStatsTimeStamp) Util.fromJSONString(
                flowStats.get("ts"),
                RawFlowStatsTimeStamp.class
        );

        Double time = timeStamp.getValue()*1.0/timeStamp.getFreq();

        Set<String> visitedStreams = new HashSet<>();
        flowStats.forEach((String key, String value) -> {
            visitedStreams.add(key);

            if (key.equals("ts") || Util.isNullOrEmpty(value) || value.equals("{}")) {
                return;
            }

            RawFlowStatsData rawData = (RawFlowStatsData) Util.fromJSONString(value, RawFlowStatsData.class);
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
            data.setTxPkts(getTxPktsValue(rawData));
            data.setTxBytes(getTxBytesValue(rawData));
            data.setRxPkts(getRxPktsValue(rawData));
            data.setRxBytes(getRxBytesValue(rawData));
            data.setTime(time);

            streamHistory.add(data);
        });

        cleanHistory(visitedStreams);

        renderChart(time);
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
        String[] streams = (String[]) streamsHistory.keySet().toArray();
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
        res.add(new XYChart.Data<>(prev.getTime(), prev.calcTxPps()));

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
        res.add(new XYChart.Data<>(prev.getTime(), prev.calcRxPps()));

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
        res.add(new XYChart.Data<>(prev.getTime(), prev.calcTxBpsL1()));

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
        res.add(new XYChart.Data<>(prev.getTime(), prev.calcTxBpsL2()));

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
        res.add(new XYChart.Data<>(prev.getTime(), prev.calcRxBps()));

        int size = dataList.size();
        for (int i = 1; i < size; ++i) {
            FlowStatsData data = dataList.get(i);
            res.add(new XYChart.Data<>(data.getTime() - currTime, data.calcRxBps(prev)));
            prev = data;
        }

        return res;
    }

    private static Integer getTxPktsValue(RawFlowStatsData rawData) {
        Map<Integer, Integer> txPkts = rawData.getTx_pkts();
        if (txPkts == null) {
            return 0;
        }
        if (txPkts.size() != 1) {
            LOG.error(
                    String.format("Invalid tx_pkts flow data value: expected 1 port but got %d", txPkts.size())
            );
            return 0;
        }
        return txPkts.get(0);
    }

    private static Integer getTxBytesValue(RawFlowStatsData rawData) {
        Map<Integer, Integer> txBytes = rawData.getTx_bytes();
        if (txBytes == null) {
            return 0;
        }
        if (txBytes.size() != 1) {
            LOG.error(
                    String.format("Invalid tx_bytes flow data value: expected 1 port but got %d", txBytes.size())
            );
            return 0;
        }
        return txBytes.get(0);
    }

    private static Integer getRxPktsValue(RawFlowStatsData rawData) {
        Map<Integer, Integer> rxPkts = rawData.getRx_pkts();
        if (rxPkts == null) {
            return 0;
        }
        if (rxPkts.size() != 1) {
            LOG.error(
                    String.format("Invalid rx_pkts flow data value: expected 1 port but got %d", rxPkts.size())
            );
            return 0;
        }
        return rxPkts.get(0);
    }

    private static Integer getRxBytesValue(RawFlowStatsData rawData) {
        Map<Integer, Integer> rxBytes = rawData.getRx_bytes();
        if (rxBytes == null) {
            return 0;
        }
        if (rxBytes.size() != 1) {
            LOG.error(
                    String.format("Invalid rx_bytes flow data value: expected 1 port but got %d", rxBytes.size())
            );
            return 0;
        }
        return rxBytes.get(0);
    }
}

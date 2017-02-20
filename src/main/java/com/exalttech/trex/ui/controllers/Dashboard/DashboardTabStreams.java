package com.exalttech.trex.ui.controllers.Dashboard;

import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.ui.views.statistics.cells.*;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Injector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Generated;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DashboardTabStreams extends BorderPane {

    @FXML HBox borderPaneStreamStatsTopHbox;
    @FXML HBox borderPaneStreamStatsCenterHbox;
    @FXML HBox borderPaneStreamStatsBottomHbox;
    @FXML GridPane borderPaneStreamStatsGridPane;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MainViewController.class.getName());

    private RPCMethods serverRPCMethods;
    private PortsManager portManager;

    Stage currentStage;
    RefreshingService readingStatService = new RefreshingService();
    List<Integer> streamList = new ArrayList<Integer>();
    StatsTableGenerator statsTableGenerator;
    Map<String, String> flowStatsListPrev = new HashMap<>();
    Map<String, String> flowStatsListLast = new HashMap<>();

    public DashboardTabStreams(Injector injector, RPCMethods serverRPCMethods, Stage stage) {
        this.serverRPCMethods = serverRPCMethods;
        this.portManager = PortsManager.getInstance();
        this.currentStage = stage;

        FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);
        fxmlLoader.setLocation(getClass().getResource("/fxml/Dashboard/DashboardStreams.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception e) {
            LOG.error("Failed to load fxml file: " + e.getMessage());
        }

        initializze();
    }

    /**
     *
     */
    //@Override
    public void initializze() {
        statsTableGenerator = new StatsTableGenerator();
        borderPaneStreamStatsGridPane.getStyleClass().add("statsTable");
        initializeReadingStats();
        initCloseAndSize();
    }

    /**
     * Initialize reading stats thread
     */
    private void initializeReadingStats() {
        readingStatService = new RefreshingService();
        readingStatService.setPeriod(Duration.seconds(Constants.REFRESH_FIFTEEN_INTERVAL_SECONDS));
        readingStatService.setOnSucceeded((WorkerStateEvent event) -> {
            try {
                //String response = serverRPCMethods.getSupportedCmds();
                //String response = serverRPCMethods.getStreamList(0);
                //JSONObject jsonObject = new JSONObject(response);
                generateFlowStatsPane(streamList);
            } catch (Exception e) {
                LOG.error("Failed to get stream list: " + e.getMessage());
            }

            /* Not working now *//*
            try {
                String response2 = serverRPCMethods.getStream(0, 0);
            } catch (Exception e) {
                LOG.error("Failed to get stream: " + e.getMessage());
            }
            try {
                String response3 = serverRPCMethods.getStreamStats(0, 0);
            } catch (Exception e) {
                LOG.error("Failed to get stream stats: " + e.getMessage());
            }*/
        });
        readingStatService.start();
    }

    /**
     * Init stage
     */
    public void initCloseAndSize() {
        currentStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (window) -> {
            if (readingStatService.isRunning()) {
                readingStatService.cancel();
            }
            Util.optimizeMemory();
        });

        // add size listener
        currentStage.getScene().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ;
            }
        });

        // add size listener
        currentStage.getScene().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ;
            }
        });
    }


    /**
     * Build global statistic pane
     *
     * @return
     */
    public void generateFlowStatsPane(List<Integer> streamList) {
        ObservableList<Node> children = borderPaneStreamStatsTopHbox.getChildren();
        children.clear();
        children.add(0, generateUtilizationProcChart());
        //children.add(1, statsTableGenerator.generateGlobalStatPane());

        int first_column_width = 99;
        int second_header_width = 99;

        borderPaneStreamStatsGridPane.add(new HeaderCell(first_column_width, " "), 0, 0);

        flowStatsListPrev = StatsLoader.getInstance().getPreviousFlowStatsMap();
        flowStatsListLast = StatsLoader.getInstance().getLoadedFlowStatsMap();
        FlowStatsTimeStamp ts_prev = (FlowStatsTimeStamp) Util.fromJSONString(flowStatsListPrev.get("ts"), FlowStatsTimeStamp.class);
        FlowStatsTimeStamp ts_last = (FlowStatsTimeStamp) Util.fromJSONString(flowStatsListLast.get("ts"), FlowStatsTimeStamp.class);

        HashMap<String, FlowStatsData> streams = new HashMap<>();
        HashSet<Integer> ports = new HashSet<>();
        flowStatsListLast.forEach((k,v) -> {
            if (!k.equals("ts")) {
                String stream_id = k;
                FlowStatsData stream_data = (FlowStatsData) Util.fromJSONString(v, FlowStatsData.class);
                streams.put(k, stream_data);
                stream_data.getTx_pkts().forEach((pk,pv) -> ports.add(pk));
                stream_data.getRx_pkts().forEach((pk,pv) -> ports.add(pk));
                stream_data.getTx_bytes().forEach((pk,pv) -> ports.add(pk));
                stream_data.getRx_bytes().forEach((pk,pv) -> ports.add(pk));
            }
        });

        int colIndex = 1;
        borderPaneStreamStatsGridPane.add(new HeaderCell(second_header_width, "Tx pps"), colIndex++, 0);
        borderPaneStreamStatsGridPane.add(new HeaderCell(second_header_width, "Tx bps L2"), colIndex++, 0);
        borderPaneStreamStatsGridPane.add(new HeaderCell(second_header_width, "Tx bps L1"), colIndex++, 0);

        borderPaneStreamStatsGridPane.add(new HeaderCell(second_header_width, "Rx pps"), colIndex++, 0);
        borderPaneStreamStatsGridPane.add(new HeaderCell(second_header_width, "Rx bps"), colIndex++, 0);

        borderPaneStreamStatsGridPane.add(new HeaderCell(second_header_width, "tx_pkts"), colIndex++, 0);
        borderPaneStreamStatsGridPane.add(new HeaderCell(second_header_width, "rx_pkts"), colIndex++, 0);
        borderPaneStreamStatsGridPane.add(new HeaderCell(second_header_width, "tx_bytes"), colIndex++, 0);
        borderPaneStreamStatsGridPane.add(new HeaderCell(second_header_width, "rx_bytes"), colIndex++, 0);

        AtomicInteger rowIndex = new AtomicInteger(1);
        AtomicBoolean odd = new AtomicBoolean(false);
        streams.forEach((k,v) -> {
            borderPaneStreamStatsGridPane.add(new StatisticLabelCell("Stream " + k, first_column_width, odd.get(), CellType.DEFAULT_CELL, false), 0, rowIndex.get());

            AtomicInteger tx_pkts = new AtomicInteger(0);
            AtomicInteger rx_pkts = new AtomicInteger(0);
            AtomicInteger tx_bytes = new AtomicInteger(0);
            AtomicInteger rx_bytes = new AtomicInteger(0);
            v.getTx_pkts().forEach((k2,v2) -> {
                tx_pkts.addAndGet(v2);
            });
            v.getTx_bytes().forEach((k2,v2) -> {
                tx_bytes.addAndGet(v2);
            });
            v.getRx_pkts().forEach((k2,v2) -> {
                rx_pkts.addAndGet(v2);
            });
            v.getRx_bytes().forEach((k2,v2) -> {
                rx_bytes.addAndGet(v2);
            });
            borderPaneStreamStatsGridPane.add(new StatisticLabelCell(tx_pkts.toString(),  second_header_width, odd.get(), CellType.DEFAULT_CELL, true), 6, rowIndex.get());
            borderPaneStreamStatsGridPane.add(new StatisticLabelCell(rx_pkts.toString(),  second_header_width, odd.get(), CellType.DEFAULT_CELL, true), 7, rowIndex.get());
            borderPaneStreamStatsGridPane.add(new StatisticLabelCell(tx_bytes.toString(), second_header_width, odd.get(), CellType.DEFAULT_CELL, true), 8, rowIndex.get());
            borderPaneStreamStatsGridPane.add(new StatisticLabelCell(rx_bytes.toString(), second_header_width, odd.get(), CellType.DEFAULT_CELL, true), 9, rowIndex.get());

            rowIndex.addAndGet(1);
            odd.getAndSet(!odd.get());
        });

        return;
    }


    public LineChart generateUtilizationProcChart() {
        Axis x = new NumberAxis();
        Axis y = new NumberAxis(0, 100, 10);
        LineChart lineChart = new LineChart(x, y);

        return lineChart;
    }

    static class FlowStatsDataJsonDeserializer extends JsonDeserializer<DashboardTabStreams.FlowStatsData> {

        @Override
        public DashboardTabStreams.FlowStatsData deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);

            JsonNode data = node.get("tx_pkts");
            HashMap<Integer,Integer> tx_pkts = getDataFromJSONNode(data);

            data = node.get("tx_bytes");
            HashMap<Integer,Integer> tx_bytes = getDataFromJSONNode(data);

            data = node.get("rx_pkts");
            HashMap<Integer,Integer> rx_pkts = getDataFromJSONNode(data);

            data = node.get("rx_bytes");
            HashMap<Integer,Integer> rx_bytes = getDataFromJSONNode(data);

            return new DashboardTabStreams.FlowStatsData(tx_pkts, tx_bytes, rx_pkts, rx_bytes);
        }

        public HashMap<Integer, Integer> getDataFromJSONNode(JsonNode data) {
            HashMap<Integer, Integer> statsList = new HashMap<>();
            if (data != null) {
                Iterator<Map.Entry<String, JsonNode>> iter = data.fields();
                for (; iter.hasNext(); ) {
                    Map.Entry<String, JsonNode> elem = iter.next();
                    statsList.put(Integer.decode(elem.getKey()), new Integer((int) elem.getValue().asInt()));
                }
            }
            return statsList;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonDeserialize(using = FlowStatsDataJsonDeserializer.class)
    public static class FlowStatsData {

        HashMap<Integer,Integer> tx_pkts;
        HashMap<Integer,Integer> tx_bytes;
        HashMap<Integer,Integer> rx_pkts;
        HashMap<Integer,Integer> rx_bytes;

        public FlowStatsData() {
            tx_pkts = new HashMap<>();
            tx_bytes = new HashMap<>();
            rx_pkts = new HashMap<>();
            rx_bytes = new HashMap<>();
        }

        public FlowStatsData(HashMap<Integer,Integer> tx_pkts,
                HashMap<Integer,Integer> tx_bytes,
                HashMap<Integer,Integer> rx_pkts,
                HashMap<Integer,Integer> rx_bytes) {
            this.tx_pkts = tx_pkts;
            this.tx_bytes = tx_bytes;
            this.rx_pkts = rx_pkts;
            this.rx_bytes = rx_bytes;
        }

        public HashMap<Integer, Integer> getTx_pkts() {
            return tx_pkts;
        }

        public void setTx_pkts(HashMap<Integer, Integer> tx_pkts) {
            this.tx_pkts = tx_pkts;
        }

        public HashMap<Integer, Integer> getTx_bytes() {
            return tx_bytes;
        }

        public void setTx_bytes(HashMap<Integer, Integer> tx_bytes) {
            this.tx_bytes = tx_bytes;
        }

        public HashMap<Integer, Integer> getRx_pkts() {
            return rx_pkts;
        }

        public void setRx_pkts(HashMap<Integer, Integer> rx_pkts) {
            this.rx_pkts = rx_pkts;
        }

        public HashMap<Integer, Integer> getRx_bytes() {
            return rx_bytes;
        }

        public void setRx_bytes(HashMap<Integer, Integer> rx_bytes) {
            this.rx_bytes = rx_bytes;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Generated("org.jsonschema2pojo")
    @JsonPropertyOrder({
            "freq",
            "value"
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlowStatsTimeStamp {

        @JsonProperty("freq")
        long freq;
        @JsonProperty("value")
        long value;

        public FlowStatsTimeStamp() {
        }

        public FlowStatsTimeStamp(long freq, long value) {
            this.freq = freq;
            this.value = value;
        }

        @JsonProperty("freq")
        public long getFreq() {
            return freq;
        }

        @JsonProperty("freq")
        public void setFreq(long freq) {
            this.freq = freq;
        }

        @JsonProperty("value")
        public long getValue() {
            return value;
        }

        @JsonProperty("value")
        public void setValue(long value) {
            this.value = value;
        }

    }
}

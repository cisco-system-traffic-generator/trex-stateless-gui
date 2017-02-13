package com.exalttech.trex.ui.controllers.Dashboard;

import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.PortStatus;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.ui.views.statistics.cells.StatisticCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticConstantsKeys;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticRow;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardTabStreams extends BorderPane {

    @FXML BorderPane borderPaneStreamStats;
    @FXML HBox borderPaneStreamStatsTopHbox;
    @FXML HBox borderPaneStreamStatsCenterHbox;
    @FXML HBox gridPaneStreamStatsBottomHbox;
    @FXML GridPane gridPaneStreamStatsBottomGridPane;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MainViewController.class.getName());

    private RPCMethods serverRPCMethods;
    private PortsManager portManager;

    Stage currentStage;
    RefreshingService readingStatService = new RefreshingService();
    List<Integer> streamList = new ArrayList<Integer>();
    StatsTableGenerator statsTableGenerator;

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
                String response = serverRPCMethods.getStreamList(0);
                JSONObject jsonObject = new JSONObject(response);
                if (!"null".equals(jsonObject.get("result").toString())) {
                    String result = ((JSONArray) jsonObject.get("result")).toString();
                    ObjectMapper mapper = new ObjectMapper();
                    streamList = mapper.readValue(result, mapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
                }
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
        /*Map<String, String> statsMap = StatsLoader.getInstance().getLoadedFlowStatsMap();
        statTable.getChildren().clear();
        Util.optimizeMemory();

        double columnWidth = 150;
        addHeaderCell("Value", 1, columnWidth);
        addCounterColumn(StatisticConstantsKeys.GLOBAL_STATS_ROW_NAME);
        rowIndex = 1;
        odd = true;
        for (StatisticRow row : StatisticConstantsKeys.GLOBAL_STATS_KEY) {
            StatisticCell cell = getGridCell(row, columnWidth, row.getKey());
            ((StatisticLabelCell) cell).setLeftPosition();
            if (row.getAttributeName().equals("active-port")) {
                cell.updateItem("", PortsManager.getInstance().getActivePort());
            } else {
                String value = statsList.get(row.getAttributeName());
                if (row.isFormatted()) {
                    value = Util.getFormatted(value, true, row.getUnit());
                }
                cell.updateItem("", value);
            }
            statTable.getChildren().remove(cell);
            statTable.add((Node) cell, 1, rowIndex++);
        }
        return statTable;*/

        BorderPane top = new BorderPane();
        top.setRight(statsTableGenerator.generateGlobalStatPane());
        borderPaneStreamStatsTopHbox.getChildren().add(0, top);

        return;
    }

}

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

import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.dialog.DialogCloseHandler;
import com.exalttech.trex.ui.dialog.DialogManager;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.dialog.DialogWindow;
import com.exalttech.trex.ui.views.StatsInfoView;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Util;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import jfxtras.labs.scene.control.gauge.linear.SimpleMetroArcGauge;
import jfxtras.labs.scene.control.gauge.linear.elements.PercentSegment;
import org.apache.log4j.Logger;

/**
 * Dashboard FXML controller
 *
 * @author Georgekh
 */
public class DashboardController extends DialogView implements Initializable, DialogCloseHandler {

    private static final Logger LOG = Logger.getLogger(DashboardController.class.getName());

    @FXML
    ScrollPane statTableContainer;
    @FXML
    AnchorPane gaugeContainer;
    @FXML
    AnchorPane totalTx;
    @FXML
    AnchorPane totalRx;
    @FXML
    AnchorPane totalPPS;
    @FXML
    AnchorPane totalStream;
    @FXML
    AnchorPane activePort;
    @FXML
    AnchorPane dropRate;
    @FXML
    AnchorPane queueFull;
    @FXML
    Button clearCacheBtn;
    @FXML
    ComboBox portFilterCB;
    @FXML
    AnchorPane mainDashboardContainer;

    @FXML
    AnchorPane latencyChart;
    @FXML
    LatencyChartController latencyChartController;

    boolean ownerFilter;
    RefreshingService readingStatService = new RefreshingService();
    Map<String, String> currentStatsList = new HashMap<>();
    Map<String, String> cachedStatsList = new HashMap<>();
    private SimpleMetroArcGauge simpleMetroArcGauge;
    PortsManager portManager;
    Stage currentStage;

    /**
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearCacheBtn.setGraphic(new ImageView(new Image("/icons/clean.png")));
        portManager = PortsManager.getInstance();
        initializePortFilter();
        initializeGauge();
        initializeStatsWidgets();
        initializeReadingStats();

        // add current dashboard to opening window
        DialogManager.getInstance().addHandler(this);

    }

    /**
     * Init stage
     */
    public void init() {
        currentStage = (Stage) mainDashboardContainer.getScene().getWindow();
        currentStage.getScene().getWindow().setOnCloseRequest((WindowEvent event) -> {
            stopRunningThread();
            // remove current opened dashboard from opening window
            DialogManager.getInstance().removeHandler(this);
        });

        // add size listener
        currentStage.getScene().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                buildPortStatTable();
            }
        });
    }

    /**
     * Initialize reading stats thread
     */
    private void initializeReadingStats() {
        readingStatService = new RefreshingService();
        readingStatService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        readingStatService.setOnSucceeded((WorkerStateEvent event) -> {
            currentStatsList = StatsLoader.getInstance().getLoadedStatsList();
            String data = currentStatsList.get("m_cpu_util");
            if (Util.isNullOrEmpty(data)) {
                data = "0";
            }
            updateGaugeSegmentsAndColoring(Double.parseDouble(data));

            ((StatsInfoView) totalTx.getChildren().get(0)).setValue(Util.getFormatted(currentStatsList.get("m_tx_bps"), true, "b/sec"));
            ((StatsInfoView) totalRx.getChildren().get(0)).setValue(Util.getFormatted(currentStatsList.get("m_rx_bps"), true, "b/sec"));
            ((StatsInfoView) totalPPS.getChildren().get(0)).setValue(Util.getFormatted(currentStatsList.get("m_tx_pps"), true, "pkt/sec"));
            ((StatsInfoView) totalStream.getChildren().get(0)).setValue("0");
            ((StatsInfoView) activePort.getChildren().get(0)).setValue(portManager.getActivePort());
            ((StatsInfoView) dropRate.getChildren().get(0)).setValue(Util.getFormatted(currentStatsList.get("m_rx_drop_bps"), true, "b/sec"));
            ((StatsInfoView) queueFull.getChildren().get(0)).setValue(Util.getFormatted(getDiffQueueFull(), true, "pkts"));
            buildPortStatTable();
        });
        readingStatService.start();

        latencyChartController.runChart();
    }

    /**
     * Get different queue full
     *
     * @return
     */
    private String getDiffQueueFull() {
        try {
            String cached = cachedStatsList.get("m_total_queue_full");
            String current = currentStatsList.get("m_total_queue_full");
            long data;
            if (Util.isNullOrEmpty(cached)) {
                data = (long) Double.parseDouble(current);
            } else {
                data = (long) Double.parseDouble(current) - (long) Double.parseDouble(cached);
            }
            return String.valueOf(data);
        } catch (NumberFormatException e) {
            LOG.error("Error calculating queue full value", e);
            return String.valueOf(0);
        }
    }

    /**
     * Update gauge value and segments coloring
     *
     * @param value
     */
    private void updateGaugeSegmentsAndColoring(double value) {
        simpleMetroArcGauge.segments().clear();

        simpleMetroArcGauge.getStyleClass().removeAll("colorscheme-red-to-grey-2", "colorscheme-green-to-grey-2");
        if (value >= 90) {
            simpleMetroArcGauge.getStyleClass().add("colorscheme-red-to-grey-2");
        } else {
            simpleMetroArcGauge.getStyleClass().add("colorscheme-green-to-grey-2");
        }

        simpleMetroArcGauge.setValue(value / 100);
        simpleMetroArcGauge.segments().add(new PercentSegment(simpleMetroArcGauge, 0.0, value));
        simpleMetroArcGauge.segments().add(new PercentSegment(simpleMetroArcGauge, value, 100.0));

    }

    /**
     * Build port stats table
     */
    private void buildPortStatTable() {
        StatsTableGenerator statsTableGenerator = new StatsTableGenerator();
        double colWidth = (statTableContainer.getWidth() - 150) / (portManager.getPortCount(ownerFilter) + 1);
        if (colWidth < 150) {
            colWidth = 150;
        }

        statTableContainer.setContent(statsTableGenerator.getPortStatTable(cachedStatsList, portManager.getPortList().size(), true, colWidth, ownerFilter));
    }

    /**
     * Initialize gauge
     */
    private void initializeGauge() {
        simpleMetroArcGauge = new SimpleMetroArcGauge();
        simpleMetroArcGauge.setId("cpuIndicator");
        simpleMetroArcGauge.setAccessibleHelp("setAccessibleHelp");
        simpleMetroArcGauge.setMinValue(0.0);
        simpleMetroArcGauge.setMaxValue(1.0);

        // define size
        simpleMetroArcGauge.setMaxSize(140, 90);
        gaugeContainer.getChildren().add(simpleMetroArcGauge);
    }

    /**
     * Initialize global stats value
     */
    private void initializeStatsWidgets() {
        totalTx.getChildren().add(new StatsInfoView("Total Tx L2"));
        totalRx.getChildren().add(new StatsInfoView("Total Rx"));
        totalPPS.getChildren().add(new StatsInfoView("Total PPS"));
        totalStream.getChildren().add(new StatsInfoView("Total Stream"));
        activePort.getChildren().add(new StatsInfoView("Active Ports"));
        dropRate.getChildren().add(new StatsInfoView("Drop Rate", true));
        queueFull.getChildren().add(new StatsInfoView("Queue Full", true));
    }

    /**
     * Handle clear cache button clicked
     *
     * @param event
     */
    @FXML
    public void handleClearCacheButtonClicked(ActionEvent event) {
        cachedStatsList = StatsLoader.getInstance().getLoadedStatsList();
    }

    /**
     * Initialize port filter
     */
    private void initializePortFilter() {
        portFilterCB.getItems().clear();
        portFilterCB.getItems().addAll("All", "Owned by me");
        portFilterCB.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                ownerFilter = "Owned by me".equals(String.valueOf(newValue));
            }
        });
        portFilterCB.getSelectionModel().select(0);
    }

    /**
     * Stop Statistics listening thread
     */
    private void stopRunningThread() {
        if (readingStatService.isRunning()) {
            readingStatService.cancel();
        }
        latencyChartController.stopRenderingChart();
    }

    /**
     * close current stage
     */
    public void close() {
        currentStage.close();
    }

    /**
     * Close dashboard dialog
     */
    @Override
    public void closeDialog() {
        stopRunningThread();
        currentStage.close();
    }

    /**
     * Handle enter key pressed
     *
     * @param stage
     */
    @Override
    public void onEnterKeyPressed(Stage stage) {

        // fire close event 
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Handle show series button clicked
     *
     * @param event
     */
    @FXML
    public void handleShowSeriesButtonClicked(MouseEvent event) {
        try {
            DialogWindow latencyOptionWindow = new DialogWindow("LatencySeriesOptionWindow.fxml", "Latency Series", 250, 100, false, currentStage);
            LatencySeriesOptionWindowController controller = (LatencySeriesOptionWindowController) latencyOptionWindow.getController();
            controller.initOption(latencyChartController.getChartSeriesList(), latencyChartController.getChartSeriesUpdatedProperty(), latencyChartController.getIntervalProperty());
            latencyOptionWindow.show(true);
        } catch (IOException ex) {
            LOG.error("Error opening latency series window", ex);
        }
    }
}

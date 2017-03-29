package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.Set;

import com.exalttech.trex.util.Initialization;


public class DashboardTabChartsContainer extends AnchorPane {
    @FXML
    private AnchorPane root;

    @FXML
    private void handleClicked(MouseEvent event) {
        contextMenu.show(root, event.getScreenX(), event.getScreenY());
    }

    private ContextMenu contextMenu;
    private DashboardTabChartsUpdatable chart;
    private StringProperty chartType;
    private IntegerProperty interval;
    private Set<Integer> lastVisiblePorts;
    private Set<String> lastVisibleStreams;
    private int lastStreamsCount;

    public DashboardTabChartsContainer(String selectedType, IntegerProperty interval) {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/charts/DashboardTabChartsContainer.fxml");

        this.interval = interval;

        chartType = new SimpleStringProperty();
        chartType.addListener(this::handleChartTypeChanged);
        chartType.set(selectedType);

        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                createContextMenuItem(DashboardTabChartsFactory.ChartTypes.TX_PPS),
                createContextMenuItem(DashboardTabChartsFactory.ChartTypes.RX_PPS),
                createContextMenuItem(DashboardTabChartsFactory.ChartTypes.TX_BPS_L1),
                createContextMenuItem(DashboardTabChartsFactory.ChartTypes.TX_BPS_L2),
                createContextMenuItem(DashboardTabChartsFactory.ChartTypes.RX_BPS_L2),
                new SeparatorMenuItem(),
                createContextMenuItem(DashboardTabChartsFactory.ChartTypes.MAX_LATENCY),
                createContextMenuItem(DashboardTabChartsFactory.ChartTypes.AVG_LATENCY),
                createContextMenuItem(DashboardTabChartsFactory.ChartTypes.LATENCY_WINDOW),
                createContextMenuItem(DashboardTabChartsFactory.ChartTypes.LATENCY_HISTOGRAM)
        );
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams, int streamsCount) {
        chart.update(visiblePorts, visibleStreams, streamsCount);
        lastVisiblePorts = visiblePorts;
        lastVisibleStreams = visibleStreams;
        lastStreamsCount = streamsCount;
    }

    private MenuItem createContextMenuItem(String chartType) {
        MenuItem item = new MenuItem(chartType);
        item.setOnAction(this::handleContextMenuAction);
        return item;
    }

    private void handleContextMenuAction(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        chartType.set(source.getText());
    }

    private void handleChartTypeChanged(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        chart = DashboardTabChartsFactory.create(newValue, interval);

        AnchorPane.setTopAnchor((Node) chart, 0.0);
        AnchorPane.setRightAnchor((Node) chart, 0.0);
        AnchorPane.setBottomAnchor((Node) chart, 0.0);
        AnchorPane.setLeftAnchor((Node) chart, 0.0);
        chart.update(lastVisiblePorts, lastVisibleStreams, lastStreamsCount);

        root.getChildren().clear();
        root.getChildren().add((Node) chart);
    }
}

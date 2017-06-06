package com.cisco.trex.stl.gui.controllers.dashboard.charts;

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

import com.exalttech.trex.util.Initialization;


public class ChartContainer extends AnchorPane {
    @FXML
    private AnchorPane root;

    @FXML
    private void handleClicked(MouseEvent event) {
        contextMenu.show(root, event.getScreenX(), event.getScreenY());
    }

    private ContextMenu contextMenu;
    private FlowChart chart;
    private StringProperty chartType;
    private IntegerProperty interval;

    public ChartContainer(String selectedType, IntegerProperty interval) {
        Initialization.initializeFXML(this, "/fxml/Dashboard/charts/ChartContainer.fxml");

        this.interval = interval;

        chartType = new SimpleStringProperty();
        chartType.addListener(this::handleChartTypeChanged);
        chartType.set(selectedType);

        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                createContextMenuItem(ChartsFactory.ChartTypes.TX_PPS),
                createContextMenuItem(ChartsFactory.ChartTypes.RX_PPS),
                createContextMenuItem(ChartsFactory.ChartTypes.TX_BPS_L1),
                createContextMenuItem(ChartsFactory.ChartTypes.TX_BPS_L2),
                createContextMenuItem(ChartsFactory.ChartTypes.RX_BPS_L2),
                new SeparatorMenuItem(),
                createContextMenuItem(ChartsFactory.ChartTypes.MAX_LATENCY),
                createContextMenuItem(ChartsFactory.ChartTypes.AVG_LATENCY),
                createContextMenuItem(ChartsFactory.ChartTypes.JITTER_LATENCY),
                createContextMenuItem(ChartsFactory.ChartTypes.TEMPORARY_MAX_LATENCY),
                createContextMenuItem(ChartsFactory.ChartTypes.LATENCY_HISTOGRAM)
        );
    }

    public void setActive(final boolean isActive) {
        chart.setActive(isActive);
    }

    private MenuItem createContextMenuItem(String chartType) {
        MenuItem item = new MenuItem(chartType);
        item.setOnAction(this::handleContextMenuAction);
        return item;
    }

    private void handleContextMenuAction(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        chartType.set(source.getText());
        chart.setActive(true);
    }

    private void handleChartTypeChanged(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        chart = ChartsFactory.create(newValue, interval);

        AnchorPane.setTopAnchor((Node) chart, 0.0);
        AnchorPane.setRightAnchor((Node) chart, 0.0);
        AnchorPane.setBottomAnchor((Node) chart, 0.0);
        AnchorPane.setLeftAnchor((Node) chart, 0.0);

        root.getChildren().clear();
        root.getChildren().add((Node) chart);
    }
}

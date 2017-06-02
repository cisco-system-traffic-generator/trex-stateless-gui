package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import com.exalttech.trex.util.Initialization;


public class Charts extends BorderPane {
    private static String[] defaultChartTypes = new String[]{
            ChartsFactory.ChartTypes.TX_PPS,
            ChartsFactory.ChartTypes.RX_PPS,
            ChartsFactory.ChartTypes.TX_BPS_L2,
            ChartsFactory.ChartTypes.RX_BPS_L2
    };

    @FXML
    private ComboBox<Integer> intervalComboBox;
    @FXML
    private Label layoutIcon1Label;
    @FXML
    private Label layoutIcon2Label;
    @FXML
    private Label layoutIcon4Label;
    @FXML
    private GridPane gridPane;

    private LayoutConfiguration[] layoutConfigurations;
    private int selectedConfigurationIndex;
    private ChartContainer[] charts;
    private IntegerProperty interval;

    public Charts() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/charts/Charts.fxml");
        charts = new ChartContainer[4];
        interval = new SimpleIntegerProperty();
        interval.bind(intervalComboBox.valueProperty());
        initLayoutConfigurations();
        rebuildLayout(1);
    }

    @FXML
    public void handleLayoutIcon1LabelClicked(MouseEvent event) {
        handleLayoutChanged(1);
    }
    @FXML
    public void handleLayoutIcon2LabelClicked(MouseEvent event) {
        handleLayoutChanged(2);
    }
    @FXML
    public void handleLayoutIcon4LabelClicked(MouseEvent event) {
        handleLayoutChanged(4);
    }

    public void setActive(final boolean isActive) {
        LayoutConfiguration layoutConfiguration = layoutConfigurations[selectedConfigurationIndex];
        int size = layoutConfiguration.getColumnsCount()*layoutConfiguration.getRowsCount();
        for (int i = 0; i < size; ++i) {
            charts[i].setActive(isActive);
        }
    }

    private void initLayoutConfigurations() {
        layoutConfigurations = new LayoutConfiguration[3];
        layoutConfigurations[0] = new LayoutConfiguration(1, 1);
        layoutConfigurations[1] = new LayoutConfiguration(1, 2);
        layoutConfigurations[2] = new LayoutConfiguration(2, 2);
    }

    private void handleLayoutChanged(final int gridSize) {
        setActive(false);
        rebuildLayout(gridSize);
        setActive(true);
    }

    private void rebuildLayout(int gridSize) {
        switch (gridSize) {
            case 1:
                layoutIcon1Label.setDisable(true);
                layoutIcon2Label.setDisable(false);
                layoutIcon4Label.setDisable(false);
                selectedConfigurationIndex = 0;
                break;
            case 2:
                layoutIcon1Label.setDisable(false);
                layoutIcon2Label.setDisable(true);
                layoutIcon4Label.setDisable(false);
                selectedConfigurationIndex = 1;
                break;
            case 4:
                layoutIcon1Label.setDisable(false);
                layoutIcon2Label.setDisable(false);
                layoutIcon4Label.setDisable(true);
                selectedConfigurationIndex = 2;
                break;
        }
        LayoutConfiguration layoutConfiguration = layoutConfigurations[selectedConfigurationIndex];

        gridPane.getColumnConstraints().clear();
        gridPane.getColumnConstraints().addAll(layoutConfiguration.getColumnConstraints());
        gridPane.getRowConstraints().clear();
        gridPane.getRowConstraints().addAll(layoutConfiguration.getRowConstraints());
        gridPane.getChildren().clear();
        int chartIndex = 0;
        for (int i = 0; i < layoutConfiguration.getColumnsCount(); ++i) {
            for (int j = 0; j < layoutConfiguration.getRowsCount(); ++j) {
                gridPane.add(getChart(chartIndex++), i, j);
            }
        }
    }

    private ChartContainer getChart(int index) {
        if (charts[index] == null) {
            charts[index] = new ChartContainer(defaultChartTypes[index], interval);
        }
        return charts[index];
    }

    private static class LayoutConfiguration {
        private int columnsCount;
        private int rowsCount;
        private ColumnConstraints[] columnConstraints;
        private RowConstraints[] rowConstraints;

        public LayoutConfiguration(int columnsCount, int rowsCount) {
            this.columnsCount = columnsCount;
            this.rowsCount = rowsCount;

            columnConstraints = new ColumnConstraints[this.columnsCount];
            double columnPercentWidth = 100.0/this.columnsCount;
            for (int i = 0; i < this.columnsCount; ++i) {
                columnConstraints[i] = new ColumnConstraints();
                columnConstraints[i].setPercentWidth(columnPercentWidth);
            }

            rowConstraints = new RowConstraints[this.rowsCount];
            double rowPercentHeight = 100.0/this.rowsCount;
            for (int i = 0; i < this.rowsCount; ++i) {
                rowConstraints[i] = new RowConstraints();
                rowConstraints[i].setPercentHeight(rowPercentHeight);
            }
        }

        public int getColumnsCount() { return columnsCount; }

        public int getRowsCount() { return rowsCount; }

        public ColumnConstraints[] getColumnConstraints() { return columnConstraints; }

        public RowConstraints[] getRowConstraints() { return rowConstraints; }
    }
}

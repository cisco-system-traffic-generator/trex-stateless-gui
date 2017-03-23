package com.exalttech.trex.ui.controllers.dashboard.global;

import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import com.exalttech.trex.util.Initialization;


public class DashboardGlobalStatisticsPanel extends AnchorPane {
    @FXML
    private Label valueLabel;
    @FXML
    private Label unitLabel;
    @FXML
    private Label titleLabel;

    private Double threshold;

    public DashboardGlobalStatisticsPanel(@NamedArg("title") String title) {
        this(title, null);
    }

    public DashboardGlobalStatisticsPanel(@NamedArg("title") String title, @NamedArg("threshold") Double threshold) {
        Initialization.initializeFXML(this, "/fxml/Dashboard/global/DashboardGlobalStatisticsPanel.fxml");

        this.threshold = threshold;
        if (this.threshold != null) {
            valueLabel.getStyleClass().add("statsTableGreenValue");
        }
        titleLabel.setText(title);
    }

    public void setValue(String value) {
        valueLabel.getStyleClass().remove("statsTableRedValue");
        if (value.contains(" ")) {
            String[] splitValue = value.split(" ");
            String data = splitValue[0];
            valueLabel.setText(data);
            unitLabel.setText(splitValue[1]);
            if (threshold != null && Double.parseDouble(data) > threshold) {
                valueLabel.getStyleClass().add("statsTableRedValue");
            }
        } else {
            valueLabel.setText(value);
        }
    }
}

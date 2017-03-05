package com.exalttech.trex.ui.views;

import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private boolean isColored;

    public DashboardGlobalStatisticsPanel(@NamedArg("title") String title) {
        this(title, false);
    }

    public DashboardGlobalStatisticsPanel(@NamedArg("title") String title, @NamedArg("isColored") boolean isColored) {
        Initialization.initializeFXML(this, "/fxml/Dashboard/DashboardGlobalStatisticsPanel.fxml");

        this.isColored = isColored;
        if (isColored) {
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
            if (isColored && Double.parseDouble(data) > 0) {
                valueLabel.getStyleClass().add("statsTableRedValue");
            }
        } else {
            valueLabel.setText(value);
        }
    }
}

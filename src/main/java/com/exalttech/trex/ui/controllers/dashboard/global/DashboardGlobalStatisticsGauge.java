package com.exalttech.trex.ui.controllers.dashboard.global;

import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import jfxtras.labs.scene.control.gauge.linear.SimpleMetroArcGauge;
import jfxtras.labs.scene.control.gauge.linear.elements.PercentSegment;

import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class DashboardGlobalStatisticsGauge extends AnchorPane {
    @FXML
    private SimpleMetroArcGauge gauge;
    @FXML
    private Label titleLabel;

    public DashboardGlobalStatisticsGauge(@NamedArg("title") String title) {
        Initialization.initializeFXML(this, "/fxml/Dashboard/global/DashboardGlobalStatisticsGauge.fxml");

        titleLabel.setText(title);
    }

    public void setData(String data) {
        if (Util.isNullOrEmpty(data)) {
            data = "0";
        }
        Double value = Double.parseDouble(data);

        gauge.segments().clear();

        gauge.getStyleClass().removeAll("colorscheme-red-to-grey-2", "colorscheme-green-to-grey-2");
        if (value >= 90) {
            gauge.getStyleClass().add("colorscheme-red-to-grey-2");
        } else {
            gauge.getStyleClass().add("colorscheme-green-to-grey-2");
        }

        gauge.setValue(value / 100);
        gauge.segments().add(new PercentSegment(gauge, 0.0, value));
        gauge.segments().add(new PercentSegment(gauge, value, 100.0));
    }
}

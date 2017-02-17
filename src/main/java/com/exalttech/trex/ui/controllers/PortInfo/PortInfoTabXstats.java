package com.exalttech.trex.ui.controllers.PortInfo;

import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.util.Util;
import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class PortInfoTabXstats extends BorderPane {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MainViewController.class.getName());

    private Port port;
    private RPCMethods serverRPCMethods;
    private PortsManager portManager;

    @FXML private Text textTabConfigPortNameTitle;
    @FXML ScrollPane statXTableContainer;
    @FXML AnchorPane statXTableWrapper;
    @FXML CheckBox   statXTableNotEmpty;
    @FXML TextField  statXTableFilter;

    private String savedPingIPv4 = "";
    StatsTableGenerator statsTableGenerator;
    boolean statXTableNotEmpty_changed = false;
    boolean statXTableFilter_changed = false;

    public PortInfoTabXstats(Injector injector, RPCMethods serverRPCMethods, Port port) {
        this.port = port;
        this.serverRPCMethods = serverRPCMethods;
        this.portManager = PortsManager.getInstance();

        FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);

        fxmlLoader.setLocation(getClass().getResource("/fxml/PortInfo/TabXstats.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception e) {
            LOG.error("Failed to load fxml file: " + e.getMessage());
        }

        statXTableNotEmpty.setOnAction((e) -> {
            statXTableNotEmpty_changed = true;
        });

        statXTableFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                statXTableFilter_changed = true;
            }
        });

        statsTableGenerator = new StatsTableGenerator();
        update(true);
    }

    private void updatePortForce(boolean full) {
        Platform.runLater(() -> {
            portManager.updatePortForce();
            Platform.runLater(() -> {
                update(full);
            });
        });
    }

    public void update(boolean full) {
        if (statXTableNotEmpty_changed) {
            statXTableNotEmpty_changed = false;
            full = true;
        }
        if (statXTableFilter_changed) {
            statXTableFilter_changed = false;
            full = true;
        }

        textTabConfigPortNameTitle.setText("Port " + port.getIndex());
        Pane pane = statsTableGenerator.generateXStatPane(full, port, statXTableNotEmpty.isSelected(), statXTableFilter.getText());

        statXTableContainer.setContent(pane);
        statXTableContainer.setVisible(true);
    }
}

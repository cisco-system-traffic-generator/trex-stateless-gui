package com.exalttech.trex.ui.controllers.PortInfo;

import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import com.google.inject.Injector;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class PortInfoTabConfig extends GridPane {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MainViewController.class.getName());

    private Port port;
    private RPCMethods serverRPCMethods;

    @FXML private GridPane root;

    public PortInfoTabConfig(Injector injector, RPCMethods serverRPCMethods, Port port) {
        this.port = port;
        this.serverRPCMethods = serverRPCMethods;

        FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);

        fxmlLoader.setLocation(getClass().getResource("/fxml/PortInfo/TabConfig.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception e) {
            LOG.error("Failed to load fxml file: " + e.getMessage());
        }
    }

}

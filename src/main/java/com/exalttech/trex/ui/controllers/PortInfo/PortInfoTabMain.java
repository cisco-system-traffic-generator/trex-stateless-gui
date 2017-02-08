package com.exalttech.trex.ui.controllers.PortInfo;

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class PortInfoTabMain extends GridPane {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MainViewController.class.getName());

    private Port port;
    private RPCMethods serverRPCMethods;
    private PortsManager portManager;

    @FXML private GridPane root;
    @FXML private Text textTabMainPortNameTitle;
    @FXML private Label labelTabMainPortName;
    @FXML private Label labelTabMainPortIndex;
    @FXML private Label labelTabMainPortDriver;
    @FXML private Label labelTabMainPortOwner;
    @FXML private Label labelTabMainPortSpeed;
    @FXML private Label labelTabMainPortStatus;
    @FXML private Label labelTabMainPortPromiscuous;
    @FXML private Button buttonTabMainPortPromiscuous;
    @FXML private ChoiceBox choiceTabMainPortFlowControl;
    @FXML private Label labelTabMainPortLink;
    @FXML private Button buttonTabMainPortLink;
    @FXML private Label labelTabMainPortLED;
    @FXML private Button buttonTabMainPortLED;
    @FXML private Label labelTabMainPortCapturing;
    @FXML private Button buttonTabMainPortAcquireRelease;
    @FXML private Button buttonTabMainPortForceAcquire;

    public PortInfoTabMain(Injector injector, RPCMethods serverRPCMethods, Port port) {
        this.port = port;
        this.serverRPCMethods = serverRPCMethods;
        this.portManager = PortsManager.getInstance();

        FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);

        fxmlLoader.setLocation(getClass().getResource("/fxml/PortInfo/TabMain.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception e) {
            LOG.error("Failed to load fxml file: " + e.getMessage());
        }

        textTabMainPortNameTitle.setText("Port " + port.getIndex());
        labelTabMainPortDriver.setText(port.getDriver());
        labelTabMainPortIndex.setText("" + port.getIndex());
        labelTabMainPortName.setText("Port " + port.getIndex());
        labelTabMainPortOwner.setText(port.getOwner());
        labelTabMainPortSpeed.setText("" + port.getSpeed());
        labelTabMainPortStatus.setText(port.getStatus());

        labelTabMainPortPromiscuous.setText(port.getAttr().getPromiscuous().toString());
        buttonTabMainPortPromiscuous.setText(port.getAttr().getPromiscuous().toString().compareToIgnoreCase("enabled")==0 ? "Disable" : "Enable");

        String str = port.getAttr().getFc().toString();
        choiceTabMainPortFlowControl.getSelectionModel().select(str.substring(0, 1).toUpperCase() + str.substring(1));
        if (!port.isIs_led_supported()) {
            labelTabMainPortLED.setText("NOT SUPPORTED");
            buttonTabMainPortLED.setVisible(false);
        }
        else if (port.getAttr().getLed() != null) {
            labelTabMainPortLED.setText(port.getAttr().getLed().toString());
            buttonTabMainPortLED.setText(port.getAttr().getLed().toString().compareToIgnoreCase("on")==0 ? "Off" : "On");
        }
        else {
            labelTabMainPortLED.setText("N/A");
            buttonTabMainPortLED.setText("On");
        }
        labelTabMainPortLink.setText(port.getAttr().getLink().toString());
        buttonTabMainPortLink.setText(port.getAttr().getLink().toString().compareToIgnoreCase("up")==0 ? "Down" : "Up");

        if (port.getOwner()==null || port.getOwner().compareTo("")==0) {
            buttonTabMainPortAcquireRelease.setText("Acquire port");
            buttonTabMainPortForceAcquire.setVisible(true);
            //buttonTabMainPortForceAcquire.managedProperty().setValue(true);
            buttonTabMainPortForceAcquire.setDisable(false);

            buttonTabMainPortLED.setVisible(false);
            buttonTabMainPortLink.setVisible(false);
            buttonTabMainPortPromiscuous.setVisible(false);
            choiceTabMainPortFlowControl.setDisable(true);
        }
        else {
            buttonTabMainPortAcquireRelease.setText("Release port");
            buttonTabMainPortForceAcquire.setVisible(false);
            //buttonTabMainPortForceAcquire.managedProperty().setValue(false);
            buttonTabMainPortForceAcquire.setDisable(true);

            buttonTabMainPortLED.setVisible(true);
            if (port.isIs_link_supported()) {
                buttonTabMainPortLink.setVisible(true);
            }
            else {
                buttonTabMainPortLink.setVisible(false);
            }
            buttonTabMainPortPromiscuous.setVisible(true);
            choiceTabMainPortFlowControl.setDisable(false);
        }



        // events
        buttonTabMainPortAcquireRelease.setOnAction((e) -> {
            if (buttonTabMainPortAcquireRelease.getText().startsWith("Acquire")) {
                try {
                    serverRPCMethods.acquireServerPort(port.getIndex(), false);
                    buttonTabMainPortAcquireRelease.setText("Release port");
                } catch (PortAcquireException ex) {
                    LOG.error("Error aquiring port " + port.getIndex() + ": " + ex.getMessage());
                }
            }
            else {
                serverRPCMethods.releasePort(port.getIndex(), true);
                buttonTabMainPortAcquireRelease.setText("Acquire port");
            }
            forceUpdate();
        });
        buttonTabMainPortForceAcquire.setOnAction((e) -> {
            try {
                serverRPCMethods.acquireServerPort(port.getIndex(), true);
                buttonTabMainPortAcquireRelease.setText("Release port");
                buttonTabMainPortForceAcquire.setVisible(false);
                //buttonTabMainPortForceAcquire.managedProperty().setValue(false);
                buttonTabMainPortForceAcquire.setDisable(true);
            } catch (PortAcquireException ex) {
                LOG.error("Error aquiring port " + port.getIndex() + ": " + ex.getMessage());
            }
            forceUpdate();
        });

        buttonTabMainPortLink.setOnAction((e) -> {
            try {
                if (port.getAttr().getLink().getUp()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), false, null, null, null);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), true, null, null, null);
                }
                forceUpdate();
            } catch (Exception ex) {
                LOG.error("Error changing link status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        buttonTabMainPortPromiscuous.setOnAction((e) -> {
            try {
                if (port.getAttr().getPromiscuous().getEnabled()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, false, null, null);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, true, null, null);
                }
                forceUpdate();
            } catch (Exception ex) {
                LOG.error("Error changing promiscuous status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        buttonTabMainPortLED.setOnAction((e) -> {
            try {
                if (port.getAttr().getLed() == null) {
                    if (port.isIs_led_supported()) {
                        serverRPCMethods.setPortAttribute(port.getIndex(), null, null, true, null);
                    }
                }
                else if (port.getAttr().getLed().getOn()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, false, null);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, true, null);
                }
                forceUpdate();
            } catch (Exception ex) {
                LOG.error("Error changing promiscuous status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        // Flow control: 0 = none, 1 = tx, 2 = rx, 3 = full
        choiceTabMainPortFlowControl.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (((String)newValue).compareToIgnoreCase("none") == 0) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 0);
                }
                else if (((String)newValue).compareToIgnoreCase("tx") == 0) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 1);
                }
                else if (((String)newValue).compareToIgnoreCase("rx") == 0) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 2);
                }
                else if (((String)newValue).compareToIgnoreCase("full") == 0) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 3);
                }
                forceUpdate();
            } catch (Exception ex) {
                LOG.error("Error changing flow control mode of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });
    }

    private void forceUpdate() {
        Platform.runLater(() -> {
            portManager.updatePortForce();
        });
    }

}

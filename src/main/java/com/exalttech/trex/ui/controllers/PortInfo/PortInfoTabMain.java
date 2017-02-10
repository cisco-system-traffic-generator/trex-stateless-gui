package com.exalttech.trex.ui.controllers.PortInfo;

import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class PortInfoTabMain extends BorderPane {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MainViewController.class.getName());

    private Port port;
    private RPCMethods serverRPCMethods;
    private PortsManager portManager;

    @FXML private BorderPane rootPortInfoTabMain;
    @FXML private GridPane gridPanePortInfoTabMain;
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
    @FXML private Label labelTabMainPortRxFilterMode;
    @FXML private Label labelTabMainPortMulticast;
    @FXML private Button buttonTabMainPortMulticast;

    private int ledState = -1;

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

        update(true);

        // events
        buttonTabMainPortAcquireRelease.setOnAction((e) -> {
            if (buttonTabMainPortAcquireRelease.getText().startsWith("Acquire")) {
                try {
                    serverRPCMethods.acquireServerPort(port.getIndex(), false);
                    buttonTabMainPortAcquireRelease.setText("Release port");
                } catch (PortAcquireException ex) {
                    LOG.error("Error acquiring port " + port.getIndex() + ": " + ex.getMessage());
                }
            }
            else {
                serverRPCMethods.releasePort(port.getIndex(), true);
                buttonTabMainPortAcquireRelease.setText("Acquire port");
            }
            updatePortForce(true);
        });
        buttonTabMainPortForceAcquire.setOnAction((e) -> {
            try {
                serverRPCMethods.acquireServerPort(port.getIndex(), true);
                buttonTabMainPortAcquireRelease.setText("Release port");
                buttonTabMainPortForceAcquire.setVisible(false);
                //buttonTabMainPortForceAcquire.managedProperty().setValue(false);
                buttonTabMainPortForceAcquire.setDisable(true);
            } catch (PortAcquireException ex) {
                LOG.error("Error acquiring port " + port.getIndex() + ": " + ex.getMessage());
            }
            updatePortForce(true);
        });

        buttonTabMainPortLink.setOnAction((e) -> {
            try {
                if (port.getAttr().getLink().getUp()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), false, null, null, null, null);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), true, null, null, null, null);
                }
                updatePortForce(false);
            } catch (Exception ex) {
                LOG.error("Error changing link status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        buttonTabMainPortPromiscuous.setOnAction((e) -> {
            try {
                if (port.getAttr().getPromiscuous().getEnabled()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, false, null, null, null);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, true, null, null, null);
                }
                updatePortForce(false);
            } catch (Exception ex) {
                LOG.error("Error changing promiscuous status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        buttonTabMainPortMulticast.setOnAction((e) -> {
            try {
                if (port.getAttr().getMulticast().getEnabled()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, null, false);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, null, true);
                }
                updatePortForce(false);
            } catch (Exception ex) {
                LOG.error("Error changing multicast status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        buttonTabMainPortLED.setOnAction((e) -> {
            try {
                if (port.getAttr().getLed() == null) {
                    if (port.isIs_led_supported()) {
                        serverRPCMethods.setPortAttribute(port.getIndex(), null, null, ledState != 1, null, null);
                        ledState = ledState != 1 ? 1 : 0;
                    }
                }
                else if (port.getAttr().getLed().getOn()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, false, null, null);
                    ledState = 0;
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, true, null, null);
                    ledState = 1;
                }
                updatePortForce(false);
            } catch (Exception ex) {
                LOG.error("Error changing LED status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        // Flow control: 0 = none, 1 = tx, 2 = rx, 3 = full
        choiceTabMainPortFlowControl.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (((String)newValue).compareToIgnoreCase("none") == 0) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 0, null);
                }
                else if (((String)newValue).compareToIgnoreCase("tx") == 0) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 1, null);
                }
                else if (((String)newValue).compareToIgnoreCase("rx") == 0) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 2, null);
                }
                else if (((String)newValue).compareToIgnoreCase("full") == 0) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 3, null);
                }
                updatePortForce(false);
            } catch (Exception ex) {
                LOG.error("Error changing flow control mode of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        // for debug
        //gridPanePortInfoTabMain.setGridLinesVisible(true);
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
        boolean iamowner = portManager.isCurrentUserOwner(port.getIndex());

        labelTabMainPortName.setText("Port " + port.getIndex());
        textTabMainPortNameTitle.setText("Port " + port.getIndex());
        labelTabMainPortDriver.setText(port.getDriver());
        labelTabMainPortIndex.setText("" + port.getIndex());
        labelTabMainPortOwner.setText(port.getOwner());
        labelTabMainPortSpeed.setText("" + port.getSpeed() + " Gb/s");
        labelTabMainPortStatus.setText(port.getStatus());
        labelTabMainPortRxFilterMode.setText(port.getAttr().getRx_filter_mode());

        labelTabMainPortPromiscuous.setText(port.getAttr().getPromiscuous().toString());
        buttonTabMainPortPromiscuous.setText(port.getAttr().getPromiscuous().toString().compareToIgnoreCase("enabled")==0 ? "Disable" : "Enable");

        labelTabMainPortMulticast.setText(port.getAttr().getMulticast().toString());
        buttonTabMainPortMulticast.setText(port.getAttr().getMulticast().toString().compareToIgnoreCase("enabled")==0 ? "Disable" : "Enable");

        String str = port.getAttr().getFc().toString();
        choiceTabMainPortFlowControl.getSelectionModel().select(str.substring(0, 1).toUpperCase() + str.substring(1));
        if (!port.isIs_led_supported()) {
            labelTabMainPortLED.setText("NOT SUPPORTED");
            buttonTabMainPortLED.setVisible(false);
        }
        else if (port.getAttr().getLed() != null) {
            labelTabMainPortLED.setText(port.getAttr().getLed().toString());
            buttonTabMainPortLED.setText(port.getAttr().getLed().getOn() ? "Off" : "On");
        }
        else {
            if (full) {
                labelTabMainPortLED.setText("N/A");
                buttonTabMainPortLED.setText("On");
                ledState = -1;
            }
            else {
                if (ledState == 1) {
                    labelTabMainPortLED.setText("on");
                    buttonTabMainPortLED.setText("Off");
                }
                else if (ledState == 0) {
                    labelTabMainPortLED.setText("off");
                    buttonTabMainPortLED.setText("On");
                }
                else {
                    labelTabMainPortLED.setText("N/A");
                    buttonTabMainPortLED.setText("On");
                    ledState = -1;
                }
            }
        }
        labelTabMainPortLink.setText(port.getAttr().getLink().toString());
        buttonTabMainPortLink.setText(port.getAttr().getLink().toString().compareToIgnoreCase("up")==0 ? "Down" : "Up");

        if (port.getOwner()==null || port.getOwner().compareTo("")==0 || !iamowner) {
            buttonTabMainPortAcquireRelease.setText("Acquire port");
            buttonTabMainPortForceAcquire.setVisible(true);
            buttonTabMainPortForceAcquire.managedProperty().setValue(true);
            buttonTabMainPortForceAcquire.setDisable(false);

            buttonTabMainPortLED.setVisible(false);
            buttonTabMainPortLink.setVisible(false);
            buttonTabMainPortPromiscuous.setVisible(false);
            buttonTabMainPortMulticast.setVisible(false);
            choiceTabMainPortFlowControl.setDisable(true);
        }
        else {
            buttonTabMainPortAcquireRelease.setText("Release port");
            buttonTabMainPortForceAcquire.setVisible(false);
            buttonTabMainPortForceAcquire.managedProperty().setValue(false);
            buttonTabMainPortForceAcquire.setDisable(true);

            if (port.isIs_led_supported()) {
                buttonTabMainPortLED.setVisible(true);
            }
            else {
                buttonTabMainPortLED.setVisible(false);
            }
            if (port.isIs_link_supported()) {
                buttonTabMainPortLink.setVisible(true);
            }
            else {
                buttonTabMainPortLink.setVisible(false);
            }
            if (port.isIs_fc_supported()) {
                choiceTabMainPortFlowControl.setDisable(false);
            }
            else {
                choiceTabMainPortFlowControl.setDisable(true);
            }
            buttonTabMainPortPromiscuous.setVisible(true);
            buttonTabMainPortMulticast.setVisible(true);
        }
    }

}

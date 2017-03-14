package com.exalttech.trex.ui.controllers.ports;

import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.models.datastore.CaptureStatus;
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
import org.controlsfx.control.ToggleSwitch;

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
    @FXML private ToggleSwitch buttonTabMainPortPromiscuous;
    @FXML private ChoiceBox choiceTabMainPortFlowControl;
    @FXML private ToggleSwitch buttonTabMainPortLink;
    @FXML private ToggleSwitch buttonTabMainPortLED;
    @FXML private Label labelTabMainPortCaptureStatus;
    @FXML private Button buttonTabMainPortAcquireRelease;
    @FXML private Button buttonTabMainPortForceAcquire;
    @FXML private Label labelTabMainPortRxFilterMode;
    @FXML private ToggleSwitch buttonTabMainPortMulticast;
    @FXML private Label labelTabMainPortNUMA;
    @FXML private Label labelTabMainPortPCI;
    @FXML private Label labelTabMainPortRxQueueing;
    @FXML private Label labelTabMainPortGratARP;

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

        buttonTabMainPortLink.selectedProperty().addListener((e) -> {
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

        buttonTabMainPortPromiscuous.selectedProperty().addListener((e) -> {
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

        buttonTabMainPortMulticast.selectedProperty().addListener((e) -> {
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

        buttonTabMainPortLED.selectedProperty().addListener((e) -> {
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
                updatePortForce(true);
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

        boolean ison = port.getAttr().getPromiscuous().toString().compareToIgnoreCase("enabled")==0;
        buttonTabMainPortPromiscuous.setText(ison ? "On" : "Off");
        buttonTabMainPortPromiscuous.setSelected(ison);

        ison = port.getAttr().getMulticast().toString().compareToIgnoreCase("enabled")==0;
        buttonTabMainPortMulticast.setText(ison ? "On" : "Off");
        buttonTabMainPortMulticast.setSelected(ison);

        labelTabMainPortNUMA.setText("" + port.getNuma());
        labelTabMainPortPCI.setText(port.getPci_addr());
        labelTabMainPortRxQueueing.setText(port.getRx_info().getQueue().isIs_active() ? "on" : "off");
        labelTabMainPortGratARP.setText(port.getRx_info().getGrat_arp().isIs_active() ? "on" : "off");

        if (full) {
            String str = port.getAttr().getFc().toString();
            choiceTabMainPortFlowControl.getSelectionModel().select(str.substring(0, 1).toUpperCase() + str.substring(1));
        }

        if (!port.isIs_led_supported()) {
            buttonTabMainPortLED.setText("NOT SUPPORTED");
            buttonTabMainPortLED.setDisable(true);
        }
        else if (port.getAttr().getLed() != null) {
            ison = port.getAttr().getLed().getOn();
            buttonTabMainPortLED.setText(ison ? "On" : "Off");
            buttonTabMainPortLED.setSelected(ison);
        }
        else {
            if (full) {
                buttonTabMainPortLED.setText("Off");
                ledState = -1;
            }
            else {
                if (ledState == 1) {
                    buttonTabMainPortLED.setText("On");
                }
                else if (ledState == 0) {
                    buttonTabMainPortLED.setText("Off");
                }
                else {
                    buttonTabMainPortLED.setText("Off");
                    ledState = -1;
                }
            }
        }
        boolean up = port.getAttr().getLink().toString().compareToIgnoreCase("up")==0;
        buttonTabMainPortLink.setText(up ? "Up" : "Down");
        buttonTabMainPortLink.setSelected(up);

        if (port.getOwner()==null || port.getOwner().compareTo("")==0 || !iamowner) {
            buttonTabMainPortAcquireRelease.setText("Acquire port");
            buttonTabMainPortForceAcquire.setVisible(true);
            buttonTabMainPortForceAcquire.managedProperty().setValue(true);
            buttonTabMainPortForceAcquire.setDisable(false);

            buttonTabMainPortLED.setDisable(true);
            buttonTabMainPortLink.setDisable(true);
            buttonTabMainPortPromiscuous.setDisable(true);
            buttonTabMainPortMulticast.setDisable(true);
            choiceTabMainPortFlowControl.setDisable(true);
        }
        else {
            buttonTabMainPortAcquireRelease.setText("Release port");
            buttonTabMainPortForceAcquire.setVisible(false);
            buttonTabMainPortForceAcquire.managedProperty().setValue(false);
            buttonTabMainPortForceAcquire.setDisable(true);

            if (port.isIs_led_supported()) {
                buttonTabMainPortLED.setDisable(false);
            }
            else {
                buttonTabMainPortLED.setDisable(true);
            }
            if (port.isIs_link_supported()) {
                buttonTabMainPortLink.setDisable(false);
            }
            else {
                buttonTabMainPortLink.setDisable(true);
            }
            if (port.isIs_fc_supported()) {
                choiceTabMainPortFlowControl.setDisable(false);
            }
            else {
                choiceTabMainPortFlowControl.setDisable(true);
            }
            buttonTabMainPortPromiscuous.setDisable(false);
            buttonTabMainPortMulticast.setDisable(false);
        }

        CaptureStatus[] capture = port.getCaptureStatus();
        String status = "None";
        if (capture != null && capture.length > 0) {
            int port_index_mask = 1 << port.getIndex();
            boolean tx = false;
            boolean rx = false;

            for (int i = 0; i < capture.length; i++) {
                if ((port_index_mask & capture[i].getFilter().getRx()) != 0) {
                    rx = rx || true;
                }
                if ((port_index_mask & capture[i].getFilter().getTx()) != 0) {
                    tx = tx || true;
                }
            }

            if (rx && tx) {
                status = "Rx + Tx";
            }
            else if (rx) {
                status = "Rx";
            }
            else if (tx) {
                status = "Tx";
            }
        }
        labelTabMainPortCaptureStatus.setText(status);
    }

}

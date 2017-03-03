package com.exalttech.trex.ui.controllers.PortInfo;

import com.cisco.trex.stateless.TRexClient;
import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class PortInfoTabConfig extends BorderPane {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MainViewController.class.getName());

    private Port port;
    private RPCMethods serverRPCMethods;
    private PortsManager portManager;

    @FXML private BorderPane rootPortInfoTabConfig;
    @FXML private Text textTabConfigPortNameTitle;
    @FXML private GridPane gridPanePortInfoTabConfig;
    @FXML private ToggleGroup toggleGroupTabConfigPortMode;
    @FXML private RadioButton toggleGroupTabConfigPortL2;
    @FXML private RadioButton toggleGroupTabConfigPortL3;
    @FXML private Label labelTabConfigPortSourceMAC;
    @FXML private TextField textFieldTabConfigPortSourceIPv4;
    @FXML private TextField textFieldTabConfigPortDestinationMAC;
    @FXML private TextField textFieldTabConfigPortDestinationIPv4;
    @FXML private Label labelTabConfigPortArpResolution;
    @FXML private TextField textFieldTabConfigPortPingIPv4;
    @FXML private Button buttonTabConfigPortPing;
    @FXML private Button buttonTabConfigPortApply;
    @FXML private Button buttonTabConfigPortAcquireRelease;
    @FXML private Button buttonTabConfigPortForceAcquire;

    private String savedPingIPv4 = "";

    private TRexClient trexClient = ConnectionManager.getInstance().getTrexClient();
    
    public PortInfoTabConfig(Injector injector, RPCMethods serverRPCMethods, Port port) {
        this.port = port;
        this.serverRPCMethods = serverRPCMethods;
        this.portManager = PortsManager.getInstance();

        FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);

        fxmlLoader.setLocation(getClass().getResource("/fxml/PortInfo/TabConfig.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception e) {
            LOG.error("Failed to load fxml file: " + e.getMessage());
        }

        update(true);

        buttonTabConfigPortPing.setOnAction((e) -> {
            savedPingIPv4 = textFieldTabConfigPortPingIPv4.getText();
//            trexClient.pingHost(savedPingIPv4);
        });

        buttonTabConfigPortApply.setOnAction((e) -> {
            if (toggleGroupTabConfigPortL2.isSelected()) {
                String dstMac = textFieldTabConfigPortDestinationMAC.getText();
                try {
                    serverRPCMethods.setSetL2(port.getIndex(), dstMac);
                    updatePortForce(true);
                } catch (Exception e1) {
                    LOG.error("Failed to set L2 mode: " + e1.getMessage());
                }
            }
            else if (toggleGroupTabConfigPortL3.isSelected()) {
                try {
                    String portSrcIP = textFieldTabConfigPortSourceIPv4.getText();
                    String portDstIP = textFieldTabConfigPortDestinationIPv4.getText();
                    trexClient.serviceMode(port.getIndex(), true);
                    trexClient.setL3Mode(port.getIndex(), null, portSrcIP, portDstIP);
                    String nextHopMac = trexClient.resolveArp(port.getIndex(), portSrcIP, portDstIP);
                    trexClient.setL3Mode(port.getIndex(), nextHopMac, portSrcIP, portDstIP);
                    
                    updatePortForce(true);
                } catch (Exception e1) {
                    LOG.error("Failed to set L3 mode: " + e1.getMessage());
                } finally {
                    trexClient.serviceMode(port.getIndex(), false);
                }
            }
        });

        toggleGroupTabConfigPortL2.setOnAction((e) -> {
            setL2();
            verifyOwner();
        });

        toggleGroupTabConfigPortL3.setOnAction((e) -> {
            setL3();
            verifyOwner();
        });

        buttonTabConfigPortAcquireRelease.setOnAction((e) -> {
            if (buttonTabConfigPortAcquireRelease.getText().startsWith("Acquire")) {
                try {
                    serverRPCMethods.acquireServerPort(port.getIndex(), false);
                    buttonTabConfigPortAcquireRelease.setText("Release port");
                } catch (PortAcquireException ex) {
                    LOG.error("Error acquiring port " + port.getIndex() + ": " + ex.getMessage());
                }
            }
            else {
                serverRPCMethods.releasePort(port.getIndex(), true);
                buttonTabConfigPortAcquireRelease.setText("Acquire port");
            }
            updatePortForce(true);
        });
        buttonTabConfigPortForceAcquire.setOnAction((e) -> {
            try {
                serverRPCMethods.acquireServerPort(port.getIndex(), true);
                buttonTabConfigPortAcquireRelease.setText("Release port");
                buttonTabConfigPortForceAcquire.setVisible(false);
                buttonTabConfigPortForceAcquire.setDisable(true);
            } catch (PortAcquireException ex) {
                LOG.error("Error acquiring port " + port.getIndex() + ": " + ex.getMessage());
            }
            updatePortForce(true);
        });
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

        textTabConfigPortNameTitle.setText("Port " + port.getIndex());

        String srcIPv4 = port.getSrcIp() != null
                ? port.getSrcIp()
                : null;
        
        String dstIPv4 = port.getDstIp() != null
                ? port.getDstIp()
                : null;
        
        String srcMAC = port.getSrcMac() != null
                ? port.getSrcMac()
                : null;
        
        String dstMAC = port.getDstMac() != null
                ? port.getDstMac()
                : null;
        
        if (full) {
            if (port.getAttr().getLayer_cfg().getIpv4().getState().compareToIgnoreCase("none") != 0) {
                toggleGroupTabConfigPortMode.selectToggle(toggleGroupTabConfigPortL3);
            } else if (port.getAttr().getLayer_cfg().getEther().getState().compareToIgnoreCase("configured") == 0) {
                toggleGroupTabConfigPortMode.selectToggle(toggleGroupTabConfigPortL2);
            } else {
                toggleGroupTabConfigPortL2.setSelected(false);
                toggleGroupTabConfigPortL3.setSelected(false);
            }
            if (srcIPv4 != null) {
                textFieldTabConfigPortSourceIPv4.setText(srcIPv4);
            }
            else {
                textFieldTabConfigPortSourceIPv4.setText("");
            }
            if (dstIPv4 != null) {
                textFieldTabConfigPortDestinationIPv4.setText(dstIPv4);
            }
            else {
                textFieldTabConfigPortDestinationIPv4.setText("");
            }
            if (dstMAC != null) {
                textFieldTabConfigPortDestinationMAC.setText(dstMAC);
            }
            else {
                textFieldTabConfigPortDestinationMAC.setText("");
            }

            textFieldTabConfigPortPingIPv4.setText(savedPingIPv4);
        }
        if (srcMAC != null) {
            labelTabConfigPortSourceMAC.setText(srcMAC);
        }
        else {
            labelTabConfigPortSourceMAC.setText("");
        }
        String arpState = port.getAttr().getLayer_cfg().getIpv4().getState().toUpperCase();
        
        labelTabConfigPortArpResolution.setText(arpState);
        Paint color = Color.BLACK;
        switch (arpState) {
            case "RESOLVED":
                color =  Color.GREEN;
                break;
            case "UNRESOLVED":
                color =  Color.RED;
                break;
        }
        labelTabConfigPortArpResolution.setTextFill(color);
        if (toggleGroupTabConfigPortL2.isSelected()) {
            setL2();
        }
        else if (toggleGroupTabConfigPortL3.isSelected()) {
            setL3();
        }

        verifyOwner();
    }

    private void setL2() {
        textFieldTabConfigPortSourceIPv4.setVisible(false);
        textFieldTabConfigPortSourceIPv4.setDisable(true);
        textFieldTabConfigPortSourceIPv4.setManaged(false);
        textFieldTabConfigPortDestinationIPv4.setVisible(false);
        textFieldTabConfigPortDestinationIPv4.setDisable(true);
        textFieldTabConfigPortDestinationIPv4.setManaged(false);

        labelTabConfigPortSourceMAC.setVisible(true);
        labelTabConfigPortSourceMAC.setDisable(false);
        labelTabConfigPortSourceMAC.setManaged(true);
        textFieldTabConfigPortDestinationMAC.setVisible(true);
        textFieldTabConfigPortDestinationMAC.setDisable(false);
        textFieldTabConfigPortDestinationMAC.setManaged(true);
    }

    private void setL3() {
        textFieldTabConfigPortSourceIPv4.setVisible(true);
        textFieldTabConfigPortSourceIPv4.setDisable(false);
        textFieldTabConfigPortSourceIPv4.setManaged(true);
        textFieldTabConfigPortDestinationIPv4.setVisible(true);
        textFieldTabConfigPortDestinationIPv4.setDisable(false);
        textFieldTabConfigPortDestinationIPv4.setManaged(true);

        labelTabConfigPortSourceMAC.setVisible(false);
        labelTabConfigPortSourceMAC.setDisable(true);
        labelTabConfigPortSourceMAC.setManaged(false);
        textFieldTabConfigPortDestinationMAC.setVisible(false);
        textFieldTabConfigPortDestinationMAC.setDisable(true);
        textFieldTabConfigPortDestinationMAC.setManaged(false);
    }

    private void verifyOwner() {
        boolean iamowner = portManager.isCurrentUserOwner(port.getIndex());

        if (!iamowner) {
            buttonTabConfigPortApply.setVisible(false);
            buttonTabConfigPortApply.setDisable(true);
            buttonTabConfigPortApply.setManaged(false);

            buttonTabConfigPortAcquireRelease.setVisible(true);
            buttonTabConfigPortAcquireRelease.setDisable(false);
            buttonTabConfigPortAcquireRelease.setManaged(true);

            buttonTabConfigPortForceAcquire.setVisible(true);
            buttonTabConfigPortForceAcquire.setDisable(false);
            buttonTabConfigPortForceAcquire.setManaged(true);

            buttonTabConfigPortAcquireRelease.setText("Acquire port");
            labelTabConfigPortSourceMAC.setDisable(true);
            textFieldTabConfigPortSourceIPv4.setDisable(true);
            labelTabConfigPortSourceMAC.setDisable(true);
            textFieldTabConfigPortDestinationMAC.setDisable(true);
            textFieldTabConfigPortDestinationIPv4.setDisable(true);
            textFieldTabConfigPortPingIPv4.setDisable(true);
            buttonTabConfigPortPing.setDisable(true);
            toggleGroupTabConfigPortL2.setDisable(true);
            toggleGroupTabConfigPortL3.setDisable(true);
        } else {
            buttonTabConfigPortApply.setVisible(true);
            buttonTabConfigPortApply.setDisable(false);
            buttonTabConfigPortApply.setManaged(true);

            buttonTabConfigPortAcquireRelease.setVisible(false);
            buttonTabConfigPortAcquireRelease.setDisable(true);
            buttonTabConfigPortAcquireRelease.setManaged(false);

            buttonTabConfigPortForceAcquire.setVisible(false);
            buttonTabConfigPortForceAcquire.setDisable(true);
            buttonTabConfigPortForceAcquire.setManaged(false);

            buttonTabConfigPortAcquireRelease.setText("Release port");
            labelTabConfigPortSourceMAC.setDisable(false);
            textFieldTabConfigPortSourceIPv4.setDisable(false);
            labelTabConfigPortSourceMAC.setDisable(false);
            textFieldTabConfigPortDestinationMAC.setDisable(false);
            textFieldTabConfigPortDestinationIPv4.setDisable(false);
            textFieldTabConfigPortPingIPv4.setDisable(false);
            buttonTabConfigPortPing.setDisable(false);
            toggleGroupTabConfigPortL2.setDisable(false);
            toggleGroupTabConfigPortL3.setDisable(false);
        }
    }
}

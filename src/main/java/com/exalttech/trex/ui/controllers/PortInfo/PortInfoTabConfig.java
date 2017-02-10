package com.exalttech.trex.ui.controllers.PortInfo;

import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
    @FXML private Button buttonTabConfigPortResolveARP;
    @FXML private Button buttonTabConfigPortReset;
    @FXML private Button buttonTabConfigPortApply;
    @FXML private Button buttonTabConfigPortAcquireRelease;
    @FXML private Button buttonTabConfigPortForceAcquire;

    private String savedPingIPv4 = "";

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

        buttonTabConfigPortReset.setOnAction((e) -> {
            update(true);
            textFieldTabConfigPortPingIPv4.setText(savedPingIPv4);
        });

        buttonTabConfigPortPing.setOnAction((e) -> {
            savedPingIPv4 = textFieldTabConfigPortPingIPv4.getText();
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
                String srcIPv4 = textFieldTabConfigPortSourceIPv4.getText();
                String dstIPv4 = textFieldTabConfigPortDestinationIPv4.getText();
                try {
                    serverRPCMethods.setSetL3(port.getIndex(), dstIPv4, srcIPv4);
                    updatePortForce(true);
                } catch (Exception e1) {
                    LOG.error("Failed to set L3 mode: " + e1.getMessage());
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

        String srcIPv4 = port.getAttr().getLayer_cfg().getIpv4().getSrc() != null
                ? port.getAttr().getLayer_cfg().getIpv4().getSrc()
                : null;
        String dstIPv4 = port.getAttr().getLayer_cfg().getIpv4().getDst() != null
                ? port.getAttr().getLayer_cfg().getIpv4().getDst()
                : null;
        String srcMAC = port.getAttr().getLayer_cfg().getEther().getSrc() != null
                ? port.getAttr().getLayer_cfg().getEther().getSrc()
                : null;
        String dstMAC = port.getAttr().getLayer_cfg().getEther().getDst() != null
                ? port.getAttr().getLayer_cfg().getEther().getDst()
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
        labelTabConfigPortArpResolution.setText(port.getAttr().getLayer_cfg().getIpv4().getState());

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

        buttonTabConfigPortResolveARP.setVisible(false);
        buttonTabConfigPortResolveARP.setDisable(true);
        buttonTabConfigPortResolveARP.setManaged(false);
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

        buttonTabConfigPortResolveARP.setVisible(true);
        buttonTabConfigPortResolveARP.setDisable(false);
        buttonTabConfigPortResolveARP.setManaged(true);
    }

    private void verifyOwner() {
        boolean iamowner = portManager.isCurrentUserOwner(port.getIndex());

        if (!iamowner) {
            buttonTabConfigPortResolveARP.setVisible(false);
            buttonTabConfigPortResolveARP.setDisable(true);
            buttonTabConfigPortResolveARP.setManaged(false);

            buttonTabConfigPortApply.setVisible(false);
            buttonTabConfigPortApply.setDisable(true);
            buttonTabConfigPortApply.setManaged(false);

            buttonTabConfigPortReset.setVisible(false);
            buttonTabConfigPortReset.setDisable(true);
            buttonTabConfigPortReset.setManaged(false);

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
        }
        else {
            buttonTabConfigPortResolveARP.setVisible(true);
            buttonTabConfigPortResolveARP.setDisable(false);
            buttonTabConfigPortResolveARP.setManaged(true);

            buttonTabConfigPortApply.setVisible(true);
            buttonTabConfigPortApply.setDisable(false);
            buttonTabConfigPortApply.setManaged(true);

            buttonTabConfigPortReset.setVisible(true);
            buttonTabConfigPortReset.setDisable(false);
            buttonTabConfigPortReset.setManaged(true);

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

package com.exalttech.trex.ui.controllers.PortInfo;

import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import com.google.inject.Injector;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
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

    @FXML private GridPane root;
    @FXML private Text textTabConfigPortNameTitle;
    @FXML private Label labelTabConfigPortName;
    @FXML private Label labelTabConfigPortIndex;
    @FXML private Label labelTabConfigPortDriver;
    @FXML private Label labelTabConfigPortOwner;
    @FXML private Label labelTabConfigPortSpeed;
    @FXML private Label labelTabConfigPortStatus;
    @FXML private Label labelTabConfigPortPromiscuous;
    @FXML private Button buttonTabConfigPortPromiscuous;
    @FXML private ChoiceBox choiceTabConfigPortFlowControl;
    @FXML private Label labelTabConfigPortLink;
    @FXML private Button buttonTabConfigPortLink;
    @FXML private Label labelTabConfigPortLED;
    @FXML private Button buttonTabConfigPortLED;
    @FXML private Label labelTabConfigPortCapturing;
    @FXML private Button buttonTabConfigPortAcquireRelease;

    private EventHandler<ActionEvent> handlerActionSaveExternal;

    private ChangeListener<String> onlyNumberListener = (observable, oldValue, newValue) -> {
        if (!newValue.matches("\\d*")) {
            ((StringProperty) observable).set(oldValue);
        }
    };

    private ChangeListener<String> onlyHexListener = (observable, oldValue, newValue) -> {
        if (!newValue.matches("([0-9a-fA-F]*|0x[0-9a-fA-F]*|\\s*[0-9a-fA-F]*|\\s*0x[0-9a-fA-F]*)*")) {
            ((StringProperty) observable).set(oldValue);
        }
    };

    private EventHandler<ActionEvent> handlerActionSaveInternal = (event) -> {
        if (handlerActionSaveExternal != null) {
            handlerActionSaveExternal.handle(event);
        }
    };

    public PortInfoTabMain(Injector injector, RPCMethods serverRPCMethods, Port port) {
        this.port = port;
        this.serverRPCMethods = serverRPCMethods;

        FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);

        fxmlLoader.setLocation(getClass().getResource("/fxml/PortInfo/TabMain.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception e) {
            LOG.error("Failed to load fxml file: " + e.getMessage());
        }

        textTabConfigPortNameTitle.setText("Port " + port.getIndex());
        labelTabConfigPortDriver.setText(port.getDriver());
        labelTabConfigPortIndex.setText("" + port.getIndex());
        labelTabConfigPortName.setText("Port " + port.getIndex());
        labelTabConfigPortOwner.setText(port.getOwner());
        labelTabConfigPortSpeed.setText("" + port.getSpeed());
        labelTabConfigPortStatus.setText(port.getStatus());

        labelTabConfigPortPromiscuous.setText(port.getAttr().getPromiscuous().toString());
        buttonTabConfigPortPromiscuous.setText(port.getAttr().getPromiscuous().toString().compareToIgnoreCase("enabled")==0 ? "Disable" : "Enable");

        String str = port.getAttr().getFc().toString();
        choiceTabConfigPortFlowControl.getSelectionModel().select(str.substring(0, 1).toUpperCase() + str.substring(1));
        if (!port.isIs_led_supported()) {
            labelTabConfigPortLED.setText("NOT SUPPORTED");
            buttonTabConfigPortLED.setVisible(false);
        }
        else if (port.getAttr().getLed() != null) {
            labelTabConfigPortLED.setText(port.getAttr().getLed().toString());
            buttonTabConfigPortLED.setText(port.getAttr().getLed().toString().compareToIgnoreCase("on")==0 ? "Off" : "On");
        }
        else {
            labelTabConfigPortLED.setText("N/A");
            buttonTabConfigPortLED.setText("On");
        }
        labelTabConfigPortLink.setText(port.getAttr().getLink().toString());
        buttonTabConfigPortLink.setText(port.getAttr().getLink().toString().compareToIgnoreCase("up")==0 ? "Down" : "Up");

        buttonTabConfigPortAcquireRelease.setText(port.getOwner()==null || port.getOwner().compareTo("")==0 ? "Acquire port" : "Release port");


        // events
        buttonTabConfigPortAcquireRelease.setOnAction((e) -> {
            if (buttonTabConfigPortAcquireRelease.getText().startsWith("Acquire")) {
                try {
                    serverRPCMethods.acquireServerPort(port.getIndex(), false);
                    buttonTabConfigPortAcquireRelease.setText("Release port");
                } catch (PortAcquireException ex) {
                    LOG.error("Error aquiring port " + port.getIndex() + ": " + ex.getMessage());
                }
            }
            else {
                serverRPCMethods.releasePort(port.getIndex(), true);
                buttonTabConfigPortAcquireRelease.setText("Acquire port");
            }
        });

        buttonTabConfigPortLink.setOnAction((e) -> {
            try {
                if (port.getAttr().getLink().getUp()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), false, null, null, null);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), true, null, null, null);
                }
            } catch (Exception ex) {
                LOG.error("Error changing link status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        buttonTabConfigPortPromiscuous.setOnAction((e) -> {
            try {
                if (port.getAttr().getPromiscuous().getEnabled()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, false, null, null);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, true, null, null);
                }
            } catch (Exception ex) {
                LOG.error("Error changing promiscuous status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        buttonTabConfigPortLED.setOnAction((e) -> {
            try {
                if (port.getAttr().getLed().getOn()) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, false, null);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, true, null);
                }
            } catch (Exception ex) {
                LOG.error("Error changing promiscuous status of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });

        choiceTabConfigPortFlowControl.setOnAction((e) -> {
            try {
                if (true) {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 0);
                }
                else {
                    serverRPCMethods.setPortAttribute(port.getIndex(), null, null, null, 1);
                }
            } catch (Exception ex) {
                LOG.error("Error changing flow control mode of port " + port.getIndex() + ": " + ex.getMessage());
            }
        });
    }

}

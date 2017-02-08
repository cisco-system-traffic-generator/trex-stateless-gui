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
    @FXML private Text textTabConfigPortNameTitle;
    @FXML private Label labelTabConfigPortName;
    @FXML private Label labelTabConfigPortIndex;
    @FXML private Label labelTabConfigPortDriver;
    @FXML private Label labelTabConfigPortOwner;
    @FXML private Label labelTabConfigPortSpeed;
    @FXML private Label labelTabConfigPortStatus;
    @FXML private Label labelTabConfigPortPromiscuous;
    @FXML private ChoiceBox choiceTabConfigPortFlowControl;
    @FXML private Label labelTabConfigPortLink;
    @FXML private Label labelTabConfigPortLED;
    @FXML private Label labelTabConfigPortCapturing;

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

        textTabConfigPortNameTitle.setText("Port " + port.getIndex());
        labelTabConfigPortDriver.setText(port.getDriver());
        labelTabConfigPortIndex.setText("" + port.getIndex());
        labelTabConfigPortName.setText("Port " + port.getIndex());
        labelTabConfigPortOwner.setText(port.getOwner());
        labelTabConfigPortSpeed.setText("" + port.getSpeed());
        labelTabConfigPortStatus.setText(port.getStatus());

        labelTabConfigPortPromiscuous.setText(port.getAttr().getPromiscuous().toString());
        String str = port.getAttr().getFc().toString();
        choiceTabConfigPortFlowControl.getSelectionModel().select(str.substring(0, 1).toUpperCase() + str.substring(1));
        if (!port.isIs_led_supported()) {
            labelTabConfigPortLED.setText("NOT SUPPORTED");
        }
        else if (port.getAttr().getLed() != null) {
            labelTabConfigPortLED.setText(port.getAttr().getLed().toString());
        }
        else {
            labelTabConfigPortLED.setText("N/A");
        }
        labelTabConfigPortLink.setText(port.getAttr().getLink().toString());
    }

}

package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.ui.models.FlowControl;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.ui.views.logs.LogType;
import com.exalttech.trex.ui.views.logs.LogsController;
import com.exalttech.trex.util.Initialization;
import com.google.common.base.Strings;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import java.util.Arrays;

public class PortAttributes extends BorderPane {

    private static final Logger logger = Logger.getLogger(PortAttributes.class);

    private LogsController guiLogger = LogsController.getInstance();
    
    private final RPCMethods trexClient = TrexApp.injector.getInstance(RPCMethods.class);

    private RPCMethods serverRPCMethods;
    
    private PortModel port;
    
    @FXML
    private Label driver;

    @FXML
    private Label rxFilterMode;

    @FXML
    private ToggleSwitch multicast;

    @FXML
    private ToggleSwitch promiscuousMode;

    @FXML
    private ToggleSwitch serviceMode;

    @FXML
    private Label owner;

    @FXML
    private Label speed;

    @FXML
    private Label status;

    @FXML
    private Label captureStatus;

    @FXML
    private ToggleSwitch link;

    @FXML
    private ToggleSwitch led;

    @FXML
    private Label numaMode;

    @FXML
    private Label pciAddress;

    @FXML
    private Label gratArp;

    @FXML
    private ChoiceBox<FlowControl> flowControl;
    
    @FXML
    private Button acquireReleaseBtn;
    @FXML
    private Button forceAcquireBtn;
    
    
    
    public PortAttributes() {
        
        serverRPCMethods = TrexApp.injector.getInstance(RPCMethods.class);
        
        Initialization.initializeFXML(this, "/fxml/ports/PortAttributes.fxml");

        acquireReleaseBtn.setOnAction(event -> {
            try {
                acquireReleaseBtn.setDisable(true);
                forceAcquireBtn.setDisable(true);
                if (acquireReleaseBtn.getText().equalsIgnoreCase("Acquire")) {
                    trexClient.acquireServerPort(port.getIndex(), false);
                    port.setIsOwned(true);
                    acquireReleaseBtn.setText("Release");
                } else {
                    trexClient.releasePort(port.getIndex(), true);
                    acquireReleaseBtn.setText("Acquire");
                    forceAcquireBtn.setDisable(false);
                    port.setIsOwned(false);
                }
                acquireReleaseBtn.setDisable(false);
                
            } catch (Exception e) {
                acquireReleaseBtn.setDisable(false);
                forceAcquireBtn.setDisable(false);
                
                String message = "Unable acquire port " + port.getIndex();
                logger.error(message, e);
                LogsController.getInstance().appendText(LogType.ERROR, message);
                
            }
        });
        
        forceAcquireBtn.setOnAction(event -> {
            try {
                acquireReleaseBtn.setDisable(true);
                forceAcquireBtn.setDisable(true);
                trexClient.acquireServerPort(port.getIndex(), true);
                acquireReleaseBtn.setDisable(false);
                acquireReleaseBtn.setText("Release");
                port.setIsOwned(true);
            } catch (Exception e) {
                acquireReleaseBtn.setDisable(false);
                forceAcquireBtn.setDisable(false);

                String message = "Unable acquire port " + port.getIndex();
                logger.error(message, e);
                LogsController.getInstance().appendText(LogType.ERROR, message);

            }
        });

        flowControl.getSelectionModel().selectedItemProperty().addListener((observable , oldVal, newVal) -> port.flowControlProperty().setValue(newVal));
        multicast.selectedProperty().addListener((observable , oldVal, newVal) -> port.multicastProperty().setValue(newVal));
        promiscuousMode.selectedProperty().addListener((observable, oldVal, newVal) -> port.promiscuousModeProperty().setValue(newVal));
        serviceMode.selectedProperty().addListener((observable, oldVal, newVal) -> port.serviceModeProperty().setValue(newVal));
        link.selectedProperty().addListener((observable , oldVal, newVal) -> port.linkStatusProperty().setValue(newVal));
        led.selectedProperty().addListener((observable , oldVal, newVal) -> port.ledControlProperty().setValue(newVal));
    }

    public void bindModel(PortModel model) {

        unbindPrevious();
        
        this.port = model;
        
        acquireReleaseBtn.setDisable(false);
        forceAcquireBtn.setDisable(false);
        
        if (this.port.isOwnedProperty().get()) {
            acquireReleaseBtn.setText("Release");
            forceAcquireBtn.setDisable(true);
        } else {
            acquireReleaseBtn.setText("Acquire");
            acquireReleaseBtn.setDisable(!Strings.isNullOrEmpty(this.port.getOwner()));
        }


        driver.textProperty().bind(model.portDriverProperty());
        rxFilterMode.textProperty().bind(model.rxFilterModeProperty());
        owner.textProperty().bind(model.ownerProperty());
        speed.textProperty().bind(model.portSpeedProperty());
        status.textProperty().bind(model.portStatusProperty());
        captureStatus.textProperty().bind(model.capturingModeProperty());
        numaMode.textProperty().bind(model.numaModeProperty());
        pciAddress.textProperty().bind(model.numaModeProperty());
        gratArp.textProperty().bind(model.gratARPProperty());

        multicast.selectedProperty().set(model.getMulticast());
        promiscuousMode.selectedProperty().set(model.getPromiscuousMode());
        link.selectedProperty().set(model.getLinkStatus());
        led.selectedProperty().set(model.getLedControl());
        serviceMode.selectedProperty().set(model.getServiceMode());
        
        Arrays.asList(
                link,
                led,
                promiscuousMode,
                multicast,
                flowControl,
                serviceMode
        ).forEach(control -> {
            
            if (!port.isOwnedProperty().get()) {
                control.setDisable(true);
            } else {
                control.setDisable(!port.getSupport(control.getId()).get());
            }
            
            control.disableProperty().bind(Bindings.or(
                    port.isOwnedProperty().not(),
                    port.getSupport(control.getId()).not()
            ));
        });

    }

    private void unbindPrevious() {
        Arrays.asList(
            driver,
            rxFilterMode,
            owner,
            speed,
            status,
            captureStatus,
            numaMode,
            pciAddress,
            gratArp
        ).forEach(label -> label.textProperty().unbind());
        
        port = null;

        Arrays.asList(
            link,
            led,
            promiscuousMode,
            serviceMode,
            multicast,
            flowControl,
            serviceMode
        ).forEach(control -> control.disableProperty().unbind());
    }
}

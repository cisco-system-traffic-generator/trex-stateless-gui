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

    private final RPCMethods trexClient = TrexApp.injector.getInstance(RPCMethods.class);
    
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
        multicast.selectedProperty().bindBidirectional(model.multicastProperty());
        promiscuousMode.selectedProperty().bindBidirectional(model.promiscuousModeProperty());
        owner.textProperty().bind(model.ownerProperty());
        speed.textProperty().bind(model.portSpeedProperty());
        status.textProperty().bind(model.portStatusProperty());
        captureStatus.textProperty().bind(model.capturingModeProperty());
        link.selectedProperty().bindBidirectional(model.linkStatusProperty());
        led.selectedProperty().bindBidirectional(model.ledControlSupportProperty());
        numaMode.textProperty().bind(model.numaModeProperty());
        pciAddress.textProperty().bind(model.numaModeProperty());
        gratArp.textProperty().bind(model.gratARPProperty());

        Arrays.asList(
                link,
                led,
                promiscuousMode,
                multicast,
                flowControl
        ).forEach(control -> {
            control.setDisable(port.isOwnedProperty().get());
            control.disableProperty().bind(
                    Bindings.and(
                        port.isOwnedProperty(),
                        port.getSupport(control.getId())
                    ).not()
            );
        });
    }

    private void unbindPrevious() {
        driver.textProperty().unbind();
        rxFilterMode.textProperty().unbind();
        multicast.selectedProperty().unbind();
        promiscuousMode.selectedProperty().unbind();
        owner.textProperty().unbind();
        speed.textProperty().unbind();
        status.textProperty().unbind();
        captureStatus.textProperty().unbind();
        link.selectedProperty().unbind();
        led.selectedProperty().unbind();
        numaMode.textProperty().unbind();
        pciAddress.textProperty().unbind();
        gratArp.textProperty().unbind();
        port = null;

        Arrays.asList(
            link,
            led,
            promiscuousMode,
            multicast,
            flowControl
        ).forEach(control -> control.disableProperty().unbind());
    }
}

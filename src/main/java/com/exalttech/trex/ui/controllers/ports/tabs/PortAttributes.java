package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.ui.models.FlowControl;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.ToggleSwitch;

public class PortAttributes extends BorderPane {
    
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
    
    public PortAttributes() {
        Initialization.initializeFXML(this, "/fxml/ports/PortAttributes.fxml");
    }

    public void bindModel(PortModel model) {
        unbindPrevious();
        
        driver.textProperty().bind(model.portDriverProperty());
        rxFilterMode.textProperty().bind(model.rxFilterModeProperty());
        multicast.selectedProperty().bindBidirectional(model.multicastProperty());
        promiscuousMode.selectedProperty().bindBidirectional(model.promiscuousModeProperty());
        owner.textProperty().bind(model.ownerProperty());
        speed.textProperty().bind(model.ownerProperty());
        status.textProperty().bind(model.portStatusProperty());
        captureStatus.textProperty().bind(model.capturingModeProperty());
        link.selectedProperty().bindBidirectional(model.linkStatusProperty());
        led.selectedProperty().bindBidirectional(model.ledStatusProperty());
        numaMode.textProperty().bind(model.numaModeProperty());
        pciAddress.textProperty().bind(model.numaModeProperty());
        gratArp.textProperty().bind(model.gratARPProperty());
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
    }
}

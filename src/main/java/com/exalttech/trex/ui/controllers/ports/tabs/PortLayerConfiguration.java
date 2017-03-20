package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.ui.models.ConfigurationMode;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.util.Initialization;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.util.Arrays;

public class PortLayerConfiguration extends BorderPane {
    @FXML
    private AnchorPane root;
    @FXML
    private ToggleGroup mode;

    @FXML
    private TextField l2Source;
    @FXML
    private TextField l3Source;

    @FXML
    private TextField l2Destination;

    @FXML
    private TextField l3Destination;

    @FXML
    private Label pingLabel;

    @FXML
    private TextField pingDestination;

    @FXML
    private Button pingCommandBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Label arpStatus;

    @FXML
    private Label arpLabel;

    @FXML
    RadioButton l2Mode;

    @FXML
    RadioButton l3Mode;
    private PortModel model;

    private ChangeListener<ConfigurationMode> configurationModeChangeListener = (observable, prevMode, mode) -> updateControlsState();

    private void updateControlsState() {
        Arrays.asList(l2Source, l2Destination, l3Source, l3Destination).forEach(textField -> {
            textField.setVisible(false);
            textField.setManaged(false);
        });
        
        if (ConfigurationMode.L2.equals(model.getLayerMode())) {
            l2Source.setVisible(true);
            l2Destination.setVisible(true);
            l2Source.setManaged(true);
            l2Destination.setManaged(true);
            
            l2Mode.setSelected(true);
            arpStatus.setVisible(false);
            arpLabel.setVisible(false);
            pingLabel.setVisible(false);
            pingDestination.setVisible(false);
            pingCommandBtn.setVisible(false);
        } else {
            l3Source.setVisible(true);
            l3Destination.setVisible(true);
            l3Source.setManaged(true);
            l3Destination.setManaged(true);
            arpStatus.textProperty().bind(model.getL3LayerConfiguration().stateProperty());
            l3Mode.setSelected(true);
            arpLabel.setVisible(true);
            arpStatus.setVisible(true);
            pingLabel.setVisible(true);
            pingDestination.setVisible(true);
            pingCommandBtn.setVisible(true);
        }
    }

    public PortLayerConfiguration() {
        Initialization.initializeFXML(this, "/fxml/ports/PortLayerConfiguration.fxml");
        
        l2Mode.setOnAction(event -> model.setLayerMode(ConfigurationMode.L2));
        
        l3Mode.setOnAction(event -> model.setLayerMode(ConfigurationMode.L3));
    }

    public void bindModel(PortModel model) {
        unbindAll();
        this.model = model;
        
        l2Destination.textProperty().bindBidirectional(this.model.getL2LayerConfiguration().dstProperty());
        l2Source.textProperty().bindBidirectional(this.model.getL2LayerConfiguration().srcProperty());
        
        l3Destination.textProperty().bindBidirectional(this.model.getL3LayerConfiguration().dstProperty());
        l3Source.textProperty().bindBidirectional(this.model.getL3LayerConfiguration().srcProperty());
        
        updateControlsState();
        
        this.model.layerConfigurationTypeProperty().addListener(configurationModeChangeListener);
    }

    private void unbindAll() {
        if(model == null) {
            return;
        }
        l2Destination.textProperty().unbind();
        l2Source.textProperty().unbind();
        l3Destination.textProperty().bindBidirectional(this.model.getL3LayerConfiguration().dstProperty());
        l3Source.textProperty().unbind();
        arpStatus.textProperty().unbind();
        model.layerConfigurationTypeProperty().removeListener(configurationModeChangeListener);
    }
}

package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.ui.models.ConfigurationMode;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.util.Initialization;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class PortLayerConfiguration extends BorderPane {
    @FXML
    private AnchorPane root;

    @FXML
    private ToggleGroup mode;

    @FXML
    private TextField source;
    
    @FXML
    private TextField destination;

    @FXML
    private Label pingLabel;
    
    @FXML
    private TextField pingDestination;
    
    @FXML
    private Button pingCommandBtn;
    @FXML
    private Label arpStatus;
    @FXML
    private Label arpLabel;

    @FXML
    RadioButton l2Mode;
    @FXML
    RadioButton l3Mode;
    
    private PortModel model;
    
    private ChangeListener<ConfigurationMode> configurationModeChangeListener = (observable, prevMode, mode) -> updateControlsState(mode);

    private void updateControlsState(ConfigurationMode type) {
        source.setText(model.getLayerConfiguration().getSrc());
        destination.setText(model.getLayerConfiguration().getDst());
        
        if (ConfigurationMode.L2.equals(type)) {
            l2Mode.setSelected(true);
            source.setDisable(true);
            arpStatus.setVisible(false);
            arpLabel.setVisible(false);
            pingLabel.setVisible(false);
            pingDestination.setVisible(false);
            pingCommandBtn.setVisible(false);
        } else {
            arpStatus.textProperty().bind(model.getLayerConfiguration().stateProperty());
            l3Mode.setSelected(true);
            source.setDisable(false);
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
        
        updateControlsState(this.model.getLayerConfiguration().getType());
        
        this.model.layerConfigurationTypeProperty().addListener(configurationModeChangeListener);
    }

    
    
    private void unbindAll() {
        if(model == null) {
            return;
        }
        source.textProperty().unbind();
        destination.textProperty().unbind();
        arpStatus.textProperty().unbind();
        model.layerConfigurationTypeProperty().removeListener(configurationModeChangeListener);
    }
}

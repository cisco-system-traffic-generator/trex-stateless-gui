package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.ui.components.GlobalPortFilter;
import com.exalttech.trex.ui.dialog.DialogView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class DashboardController extends DialogView {

    private GlobalPortFilter portFilter = TrexApp.injector.getInstance(GlobalPortFilter.class);

    @FXML private ComboBox<String> portFilterSelector; 
    
    public void handlePortFilterSelection(Event event) {
        String val = portFilterSelector.getSelectionModel().getSelectedItem();
        portFilter.onlyOwnedPortsProperty().setValue(!"All".equals(val));
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        
    }
}

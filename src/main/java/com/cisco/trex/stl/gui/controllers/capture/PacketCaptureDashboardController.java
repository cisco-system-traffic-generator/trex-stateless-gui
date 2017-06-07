package com.cisco.trex.stl.gui.controllers.capture;

import com.exalttech.trex.ui.dialog.DialogView;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class PacketCaptureDashboardController extends DialogView {
    @FXML
    private MonitorController monitorController;
    
    @FXML
    private RecordController recordController;

    public PacketCaptureDashboardController() {
        
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        // Nothing to do
    }
}

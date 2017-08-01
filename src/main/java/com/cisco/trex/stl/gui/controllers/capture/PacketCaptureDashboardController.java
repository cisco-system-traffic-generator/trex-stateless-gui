package com.cisco.trex.stl.gui.controllers.capture;

import com.exalttech.trex.ui.dialog.DialogView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PacketCaptureDashboardController extends DialogView implements Initializable {

    @FXML
    private Label startMonitorBtn;
    
    @FXML
    private Label stopMonitorBtn;
    
    @FXML
    private Label clearMonitorBtn;
    
    @FXML
    private Label startWiresharkBtn;
    
    @FXML
    private Label startRecorderBtn;
    
    @FXML
    private TabPane captureTabPane;
    
    @FXML
    private HBox buttonBar;
    
    @FXML
    private MonitorController monitorController;
    
    @FXML
    private RecordController recordController;

    @Override
    public void onEnterKeyPressed(Stage stage) {
        // Nothing to do
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        captureTabPane.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            boolean isMonitorTabSelected = newVal.getText().equalsIgnoreCase("Monitor");
            
            updateButtonBar(isMonitorTabSelected);
        });
        updateButtonBar(true);
    }

    private void updateButtonBar(boolean isMonitorTabSelected) {
        startMonitorBtn.setDisable(!isMonitorTabSelected);
        stopMonitorBtn.setDisable(!isMonitorTabSelected);
        clearMonitorBtn.setDisable(!isMonitorTabSelected);
        startWiresharkBtn.setDisable(!isMonitorTabSelected);
        startRecorderBtn.setDisable(isMonitorTabSelected);
    }

}

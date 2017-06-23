package com.cisco.trex.stl.gui.controllers.capture;

import com.cisco.trex.stateless.model.capture.CapturedPkt;
import com.cisco.trex.stl.gui.models.CapturedPktModel;
import com.cisco.trex.stl.gui.services.capture.PktCaptureService;
import com.cisco.trex.stl.gui.services.capture.PktCaptureServiceException;
import com.exalttech.trex.util.Initialization;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class MonitorController extends BorderPane {

    @FXML
    private Button startBtn;
    
    @FXML
    private Button stopBtn;
    @FXML
    private Button clearBtn;
    
    @FXML
    private PortFilterController portFilter;
    
    @FXML
    private TableView<CapturedPktModel> capturedPkts;

    @FXML
    private TableColumn<CapturedPktModel, String> number;
    
    @FXML
    private TableColumn<CapturedPktModel, String> port;
    
    @FXML
    private TableColumn<CapturedPktModel, String> mode;
    
    @FXML
    private TableColumn<CapturedPktModel, String> time;
    
    @FXML
    private TableColumn<CapturedPktModel, String> ipDst;
    
    @FXML
    private TableColumn<CapturedPktModel, String> ipSrc;
    
    @FXML
    private TableColumn<CapturedPktModel, String> type;
    
    @FXML
    private TableColumn<CapturedPktModel, String> length;
    
    @FXML
    private TableColumn<CapturedPktModel, String> info;
    
    private PktCaptureService pktCaptureService = new PktCaptureService();
    
    public MonitorController() {
        Initialization.initializeFXML(this, "/fxml/pkt_capture/Monitor.fxml");

        number.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asString());
        port.setCellValueFactory(cellData -> cellData.getValue().portProperty().asString());
        mode.setCellValueFactory(cellData -> cellData.getValue().modeProperty());
        time.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        ipDst.setCellValueFactory(cellData -> cellData.getValue().ipDstProperty());
        ipSrc.setCellValueFactory(cellData -> cellData.getValue().ipSrcProperty());
        type.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        length.setCellValueFactory(cellData -> cellData.getValue().lengthProperty().asString());
        info.setCellValueFactory(cellData -> cellData.getValue().infoProperty());
        
        pktCaptureService.setOnSucceeded(this::handleOnPktsReceived);
        
        startBtn.setOnAction(this::handleStartMonitorAction);
        stopBtn.setOnAction(this::handleStopMonitorAction);
        clearBtn.setOnAction(this::handleClearMonitorAction);
    }

    private void handleOnPktsReceived(WorkerStateEvent workerStateEvent) {
        synchronized (capturedPkts) {
            pktCaptureService.getValue().getPkts().stream().map(this::toModel).forEach(pktModel -> capturedPkts.getItems().add(pktModel));
        }
    }

    public void handleStartMonitorAction(ActionEvent event) {
        try {
            pktCaptureService.reset();
            pktCaptureService.startMonitor(portFilter.getRxPorts(), portFilter.getTxPorts());
        } catch (PktCaptureServiceException e) {
            // TODO: logger
        }
    }
    
    public void handleStopMonitorAction(ActionEvent event) {
        pktCaptureService.stopMonitor();
        pktCaptureService.cancel();
    }
    
    public void handleClearMonitorAction(ActionEvent event) {
        synchronized (capturedPkts) {
            capturedPkts.getItems().clear();
        }
    }
    
    private CapturedPktModel toModel(CapturedPkt pkt) {
        return new CapturedPktModel(pkt.getIndex(), pkt.getPort(), pkt.getOrigin(),  0.000001, "0.0.0.0", "0.0.0.0", "?", 64, "?");
    }
}

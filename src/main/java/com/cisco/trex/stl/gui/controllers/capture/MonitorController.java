package com.cisco.trex.stl.gui.controllers.capture;

import com.cisco.trex.stl.gui.models.CapturedPkt;
import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class MonitorController extends BorderPane {
    
    @FXML
    private TableView<CapturedPkt> capturedPkts;

    @FXML
    private TableColumn<CapturedPkt, String> number;
    @FXML
    private TableColumn<CapturedPkt, String> port;
    @FXML
    private TableColumn<CapturedPkt, String> mode;
    @FXML
    private TableColumn<CapturedPkt, String> time;
    @FXML
    private TableColumn<CapturedPkt, String> ipDst;
    @FXML
    private TableColumn<CapturedPkt, String> ipSrc;
    @FXML
    private TableColumn<CapturedPkt, String> type;
    @FXML
    private TableColumn<CapturedPkt, String> length;
    @FXML
    private TableColumn<CapturedPkt, String> info;
    
    
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
        
        capturedPkts.getItems().addAll(
            new CapturedPkt(1, 1, "RX",  0.000001, "1.1.1.1", "1.1.1.22", "UDP", 64, "Ethernet/IPV4/UDP"),
            new CapturedPkt(2, 1, "RX",  0.000002, "1.1.1.1", "1.1.1.23", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(3, 1, "RX",  0.000003, "1.1.1.1", "1.1.1.24", "UDP", 64, "Ethernet/IPV4/UDP"),
            new CapturedPkt(4, 1, "RX",  0.000004, "1.1.1.1", "1.1.1.25", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(5, 1, "RX",  0.000005, "1.1.1.1", "1.1.1.26", "UDP", 64, "Ethernet/IPV4/UDP"),
            new CapturedPkt(6, 1, "RX",  0.000006, "1.1.1.1", "1.1.1.27", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(7, 1, "RX",  0.000007, "1.1.1.1", "1.1.1.28", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(8, 1, "RX",  0.000008, "1.1.1.1", "1.1.1.29", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(9, 1, "RX",  0.000009, "1.1.1.1", "1.1.1.10", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(10, 1, "RX",  0.000011, "1.1.1.1", "1.1.1.11", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(12, 1, "RX",  0.000021, "1.1.1.1", "1.1.1.12", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(13, 1, "RX",  0.000031, "1.1.1.1", "1.1.1.13", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(14, 1, "RX",  0.000041, "1.1.1.1", "1.1.1.14", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(15, 1, "RX",  0.000051, "1.1.1.1", "1.1.1.15", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(16, 1, "RX",  0.000061, "1.1.1.1", "1.1.1.16", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(17, 1, "RX",  0.000071, "1.1.1.1", "1.1.1.17", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(18, 1, "RX",  0.000081, "1.1.1.1", "1.1.1.18", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(19, 1, "RX",  0.000081, "1.1.1.1", "1.1.1.19", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(20, 1, "RX",  0.000091, "1.1.1.1", "1.1.1.20", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(21, 1, "RX",  0.000021, "1.1.1.1", "1.1.1.21", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(22, 1, "RX",  0.000031, "1.1.1.1", "1.1.1.22", "TCP", 64, "Ethernet/IPV4/TCP"),
            new CapturedPkt(23, 1, "RX",  0.000041, "1.1.1.1", "2.2.2.25", "UDP", 64, "Ethernet/IPV4/UDP")
        );
    }
}

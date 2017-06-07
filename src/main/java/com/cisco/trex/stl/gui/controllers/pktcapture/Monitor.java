package com.cisco.trex.stl.gui.controllers.pktcapture;

import com.cisco.trex.stl.gui.models.CapturedPkt;
import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class Monitor extends BorderPane {
    
    @FXML
    private TableView<CapturedPkt> capturedPkts;

    @FXML
    private TableColumn<CapturedPkt, String> type;
    @FXML
    private TableColumn<CapturedPkt, String> length;
    @FXML
    private TableColumn<CapturedPkt, String> hwSrc;
    @FXML
    private TableColumn<CapturedPkt, String> hwDst;
    @FXML
    private TableColumn<CapturedPkt, String> ipSrc;
    @FXML
    private TableColumn<CapturedPkt, String> ipDst;
    
    
    public Monitor() {
        Initialization.initializeFXML(this, "/fxml/pkt_capture/Monitor.fxml");

        type.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        length.setCellValueFactory(cellData -> cellData.getValue().lengthProperty().asString());
        hwSrc.setCellValueFactory(cellData -> cellData.getValue().hwSrcProperty());
        hwDst.setCellValueFactory(cellData -> cellData.getValue().hwDstProperty());
        ipSrc.setCellValueFactory(cellData -> cellData.getValue().ipSrcProperty());
        ipDst.setCellValueFactory(cellData -> cellData.getValue().ipDstProperty());
        
        capturedPkts.getItems().addAll(
            new CapturedPkt("Ethernet/IPV4/UDP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:00", "1.1.1.1", "1.1.1.2"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:01", "1.1.1.1", "1.1.1.3"),
            new CapturedPkt("Ethernet/IPV4/UDP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:02", "1.1.1.1", "1.1.1.4"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:03", "1.1.1.1", "1.1.1.5"),
            new CapturedPkt("Ethernet/IPV4/UDP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:04", "1.1.1.1", "1.1.1.6"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:05", "1.1.1.1", "1.1.1.7"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:06", "1.1.1.1", "1.1.1.8"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:07", "1.1.1.1", "1.1.1.9"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:08", "1.1.1.1", "1.1.1.10"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:09", "1.1.1.1", "1.1.1.11"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:0a", "1.1.1.1", "1.1.1.12"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:0b", "1.1.1.1", "1.1.1.13"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:0c", "1.1.1.1", "1.1.1.14"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:0d", "1.1.1.1", "1.1.1.15"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:0e", "1.1.1.1", "1.1.1.16"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:0f", "1.1.1.1", "1.1.1.17"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:10", "1.1.1.1", "1.1.1.18"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:11", "1.1.1.1", "1.1.1.19"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:12", "1.1.1.1", "1.1.1.20"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:14", "1.1.1.1", "1.1.1.21"),
            new CapturedPkt("Ethernet/IPV4/TCP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:15", "1.1.1.1", "1.1.1.22"),
            new CapturedPkt("Ethernet/IPV4/UDP", 64, "aa:aa:aa:aa:aa:aa", "aa:aa:aa:aa:aa:16", "1.1.1.1", "255.255.255.255")
        );
    }
}

package com.cisco.trex.stl.gui.controllers;

import com.cisco.trex.stl.gui.models.Recorder;
import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class Record extends AnchorPane {

    @FXML
    private PortFilter portFilter;
    
    @FXML
    private TextField limit;
    
    @FXML
    private TableView<Recorder> activeRecorders;

    @FXML
    private TableColumn<Recorder, String> id;
    @FXML
    private TableColumn<Recorder, String> status;
    @FXML
    private TableColumn<Recorder, String> packets;
    @FXML
    private TableColumn<Recorder, String> bytes;
    @FXML
    private TableColumn<Recorder, String> rxFilter;
    @FXML
    private TableColumn<Recorder, String> txFilter;
    
    public Record() {
        Initialization.initializeFXML(this, "/fxml/pkt_capture/Record.fxml");
        
        id.setCellValueFactory(cellData -> cellData.getValue().idProperty().asString());
        status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        packets.setCellValueFactory(cellData -> cellData.getValue().packetsProperty());
        bytes.setCellValueFactory(cellData -> cellData.getValue().bytesProperty().asString());
        rxFilter.setCellValueFactory(cellData -> cellData.getValue().rxFilterProperty());
        txFilter.setCellValueFactory(cellData -> cellData.getValue().txFilterProperty());

        activeRecorders.getItems().addAll(
            new Recorder(1, "Active", "1/1000", 64, "-", "0"),
            new Recorder(2, "Active", "1/1000", 64, "0, 1", "-"),
            new Recorder(3, "Active", "1/1000", 64, "0, 1", "1"),
            new Recorder(4, "Active", "1/1000", 64, "1", "0, 1")
        );
    }
}

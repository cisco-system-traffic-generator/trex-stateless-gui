package com.cisco.trex.stl.gui.controllers.capture;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.util.Initialization;
import com.xored.javafx.packeteditor.controllers.FieldEditorController;
import com.xored.javafx.packeteditor.view.FieldEditorView;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.apache.log4j.Logger;

import java.io.IOException;

public class PacketViewer extends BorderPane {
    private static Logger LOG = Logger.getLogger(PacketViewer.class);

    @FXML
    private BorderPane root;
    
    @FXML
    private FlowPane fieldEditorTopPane;
    
    @FXML
    private StackPane fieldEditorCenterPane;
    
    @FXML
    private StackPane  fieldEditorBottomPane;
    
    @FXML
    private ScrollPane fieldEditorScrollPane;
    
    private FieldEditorController fieldEditorController = TrexApp.injector.getInstance(FieldEditorController.class);
    
    public PacketViewer() {
        Initialization.initializeFXML(this, "/fxml/pkt_capture/PacketViewer.fxml");

        FieldEditorView fieldEditorView = fieldEditorController.getFieldEditorView();
        fieldEditorController.setFieldEditorScrollPane(fieldEditorScrollPane);
        fieldEditorController.setFieldEditorBorderPane(root);
        fieldEditorView.setRootPane(fieldEditorCenterPane);
        fieldEditorView.setBreadCrumbPane(fieldEditorTopPane);
        fieldEditorView.setBottomPane(fieldEditorBottomPane);
    }


    public void showPkt(byte[] bytes) throws IOException {
        try {
            fieldEditorController.setViewOnly(true);
            fieldEditorController.loadPcapBinary(bytes);
        } finally {
            fieldEditorController.setViewOnly(false);
        }
    }
}

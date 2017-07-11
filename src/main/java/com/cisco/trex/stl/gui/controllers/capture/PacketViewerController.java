package com.cisco.trex.stl.gui.controllers.capture;

import com.exalttech.trex.ui.dialog.DialogKeyPressHandler;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class PacketViewerController implements DialogKeyPressHandler {
    private static Logger LOG = Logger.getLogger(PacketViewerController.class);

    @FXML
    private PacketViewer packetViewer;

    public PacketViewer getPacketViewer() {
        return packetViewer;
    }

    @Override
    public void onEscapKeyPressed() {
        
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {

    }

    @Override
    public void setupStage(Stage stage) {

    }
}

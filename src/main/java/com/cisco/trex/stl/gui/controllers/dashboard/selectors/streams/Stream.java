package com.cisco.trex.stl.gui.controllers.dashboard.selectors.streams;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import com.exalttech.trex.util.Initialization;


public class Stream extends HBox {
    @FXML
    private Label label;

    private final int pgid;
    private final EventHandler<Event> onAddClicked;

    public Stream(final int pgid, final EventHandler<Event> onAddClicked) {
        Initialization.initializeFXML(this, "/fxml/dashboard/selectors/streams/Stream.fxml");

        this.pgid = pgid;
        this.onAddClicked = onAddClicked;
        label.setText(String.format("PG ID - %d", pgid));
    }

    public int getPGId() {
        return pgid;
    }

    @FXML
    public void handleAddClicked(final MouseEvent event) {
        if (onAddClicked != null) {
            onAddClicked.handle(new Event(this, null, null));
        }
    }
}

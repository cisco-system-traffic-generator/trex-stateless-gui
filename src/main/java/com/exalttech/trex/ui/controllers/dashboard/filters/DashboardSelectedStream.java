package com.exalttech.trex.ui.controllers.dashboard.filters;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import com.exalttech.trex.util.Initialization;
import javafx.scene.layout.Pane;


public class DashboardSelectedStream extends HBox {
    @FXML
    private Pane legend;
    @FXML
    private Label label;
    @FXML
    private Label error;

    private final int pgid;
    private final EventHandler<Event> onDeleteClicked;

    public DashboardSelectedStream(
            final int pgid,
            final String color,
            final boolean isDeleted,
            final EventHandler<Event> onDeleteClicked
    ) {
        Initialization.initializeFXML(this, "/fxml/Dashboard/filters/DashboardSelectedStream.fxml");

        this.pgid = pgid;
        this.onDeleteClicked = onDeleteClicked;

        legend.setStyle(String.format("-fx-border-color: %s;", color));
        label.setText(String.format("PG ID - %d", pgid));
        error.setVisible(isDeleted);
    }

    public int getPGId() {
        return pgid;
    }

    @FXML
    public void handleDeleteClicked(final MouseEvent event) {
        if (onDeleteClicked != null) {
            onDeleteClicked.handle(new Event(this, null, null));
        }
    }
}

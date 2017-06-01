package com.exalttech.trex.ui.controllers.dashboard.filters;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.*;

import com.exalttech.trex.ui.views.services.ActivePGIDsService;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;


public class DashboardStreamsSelector extends GridPane {
    @FXML
    private GridPane root;
    @FXML
    private VBox selectedStreamsContainer;
    @FXML
    private VBox streamsContainer;

    private final Map<String, Boolean> legendColorMap = new LinkedHashMap<String, Boolean>(){{
        put("#f3622d", false);
        put("#fba71b", false);
        put("#57b757", false);
        put("#41a9c9", false);
        put("#4258c9", false);
        put("#9a42c8", false);
        put("#c84164", false);
        put("#888888", false);
    }};

    private ActivePGIDsService activePGIDsService = new ActivePGIDsService();
    private Set<Integer> pgids = new HashSet<>();
    private Map<Integer, String> selectedPGIds = null;
    private EventHandler<Event> onFiltersChanged;

    public DashboardStreamsSelector() {
        Initialization.initializeFXML(
                this,
                "/fxml/Dashboard/filters/DashboardStreamsSelector.fxml"
        );

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);

        activePGIDsService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        activePGIDsService.setOnSucceeded(this::handleStreamsReceived);
        activePGIDsService.start();
    }

    public EventHandler<Event> getOnFiltersChanged() {
        return onFiltersChanged;
    }

    public void setOnFiltersChanged(final EventHandler<Event> onFiltersChanged) {
        this.onFiltersChanged = onFiltersChanged;
    }

    public Map<Integer, String> getSelectedPGIds() {
        return selectedPGIds != null ? selectedPGIds : new HashMap<>();
    }

    private void handleStreamsReceived(final WorkerStateEvent event) {
        final Set<Integer> receivedPGIds = (Set<Integer>) event.getSource().getValue();

        if (receivedPGIds == null || pgids.equals(receivedPGIds)) {
            return;
        }

        pgids = receivedPGIds;

        if (!pgids.isEmpty() && selectedPGIds == null) {
            selectedPGIds = new HashMap<>();
            int count = 4;
            for (final int pgid : pgids) {
                selectedPGIds.put(pgid, holdColor());
                if (--count == 0) {
                    break;
                }
            }
            handleFiltersUpdated();
        }

        build();
    }

    private void onWindowCloseRequest(final WindowEvent window) {
        if (activePGIDsService.isRunning()) {
            activePGIDsService.cancel();
        }
    }

    private void build() {
        selectedStreamsContainer.getChildren().clear();
        streamsContainer.getChildren().clear();

        for (final Map.Entry<Integer, String> entry : selectedPGIds.entrySet()) {
            final Integer pgid = entry.getKey();
            selectedStreamsContainer.getChildren().add(
                    new DashboardSelectedStream(
                            pgid,
                            entry.getValue(),
                            !pgids.contains(pgid),
                            this::handleStreamDeleteClicked
                    )
            );
        }

        for (final int pgid : pgids) {
            if (selectedPGIds.get(pgid) == null) {
                streamsContainer.getChildren().add(new DashboardStream(pgid, this::handleStreamAddClicked));
            }
        }

        streamsContainer.setDisable(selectedPGIds.size() >= 8);
    }

    private void handleStreamDeleteClicked(final Event event) {
        final DashboardSelectedStream source = (DashboardSelectedStream) event.getSource();
        final Integer pgId = source.getPGId();
        freeColor(selectedPGIds.get(pgId));
        selectedPGIds.remove(pgId);
        build();
        handleFiltersUpdated();
    }

    private void handleStreamAddClicked(final Event event) {
        final DashboardStream source = (DashboardStream) event.getSource();
        selectedPGIds.put(source.getPGId(), holdColor());
        build();
        handleFiltersUpdated();
    }

    private void handleFiltersUpdated() {
        if (onFiltersChanged != null) {
            onFiltersChanged.handle(new Event(this, null, null));
        }
    }

    private String holdColor() {
        for (final Map.Entry<String, Boolean> entry : legendColorMap.entrySet()) {
            if (!entry.getValue()) {
                entry.setValue(true);
                return entry.getKey();
            }
        }
        return null;
    }

    private void freeColor(final String color) {
        legendColorMap.put(color, false);
    }
}

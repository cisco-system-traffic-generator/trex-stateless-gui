package com.cisco.trex.stl.gui.controllers.dashboard.selectors.streams;

import com.cisco.trex.stl.gui.storages.PGIDStatsStorage;
import com.exalttech.trex.application.TrexApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cisco.trex.stl.gui.storages.PGIDsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;

import com.exalttech.trex.util.Initialization;


public class StreamsSelectorController extends GridPane {
    private final String IDLE_STREAM = "Stream is idle";

    @FXML
    private GridPane root;
    @FXML
    private VBox selectedStreamsContainer;
    @FXML
    private VBox streamsContainer;

    private PGIDsStorage.PGIDsChangedListener pgIDsChangedListener = this::render;
    private PGIDStatsStorage.StatsChangedListener pgIDStatsChangedListener = this::render;

    private boolean isActive = false;
    private Map<Integer, String> selectedPGIDs = new HashMap<>();
    private Set<Integer> pgIDs = new HashSet<>();

    StatsStorage statsStorage = TrexApp.injector.getInstance(StatsStorage.class);

    public StreamsSelectorController() {
        Initialization.initializeFXML(
                this,
                "/fxml/dashboard/selectors/streams/StreamsSelector.fxml"
        );
        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    public void setActive(final boolean isActive) {
        if (this.isActive == isActive) {
            return;
        }

        final PGIDsStorage pgIdsStorage = statsStorage.getPGIDsStorage();
        final PGIDStatsStorage pgIdStatsStorage = statsStorage.getPGIDStatsStorage();

        this.isActive = isActive;
        if (this.isActive) {
            pgIdsStorage.addPGIDsChangedListener(pgIDsChangedListener);
            pgIdStatsStorage.addStatsChangeListener(pgIDStatsChangedListener);
            render();
        } else {
            pgIdsStorage.removePGIDsChangedListener(pgIDsChangedListener);
            pgIdStatsStorage.removeStatsChangeListener(pgIDStatsChangedListener);
        }
    }

    private void onWindowCloseRequest(final WindowEvent window) {
        setActive(false);
    }

    private void render() {
        final PGIDsStorage pgIDStorage = statsStorage.getPGIDsStorage();
        final PGIDStatsStorage pgIdStatsStorage = statsStorage.getPGIDStatsStorage();
        final Set<Integer> pgIDs = new HashSet<>(pgIDStorage.getPgIDs());
        final Map<Integer, String> selectedPGIDs = pgIDStorage.getSelectedPGIds();
        final Set<Integer> stoppedPGIds = pgIdStatsStorage.getStoppedPGIds();

        synchronized (pgIDStorage.getDataLock()) {
            synchronized (pgIdStatsStorage.getDataLock()) {
                if (selectedPGIDs != null) {
                    if (selectedPGIDs.equals(this.selectedPGIDs)) {
                        selectedStreamsContainer.getChildren().forEach((final Node node) -> {
                            final SelectedStreamController selectedStream = (SelectedStreamController) node;
                            selectedStream.setWarning(
                                    stoppedPGIds.contains(selectedStream.getPGId()) ? IDLE_STREAM : null
                            );
                        });
                    } else {
                        selectedStreamsContainer.getChildren().clear();

                        selectedPGIDs.forEach((final Integer pgID, final String color) -> {
                            selectedStreamsContainer.getChildren().add(
                                    new SelectedStreamController(
                                            pgID,
                                            color,
                                            stoppedPGIds.contains(pgID) ? IDLE_STREAM : null,
                                            this::handleStreamDeleteClicked
                                    )
                            );
                        });
                    }

                    this.selectedPGIDs = new HashMap<>(selectedPGIDs);
                } else {
                    selectedStreamsContainer.getChildren().clear();
                    this.selectedPGIDs = null;
                }

                if (selectedPGIDs != null) {
                    pgIDs.removeIf(selectedPGIDs::containsKey);
                }

                if (!pgIDs.equals(this.pgIDs)) {
                    streamsContainer.getChildren().clear();

                    for (final Integer pgID : pgIDs) {
                        if (selectedPGIDs == null || selectedPGIDs.get(pgID) == null) {
                            streamsContainer.getChildren().add(
                                    new StreamController(pgID, this::handleStreamAddClicked)
                            );
                        }
                    }
                }

                this.pgIDs = pgIDs;

                streamsContainer.setDisable(selectedPGIDs != null && selectedPGIDs.size() >= 8);
            }
        }
    }

    private void handleStreamDeleteClicked(final Event event) {
        final SelectedStreamController source = (SelectedStreamController) event.getSource();
        statsStorage.getPGIDsStorage().deselectPGID(source.getPGId());
    }

    private void handleStreamAddClicked(final Event event) {
        final StreamController source = (StreamController) event.getSource();
        statsStorage.getPGIDsStorage().selectPGID(source.getPGId());
    }
}

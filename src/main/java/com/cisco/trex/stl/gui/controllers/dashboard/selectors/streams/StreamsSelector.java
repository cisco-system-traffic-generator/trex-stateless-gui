package com.cisco.trex.stl.gui.controllers.dashboard.selectors.streams;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import java.util.Map;
import java.util.Set;

import com.cisco.trex.stl.gui.storages.PGIDsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;

import com.exalttech.trex.util.Initialization;


public class StreamsSelector extends GridPane {
    @FXML
    private GridPane root;
    @FXML
    private VBox selectedStreamsContainer;
    @FXML
    private VBox streamsContainer;

    private PGIDsStorage.SelectedPGIDsInitializedListener selectedPGIDsInitializedListener = this::render;
    private PGIDsStorage.SelectedPGIDsChangedListener selectedPGIDsChangedListener = this::render;
    private PGIDsStorage.PGIDsChangedListener pgIDsChangedListener = this::render;

    public StreamsSelector() {
        Initialization.initializeFXML(
                this,
                "/fxml/dashboard/selectors/streams/StreamsSelector.fxml"
        );

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);

        final PGIDsStorage pgIDStorage = StatsStorage.getInstance().getPGIDsStorage();
        pgIDStorage.addSelectedPGIDsInitializedListener(selectedPGIDsInitializedListener);
        pgIDStorage.addSelectedPGIDsChangedListener(selectedPGIDsChangedListener);
        pgIDStorage.addPGIDsChangedListener(pgIDsChangedListener);

        render();
    }

    private void onWindowCloseRequest(final WindowEvent window) {
        final PGIDsStorage pgIDStorage = StatsStorage.getInstance().getPGIDsStorage();
        pgIDStorage.removeSelectedPGIDsInitializedListener(selectedPGIDsInitializedListener);
        pgIDStorage.removeSelectedPGIDsChangedListener(selectedPGIDsChangedListener);
        pgIDStorage.removePGIDsChangedListener(pgIDsChangedListener);
    }

    private void render() {
        selectedStreamsContainer.getChildren().clear();
        streamsContainer.getChildren().clear();

        final PGIDsStorage pgIDStorage = StatsStorage.getInstance().getPGIDsStorage();
        synchronized (pgIDStorage.getDataLock()) {
            final Set<Integer> pgIDs = pgIDStorage.getPgIDs();
            final Map<Integer, String> selectedPGIDs = pgIDStorage.getSelectedPGIds();

            if (selectedPGIDs != null) {
                for (final Map.Entry<Integer, String> entry : selectedPGIDs.entrySet()) {
                    final Integer pgID = entry.getKey();
                    selectedStreamsContainer.getChildren().add(
                            new SelectedStream(
                                    pgID,
                                    entry.getValue(),
                                    !pgIDs.contains(pgID),
                                    this::handleStreamDeleteClicked
                            )
                    );
                }
            }

            for (final Integer pgID : pgIDs) {
                if (selectedPGIDs == null || selectedPGIDs.get(pgID) == null) {
                    streamsContainer.getChildren().add(new Stream(pgID, this::handleStreamAddClicked));
                }
            }

            streamsContainer.setDisable(selectedPGIDs != null && selectedPGIDs.size() >= 8);
        }
    }

    private void handleStreamDeleteClicked(final Event event) {
        final SelectedStream source = (SelectedStream) event.getSource();
        StatsStorage.getInstance().getPGIDsStorage().deselectPGID(source.getPGId());
    }

    private void handleStreamAddClicked(final Event event) {
        final Stream source = (Stream) event.getSource();
        StatsStorage.getInstance().getPGIDsStorage().selectPGID(source.getPGId());
    }
}

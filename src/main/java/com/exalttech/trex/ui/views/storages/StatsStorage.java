package com.exalttech.trex.ui.views.storages;

import com.cisco.trex.stateless.gui.storages.PGIDStatsStorage;
import com.cisco.trex.stateless.gui.storages.PGIDsStorage;


public class StatsStorage {
    private static StatsStorage instance;

    public static StatsStorage getInstance() {
        if (instance == null) {
            instance = new StatsStorage();
        }
        return instance;
    }

    private final PGIDsStorage pgIDsStorage = new PGIDsStorage();
    private final PGIDStatsStorage pgIDStatsStorage = new PGIDStatsStorage();

    private StatsStorage() {
        pgIDsStorage.addSelectedPGIDsChangedListener(this::handleSelectedPGIDsChanged);
        pgIDsStorage.addSelectedPGIDsInitializedListener(this::handleSelectedPGIDsInitialized);
    }

    public void startPolling() {
        pgIDsStorage.startPolling();
    }

    public void stopPolling() {
        pgIDsStorage.stopPolling();
        pgIDStatsStorage.stopPolling();
    }

    public PGIDsStorage getPGIDsStorage() {
        return pgIDsStorage;
    }

    public PGIDStatsStorage getPGIDStatsStorage() {
        return pgIDStatsStorage;
    }

    private void handleSelectedPGIDsChanged() {
        synchronized (pgIDsStorage.getDataLock()) {
            pgIDStatsStorage.setPGIDs(pgIDsStorage.getSelectedPGIds().keySet());
        }
    }

    private void handleSelectedPGIDsInitialized() {
        handleSelectedPGIDsChanged();
        pgIDStatsStorage.startPolling();
    }
}

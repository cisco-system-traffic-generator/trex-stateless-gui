package com.cisco.trex.stl.gui.storages;

import java.util.HashSet;
import java.util.Set;


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
        pgIDsStorage.addPGIDsChangedListener(this::handleSelectedPGIDsChanged);
    }

    public void startPolling() {
        pgIDsStorage.startPolling();
    }

    public void stopPolling() {
        pgIDsStorage.stopPolling();
        if (pgIDStatsStorage.isRunning()) {
            pgIDStatsStorage.stopPolling();
        }
    }

    public PGIDsStorage getPGIDsStorage() {
        return pgIDsStorage;
    }

    public PGIDStatsStorage getPGIDStatsStorage() {
        return pgIDStatsStorage;
    }

    private void handleSelectedPGIDsChanged() {
        synchronized (pgIDsStorage.getDataLock()) {
            final Set<Integer> pgIdsToRequest = new HashSet<>(pgIDsStorage.getSelectedPGIds().keySet());
            pgIdsToRequest.retainAll(pgIDsStorage.getPgIDs());
            if (!pgIdsToRequest.isEmpty()) {
                pgIDStatsStorage.setPGIDs(pgIdsToRequest);
                if (!pgIDStatsStorage.isRunning()) {
                    pgIDStatsStorage.startPolling();
                }
            } else if (pgIDStatsStorage.isRunning()) {
                pgIDStatsStorage.stopPolling();
            }
        }
    }
}

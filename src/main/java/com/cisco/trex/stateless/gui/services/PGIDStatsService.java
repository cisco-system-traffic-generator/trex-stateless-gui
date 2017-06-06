package com.cisco.trex.stateless.gui.services;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.cisco.trex.stateless.model.stats.PGIdStatsRPCResult;

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.core.RPCCommands;


public class PGIDStatsService extends ScheduledService<PGIdStatsRPCResult> {
    private static Logger LOG = Logger.getLogger(PGIDStatsService.class);

    private Set<Integer> pgIDs = null;
    private final Object lock = new Object();

    public void setPGIDs(final Set<Integer> pgIDs) {
        synchronized (lock) {
            this.pgIDs = pgIDs != null ? new HashSet<>(pgIDs) : null;
        }
    }

    @Override
    public void reset() {
        super.reset();
        synchronized (lock) {
            pgIDs = null;
        }
    }

    @Override
    protected Task<PGIdStatsRPCResult> createTask() {
        return new Task<PGIdStatsRPCResult>() {
            @Override
            protected PGIdStatsRPCResult call() {
                // TODO: remove when ConnectionManager.isConnected will be returns valid result
                if (ConnectionManager.getInstance().getApiH() == null) {
                    return null;
                }

                PGIdStatsRPCResult pgIDStats = null;
                try {
                    synchronized (lock) {
                        pgIDStats = RPCCommands.getPGIdStats(new ArrayList<>(pgIDs));
                    }
                } catch (Exception exc) {
                    LOG.error("Failed to get PGID stats", exc);
                }

                return pgIDStats;
            }
        };
    }
}

package com.cisco.trex.stateless.gui.services;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import com.cisco.trex.stateless.model.stats.ActivePGIdsRPCResult;

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.core.RPCCommands;


public class ActivePGIDsService extends ScheduledService<Set<Integer>> {
    private static Logger LOG = Logger.getLogger(ActivePGIDsService.class);

    @Override
    protected Task<Set<Integer>> createTask() {
        return new Task<Set<Integer>>() {
            @Override
            protected Set<Integer> call() {
                // TODO: remove when ConnectionManager.isConnected will be returns valid result
                if (ConnectionManager.getInstance().getApiH() == null) {
                    return null;
                }

                Set<Integer> pgIDs = null;
                try {
                    final ActivePGIdsRPCResult activePGIdsRPCResult = RPCCommands.getActivePGIds();
                    final int[] flowStats = activePGIdsRPCResult.getIds().getFlowStats();
                    final int[] latency = activePGIdsRPCResult.getIds().getLatency();
                    pgIDs = new HashSet<>();
                    for (final int pgID : flowStats) {
                        pgIDs.add(pgID);
                    }
                    for (final int pgID : latency) {
                        pgIDs.add(pgID);
                    }
                } catch (Exception exc) {
                    LOG.error("Failed to get active PGIDs", exc);
                }
                return pgIDs;
            }
        };
    }
}

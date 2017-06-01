package com.exalttech.trex.ui.views.services;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import org.apache.log4j.Logger;

import java.util.Arrays;
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
                    final Integer[] flowStats = activePGIdsRPCResult.getIds().getFlowStats();
                    final Integer[] latency = activePGIdsRPCResult.getIds().getLatency();
                    pgIDs = new HashSet<>();
                    pgIDs.addAll(Arrays.asList(flowStats));
                    pgIDs.addAll(Arrays.asList(latency));
                } catch (Exception exc) {
                    LOG.error("Failed to get active PGIDs", exc);
                }
                return pgIDs;
            }
        };
    }
}

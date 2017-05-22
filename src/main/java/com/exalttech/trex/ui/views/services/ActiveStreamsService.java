package com.exalttech.trex.ui.views.services;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.exalttech.trex.core.RPCCommands;
import com.exalttech.trex.remote.models.stats.ActivePGIdsRPCResult;


public class ActiveStreamsService extends ScheduledService<Set<Integer>> {
    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(ActiveStreamsService.class);

    private final Set<Integer> pgids = new HashSet<>();

    @Override
    protected Task<Set<Integer>> createTask() {
        return new Task<Set<Integer>>() {
            @Override
            protected Set<Integer> call() {
                try {
                    final ActivePGIdsRPCResult activePGIdsRPCResult = RPCCommands.getActivePGIds();
                    final Integer[] flowStats = activePGIdsRPCResult.getIds().getFlowStats();
                    final Integer[] latency = activePGIdsRPCResult.getIds().getLatency();
                    synchronized (pgids) {
                        pgids.clear();
                        pgids.addAll(Arrays.asList(flowStats));
                        pgids.addAll(Arrays.asList(latency));
                    }
                } catch (Exception exc) {
                    LOG.error("Failed to get active PGIDs", exc);
                    return null;
                }
                return pgids;
            }
        };
    }
}

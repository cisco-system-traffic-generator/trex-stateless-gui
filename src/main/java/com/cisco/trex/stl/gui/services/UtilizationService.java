package com.cisco.trex.stl.gui.services;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import org.apache.log4j.Logger;

import com.cisco.trex.stateless.model.stats.Utilization;

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.core.RPCCommands;


public class UtilizationService extends ScheduledService<Utilization> {
    private static Logger LOG = Logger.getLogger(UtilizationService.class);

    @Override
    protected Task<Utilization> createTask() {
        return new Task<Utilization>() {
            @Override
            protected Utilization call() {
                // TODO: remove when ConnectionManager.isConnected will be returns valid result
                if (ConnectionManager.getInstance().getApiH() == null) {
                    return null;
                }

                try {
                    return RPCCommands.getUtilization();
                } catch (Exception exc) {
                    LOG.error("Failed to get utilization", exc);
                }
                return null;
            }
        };
    }
}

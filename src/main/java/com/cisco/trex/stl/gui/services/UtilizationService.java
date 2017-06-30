package com.cisco.trex.stl.gui.services;

import com.cisco.trex.stateless.model.stats.Utilization;
import com.exalttech.trex.core.RPCCommands;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;


public class UtilizationService extends ScheduledService<Utilization> {
    private static Logger LOG = Logger.getLogger(UtilizationService.class);

    @Override
    protected Task<Utilization> createTask() {
        return new Task<Utilization>() {
            @Override
            protected Utilization call() {
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

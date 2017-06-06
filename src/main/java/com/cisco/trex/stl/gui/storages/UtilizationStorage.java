package com.cisco.trex.stl.gui.storages;

import javafx.concurrent.WorkerStateEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import com.cisco.trex.stateless.model.stats.Utilization;

import com.cisco.trex.stl.gui.services.UtilizationService;


public class UtilizationStorage {
    public interface UtilizationChangedListener {
        void utilizationChanged();
    }

    private static final Duration POLLING_INTERVAL = Duration.seconds(1);

    private final UtilizationService utilizationService = new UtilizationService();

    private Utilization utilization = null;
    private final Object dataLock = new Object();
    private final List<UtilizationChangedListener> utilizationChangedListeners = new ArrayList<>();

    public Object getDataLock() {
        return dataLock;
    }

    public Utilization getUtilization() {
        return utilization;
    }

    public void startPolling() {
        synchronized (utilizationService) {
            if (!utilizationService.isRunning()) {
                utilizationService.setPeriod(POLLING_INTERVAL);
                utilizationService.setOnSucceeded(this::handleUtilizationReceived);
                utilizationService.start();
            }
        }
    }

    public void stopPolling() {
        synchronized (utilizationService) {
            if (utilizationService.isRunning()) {
                utilizationService.cancel();
                utilizationService.reset();
            }
        }

        synchronized (dataLock) {
            utilization = null;
        }
    }

    public void addUtilizationChangedListener(final UtilizationChangedListener listener) {
        synchronized (utilizationChangedListeners) {
            utilizationChangedListeners.add(listener);
        }
    }

    public void removeUtilizationChangedListener(final UtilizationChangedListener listener) {
        synchronized (utilizationChangedListeners) {
            utilizationChangedListeners.remove(listener);
        }
    }

    private void handleUtilizationReceived(final WorkerStateEvent event) {
        final UtilizationService service = (UtilizationService) event.getSource();
        final Utilization receivedUtilization = service.getValue();

        if (receivedUtilization == null) {
            return;
        }

        utilization = receivedUtilization;

        handleUtilizationChanged();
    }

    private void handleUtilizationChanged() {
        synchronized (utilizationChangedListeners) {
            utilizationChangedListeners.forEach(UtilizationChangedListener::utilizationChanged);
        }
    }
}

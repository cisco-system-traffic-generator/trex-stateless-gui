package com.cisco.trex.stl.gui.storages;

import com.cisco.trex.stateless.model.stats.Utilization;
import com.cisco.trex.stateless.model.stats.UtilizationCPU;
import com.cisco.trex.stl.gui.models.UtilizationCPUModel;
import com.cisco.trex.stl.gui.services.UtilizationService;
import javafx.concurrent.WorkerStateEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class UtilizationStorage {
    private List<UtilizationCPUModel> cpuUtilsModels = new ArrayList<>();

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

    public List<UtilizationCPUModel> getCpuUtilsModels() {
        return cpuUtilsModels;
    }

    public void startPolling() {
        synchronized (utilizationService) {
            if (!utilizationService.isRunning()) {
                utilizationService.reset();
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
        synchronized (cpuUtilsModels) {
            cpuUtilsModels = toCPUUtilModel(receivedUtilization.getCpu());
        }

        handleUtilizationChanged();
    }
    
    private List<UtilizationCPUModel> toCPUUtilModel(List<UtilizationCPU> cpuUtils) {
        List<UtilizationCPUModel> models = new ArrayList<>();
        Iterator<UtilizationCPU> iterator = cpuUtils.iterator();
        int idx = 0;
        while (iterator.hasNext()) {
            UtilizationCPU utilizationCPU = iterator.next();
            UtilizationCPUModel model = new UtilizationCPUModel(
                    idx,
                    utilizationCPU.getPorts(),
                    calculateAVG(utilizationCPU),
                    utilizationCPU.getHistory()
            );
            models.add(model);
        }
        
        
        return models;
    }

    private int calculateAVG(UtilizationCPU utilizationCPU) {
        List<Integer> history = utilizationCPU.getHistory();
        int avgIndex = Math.min(history.size()/2, history.size());
        int avgLen = avgIndex;
        int sum = history.stream().limit(avgIndex).mapToInt(Integer::intValue).sum();
        return Math.round(sum/avgLen);
    }

    private void handleUtilizationChanged() {
        synchronized (utilizationChangedListeners) {
            utilizationChangedListeners.forEach(UtilizationChangedListener::utilizationChanged);
        }
    }
}

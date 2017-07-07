package com.cisco.trex.stl.gui.storages;

import com.cisco.trex.stateless.model.stats.Utilization;
import com.cisco.trex.stateless.model.stats.UtilizationCPU;
import com.cisco.trex.stl.gui.models.CpuUtilStatPoint;
import com.cisco.trex.stl.gui.models.MemoryUtilizationModel;
import com.cisco.trex.stl.gui.models.UtilizationCPUModel;
import com.cisco.trex.stl.gui.services.UtilizationService;
import com.exalttech.trex.util.ArrayHistory;
import javafx.concurrent.WorkerStateEvent;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


public class UtilizationStorage {
    private List<UtilizationCPUModel> cpuUtilsModels = new ArrayList<>();
    private List<MemoryUtilizationModel> memUtilsModels = new ArrayList<>();
    private Object utilizationStatsMonitor = new Object();
    private Map<String, ArrayHistory<CpuUtilStatPoint>> cpuUtilizationHistoryMap = new HashMap<>();

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

    public Map<String, ArrayHistory<CpuUtilStatPoint>> getCpuUtilizationHistoryMap() {
        return cpuUtilizationHistoryMap;
    }

    public List<MemoryUtilizationModel> getMemUtilsModels() {
        return memUtilsModels;
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
        synchronized (utilizationStatsMonitor) {
            synchronized (dataLock) {
                List<UtilizationCPU> cpuUtilizationStats = receivedUtilization.getCpu();
                int idx = 0;
                for (UtilizationCPU cpuUtilizationStat : cpuUtilizationStats) {
                    String ports = cpuUtilizationStat.getPorts().stream()
                                                                .map(Objects::toString)
                                                                .collect(joining(", "));
                    
                    String key = String.format("Socket %s (%s)", idx, ports);
                    String socketKey = String.format("Socket %s", idx);
                    
                    Optional<Map.Entry<String, ArrayHistory<CpuUtilStatPoint>>> entryOptional = cpuUtilizationHistoryMap.entrySet()
                                                                                                                 .stream()
                                                                                                                 .filter(entry -> entry.getKey().startsWith(socketKey))
                                                                                                                 .findFirst();
                    ArrayHistory<CpuUtilStatPoint> history;
                    if (entryOptional.isPresent()) {
                        history = entryOptional.get().getValue();
                        cpuUtilizationHistoryMap.remove(entryOptional.get().getKey());
                    } else {
                        history = new ArrayHistory<>(303);
                    }
                    int value = cpuUtilizationStat.getHistory().get(0);
                    double time = System.currentTimeMillis() / 1000.0;
                    history.add(new CpuUtilStatPoint(value, time));
                    cpuUtilizationHistoryMap.put(key, history);
                    idx++;
                }
            }
            cpuUtilsModels = toCPUUtilModel(receivedUtilization.getCpu());
            memUtilsModels.clear();
            memUtilsModels.add(totalMemUtilization(receivedUtilization.getMbufStats()));
            memUtilsModels.addAll(toMemUtilModel(receivedUtilization.getMbufStats()));
            memUtilsModels.add(percentageMemUtilization(receivedUtilization.getMbufStats()));
        }

        handleUtilizationChanged();
    }

    private ArrayHistory<Integer> toArrayHistory(List<Integer> utilizationHistory) {
        ArrayHistory<Integer> result = new ArrayHistory<>(utilizationHistory.size());
        utilizationHistory.forEach(result::add);
        return result;
    }

    private MemoryUtilizationModel percentageMemUtilization(Map<String, Map<String, List<Integer>>> mbufStats) {
        List<Map<String, Integer>> listOfUsedBanksPerSocket = mbufStats.entrySet().stream().map(socketEntry -> {
            Map<String, List<Integer>> socketStats = socketEntry.getValue();
            // return map of used banks {"64b": 1, "128b": 2...}
            return socketStats.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, bankEntity -> bankEntity.getValue().get(1) - bankEntity.getValue().get(0)));
        }).collect(toList());

        Map<String, Integer> totalUsageAcrossSocketsPerBank = listOfUsedBanksPerSocket.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(groupingBy(Map.Entry::getKey, summingInt(Map.Entry::getValue)));

        
        Iterator<Map.Entry<String, Map<String, List<Integer>>>> iterator = mbufStats.entrySet().iterator();
        Map.Entry<String, Map<String, List<Integer>>> firstSocket = iterator.next();
        Map<String, List<Integer>> memoryStats = firstSocket.getValue();
        

        return new MemoryUtilizationModel(
                "Percent",
                100 * totalUsageAcrossSocketsPerBank.get("64b") / memoryStats.get("64b").get(1),
                100 * totalUsageAcrossSocketsPerBank.get("128b") / memoryStats.get("128b").get(1),
                100 * totalUsageAcrossSocketsPerBank.get("256b") / memoryStats.get("256b").get(1),
                100 * totalUsageAcrossSocketsPerBank.get("512b") / memoryStats.get("512b").get(1),
                100 * totalUsageAcrossSocketsPerBank.get("1024b") / memoryStats.get("1024b").get(1),
                100 * totalUsageAcrossSocketsPerBank.get("2048b") / memoryStats.get("2048b").get(1),
                100 * totalUsageAcrossSocketsPerBank.get("4096b") / memoryStats.get("4096b").get(1),
                100 * totalUsageAcrossSocketsPerBank.get("9kb") / memoryStats.get("9kb").get(1),
                ""
        );
    }

    private MemoryUtilizationModel totalMemUtilization(Map<String, Map<String, List<Integer>>> mbufStats) {
        Iterator<Map.Entry<String, Map<String, List<Integer>>>> iterator = mbufStats.entrySet().iterator();
        Map.Entry<String, Map<String, List<Integer>>> firstSocket = iterator.next();
        Map<String, List<Integer>> memoryStats = firstSocket.getValue();
        int usedTotalMemoryBytes = memoryStats.entrySet().stream()
                .map(this::computeTotalUsedMemoryPerBank)
                .mapToInt(Integer::new)
                .sum();
        
        return new MemoryUtilizationModel(
                "Total",
                memoryStats.get("64b").get(1),
                memoryStats.get("128b").get(1),
                memoryStats.get("256b").get(1),
                memoryStats.get("512b").get(1),
                memoryStats.get("1024b").get(1),
                memoryStats.get("2048b").get(1),
                memoryStats.get("4096b").get(1),
                memoryStats.get("9kb").get(1),
                String.valueOf(usedTotalMemoryBytes/1000000)
        );
    }

    private List<MemoryUtilizationModel> toMemUtilModel(Map<String, Map<String, List<Integer>>> mbufStats) {
        
        return mbufStats.entrySet().stream().map(socketEntry -> {
            String title = transformSocketName(socketEntry.getKey());
            Map<String, List<Integer>> memoryStats = socketEntry.getValue();
            int usedMemoryPerSocket = memoryStats.entrySet().stream()
                                            .map(this::computeUsedMemoryPerBank)
                                            .mapToInt(Integer::new)
                                            .sum();
            
            return new MemoryUtilizationModel(
                title,
                memoryStats.get("64b").get(1) - memoryStats.get("64b").get(0),
                memoryStats.get("128b").get(1) - memoryStats.get("128b").get(0),
                memoryStats.get("256b").get(1) - memoryStats.get("256b").get(0),
                memoryStats.get("512b").get(1) - memoryStats.get("512b").get(0),
                memoryStats.get("1024b").get(1) - memoryStats.get("1024b").get(0),
                memoryStats.get("2048b").get(1) - memoryStats.get("2048b").get(0),
                memoryStats.get("4096b").get(1) - memoryStats.get("4096b").get(0),
                (memoryStats.get("9kb").get(1) - memoryStats.get("9kb").get(0))*1000,
                String.valueOf(usedMemoryPerSocket/1000000)
            );
        }).collect(toList());
    }

    private Integer computeUsedMemoryPerBank(Map.Entry<String, List<Integer>> entry) {
        String bufType = entry.getKey();
        bufType = bufType.substring(0, bufType.lastIndexOf("b"));
        int multipiler = 1;
        int bufTypeSize = 0;
        if (bufType.lastIndexOf("k") != -1) {
            multipiler = 1000;
            bufTypeSize = Integer.valueOf(bufType.substring(0,bufType.indexOf("k")));
        } else {
            bufTypeSize = Integer.valueOf(bufType);
        }
        int usedPerBank = entry.getValue().get(1) - entry.getValue().get(0);
        return bufTypeSize * usedPerBank * multipiler;
    }

    private Integer computeTotalUsedMemoryPerBank(Map.Entry<String, List<Integer>> entry) {
        String bufType = entry.getKey();
        bufType = bufType.substring(0, bufType.lastIndexOf("b"));
        int multipiler = 1;
        int bufTypeSize = 0;
        if (bufType.lastIndexOf("k") != -1) {
            multipiler = 1000;
            bufTypeSize = Integer.valueOf(bufType.substring(0,bufType.indexOf("k")));
        } else {
            bufTypeSize = Integer.valueOf(bufType);
        }
        
        int usedPerBank = entry.getValue().get(1);
        return bufTypeSize * usedPerBank * multipiler + 64;
    }
    
    private String transformSocketName(String socketName) {
        return "Socket " + socketName.substring(socketName.lastIndexOf("-") + 1);
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

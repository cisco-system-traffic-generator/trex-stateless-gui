package com.cisco.trex.stl.gui.storages;

import com.cisco.trex.stateless.model.stats.FlowStat;
import com.cisco.trex.stateless.model.stats.LatencyStat;
import com.cisco.trex.stateless.model.stats.PGIdStatsRPCResult;
import com.cisco.trex.stl.gui.models.FlowStatPoint;
import com.cisco.trex.stl.gui.models.LatencyStatPoint;
import com.cisco.trex.stl.gui.services.PGIDStatsService;
import com.exalttech.trex.util.ArrayHistory;
import javafx.concurrent.WorkerStateEvent;
import javafx.util.Duration;

import java.util.*;


public class PGIDStatsStorage {
    public interface StatsChangedListener {
        void flowStatsChanged();
    }

    private static final Duration POLLING_INTERVAL = Duration.seconds(1);
    private static final int HISTORY_SIZE = 301;

    private final PGIDStatsService pgIDStatsService = new PGIDStatsService();

    private final Object dataLock = new Object();

    private final Map<Integer, ArrayHistory<FlowStatPoint>> flowStatPointHistoryMap = new HashMap<>();
    private final Set<Integer> stoppedPGIds = new HashSet<>();
    private final Map<Integer, FlowStatPoint> flowStatPointShadowMap = new HashMap<>();

    private final Map<Integer, ArrayHistory<LatencyStatPoint>> latencyStatPointHistoryMap = new HashMap<>();
    private final Map<Integer, Long> maxLatencyMap = new HashMap<>();
    private final Map<Integer, LatencyStatPoint> latencyStatPointShadowMap = new HashMap<>();
    private String[] histogramKeys = new String[0];

    private Map<String, Integer> lastVerId = new HashMap<>();

    private final List<StatsChangedListener> statsChangedListeners = new ArrayList<>();

    public Object getDataLock() {
        return dataLock;
    }

    public Map<Integer, ArrayHistory<FlowStatPoint>> getFlowStatPointHistoryMap() {
        return flowStatPointHistoryMap;
    }

    public Set<Integer> getStoppedPGIds() {
        return stoppedPGIds;
    }

    public Map<Integer, FlowStatPoint> getFlowStatPointShadowMap() {
        return flowStatPointShadowMap;
    }

    public Map<Integer, ArrayHistory<LatencyStatPoint>> getLatencyStatPointHistoryMap() {
        return latencyStatPointHistoryMap;
    }

    public Map<Integer, Long> getMaxLatencyMap() {
        return maxLatencyMap;
    }

    public Map<Integer, LatencyStatPoint> getLatencyStatPointShadowMap() {
        return latencyStatPointShadowMap;
    }

    public String[] getHistogramKeys(final int size) {
        return Arrays.copyOfRange(histogramKeys, Math.max(0, histogramKeys.length - size), histogramKeys.length);
    }

    public void addStatsChangeListener(final StatsChangedListener listener) {
        synchronized (statsChangedListeners) {
            statsChangedListeners.add(listener);
        }
    }

    public void removeStatsChangeListener(final StatsChangedListener listener) {
        synchronized (statsChangedListeners) {
            statsChangedListeners.remove(listener);
        }
    }

    public void startPolling() {
        synchronized (pgIDStatsService) {
            if (!pgIDStatsService.isRunning()) {
                Set<Integer> savedPgIDs = pgIDStatsService.getPgIDs();
                pgIDStatsService.reset();
                pgIDStatsService.setPGIDs(savedPgIDs);
                pgIDStatsService.setPeriod(POLLING_INTERVAL);
                pgIDStatsService.setOnSucceeded(this::handlePGIDStatsReceived);
                pgIDStatsService.start();
            }
        }
    }

    public void stopPolling() {
        synchronized (pgIDStatsService) {
            if (pgIDStatsService.isRunning()) {
                pgIDStatsService.cancel();
            }
        }

        clearStats();
    }

    public boolean isRunning() {
        synchronized (pgIDStatsService) {
            return pgIDStatsService.isRunning();
        }
    }

    public void setPGIDs(final Set<Integer> pgIDs) {
        synchronized (pgIDStatsService) {
            pgIDStatsService.setPGIDs(pgIDs);
        }
    }

    public void reset() {
        synchronized (dataLock) {
            resetFlowStats();
            resetLatencyStats();
        }

        handleStatsChanged();
    }

    private void clearStats() {
        synchronized (dataLock) {
            clearFlowStats();
            clearLatencyStats();
        }

        handleStatsChanged();
    }

    private void handlePGIDStatsReceived(final WorkerStateEvent event) {
        final PGIDStatsService service = (PGIDStatsService) event.getSource();
        final PGIdStatsRPCResult receivedPGIDStats = service.getValue();

        if (receivedPGIDStats == null) {
            return;
        }

        final Map<String, Integer> verId = receivedPGIDStats.getVerId();
        if (verId == null) {
            clearStats();
            return;
        }

        final double time = System.currentTimeMillis() / 1000.0;

        synchronized (dataLock) {
            final Map<String, FlowStat> flowStatMap = receivedPGIDStats.getFlowStats();
            if (flowStatMap != null) {
                processFlowStats(receivedPGIDStats.getFlowStats(), verId, time);
            } else {
                clearFlowStats();
            }

            final Map<String, LatencyStat> latencyStatMap = receivedPGIDStats.getLatency();
            if (latencyStatMap != null) {
                processLatencyStats(receivedPGIDStats.getLatency(), verId, time);
            } else {
                clearLatencyStats();
            }

            lastVerId = verId;
        }

        handleStatsChanged();
    }

    private void processFlowStats(
            final Map<String, FlowStat> flowStatMap,
            final Map<String, Integer> verId,
            final double time
    ) {
        final Set<Integer> unvisitedStreams = new HashSet<>(flowStatPointHistoryMap.keySet());

        flowStatMap.forEach((final String pgID, final FlowStat flowStat) -> {
            int intPGID;
            try {
                intPGID = Integer.valueOf(pgID);
            } catch (NumberFormatException exc) {
                return;
            }

            unvisitedStreams.remove(intPGID);

            final FlowStatPoint statsFlowHistoryPoint = new FlowStatPoint(flowStat, time);
            ArrayHistory<FlowStatPoint> history = flowStatPointHistoryMap.get(intPGID);
            if (history == null) {
                history = new ArrayHistory<>(HISTORY_SIZE);
                flowStatPointHistoryMap.put(intPGID, history);
            } else if (!verId.get(pgID).equals(lastVerId.get(pgID))) {
                history.clear();
                stoppedPGIds.remove(intPGID);
                flowStatPointShadowMap.remove(intPGID);
            } else if (!history.isEmpty()) {
                final FlowStatPoint last = history.last();
                if (last.getTp() == statsFlowHistoryPoint.getTp()) {
                    stoppedPGIds.add(intPGID);
                } else {
                    stoppedPGIds.remove(intPGID);
                }
            }
            history.add(statsFlowHistoryPoint);

            flowStatPointShadowMap.putIfAbsent(intPGID, statsFlowHistoryPoint);
        });

        unvisitedStreams.forEach((final Integer pgID) -> {
            flowStatPointHistoryMap.remove(pgID);
            stoppedPGIds.remove(pgID);
            flowStatPointShadowMap.remove(pgID);
        });
    }

    private void clearFlowStats() {
        flowStatPointHistoryMap.clear();
        stoppedPGIds.clear();
        flowStatPointShadowMap.clear();
    }

    private void resetFlowStats() {
        flowStatPointShadowMap.clear();
        flowStatPointHistoryMap.forEach((final Integer pgID, final ArrayHistory<FlowStatPoint> history) -> {
            if (!history.isEmpty()) {
                final FlowStatPoint last = history.last();
                flowStatPointShadowMap.put(pgID, history.last());
                history.clear();
                history.add(last);
            }
        });
    }

    private void processLatencyStats(
            final Map<String, LatencyStat> latencyStatMap,
            final Map<String, Integer> verId,
            final double time
    ) {
        final Set<Integer> unvisitedStreams = new HashSet<>(latencyStatPointHistoryMap.keySet());
        final Set<String> histogramKeysSet = new HashSet<>();

        latencyStatMap.forEach((final String pgID, final LatencyStat latencyStat) -> {
            int intPGID;
            try {
                intPGID = Integer.valueOf(pgID);
            } catch (NumberFormatException exc) {
                return;
            }

            unvisitedStreams.remove(intPGID);

            final LatencyStatPoint statsFlowHistoryPoint = new LatencyStatPoint(latencyStat, time);
            ArrayHistory<LatencyStatPoint> history = latencyStatPointHistoryMap.get(intPGID);
            if (history == null) {
                history = new ArrayHistory<>(HISTORY_SIZE);
                latencyStatPointHistoryMap.put(intPGID, history);
            } else if (!verId.get(pgID).equals(lastVerId.get(pgID))) {
                history.clear();
                maxLatencyMap.remove(intPGID);
                latencyStatPointShadowMap.remove(intPGID);
            }
            history.add(statsFlowHistoryPoint);

            final long lastMax = latencyStat.getLat().getLastMax();
            final Long maxLatency = maxLatencyMap.get(intPGID);
            if (maxLatency == null || lastMax > maxLatency) {
                maxLatencyMap.put(intPGID, lastMax);
            }

            histogramKeysSet.addAll(latencyStat.getLat().getHistogram().keySet());

            latencyStatPointShadowMap.putIfAbsent(intPGID, statsFlowHistoryPoint);
        });

        histogramKeys = new String[histogramKeysSet.size()];
        histogramKeysSet.toArray(histogramKeys);
        Arrays.sort(histogramKeys, PGIDStatsStorage::compareHistogramKeys);

        unvisitedStreams.forEach((final Integer pgID) -> {
            latencyStatPointHistoryMap.remove(pgID);
            maxLatencyMap.remove(pgID);
            latencyStatPointShadowMap.remove(pgID);
        });
    }

    private void clearLatencyStats() {
        latencyStatPointHistoryMap.clear();
        maxLatencyMap.clear();
        latencyStatPointShadowMap.clear();
    }

    private void resetLatencyStats() {
        latencyStatPointShadowMap.clear();
        maxLatencyMap.clear();
        latencyStatPointHistoryMap.forEach((final Integer pgID, final ArrayHistory<LatencyStatPoint> history) -> {
            if (!history.isEmpty()) {
                final LatencyStatPoint last = history.last();
                maxLatencyMap.put(pgID, last.getLatencyStat().getLat().getLastMax());
                latencyStatPointShadowMap.put(pgID, last);
                history.clear();
                history.add(last);
            }
        });
    }

    private void handleStatsChanged() {
        synchronized (statsChangedListeners) {
            statsChangedListeners.forEach(StatsChangedListener::flowStatsChanged);
        }
    }

    private static int compareHistogramKeys(final String key1, final String key2) {
        return Integer.parseInt(key1) - Integer.parseInt(key2);
    }
}

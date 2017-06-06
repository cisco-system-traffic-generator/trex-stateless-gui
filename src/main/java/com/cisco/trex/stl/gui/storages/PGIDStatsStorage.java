package com.cisco.trex.stl.gui.storages;

import javafx.concurrent.WorkerStateEvent;
import javafx.util.Duration;

import java.util.*;

import com.cisco.trex.stateless.model.stats.FlowStat;
import com.cisco.trex.stateless.model.stats.LatencyStat;
import com.cisco.trex.stateless.model.stats.PGIdStatsRPCResult;

import com.cisco.trex.stl.gui.models.FlowStatPoint;
import com.cisco.trex.stl.gui.services.PGIDStatsService;

import com.cisco.trex.stl.gui.models.LatencyStatPoint;
import com.exalttech.trex.util.ArrayHistory;


public class PGIDStatsStorage {
    public interface StatsChangedListener {
        void flowStatsChanged();
    }

    public interface LatencyStatsChangedListener {
        void latencyStatsChanged();
    }

    private static final Duration POLLING_INTERVAL = Duration.seconds(1);
    private static final int HISTORY_SIZE = 301;

    private final PGIDStatsService pgIDStatsService = new PGIDStatsService();

    private final Object flowLock = new Object();
    private final Map<Integer, ArrayHistory<FlowStatPoint>> flowStatPointHistoryMap = new HashMap<>();
    private final Map<Integer, FlowStatPoint> flowStatPointShadowMap = new HashMap<>();

    private final Object latencyLock = new Object();
    private final Map<Integer, ArrayHistory<LatencyStatPoint>> latencyStatPointHistoryMap = new HashMap<>();
    private final Map<Integer, LatencyStatPoint> latencyStatPointShadowMap = new HashMap<>();
    private String[] histogramKeys = new String[0];

    private final List<StatsChangedListener> statsChangedListeners = new ArrayList<>();

    public Object getFlowLock() {
        return flowLock;
    }

    public Map<Integer, ArrayHistory<FlowStatPoint>> getFlowStatPointHistoryMap() {
        return flowStatPointHistoryMap;
    }

    public Map<Integer, FlowStatPoint> getFlowStatPointShadowMap() {
        return flowStatPointShadowMap;
    }

    public Object getLatencyLock() {
        return latencyLock;
    }

    public Map<Integer, ArrayHistory<LatencyStatPoint>> getLatencyStatPointHistoryMap() {
        return latencyStatPointHistoryMap;
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
                pgIDStatsService.reset();
            }
        }

        synchronized (flowLock) {
            flowStatPointHistoryMap.clear();
            flowStatPointShadowMap.clear();
        }

        synchronized (latencyLock) {
            latencyStatPointHistoryMap.clear();
            latencyStatPointShadowMap.clear();
        }
    }

    public void setPGIDs(final Set<Integer> pgIDs) {
        synchronized (pgIDStatsService) {
            pgIDStatsService.setPGIDs(pgIDs);
        }
    }

    public void reset() {
        resetFlowStats();
        resetLatencyStats();
        handleStatsChanged();
    }

    private void handlePGIDStatsReceived(final WorkerStateEvent event) {
        final PGIDStatsService service = (PGIDStatsService) event.getSource();
        final PGIdStatsRPCResult receivedPGIDStats = service.getValue();

        if (receivedPGIDStats == null) {
            return;
        }

        final double time = System.currentTimeMillis() / 1000.0;

        final Map<String, FlowStat> flowStatMap = receivedPGIDStats.getFlowStats();
        if (flowStatMap != null) {
            processFlowStats(receivedPGIDStats.getFlowStats(), time);
        }

        final Map<String, LatencyStat> latencyStatMap = receivedPGIDStats.getLatency();
        if (latencyStatMap != null) {
            processLatencyStats(receivedPGIDStats.getLatency(), time);
        }

        handleStatsChanged();
    }

    private void processFlowStats(final Map<String, FlowStat> flowStatMap, final double time) {
        synchronized (flowLock) {
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
                }
                history.add(statsFlowHistoryPoint);

                flowStatPointShadowMap.putIfAbsent(intPGID, statsFlowHistoryPoint);
            });

            unvisitedStreams.forEach((final Integer pgID) -> {
                flowStatPointHistoryMap.remove(pgID);
                flowStatPointShadowMap.remove(pgID);
            });
        }
    }

    private void resetFlowStats() {
        synchronized (flowLock) {
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
    }

    private void processLatencyStats(final Map<String, LatencyStat> latencyStatMap, final double time) {
        synchronized (latencyLock) {
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
                }
                history.add(statsFlowHistoryPoint);

                histogramKeysSet.addAll(latencyStat.getLat().getHistogram().keySet());

                latencyStatPointShadowMap.putIfAbsent(intPGID, statsFlowHistoryPoint);
            });

            histogramKeys = new String[histogramKeysSet.size()];
            histogramKeysSet.toArray(histogramKeys);
            Arrays.sort(histogramKeys, PGIDStatsStorage::compareHistogramKeys);

            unvisitedStreams.forEach((final Integer pgID) -> {
                latencyStatPointHistoryMap.remove(pgID);
                latencyStatPointShadowMap.remove(pgID);
            });
        }
    }

    private void resetLatencyStats() {
        synchronized (latencyLock) {
            latencyStatPointShadowMap.clear();
            latencyStatPointHistoryMap.forEach((final Integer pgID, final ArrayHistory<LatencyStatPoint> history) -> {
                if (!history.isEmpty()) {
                    final LatencyStatPoint last = history.last();
                    latencyStatPointShadowMap.put(pgID, history.last());
                    history.clear();
                    history.add(last);
                }
            });
        }
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

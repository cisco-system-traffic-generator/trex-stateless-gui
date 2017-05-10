package com.exalttech.trex.ui.views.statistics;

import javafx.beans.value.ObservableValue;

import org.slf4j.LoggerFactory;

import java.util.*;

import com.exalttech.trex.core.AsyncResponseManager;
import com.exalttech.trex.ui.models.stats.latency.*;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Util;


public class LatencyStatsLoader {
    private static LatencyStatsLoader instance;
    private static int HISTORY_SIZE = 301;
    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(LatencyStatsLoader.class);

    public static LatencyStatsLoader getInstance() {
        if (instance == null) {
            instance = new LatencyStatsLoader();
        }
        return instance;
    }

    private Map<String, LatencyInfo> latencyInfoMap = new HashMap<>();
    private Map<String, ArrayHistory<Number>> latencyWindowHistoryMap = new HashMap<>();
    private Map<String, ArrayHistory<Number>> maxLatencyHistoryMap = new HashMap<>();
    private Map<String, ArrayHistory<Number>> avgLatencyHistoryMap = new HashMap<>();
    private Map<String, ArrayHistory<Number>> latencyJitterHistoryMap = new HashMap<>();
    private Map<String, Map<String, Long>> histogramMap = new HashMap<>();
    private Set<String> unvisitedStreams = new HashSet<>();
    private Map<String, LatencyInfo> latencyInfoShadowMap = null;
    private Map<String, Map<String, Long>> histogramShadowMap = null;
    private String[] histogramKeys;

    protected LatencyStatsLoader() {}

    public void start() {
        synchronized (latencyInfoMap) {
            latencyInfoMap.clear();
        }
        synchronized (latencyWindowHistoryMap) {
            latencyWindowHistoryMap.clear();
        }
        synchronized (maxLatencyHistoryMap) {
            maxLatencyHistoryMap.clear();
        }
        synchronized (avgLatencyHistoryMap) {
            avgLatencyHistoryMap.clear();
        }
        synchronized (latencyJitterHistoryMap) {
            latencyJitterHistoryMap.clear();
        }
        synchronized (histogramMap) {
            histogramMap.clear();
        }
        unvisitedStreams.clear();

        AsyncResponseManager.getInstance().getTrexLatencyProperty().addListener(this::handleLatencyPropertyChanged);
    }

    public void reset() {
        synchronized (latencyWindowHistoryMap) {
            latencyWindowHistoryMap.clear();
        }
        synchronized (maxLatencyHistoryMap) {
            maxLatencyHistoryMap.clear();
        }
        synchronized (avgLatencyHistoryMap) {
            avgLatencyHistoryMap.clear();
        }
        synchronized (latencyJitterHistoryMap) {
            latencyJitterHistoryMap.clear();
        }

        updateLatencyInfoShadowMap();
        updateHistogramShadowMap();
    }

    public void clear() {
        synchronized (latencyWindowHistoryMap) {
            latencyWindowHistoryMap.clear();
        }
        synchronized (maxLatencyHistoryMap) {
            maxLatencyHistoryMap.clear();
        }
        synchronized (avgLatencyHistoryMap) {
            avgLatencyHistoryMap.clear();
        }
        synchronized (latencyJitterHistoryMap) {
            latencyJitterHistoryMap.clear();
        }
        latencyInfoShadowMap = null;
        histogramShadowMap = null;
    }

    public Map<String, LatencyInfo> getLatencyInfoMap() {
        return latencyInfoMap;
    }

    public Map<String, ArrayHistory<Number>> getLatencyWindowHistoryMap() {
        return latencyWindowHistoryMap;
    }

    public Map<String, ArrayHistory<Number>> getMaxLatencyHistoryMap() {
        return maxLatencyHistoryMap;
    }

    public Map<String, ArrayHistory<Number>> getAvgLatencyHistoryMap() {
        return avgLatencyHistoryMap;
    }

    public Map<String, ArrayHistory<Number>> getLatencyJitterHistoryMap() {
        return latencyJitterHistoryMap;
    }

    public Map<String, Map<String, Long>> getHistogramMap() {
        return histogramMap;
    }

    public String[] getHistogramKeys(final int size) {
        return Arrays.copyOfRange(histogramKeys, Math.max(0, histogramKeys.length - size), histogramKeys.length);
    }

    private void handleLatencyPropertyChanged(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        if (newValue == null) {
            LOG.error("Latency stats new value is null");
            return;
        }

        final LatencyStats stats = (LatencyStats) Util.fromJSONString(
                newValue,
                LatencyStats.class
        );
        if (stats == null) {
            LOG.error("Can't parse latency stats new value");
            return;
        }

        final Map<String, LatencyStatsStream> data = stats.getData();
        if (data == null) {
            LOG.error("Latency stats data field is null");
            return;
        }

        buildHistogramKeys(data);

        if (latencyInfoShadowMap == null) {
            initializeLatencyInfoShadowMap(data);
        }

        if (histogramShadowMap == null) {
            initializeHistogramShadowMap(data);
        }

        synchronized (latencyInfoMap) {
            synchronized (histogramMap) {
                synchronized (latencyWindowHistoryMap) {
                    synchronized (maxLatencyHistoryMap) {
                        synchronized (avgLatencyHistoryMap) {
                            synchronized (latencyJitterHistoryMap) {
                                synchronized (latencyInfoShadowMap) {
                                    synchronized (histogramShadowMap) {
                                        latencyInfoMap.clear();
                                        histogramMap.clear();
                                        data.forEach((final String stream, final LatencyStatsStream streamStats) -> {
                                            unvisitedStreams.remove(stream);

                                            final LatencyStatsLatency latency = streamStats.getLatency();
                                            if (latency == null) {
                                                LOG.error("Latency stats data latency field is null");
                                            }

                                            final LatencyStastsErrCntrs errCntrs = streamStats.getErrCntrs();
                                            if (errCntrs == null) {
                                                LOG.error("Latency stats data err cntrs field is null");
                                            }

                                            processLatencyInfo(stream, latency, errCntrs);
                                            processLatencyWindow(stream, latency);
                                            processMaxLatency(stream, latency);
                                            processAvgLatency(stream, latency);
                                            processLatencyJitter(stream, latency);
                                            processLatencyHistogram(stream, latency);
                                        });

                                        unvisitedStreams.forEach((final String stream) -> {
                                            latencyWindowHistoryMap.remove(stream);
                                            maxLatencyHistoryMap.remove(stream);
                                            avgLatencyHistoryMap.remove(stream);
                                            latencyJitterHistoryMap.remove(stream);
                                            latencyInfoShadowMap.remove(stream);
                                            histogramShadowMap.remove(stream);
                                        });
                                        unvisitedStreams = data.keySet();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildHistogramKeys(final Map<String, LatencyStatsStream> data) {
        final Set<String> histogramKeysSet = new HashSet<>();
        data.forEach((final String stream, final LatencyStatsStream streamStats) -> {
            final LatencyStatsLatency latency = streamStats.getLatency();
            if (latency == null) {
                return;
            }

            final Map<String, Long> histogram = latency.getHistogram();
            if (histogram == null) {
                return;
            }

            histogramKeysSet.addAll(histogram.keySet());
        });

        histogramKeys = new String[histogramKeysSet.size()];
        histogramKeysSet.toArray(histogramKeys);
        Arrays.sort(histogramKeys, LatencyStatsLoader::compareHistogramKeys);
    }

    private void updateLatencyInfoShadowMap() {
        final Map<String, LatencyInfo> newLatencyInfoShadowMap = new HashMap<>();
        synchronized (latencyInfoMap) {
            latencyInfoMap.forEach((final String stream, final LatencyInfo latencyInfo) -> {
                final LatencyInfo shadow = latencyInfoShadowMap != null ? latencyInfoShadowMap.get(stream) : null;
                final LatencyInfo newShadow = new LatencyInfo(latencyInfo);
                if (shadow != null) {
                    newShadow.plusShadow(shadow);
                }
                newLatencyInfoShadowMap.put(stream, newShadow);
                latencyInfo.resetShadow();
            });
        }
        latencyInfoShadowMap = newLatencyInfoShadowMap;
    }

    private void initializeLatencyInfoShadowMap(final Map<String, LatencyStatsStream> data) {
        latencyInfoShadowMap = new HashMap<>();
        synchronized (latencyInfoShadowMap) {
            data.forEach((final String stream, final LatencyStatsStream streamStats) -> {
                final LatencyStatsLatency latency = streamStats.getLatency();
                final LatencyStastsErrCntrs errCntrs = streamStats.getErrCntrs();
                latencyInfoShadowMap.put(stream, new LatencyInfo(latency, errCntrs));
            });
        }
    }

    private void updateHistogramShadowMap() {
        final Map<String, Map<String, Long>> newHistogramShadowMap = new HashMap<>();
        synchronized (histogramMap) {
            histogramMap.forEach((final String stream, final Map<String, Long> histogram) -> {
                final Map<String, Long> shadowHistogram = histogramShadowMap != null ?
                        histogramShadowMap.get(stream) :
                        null;
                final Map<String, Long> newShadowHistogram = new HashMap<>();
                histogram.forEach((final String key, final Long value) -> {
                    final long shadowValue = shadowHistogram != null ?
                            shadowHistogram.getOrDefault(key, 0L) :
                            0L;
                    newShadowHistogram.put(key, value + shadowValue);
                    histogram.put(key, 0L);
                });
                newHistogramShadowMap.put(stream, newShadowHistogram);
            });
        }
        histogramShadowMap = newHistogramShadowMap;
    }

    private void initializeHistogramShadowMap(final Map<String, LatencyStatsStream> data) {
        histogramShadowMap = new HashMap<>();
        synchronized (histogramShadowMap) {
            data.forEach((final String stream, final LatencyStatsStream streamStats) -> {
                final LatencyStatsLatency latency = streamStats.getLatency();
                if (latency == null) {
                    return;
                }

                final Map<String, Long> streamHistogram = latency.getHistogram();
                if (streamHistogram == null) {
                    return;
                }

                histogramShadowMap.put(stream, new HashMap<>(streamHistogram));
            });
        }
    }

    private void processLatencyInfo(
            final String stream,
            final LatencyStatsLatency latency,
            final LatencyStastsErrCntrs errCntrs
    ) {
        final LatencyInfo info = new LatencyInfo(latency, errCntrs);
        final LatencyInfo shadow = latencyInfoShadowMap != null ? latencyInfoShadowMap.get(stream) : null;
        if (shadow != null) {
            info.minusShadow(shadow);
        }
        latencyInfoMap.put(stream, info);
    }

    private void processLatencyWindow(final String stream, final LatencyStatsLatency latency) {
        ArrayHistory<Number> history = latencyWindowHistoryMap.get(stream);
        if (history == null) {
            history = new ArrayHistory<>(HISTORY_SIZE);
            latencyWindowHistoryMap.put(stream, history);
        }
        history.add(latency != null ? latency.getLastMax() : 0);
    }

    private void processMaxLatency(final String stream, final LatencyStatsLatency latency) {
        ArrayHistory<Number> history = maxLatencyHistoryMap.get(stream);
        if (history == null) {
            history = new ArrayHistory<>(HISTORY_SIZE);
            maxLatencyHistoryMap.put(stream, history);
        }
        history.add(latency != null ? latency.getTotalMax() : 0);
    }

    private void processAvgLatency(final String stream, final LatencyStatsLatency latency) {
        ArrayHistory<Number> history = avgLatencyHistoryMap.get(stream);
        if (history == null) {
            history = new ArrayHistory<>(HISTORY_SIZE);
            avgLatencyHistoryMap.put(stream, history);
        }
        history.add(latency != null ? latency.getAverage() : 0);
    }

    private void processLatencyJitter(final String stream, final LatencyStatsLatency latency) {
        ArrayHistory<Number> history = latencyJitterHistoryMap.get(stream);
        if (history == null) {
            history = new ArrayHistory<>(HISTORY_SIZE);
            latencyJitterHistoryMap.put(stream, history);
        }
        history.add(latency != null ? latency.getJitter() : 0);
    }

    private void processLatencyHistogram(final String stream, final LatencyStatsLatency latency) {
        if (latency == null) {
            return;
        }

        final Map<String, Long> streamHistogram = latency.getHistogram();
        if (streamHistogram == null) {
            LOG.error("Latency stats data latency histogram field is null");
            return;
        }

        Map<String, Long> histogram = histogramMap.get(stream);
        if (histogram == null) {
            histogram = new HashMap<>();
            histogramMap.put(stream, histogram);
        }
        final Map<String, Long> shadowValueMap = histogramShadowMap != null ? histogramShadowMap.get(stream) : null;

        for (String key : histogramKeys) {
            final long shadowValue = shadowValueMap != null ? shadowValueMap.getOrDefault(key, 0L) : 0L;
            final long value = streamHistogram.getOrDefault(key, 0L);
            histogram.put(key, value - shadowValue);
        }
    }

    private static int compareHistogramKeys(final String key1, final String key2) {
        return Integer.parseInt(key1) - Integer.parseInt(key2);
    }
}

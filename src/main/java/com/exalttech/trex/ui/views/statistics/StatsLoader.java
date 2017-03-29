/**
 * *****************************************************************************
 * Copyright (c) 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************
 */
/*



 */
package com.exalttech.trex.ui.views.statistics;

import javafx.beans.value.ObservableValue;

import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.exalttech.trex.core.AsyncResponseManager;
import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStream;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStreamErrCntrs;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStreamLatency;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Util;

/**
 * Class that present a service for updating current loaded service
 *
 * @author GeorgeKh
 */
public class StatsLoader {

    private static StatsLoader instance;
    private static final int historySize = 1000;
    private static final int latencyHistorySize = 301;

    /**
     * Create and return instance
     *
     * @return
     */
    public static StatsLoader getInstance() {
        if (instance == null) {
            instance = new StatsLoader();
        }
        return instance;
    }

    private Map<String, String> loadedStatsList = new HashMap<>();
    private Map<String, String> previousStatsList = new HashMap<>();
    private Map<String, String> shadowStatsList = null;

    private Map<String, StatsLatencyStream> latencyStatsMap = new HashMap<>();
    private Map<String, ArrayHistory<Number>> latencyWindowHistory = new HashMap<>();
    private Map<String, ArrayHistory<Number>> maxLatencyHistory = new HashMap<>();
    private Map<String, ArrayHistory<Number>> avgLatencyHistory = new HashMap<>();

    private Map<String, ArrayHistory<StatsFlowStream>> flowStatsHistoryMap = new HashMap<>();
    private Map<String, StatsFlowStream> shadowFlowStatsMap = new HashMap<>();
    private double flowStatsLastTime = 0.0;

    /**
     * Protected constructor
     */
    protected StatsLoader() {
        // empty constructor
    }

    /**
     * Return current loaded stats
     *
     * @return
     */
    public Map<String, String> getLoadedStatsList() {
        return loadedStatsList;
    }

    /**
     * Return previous loaded stats
     *
     * @return
     */
    public Map<String, String> getPreviousStatsList() {
        return previousStatsList;
    }

    public Map<String, String> getShadowStatsList() {
        return shadowStatsList != null ? shadowStatsList : new HashMap<>();
    }

    private boolean validAsyncResponse(String jsonString) {
        return !Util.isNullOrEmpty(jsonString) && jsonString.contains("m_cpu_util");
    }

    /**
     * Return latency stats map
     *
     * @return
     */
    public Map<String, StatsLatencyStream> getLatencyStatsMap() {
        return latencyStatsMap;
    }

    /**
     * Return latency window history map
     *
     * @return
     */
    public Map<String, ArrayHistory<Number>> getLatencyWindowHistory() {
        return latencyWindowHistory;
    }

    /**
     * Return max latency history map
     *
     * @return
     */
    public Map<String, ArrayHistory<Number>> getAvgLatencyHistory() {
        return avgLatencyHistory;
    }

    /**
     * Return max latency history map
     *
     * @return
     */
    public Map<String, ArrayHistory<Number>> getMaxLatencyHistory() {
        return maxLatencyHistory;
    }

    /**
     * Return flow stats history map
     *
     * @return
     */
    public Map<String, ArrayHistory<StatsFlowStream>> getFlowStatsHistoryMap() {
        return flowStatsHistoryMap;
    }

    /**
     * Return last updating time of flow stats
     *
     * @return
     */
    public double getFlowStatsLastTime() {
        return flowStatsLastTime;
    }

    /**
     * Start listening on stats changes for updating
     */
    public void start() {
        loadedStatsList.clear();
        previousStatsList.clear();
        shadowStatsList = null;

        latencyStatsMap.clear();
        latencyWindowHistory.clear();

        flowStatsHistoryMap.clear();
        shadowFlowStatsMap.clear();
        flowStatsLastTime = 0.0;

        AsyncResponseManager.getInstance().getTrexGlobalProperty().addListener(this::handleGlobalPropertyChanged);
        AsyncResponseManager.getInstance().getTrexLatencyProperty().addListener(this::handleLatencyPropertyChanged);
        AsyncResponseManager.getInstance().getTrexFlowStatsProperty().addListener(this::handleFlowStatsPropertyChanged);
    }

    public void reset() {
        shadowStatsList = loadedStatsList;

        latencyWindowHistory.clear();
        maxLatencyHistory.clear();
        avgLatencyHistory.clear();

        flowStatsHistoryMap.forEach((String stream, ArrayHistory<StatsFlowStream> statsFlowStreamHistory) -> {
            final StatsFlowStream last = statsFlowStreamHistory.last();
            if (last == null) {
                return;
            }

            statsFlowStreamHistory.clear();
            statsFlowStreamHistory.add(last.getZeroCopy());

            final StatsFlowStream prevShadow = shadowFlowStatsMap.get(stream);
            if (prevShadow == null) {
                return;
            }

            final Map<Integer, Long> txPkts = last.getTxPkts();
            final Map<Integer, Long> txBytes = last.getTxBytes();
            final Map<Integer, Long> rxPkts = last.getRxPkts();
            final Map<Integer, Long> rxBytes = last.getRxBytes();

            txPkts.forEach((Integer port, Long value) -> {
                final Long shadowValue = prevShadow.getTxPkts().get(port);
                if (shadowValue != null) {
                    txPkts.put(port, value + shadowValue);
                }
            });
            txBytes.forEach((Integer port, Long value) -> {
                final Long shadowValue = prevShadow.getTxBytes().get(port);
                if (shadowValue != null) {
                    txBytes.put(port, value + shadowValue);
                }
            });
            rxPkts.forEach((Integer port, Long value) -> {
                final Long shadowValue = prevShadow.getRxPkts().get(port);
                if (shadowValue != null) {
                    rxPkts.put(port, value + shadowValue);
                }
            });
            rxBytes.forEach((Integer port, Long value) -> {
                final Long shadowValue = prevShadow.getRxBytes().get(port);
                if (shadowValue != null) {
                    rxBytes.put(port, value + shadowValue);
                }
            });

            shadowFlowStatsMap.put(stream, new StatsFlowStream(txPkts, rxPkts, txBytes, rxBytes, last.getTime()));
        });
    }

    private void handleGlobalPropertyChanged(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        if (newValue == null) {
            return;
        }

        String data = Util.fromJSONResult(newValue, "data");
        if (!validAsyncResponse(data)) {
            return;
        }

        previousStatsList = loadedStatsList;
        loadedStatsList = Util.getStatsFromJSONString(data);

        if (shadowStatsList == null) {
            shadowStatsList = loadedStatsList;
        }
    }

    private void handleLatencyPropertyChanged(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        if (newValue == null) {
            return;
        }

        try {
            final JSONObject latencyStatsJSON = new JSONObject(newValue);
            final JSONObject dataJSON = latencyStatsJSON.getJSONObject("data");

            final Set<String> unvisitedStreams = new HashSet<>(latencyWindowHistory.keySet());
            dataJSON.keySet().forEach((String stream) -> {
                unvisitedStreams.remove(stream);

                final JSONObject latencyStreamJSON = dataJSON.getJSONObject(stream);
                final StatsLatencyStreamLatency latency = getLatency(latencyStreamJSON);
                final StatsLatencyStreamErrCntrs errCntrs = getLatencyErrCntrs(latencyStreamJSON);
                final StatsLatencyStream latencyStream = new StatsLatencyStream(latency, errCntrs);

                latencyStatsMap.put(stream, latencyStream);

                ArrayHistory<Number> windowHistory = latencyWindowHistory.get(stream);
                if (windowHistory == null) {
                    windowHistory = new ArrayHistory<>(latencyHistorySize);
                    latencyWindowHistory.put(stream, windowHistory);
                }
                windowHistory.add(latencyStream.getLatency().getLastMax());

                ArrayHistory<Number> maxHistory = maxLatencyHistory.get(stream);
                if (maxHistory == null) {
                    maxHistory = new ArrayHistory<>(latencyHistorySize);
                    maxLatencyHistory.put(stream, maxHistory);
                }
                maxHistory.add(latencyStream.getLatency().getTotalMax());

                ArrayHistory<Number> avgHistory = avgLatencyHistory.get(stream);
                if (avgHistory == null) {
                    avgHistory = new ArrayHistory<>(latencyHistorySize);
                    avgLatencyHistory.put(stream, avgHistory);
                }
                avgHistory.add(latencyStream.getLatency().getAverage());
            });

            unvisitedStreams.forEach((String stream) -> {
                latencyWindowHistory.remove(stream);
            });
        } catch (JSONException exc) {
            // TODO: logging
        }
    }

    private static StatsLatencyStreamLatency getLatency(JSONObject latencyStreamJSON) {
        final StatsLatencyStreamLatency latency = new StatsLatencyStreamLatency();

        if (!latencyStreamJSON.has("latency")) {
            return latency;
        }

        final JSONObject latencyJSON = latencyStreamJSON.getJSONObject("latency");

        if (latencyJSON.has("histogram")) {
            final Map<String, Integer> histogram = latency.getHistogram();
            final JSONObject histogramJSON = latencyJSON.getJSONObject("histogram");
            histogramJSON.keySet().forEach((String key) -> {
                try {
                    histogram.put(key, histogramJSON.getInt(key));
                } catch (JSONException exc) {
                    // TODO: logging
                }
            });
        }

        if (latencyJSON.has("average")) {
            try {
                latency.setAverage(latencyJSON.getDouble("average"));
            } catch (JSONException exc) {
                // TODO: logging
            }
        }

        if (latencyJSON.has("jitter")) {
            try {
                latency.setJitter(latencyJSON.getInt("jitter"));
            } catch (JSONException exc) {
                // TODO: logging
            }
        }

        if (latencyJSON.has("last_max")) {
            try {
                latency.setLastMax(latencyJSON.getInt("last_max"));
            } catch (JSONException exc) {
                // TODO: logging
            }
        }

        if (latencyJSON.has("total_max")) {
            try {
                latency.setTotalMax(latencyJSON.getInt("total_max"));
            } catch (JSONException exc) {
                // TODO: logging
            }
        }

        return latency;
    }

    private static StatsLatencyStreamErrCntrs getLatencyErrCntrs(JSONObject latencyStreamJSON) {
        final StatsLatencyStreamErrCntrs errCntrs = new StatsLatencyStreamErrCntrs();

        if (!latencyStreamJSON.has("err_cntrs")) {
            return errCntrs;
        }

        final JSONObject errCntrsJSON = latencyStreamJSON.getJSONObject("err_cntrs");

        if (errCntrsJSON.has("out_of_order")) {
            try {
                errCntrs.setOutOfOrder(errCntrsJSON.getInt("out_of_order"));
            } catch (JSONException exc) {
                // TODO: logging
            }
        }

        if (errCntrsJSON.has("seq_too_high")) {
            try {
                errCntrs.setSeqTooHigh(errCntrsJSON.getInt("seq_too_high"));
            } catch (JSONException exc) {
                // TODO: logging
            }
        }

        if (errCntrsJSON.has("dropped")) {
            try {
                errCntrs.setDropped(errCntrsJSON.getInt("dropped"));
            } catch (JSONException exc) {
                // TODO: logging
            }
        }

        if (errCntrsJSON.has("seq_too_low")) {
            try {
                errCntrs.setSeqTooLow(errCntrsJSON.getInt("seq_too_low"));
            } catch (JSONException exc) {
                // TODO: logging
            }
        }

        if (errCntrsJSON.has("dup")) {
            try {
                errCntrs.setDup(errCntrsJSON.getInt("dup"));
            } catch (JSONException exc) {
                // TODO: logging
            }
        }

        return errCntrs;
    }

    private void handleFlowStatsPropertyChanged(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        if (newValue == null) {
            return;
        }

        try {
            final JSONObject flowStatsJSON = new JSONObject(newValue);
            final JSONObject dataJSON = flowStatsJSON.getJSONObject("data");

            final JSONObject tsJSON = dataJSON.getJSONObject("ts");
            final long tsFreq = tsJSON.getLong("freq");
            final long tsValue = tsJSON.getLong("value");
            final double time = (tsValue*1.0)/tsFreq;
            if (time == flowStatsLastTime) {
                return;
            }
            flowStatsLastTime = time;

            final Set<String> unvisitedStreams = new HashSet<>(flowStatsHistoryMap.keySet());
            dataJSON.keySet().forEach((String key) -> {
                if (key.equals("ts")) {
                    return;
                }

                unvisitedStreams.remove(key);

                final JSONObject streamJSON = dataJSON.getJSONObject(key);
                if (streamJSON.keySet().isEmpty()) {
                    return;
                }

                final Map<Integer, Long> txPkts = getFlowStatsStreamParam(streamJSON, "tx_pkts");
                final Map<Integer, Long> txBytes = getFlowStatsStreamParam(streamJSON, "tx_bytes");
                final Map<Integer, Long> rxPkts = getFlowStatsStreamParam(streamJSON, "rx_pkts");
                final Map<Integer, Long> rxBytes = getFlowStatsStreamParam(streamJSON, "rx_bytes");

                ArrayHistory<StatsFlowStream> streamHistory = flowStatsHistoryMap.get(key);
                if (streamHistory == null) {
                    streamHistory = new ArrayHistory<>(historySize);
                    flowStatsHistoryMap.put(key, streamHistory);
                }

                final StatsFlowStream shadow = shadowFlowStatsMap.get(key);
                if (shadow == null) {
                    shadowFlowStatsMap.put(key, new StatsFlowStream(txPkts, rxPkts, txBytes, rxBytes, time));
                    return;
                }

                txPkts.forEach((Integer port, Long value) -> {
                    final Long shadowValue = shadow.getTxPkts().get(port);
                    if (shadowValue != null) {
                        txPkts.put(port, value - shadowValue);
                    }
                });
                txBytes.forEach((Integer port, Long value) -> {
                    final Long shadowValue = shadow.getTxBytes().get(port);
                    if (shadowValue != null) {
                        txBytes.put(port, value - shadowValue);
                    }
                });
                rxPkts.forEach((Integer port, Long value) -> {
                    final Long shadowValue = shadow.getRxPkts().get(port);
                    if (shadowValue != null) {
                        rxPkts.put(port, value - shadowValue);
                    }
                });
                rxBytes.forEach((Integer port, Long value) -> {
                    final Long shadowValue = shadow.getRxBytes().get(port);
                    if (shadowValue != null) {
                        rxBytes.put(port, value - shadowValue);
                    }
                });

                StatsFlowStream prev = streamHistory.last();
                if (prev == null) {
                    prev = new StatsFlowStream(shadow.getTime());
                }

                final StatsFlowStream curr = new StatsFlowStream(prev, txPkts, rxPkts, txBytes, rxBytes, time);
                streamHistory.add(curr);
            });

            unvisitedStreams.forEach((String stream) -> {
                flowStatsHistoryMap.remove(stream);
            });
        } catch (JSONException exc) {
            // TODO: logging
        }
    }

    private static Map<Integer, Long> getFlowStatsStreamParam(JSONObject streamJSON, String key) {
        Map<Integer, Long> param = new HashMap<>();

        if (!streamJSON.has(key)) {
            return param;
        }
        JSONObject paramJSON = streamJSON.getJSONObject(key);

        paramJSON.keySet().forEach((String port) -> {
            try {
                param.put(Integer.parseInt(port), paramJSON.getLong(port));
            } catch (NumberFormatException | JSONException exc) {
                // TODO: logging
            }
        });

        return param;
    }
}

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
import com.exalttech.trex.ui.models.stats.latency.MaxLatencyPoint;
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

    private Map<String, StatsLatencyStream> latencyStatsMap = new HashMap<>();
    private Map<String, ArrayHistory<MaxLatencyPoint>> maxLatencyHistory = new HashMap<>();
    private double maxLatencyLastTime = 0.0;

    private Map<String, ArrayHistory<StatsFlowStream>> flowStatsHistoryMap = new HashMap<>();
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
     * Return max latency history map
     *
     * @return
     */
    public Map<String, ArrayHistory<MaxLatencyPoint>> getMaxLatencyHistory() {
        return maxLatencyHistory;
    }

    /**
     * Return last updating time of max latency stats
     *
     * @return
     */
    public double getMaxLatencyLastTime() {
        return maxLatencyLastTime;
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
        AsyncResponseManager.getInstance().getTrexGlobalProperty().addListener(this::handleGlobalPropertyChanged);
        AsyncResponseManager.getInstance().getTrexLatencyProperty().addListener(this::handleLatencyPropertyChanged);
        AsyncResponseManager.getInstance().getTrexFlowStatsProperty().addListener(this::handleFlowStatsPropertyChanged);
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

            final Set<String> unvisitedStreams = new HashSet<>(maxLatencyHistory.keySet());
            final double time = System.currentTimeMillis()/1000.0;
            dataJSON.keySet().forEach((String stream) -> {
                unvisitedStreams.remove(stream);

                final JSONObject latencyStreamJSON = dataJSON.getJSONObject(stream);
                final StatsLatencyStreamLatency latency = getLatency(latencyStreamJSON);
                final StatsLatencyStreamErrCntrs errCntrs = getLatencyErrCntrs(latencyStreamJSON);
                final StatsLatencyStream latencyStream = new StatsLatencyStream(latency, errCntrs);

                latencyStatsMap.put(stream, latencyStream);

                ArrayHistory<MaxLatencyPoint> history = maxLatencyHistory.get(stream);
                if (history == null) {
                    history = new ArrayHistory<>(historySize);
                    maxLatencyHistory.put(stream, history);
                }

                history.add(new MaxLatencyPoint(latencyStream.getLatency().getLastMax(), time));
            });

            unvisitedStreams.forEach((String stream) -> {
                maxLatencyHistory.remove(stream);
            });

            maxLatencyLastTime = time;
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
            final long freq = tsJSON.getLong("freq");
            final long value = tsJSON.getLong("value");
            final double time = (value*1.0)/freq;
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
                final Map<Integer, Long> txPkts = getFlowStatsStreamParam(streamJSON, "tx_pkts");
                final Map<Integer, Long> txBytes = getFlowStatsStreamParam(streamJSON, "tx_bytes");
                final Map<Integer, Long> rxPkts = getFlowStatsStreamParam(streamJSON, "rx_pkts");
                final Map<Integer, Long> rxBytes = getFlowStatsStreamParam(streamJSON, "rx_bytes");

                ArrayHistory<StatsFlowStream> streamHistory = flowStatsHistoryMap.get(key);
                if (streamHistory == null) {
                    streamHistory = new ArrayHistory<>(historySize);
                    flowStatsHistoryMap.put(key, streamHistory);
                }

                StatsFlowStream prev = streamHistory.last();
                if (prev == null) {
                    prev = new StatsFlowStream();
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

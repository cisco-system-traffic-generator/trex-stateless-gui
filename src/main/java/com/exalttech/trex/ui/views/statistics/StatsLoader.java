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

import com.exalttech.trex.core.AsyncResponseManager;
import com.exalttech.trex.util.Util;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ObservableValue;

/**
 * Class that present a service for updating current loaded service
 *
 * @author GeorgeKh
 */
public class StatsLoader {

    private static StatsLoader instance;

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
    private Map<String, String> latencyStatsMap = new HashMap<>();

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
    public Map<String, String> getLatencyStatsMap() {
        return latencyStatsMap;
    }

    /**
     * Start listening on stats changes for updating
     */
    public void start() {
        AsyncResponseManager.getInstance().getTrexGlobalProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null) {
                String data = Util.fromJSONResult(newValue, "data");
                if (validAsyncResponse(data)) {
                    previousStatsList = loadedStatsList;
                    loadedStatsList = Util.getStatsFromJSONString(data);
                }
            }
        });

        AsyncResponseManager.getInstance().getTrexLatencyProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null) {
                String data = Util.fromJSONResult(newValue, "data");
                if (!Util.isNullOrEmpty(data)) {
                    latencyStatsMap = Util.getStatsFromJSONString(data);
                }
            }
        });
    }
}

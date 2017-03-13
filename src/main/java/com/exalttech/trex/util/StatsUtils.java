package com.exalttech.trex.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.exalttech.trex.ui.models.json.stats.streams.JSONFlowStatsStream;
import com.exalttech.trex.ui.views.statistics.StatsLoader;


public class StatsUtils {
    public static Set<String> getVisibleStream(Set<Integer> visiblePorts) {
        if (visiblePorts == null) {
            return null;
        }

        Set<String> visibleStreams = new HashSet<String>();

        if (visiblePorts.isEmpty()) {
            return visibleStreams;
        }

        Map<String, String> flowStats = StatsLoader.getInstance().getLoadedFlowStatsMap();

        flowStats.forEach((String key, String data) -> {
            if (key.equals("ts")) {
                return;
            }

            if (Util.isNullOrEmpty(data) || data.equals("{}")) {
                visibleStreams.add(key);
                return;
            }

            JSONFlowStatsStream jsonFlowStatsStream = (JSONFlowStatsStream) Util.fromJSONString(
                    data,
                    JSONFlowStatsStream.class
            );
            if (jsonFlowStatsStream == null) {
                visibleStreams.add(key);
                return;
            }

            if (
                visiblePorts.stream().anyMatch(jsonFlowStatsStream.getTx_pkts()::containsKey)
                || visiblePorts.stream().anyMatch(jsonFlowStatsStream.getTx_bytes()::containsKey)
                || visiblePorts.stream().anyMatch(jsonFlowStatsStream.getRx_pkts()::containsKey)
                || visiblePorts.stream().anyMatch(jsonFlowStatsStream.getRx_bytes()::containsKey)
            ) {
                visibleStreams.add(key);
            }
        });

        return visibleStreams;
    }
}

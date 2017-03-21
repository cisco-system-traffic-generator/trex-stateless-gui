package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import java.util.Set;


public interface DashboardTabChartsUpdatable {
    void update(Set<Integer> visiblePorts, Set<String> visibleStreams, int streamsCount);
}

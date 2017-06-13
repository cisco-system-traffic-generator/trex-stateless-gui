package com.exalttech.trex.ui.views.statistics;

import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exalttech.trex.core.AsyncResponseManager;
import com.exalttech.trex.util.Util;


public class StatsLoader {
    public interface GlobalStatsChangedListener {
        void globalStatsChanged();
    }

    private static StatsLoader instance;

    public static StatsLoader getInstance() {
        if (instance == null) {
            instance = new StatsLoader();
        }
        return instance;
    }

    private final List<GlobalStatsChangedListener> globalStatsChangedListeners = new ArrayList<>();

    private Map<String, String> loadedStatsList = new HashMap<>();
    private Map<String, String> previousStatsList = new HashMap<>();
    private Map<String, String> shadowStatsList = null;

    private StatsLoader() {}

    public Map<String, String> getLoadedStatsList() {
        return loadedStatsList;
    }

    public Map<String, String> getPreviousStatsList() {
        return previousStatsList;
    }

    public Map<String, String> getShadowStatsList() {
        return shadowStatsList != null ? shadowStatsList : new HashMap<>();
    }

    private boolean validAsyncResponse(String jsonString) {
        return !Util.isNullOrEmpty(jsonString) && jsonString.contains("m_cpu_util");
    }

    public void start() {
        synchronized (loadedStatsList) {
            loadedStatsList.clear();
        }
        synchronized (previousStatsList) {
            previousStatsList.clear();
        }
        shadowStatsList = null;

        handleGlobalStatsChanged();

        AsyncResponseManager.getInstance().getTrexGlobalProperty().addListener(this::handleGlobalPropertyChanged);
    }

    public void reset() {
        shadowStatsList = loadedStatsList;

        handleGlobalStatsChanged();
    }

    public void addGlobalStatsChangedListener(final GlobalStatsChangedListener listener) {
        synchronized (globalStatsChangedListeners) {
            globalStatsChangedListeners.add(listener);
        }
    }

    public void removeGlobalStatsChangedListener(final GlobalStatsChangedListener listener) {
        synchronized (globalStatsChangedListeners) {
            globalStatsChangedListeners.remove(listener);
        }
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

        handleGlobalStatsChanged();
    }

    private void handleGlobalStatsChanged() {
        synchronized (globalStatsChangedListeners) {
            globalStatsChangedListeners.forEach(GlobalStatsChangedListener::globalStatsChanged);
        }
    }
}

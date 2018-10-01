package com.exalttech.trex.ui.controllers.daemon;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class InterfaceInfo {
    public String Slot_str;
    public Map<String, String> properties = new HashMap<>();

    @JsonAnySetter
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonCreator
    public InterfaceInfo(
            @JsonProperty(value="Slot_str", required = true) String slot_str) {
        Slot_str = slot_str;
    }
}

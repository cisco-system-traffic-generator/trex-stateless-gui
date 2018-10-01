package com.exalttech.trex.ui.controllers.daemon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MetaField {
    public enum Type {
        NUMBER, FLOAT, LIST, OBJECT, STRING, BOOLEAN, IP, MAC
    }

    public Type type;
    public String id;
    public String name;

    @JsonProperty("default")
    public String default_value = null;

    public String description = "";
    public Boolean mandatory = false;
    public MetaField item = null;
    public List<MetaField> attributes = null;
    public String mandatory_if_not_set = null;

    @JsonCreator
    public MetaField(
            @JsonProperty(value="type", required = true) Type type,
            @JsonProperty(value="name", required = true) String name
    ) {
        this.type = type;
        this.name = name;
    }
}

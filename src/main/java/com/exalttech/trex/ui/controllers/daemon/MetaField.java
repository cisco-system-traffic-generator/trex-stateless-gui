package com.exalttech.trex.ui.controllers.daemon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MetaField {

    static public class MetaFieldBuilder {
        MetaField result;
        static public MetaFieldBuilder build() {
            return new MetaFieldBuilder();
        }

        private MetaFieldBuilder() {
            this.result = new MetaField(Type.NUMBER, "default");
        }

        public MetaFieldBuilder setType(Type type) {
            this.result.type = type;
            return this;
        }

        public MetaFieldBuilder setName(String name) {
            this.result.name = name;
            return this;
        }

        public MetaFieldBuilder setId(String id) {
            this.result.id = id;
            return this;
        }

        public MetaFieldBuilder setDefaultValue(String defaultValue) {
            this.result.default_value = defaultValue;
            return this;
        }

        public MetaFieldBuilder setDescription(String description) {
            this.result.description = description;
            return this;
        }

        public MetaFieldBuilder setMandatory(Boolean mandatory) {
            this.result.mandatory = mandatory;
            return this;
        }

        public MetaFieldBuilder setItem(MetaField item) {
            this.result.item = item;
            return this;
        }

        public MetaFieldBuilder setAttributes(List<MetaField> attributes) {
            this.result.attributes = attributes;
            return this;
        }

        public MetaFieldBuilder  setValues(List<Object> values) {
            this.result.values = values;
            return this;
        }

        public MetaFieldBuilder setMandatoryIfNotSet(String mandatoryIfNotSet) {
            this.result.mandatory_if_not_set = mandatoryIfNotSet;
            return this;
        }

        public MetaField getMetaField() {
            return this.result;
        }
    }

    public enum Type {
        NUMBER, FLOAT, LIST, OBJECT, STRING, BOOLEAN, IP, MAC, ENUM
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
    public List<Object> values = null;
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

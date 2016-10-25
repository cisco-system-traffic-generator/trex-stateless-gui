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
package com.exalttech.trex.remote.models.profiles;

import com.exalttech.trex.util.Util;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author GeorgeKh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({"pcap", "binary", "meta", "model"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Packet implements Cloneable {

    @JsonProperty("model")
    private String model;

    @JsonProperty("pcap")
    private String pcap;

    @JsonProperty("binary")
    private String binary;

    @JsonProperty("meta")
    String meta = "";

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return The pcap
     */
    @JsonProperty("pcap")
    public String getPcap() {
        return pcap;
    }

    /**
     *
     * @param pcap The pcap
     */
    @JsonProperty("pcap")
    public void setPcap(String pcap) {
        this.pcap = pcap;
    }

    /**
     *
     * @return
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     *
     * @param name
     * @param value
     */
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     *
     * @param additionalProperties
     */
    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    /**
     *
     * @return
     */
    @JsonProperty("binary")
    public String getBinary() {
        return binary;
    }

    /**
     *
     * @return
     */
    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    /**
     *
     * @param binary
     */
    @JsonProperty("binary")
    public void setBinary(String binary) {
        this.binary = binary;
    }

    /**
     *
     * @param model
     */
    @JsonProperty("model")
    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Packet [binary=" + pcap + ", additionalProperties=" + additionalProperties + "]";
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Packet clonedPacket = (Packet) super.clone();
        clonedPacket.setAdditionalProperties(Util.getClonedMap(additionalProperties));
        return clonedPacket;
    }

    /**
     *
     * @return
     */
    public String getMeta() {
        return meta;
    }

    /**
     *
     * @param meta
     */
    public void setMeta(String meta) {
        this.meta = meta;
    }

}

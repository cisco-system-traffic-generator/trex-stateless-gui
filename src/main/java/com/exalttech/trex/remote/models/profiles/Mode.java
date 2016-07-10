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

@JsonPropertyOrder({"rate", "type", "pps", "total_pkts", "pkts_per_burst", "ibg", "count"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Mode implements Cloneable {

    @JsonProperty("type")
    private String type;

    @JsonProperty("pps")
    private double pps = 1.0;

    @JsonProperty("total_pkts")
    private int totalPkts = 1;

    @JsonProperty("pkts_per_burst")
    private int packetsPerBurst = 1;

    @JsonProperty("ibg")
    private Double ibg = 0.0;

    @JsonProperty("count")
    private int count = 1;

    @JsonProperty("rate")
    private Rate rate = new Rate();

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     *
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     *
     * @return
     */
    public Double getIbg() {
        return ibg;
    }

    /**
     *
     * @param ibg
     */
    public void setIbg(Double ibg) {
        this.ibg = ibg;
    }

    /**
     *
     * @return
     */
    public int getPacketsPerBurst() {
        return packetsPerBurst;
    }

    /**
     *
     * @param packetsPerBurst
     */
    public void setPacketsPerBurst(int packetsPerBurst) {
        this.packetsPerBurst = packetsPerBurst;
    }

    /**
     *
     * @return The pps
     */
    @JsonProperty("pps")
    public double getPps() {
        return pps;
    }

    /**
     *
     * @return The totalPkts
     */
    @JsonProperty("total_pkts")
    public int getTotalPkts() {
        return totalPkts;
    }

    /**
     *
     * @return The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     *
     * @param pps The pps
     */
    @JsonProperty("pps")
    public void setPps(double pps) {
        this.pps = pps;
    }

    /**
     *
     * @param totalPkts The total_pkts
     */
    @JsonProperty("total_pkts")
    public void setTotalPkts(int totalPkts) {
        this.totalPkts = totalPkts;
    }

    /**
     *
     * @param type The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    @JsonProperty("rate")
    public Rate getRate() {
        return rate;
    }

    /**
     *
     * @param rate
     */
    @JsonProperty("rate")
    public void setRate(Rate rate) {
        this.rate = rate;
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

    @Override
    public String toString() {
        return "Mode [type=" + type + ", pps=" + pps + ", totalPkts=" + totalPkts + ", packetsPerBurst="
                + packetsPerBurst + ", ibg=" + ibg + ", count=" + count + ", additionalProperties="
                + additionalProperties + "]";
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Mode clonedMode = (Mode) super.clone();
        clonedMode.setRate((Rate) clonedMode.getRate().clone());
        clonedMode.setAdditionalProperties(Util.getClonedMap(additionalProperties));
        return clonedMode;
    }

}

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
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author GeorgeKh
 */
@JsonInclude(Include.NON_NULL)

@JsonPropertyOrder({"action_count", "enabled", "flags", "flow_stats", "isg", "mode", "next_stream_id", "packet", "self_start"})
@JsonIgnoreProperties(ignoreUnknown = true)

public class Stream implements Cloneable {

    @JsonProperty("self_start")
    private boolean selfStart = true;

    @JsonProperty("enabled")
    private boolean enabled = true;

    @JsonProperty("next_stream_id")
    private int nextStreamId = -1;

    @JsonProperty("isg")
    private Double isg = 0.0;

    @JsonProperty("packet")
    private Packet packet;

    @JsonProperty("mode")
    private Mode mode;

    @JsonProperty("flags")
    private int flags = 0;

    @JsonProperty("action_count")
    private int actionCount = 0;

    @JsonIgnore
    private FlowStats flowStats;

    @JsonIgnore
    private String vmRaw = "";
    
    @JsonProperty("vm")
    private Map<String, Object> vm;

    @JsonIgnore
    private String rxStatsRaw = "";
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     */
    public Stream() {
        mode = new Mode();
        packet = new Packet();
        flowStats = new FlowStats();
    }

    public Map<String, Object> getVm() {
        if (vm != null && vm.isEmpty()) {
            return null;
        }
        return vm;
    }

    public void setVm(Map<String, Object> vm) {
        this.vm = vm;
    }

    /**
     *
     * @return
     */
    @JsonIgnore
    public String getVmRaw() {
        return vmRaw;
    }

    /**
     *
     * @param vmRaw
     */
    public void setVmRaw(String vmRaw) {
        this.vmRaw = vmRaw;
    }

    /**
     *
     * @return
     */
    public String getRxStatsRaw() {
        return rxStatsRaw;
    }

    /**
     *
     * @param rxStatsRaw
     */
    public void setRxStatsRaw(String rxStatsRaw) {
        this.rxStatsRaw = rxStatsRaw;
    }

    /**
     *
     * @return The mode
     */
    @JsonProperty("mode")
    public Mode getMode() {
        return mode;
    }

    /**
     *
     * @return
     */
    @JsonProperty("next_stream_id")
    public int getNextStreamId() {
        return nextStreamId;
    }

    /**
     *
     * @return The packet
     */
    @JsonProperty("packet")
    public Packet getPacket() {
        return packet;
    }

    /**
     *
     * @return The selfStart
     */
    @JsonProperty("self_start")
    public boolean isSelfStart() {
        return selfStart;
    }

    /**
     *
     * @param mode The mode
     */
    @JsonProperty("mode")
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     *
     * @param nextStreamId
     */
    @JsonProperty("next_stream_id")
    public void setNextStreamId(int nextStreamId) {
        this.nextStreamId = nextStreamId;
    }

    /**
     *
     * @param packet The packet
     */
    @JsonProperty("packet")
    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    /**
     *
     * @param selfStart The self_start
     */
    @JsonProperty("self_start")
    public void setSelfStart(boolean selfStart) {
        this.selfStart = selfStart;
    }

    /**
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     *
     * @return
     */
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
     * @return
     */
    public Double getIsg() {
        return isg;
    }

    /**
     *
     * @param isg
     */
    public void setIsg(Double isg) {
        this.isg = isg;
    }

    /**
     *
     * @return
     */
    public int getFlags() {
        return flags;
    }

    /**
     *
     * @param flags
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }

    /**
     *
     * @return
     */
    public int getActionCount() {
        return actionCount;
    }

    /**
     *
     * @param actionCount
     */
    public void setActionCount(int actionCount) {
        this.actionCount = actionCount;
    }

    /**
     *
     * @return
     */
    @JsonProperty("flow_stats")
    public FlowStats getFlowStats() {
        return flowStats;
    }

    /**
     *
     * @param flowStats
     */
    @JsonProperty("flow_stats")
    public void setFlowStats(FlowStats flowStats) {
        this.flowStats = flowStats;
    }

    @Override
    public String toString() {
        return "Stream [selfStart=" + selfStart + ", enabled=" + enabled + ", nextStreamId=" + nextStreamId + ", isg="
                + isg + ", packet=" + packet + ", mode=" + mode + ", vmRaw=" + vmRaw + ", rxStatsRaw=" + rxStatsRaw
                + ", additionalProperties=" + additionalProperties + "]";
    }

    /**
     *
     * @param additionalProperties
     */
    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Stream clonedStream = (Stream) super.clone();
        clonedStream.setMode((Mode) clonedStream.getMode().clone());
        clonedStream.setPacket((Packet) clonedStream.getPacket().clone());
        clonedStream.setFlowStats((FlowStats) clonedStream.getFlowStats().clone());

        clonedStream.setAdditionalProperties(Util.getClonedMap(additionalProperties));
        return clonedStream;
    }

}

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

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Georgekh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "enabled"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowStats implements Cloneable {

    @JsonProperty("enabled")
    private Boolean enabled = false;

    @JsonProperty("rule_type")
    private String ruleType;

    @JsonProperty("stream_id")
    private int streamID;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return The enabled
     */
    @JsonProperty("enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     *
     * @param enabled The enabled
     */
    @JsonProperty("enabled")
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Return wither rx stat is enable
     * @return 
     */
    public boolean isEnabled(){
        return enabled;
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
     * Return rule type
     *
     * @return
     */
    @JsonProperty("rule_type")
    public String getRuleType() {
        return ruleType;
    }

    /**
     * Set rule type
     *
     * @param ruleType
     */
    @JsonProperty("rule_type")
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * Return stream ID
     *
     * @return
     */
    @JsonProperty("stream_id")
    public int getStreamID() {
        return streamID;
    }

    /**
     * Set stream ID
     *
     * @param streamID
     */
    @JsonProperty("stream_id")
    public void setStreamID(int streamID) {
        this.streamID = streamID;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        FlowStats clonedFlow = (FlowStats) super.clone();
        clonedFlow.setAdditionalProperties(Util.getClonedMap(additionalProperties));
        return clonedFlow;
    }

    @JsonIgnore
    public boolean isLatencyEnabled() {
        return "latency".equalsIgnoreCase(ruleType);
    }
}

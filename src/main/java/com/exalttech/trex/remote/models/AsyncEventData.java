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
package com.exalttech.trex.remote.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.Generated;

/**
 *
 * @author GeorgeKh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "force",
    "port_id",
    "session_id",
    "who"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncEventData {

    @JsonProperty("port_id")
    private Integer portId;
    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("who")
    private String who;
    @JsonProperty("force")
    boolean force;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return The portId
     */
    @JsonProperty("port_id")
    public Integer getPortId() {
        return portId;
    }

    /**
     *
     * @param portId The port_id
     */
    @JsonProperty("port_id")
    public void setPortId(Integer portId) {
        this.portId = portId;
    }

    /**
     *
     * @return The sessionId
     */
    @JsonProperty("session_id")
    public Long getSessionId() {
        return sessionId;
    }

    /**
     *
     * @param sessionId The session_id
     */
    @JsonProperty("session_id")
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    /**
     *
     * @return The who
     */
    @JsonProperty("who")
    public String getWho() {
        return who;
    }

    /**
     *
     * @param who The who
     */
    @JsonProperty("who")
    public void setWho(String who) {
        this.who = who;
    }
    
    /**
     * Set force
     * @param force 
     */
    @JsonProperty("force")
    public void setForce(boolean force) {
        this.force = force;
    }

    /**
     * 
     * @return is force
     */
    @JsonProperty("force")
    public boolean isForce() {
        return force;
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

}

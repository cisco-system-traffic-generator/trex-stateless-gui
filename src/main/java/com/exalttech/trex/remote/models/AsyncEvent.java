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
    "data",
    "name",
    "type"
})
@JsonIgnoreProperties(ignoreUnknown = true)

public class AsyncEvent {

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AsyncEventData data;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private Integer type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return The data
     */
    @JsonProperty("data")
    public AsyncEventData getData() {
        return data;
    }

    /**
     *
     * @param data The data
     */
    @JsonProperty("data")
    public void setData(AsyncEventData data) {
        this.data = data;
    }

    /**
     *
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return The type
     */
    @JsonProperty("type")
    public Integer getType() {
        return type;
    }

    /**
     *
     * @param type The type
     */
    @JsonProperty("type")
    public void setType(Integer type) {
        this.type = type;
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

    @Override
    public String toString() {
        int portId = -1;
        if (type != 100) {
            portId = data.getPortId();
        }
        switch (type) {
            case 0:
                return "Port {" + portId + "} has started";
            case 1:
                return "Port {" + portId + "} has stopped";
            case 2:
                return "Port {" + portId + "} has paused";
            case 3:
                return "Port {" + portId + "} has resumed";
            case 4:
                return "Port {" + portId + "} job done";
            case 5:
                if(data.isForce()){
                    return "Port {" + portId + "} was forcely taken by " + data.getWho();
                }
                return "Port {" + portId + "} was taken by " + data.getWho();
            case 100:
                return "Server has stopped";
            default:
                return null;
        }

    }

}

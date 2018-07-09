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

import com.exalttech.trex.remote.models.params.Params;
import com.exalttech.trex.util.Util;
import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author GeorgeKh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "next", "stream", "stream_id"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile extends Params implements Cloneable {

    @JsonProperty("name")
    private String name;

    @JsonProperty("next")
    private String next = "-1";

    @JsonProperty("stream")
    private Stream stream;

    @JsonProperty("stream_id")
    private int streamId;

    @JsonProperty("handler")
    private String handler;
    @JsonProperty("port_id")
    private Integer portId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonCreator
    public Profile(
            @JsonProperty(value="name", required = true) String name,
            @JsonProperty(value="stream", required = true) Stream stream,
            @JsonProperty(value="stream_id", required = true) int streamId

    ) {
        this.name = name;
        this.stream = stream;
        this.streamId = streamId;
    }
    /**
     *
     */
    public Profile() {
        stream = new Stream();
    }

    /**
     *
     * @return The handler
     */
    @JsonProperty("handler")
    public String getHandler() {
        return handler;
    }

    /**
     *
     * @param handler The handler
     */
    @JsonProperty("handler")
    public void setHandler(String handler) {
        this.handler = handler;
    }

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
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @return The stream
     */
    @JsonProperty("stream")
    public Stream getStream() {
        return stream;
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
     * @param stream The stream
     */
    @JsonProperty("stream")
    public void setStream(Stream stream) {
        this.stream = stream;
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
    @JsonProperty("next")
    public String getNext() {
        return next;
    }

    /**
     *
     * @param next
     */
    @JsonProperty("next")
    public void setNext(String next) {
        this.next = next;
    }

    /**
     *
     * @return
     */
    @JsonProperty("stream_id")
    public int getStreamId() {
        return streamId;
    }

    /**
     *
     * @param streamId
     */
    @JsonProperty("stream_id")
    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }

    @Override
    public String toString() {
        return "Profile{" + "name=" + name + ", next=" + next + ", stream=" + stream + ", stream_id=" + streamId + ", additionalProperties=" + additionalProperties + '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Profile clonedProfile = (Profile) super.clone();
        clonedProfile.setStream((Stream) clonedProfile.getStream().clone());

        clonedProfile.setAdditionalProperties(Util.getClonedMap(additionalProperties));
        return clonedProfile;
    }

}

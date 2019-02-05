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
package com.exalttech.trex.remote.models.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "handler",
        "port_id",
        "stream_id"
})
public class StreamParams extends Params {

    @JsonProperty("handler")
    private String handler;
    @JsonProperty("port_id")
    private Integer portId;
    @JsonProperty("stream_id")
    private Integer stream_id;

    public StreamParams(Integer portId, Integer stream_id, String handler) {
        this.handler = handler;
        this.portId = portId;
        this.stream_id = stream_id;
    }

    @JsonProperty("handler")
    public String getHandler() {
        return handler;
    }

    @JsonProperty("handler")
    public void setHandler(String handler) {
        this.handler = handler;
    }

    @JsonProperty("port_id")
    public Integer getPortId() {
        return portId;
    }

    @JsonProperty("port_id")
    public void setPortId(Integer portId) {
        this.portId = portId;
    }

    @JsonProperty("stream_id")
    public Integer getStream_id() {
        return stream_id;
    }

    @JsonProperty("stream_id")
    public void setStream_id(Integer stream_id) {
        this.stream_id = stream_id;
    }

}
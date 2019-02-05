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
        "dst_mac",
        "handler",
        "port_id",
        "block"
})
public class L2Params extends Params {

    @JsonProperty("dst_mac")
    private String dst_mac;
    @JsonProperty("handler")
    private String handler;
    @JsonProperty("port_id")
    private Integer portId;
    @JsonProperty("block")
    private Boolean block;

    public L2Params(Integer portId, String handler, String dst_mac, Boolean block) {
        this.handler = handler;
        this.portId = portId;
        this.dst_mac = dst_mac;
        this.block = block;
    }

    @JsonProperty("dst_mac")
    public String getDst_mac() {
        return dst_mac;
    }

    @JsonProperty("dst_mac")
    public void setDst_mac(String dst_mac) {
        this.dst_mac = dst_mac;
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

    @JsonProperty("block")
    public Boolean getBlock() {
        return block;
    }

    @JsonProperty("block")
    public void setBlock(Boolean block) {
        this.block = block;
    }
}
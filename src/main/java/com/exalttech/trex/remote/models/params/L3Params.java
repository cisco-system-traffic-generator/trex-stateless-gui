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

import javax.annotation.Generated;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "dst_addr",
        "src_addr",
        "handler",
        "port_id"
})
public class L3Params extends Params {

    @JsonProperty("dst_addr")
    private String dst_addr;
    @JsonProperty("src_addr")
    private String src_addr;
    @JsonProperty("handler")
    private String handler;
    @JsonProperty("port_id")
    private Integer portId;

    public L3Params(Integer portId, String handler, String dst_addr, String src_addr) {
        this.handler = handler;
        this.portId = portId;
        this.dst_addr = dst_addr;
        this.src_addr = src_addr;
    }

    @JsonProperty("dst_addr")
    public String getDst_addr() {
        return dst_addr;
    }

    @JsonProperty("dst_addr")
    public void setDst_addr(String dst_addr) {
        this.dst_addr = dst_addr;
    }

    @JsonProperty("src_addr")
    public String getSrc_addr() {
        return src_addr;
    }

    @JsonProperty("src_addr")
    public void setSrc_addr(String src_addr) {
        this.src_addr = src_addr;
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

}
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
package com.exalttech.trex.remote.models.common;

import com.exalttech.trex.remote.models.params.Params;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.annotation.processing.Generated;

/**
 * RPC request model
 *
 * @author GeorgeKh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "jsonrpc",
    "method",
    "params"
})
public class RPCRequest {

    @JsonProperty("id")
    private String id;
    @JsonProperty("jsonrpc")
    private String jsonrpc = "2.0";
    @JsonProperty("method")
    private String method;
    @JsonProperty("params")
    private Params params;

    /**
     *
     * @return The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     *
     * @param id The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return The jsonrpc
     */
    @JsonProperty("jsonrpc")
    public String getJsonrpc() {
        return jsonrpc;
    }

    /**
     *
     * @param jsonrpc The jsonrpc
     */
    @JsonProperty("jsonrpc")
    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    /**
     *
     * @return The method
     */
    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    /**
     *
     * @param method The method
     */
    @JsonProperty("method")
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     *
     * @return The params
     */
    @JsonProperty("params")
    public Params getParams() {
        return params;
    }

    /**
     *
     * @param params The params
     */
    @JsonProperty("params")
    public void setParams(Params params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "RPCRequest{" + "id=" + id + ", jsonrpc=" + jsonrpc + ", method=" + method + ", params=" + params + '}';
    }

}

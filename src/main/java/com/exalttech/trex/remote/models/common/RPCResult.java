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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.annotation.Generated;

/**
 * RPC result model
 *
 * @author GeorgeKh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "jsonrpc",
    "result"
})
public class RPCResult {

    @JsonProperty("id")
    private String id;
    @JsonProperty("jsonrpc")
    private String jsonrpc;
    @JsonProperty("result")
    private String result;

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
     * @return The result
     */
    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    /**
     *
     * @param result The result
     */
    @JsonProperty("result")
    public void setResult(String result) {
        this.result = result;
    }

}

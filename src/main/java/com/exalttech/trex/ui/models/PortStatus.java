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
package com.exalttech.trex.ui.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.annotation.Generated;

/**
 * Port status response data model
 * @author GeorgeKH
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "jsonrpc",
    "result"
})
public class PortStatus {

    @JsonProperty("id")
    private String id;

    @JsonProperty("jsonrpc")
    private String jsonrpc;

    @JsonProperty("result")
    private PortStatusResult result;

    /**
     * 
     * @return 
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id 
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return 
     */
    @JsonProperty("jsonrpc")
    public String getJsonrpc() {
        return jsonrpc;
    }

    /**
     * 
     * @param jsonrpc 
     */
    @JsonProperty("jsonrpc")
    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    /**
     * 
     * @return 
     */
    @JsonProperty("result")
    public PortStatusResult getResult() {
        return result;
    }

    /**
     * 
     * @param result 
     */
    @JsonProperty("result")
    public void setResult(PortStatusResult result) {
        this.result = result;
    }

    /**
     * class present port status result model
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder({
        "owner",
        "state"
    })
    public class PortStatusResult {

        @JsonProperty("owner")
        private String owner;

        @JsonProperty("state")
        private String state;

        /**
         * 
         * @return 
         */
        @JsonProperty("owner")
        public String getOwner() {
            return owner;
        }

        /**
         * 
         * @param owner 
         */
        @JsonProperty("owner")
        public void setOwner(String owner) {
            this.owner = owner;
        }

        /**
         * 
         * @return 
         */
        @JsonProperty("state")
        public String getState() {
            return state;
        }

        /**
         * 
         * @param state 
         */
        @JsonProperty("state")
        public void setState(String state) {
            this.state = state;
        }

    }
}

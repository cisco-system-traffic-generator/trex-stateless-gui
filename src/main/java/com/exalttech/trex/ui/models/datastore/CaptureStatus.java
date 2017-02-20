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
package com.exalttech.trex.ui.models.datastore;

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
        "bytes",
        "count",
        "filter",
        "id",
        "limit",
        "state"
        })
public class CaptureStatus {

    @JsonProperty("bytes")
    int bytes;

    @JsonProperty("count")
    int count;

    @JsonProperty("filter")
    CaptureFilter filter;

    @JsonProperty("id")
    int id;

    @JsonProperty("limit")
    int limit;

    @JsonProperty("state")
    String state;


    @JsonProperty("bytes")
    public int getBytes() {
        return bytes;
    }

    @JsonProperty("bytes")
    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    @JsonProperty("count")
    public int getCount() {
        return count;
    }

    @JsonProperty("count")
    public void setCount(int count) {
        this.count = count;
    }

    @JsonProperty("filter")
    public CaptureFilter getFilter() {
        return filter;
    }

    @JsonProperty("filter")
    public void setFilter(CaptureFilter filter) {
        this.filter = filter;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("limit")
    public int getLimit() {
        return limit;
    }

    @JsonProperty("limit")
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }


    /**
     * class present port status result model
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder({
            "rx",
            "tx"
    })
    public class CaptureFilter {

        @JsonProperty("rx")
        private int rx;

        @JsonProperty("tx")
        private int tx;

        @JsonProperty("rx")
        public int getRx() {
            return rx;
        }

        @JsonProperty("rx")
        public void setRx(int rx) {
            this.rx = rx;
        }

        @JsonProperty("tx")
        public int getTx() {
            return tx;
        }

        @JsonProperty("tx")
        public void setTx(int tx) {
            this.tx = tx;
        }
    }
}

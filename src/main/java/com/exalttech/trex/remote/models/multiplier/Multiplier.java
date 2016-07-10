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
package com.exalttech.trex.remote.models.multiplier;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.annotation.Generated;

/**
 *
 * @author GeorgeKh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "op",
    "type",
    "value"
})
public class Multiplier {

    @JsonProperty("op")
    private String op = "abs";
    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private Double value;

    /**
     *
     * @param type
     * @param multiplierValue
     */
    public Multiplier(String type, double multiplierValue) {
        this.type = type;
        this.value = multiplierValue;
    }

    /**
     *
     * @return The op
     */
    @JsonProperty("op")
    public String getOp() {
        return op;
    }

    /**
     *
     * @param op The op
     */
    @JsonProperty("op")
    public void setOp(String op) {
        this.op = op;
    }

    /**
     *
     * @return The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     *
     * @param type The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return The value
     */
    @JsonProperty("value")
    public Double getValue() {
        return value;
    }

    /**
     *
     * @param value The value
     */
    @JsonProperty("value")
    public void setValue(Double value) {
        this.value = value;
    }

}

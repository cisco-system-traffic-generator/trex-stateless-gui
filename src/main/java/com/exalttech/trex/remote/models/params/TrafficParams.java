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

import com.exalttech.trex.remote.models.multiplier.Multiplier;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.annotation.processing.Generated;

/**
 *
 * @author GeorgeKh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "duration",
    "force",
    "handler",
    "mul",
    "port_id"
})
public class TrafficParams extends Params {

    @JsonProperty("duration")
    private Double duration;
    @JsonProperty("force")
    private Boolean force;
    @JsonProperty("handler")
    private String handler;
    @JsonProperty("mul")
    private Multiplier mul;
    @JsonProperty("port_id")
    private Integer portId;

    /**
     *
     * @param force
     * @param handler
     * @param mul
     * @param portId
     */
    public TrafficParams(Boolean force, String handler, Multiplier mul, Integer portId) {

        this.force = force;
        this.handler = handler;
        this.mul = mul;
        this.portId = portId;
    }

    /**
     *
     * @return The duration
     */
    @JsonProperty("duration")
    public Double getDuration() {
        return duration;
    }

    /**
     *
     * @param duration The duration
     */
    @JsonProperty("duration")
    public void setDuration(Double duration) {
        this.duration = duration;
    }

    /**
     *
     * @return The force
     */
    @JsonProperty("force")
    public Boolean getForce() {
        return force;
    }

    /**
     *
     * @param force The force
     */
    @JsonProperty("force")
    public void setForce(Boolean force) {
        this.force = force;
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
     * @return The mul
     */
    @JsonProperty("mul")
    public Multiplier getMul() {
        return mul;
    }

    /**
     *
     * @param mul The mul
     */
    @JsonProperty("mul")
    public void setMul(Multiplier mul) {
        this.mul = mul;
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

}

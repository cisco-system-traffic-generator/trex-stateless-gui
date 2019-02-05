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

/**
 *
 * @author GeorgeKh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "force",
    "port_id",
    "session_id",
    "user"
})
public class AcquireParams extends Params {

    @JsonProperty("force")
    private Boolean force;
    @JsonProperty("port_id")
    private Integer portId;
    @JsonProperty("session_id")
    private int sessionId;
    @JsonProperty("user")
    private String user;

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
     * @return The sessionId
     */
    @JsonProperty("session_id")
    public int getSessionId() {
        return sessionId;
    }

    /**
     *
     * @param sessionId The session_id
     */
    @JsonProperty("session_id")
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    /**
     *
     * @return The user
     */
    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    /**
     *
     * @param user The user
     */
    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

}

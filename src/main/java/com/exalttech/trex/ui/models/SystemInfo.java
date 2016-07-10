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
import java.util.List;

/**
 *
 * System info model
 *
 * @author Georgekh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemInfo {

    @JsonProperty("core_type")
    String coreType;

    @JsonProperty("dp_core_count")
    String dpCoreCount;
    String hostname;
    @JsonProperty("port_count")
    int portCount;
    List<Port> ports;
    String uptime;

    /**
     * Return core type
     *
     * @return
     */
    public String getCoreType() {
        return coreType;
    }

    /**
     * Set core type
     *
     * @param coreType
     */
    public void setCoreType(String coreType) {
        this.coreType = coreType;
    }

    /**
     * Return Dp core count
     *
     * @return
     */
    public String getDpCoreCount() {
        return dpCoreCount;
    }

    /**
     * Set Dp core count
     *
     * @param dpCoreCount
     */
    public void setDpCoreCount(String dpCoreCount) {
        this.dpCoreCount = dpCoreCount;
    }

    /**
     * Return host name
     *
     * @return
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Set host name
     *
     * @param hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Return port count
     *
     * @return
     */
    public int getPortCount() {
        return portCount;
    }

    /**
     * Set port count
     *
     * @param portCount
     */
    public void setPortCount(int portCount) {
        this.portCount = portCount;
    }

    /**
     * Return ports list
     *
     * @return
     */
    public List<Port> getPorts() {
        return ports;
    }

    /**
     * Set ports list
     *
     * @param ports
     */
    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    /**
     * Return up time
     *
     * @return
     */
    public String getUptime() {
        return uptime;
    }

    /**
     * Set up time
     *
     * @param uptime
     */
    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

}

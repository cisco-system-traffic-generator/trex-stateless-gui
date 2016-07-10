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

/**
 * System info request model
 *
 * @author GeorgeKh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemInfoReq {
    
    String id;
    String jsonrpc;
    SystemInfo result;
    String port;
    String ip;

    /**
     * Return ID
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Set ID
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Return jsonrpc
     *
     * @return
     */
    public String getJsonrpc() {
        return jsonrpc;
    }

    /**
     * Set jsonrpc
     *
     * @param jsonrpc
     */
    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    /**
     * Return result
     *
     * @return
     */
    public SystemInfo getResult() {
        return result;
    }

    /**
     * Set result
     *
     * @param result
     */
    public void setResult(SystemInfo result) {
        this.result = result;
    }

    /**
     * Return port
     *
     * @return
     */
    public String getPort() {
        return port;
    }

    /**
     * Set port
     *
     * @param port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Return IP address
     *
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     * Set IP address
     *
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "SystemInfoReq{" + "id=" + id + ", jsonrpc=" + jsonrpc + ", result=" + result + ", port=" + port + ", ip=" + ip + '}';
    }

}

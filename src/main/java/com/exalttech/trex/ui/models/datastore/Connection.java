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
/*



 */
package com.exalttech.trex.ui.models.datastore;

import com.google.gson.Gson;

import javax.xml.bind.annotation.XmlElement;

/**
 * Class that present connection model
 *
 * @author GeorgeKh
 */
public class Connection {

    String ip;

    String rpcPort;

    String asyncPort;

    String scapyPort;

    String user;

    boolean fullControl;
    
    boolean lastUsed;

    /**
     *
     */
    public Connection() {
        // default constructor
    }

    /**
     *
     * @param ip
     * @param rpcPort
     * @param asyncPort
     * @param user
     * @param fullControl
     */
    public Connection(String ip, String rpcPort, String asyncPort, String scapyPort, String user, boolean fullControl) {
        this.ip = ip;
        this.rpcPort = rpcPort;
        this.asyncPort = asyncPort;
        this.scapyPort = scapyPort;
        this.user = user;
        this.fullControl = fullControl;
    }

    /**
     * Return IP
     *
     * @return
     */
    @XmlElement(name = "hostname")
    public String getIp() {
        return ip;
    }

    /**
     * Set IP
     *
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Return RPC port
     *
     * @return
     */
    @XmlElement(name = "rpc_port")
    public String getRpcPort() {
        return rpcPort;
    }

    /**
     * Set RPC port
     *
     * @param rpcPort
     */
    public void setRpcPort(String rpcPort) {
        this.rpcPort = rpcPort;
    }

    /**
     * Return Async port
     *
     * @return
     */
    @XmlElement(name = "async_port")
    public String getAsyncPort() {
        return asyncPort;
    }

    /**
     * Set Async port
     *
     * @param asyncPort
     */
    public void setAsyncPort(String asyncPort) {
        this.asyncPort = asyncPort;
    }

    /**
     * Return user
     *
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     * Set user
     *
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Return whether full control is select or not
     *
     * @return
     */
    public boolean isFullControl() {
        return fullControl;
    }

    /**
     * Set full control selection
     *
     * @param fullControl
     */
    public void setFullControl(boolean fullControl) {
        this.fullControl = fullControl;
    }

    /**
     * Set last used
     * @param lastUsed 
     */
    public void setLastUsed(boolean lastUsed) {
        this.lastUsed = lastUsed;
    }

    /**
     * Return true if it is last used, otherwise return false
     * @return 
     */
    @XmlElement(name = "last_used")
    public boolean isLastUsed() {
        return lastUsed;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}

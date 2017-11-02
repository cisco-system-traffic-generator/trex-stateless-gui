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

import com.exalttech.trex.util.Util;
import com.google.gson.Gson;

import javax.xml.bind.annotation.XmlElement;

/**
 * Class that present connection model
 *
 * @author GeorgeKh
 */
public class Connection {

    private String ip;

    private String rpcPort;

    private String asyncPort;

    private String scapyPort;

    private int timeout;

    private String user;

    private boolean fullControl;
    
    private boolean lastUsed;

    public Connection() {}

    public Connection(String ip,
                      String rpcPort,
                      String asyncPort,
                      String scapyPort,
                      int timeout,
                      String user,
                      boolean fullControl) {
        this.ip = ip;
        this.rpcPort = rpcPort;
        this.asyncPort = asyncPort;
        this.scapyPort = Util.isNullOrEmpty(scapyPort) ? "4507" : scapyPort;
        this.timeout = timeout;
        this.user = user;
        this.fullControl = fullControl;
    }

    @XmlElement(name = "hostname")
    public String getIp() {
        return ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    @XmlElement(name = "rpc_port")
    public String getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(String rpcPort) {
        this.rpcPort = rpcPort;
    }

    @XmlElement(name = "async_port")
    public String getAsyncPort() {
        return asyncPort;
    }

    public void setAsyncPort(final String asyncPort) {
        this.asyncPort = asyncPort;
    }

    @XmlElement(name = "scapy_port")
    public String getScapyPort() {
        return scapyPort;
    }

    public void setScapyPort(String scapyPort) {
        this.scapyPort = scapyPort;
    }

    @XmlElement(name = "timeout")
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public boolean isFullControl() {
        return fullControl;
    }

    public void setFullControl(final boolean fullControl) {
        this.fullControl = fullControl;
    }

    public void setLastUsed(final boolean lastUsed) {
        this.lastUsed = lastUsed;
    }

    @XmlElement(name = "last_used")
    public boolean isLastUsed() {
        return lastUsed;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

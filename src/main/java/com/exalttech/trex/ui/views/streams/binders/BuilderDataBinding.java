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
package com.exalttech.trex.ui.views.streams.binders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Stream builder data binding model
 *
 * @author Georgekh
 */
public class BuilderDataBinding implements Serializable {

    ProtocolSelectionDataBinding protocolSelection = new ProtocolSelectionDataBinding();

    EthernetDataBinding ethernetDB = new EthernetDataBinding();

    MacAddressDataBinding macDB = new MacAddressDataBinding();

    IPV4AddressDataBinding ipv4DB = new IPV4AddressDataBinding();

    TCPProtocolDataBinding tcpProtocolDB = new TCPProtocolDataBinding();

    UDPProtocolDataBinding udpProtocolDB = new UDPProtocolDataBinding();

    PayloadDataBinding payloadDB = new PayloadDataBinding();

    List<VlanDataBinding> vlanDB = new ArrayList<>();

    AdvancedPropertiesDataBinding advancedPropertiesDB = new AdvancedPropertiesDataBinding();
    
    /**
     * Constructor
     */
    public BuilderDataBinding() {
        // constructor
    }

    /**
     * Return Protocol selection data binding model
     *
     * @return
     */
    public ProtocolSelectionDataBinding getProtocolSelection() {
        return protocolSelection;
    }

    /**
     * Set Protocol selection data binding model
     *
     * @param protocolSelection
     */
    public void setProtocolSelection(ProtocolSelectionDataBinding protocolSelection) {
        this.protocolSelection = protocolSelection;
    }

    /**
     * Return Ethernet data binding model
     *
     * @return
     */
    public EthernetDataBinding getEthernetDB() {
        return ethernetDB;
    }

    /**
     * Set Ethernet data binding model
     *
     * @param ethernetDB
     */
    public void setEthernetDB(EthernetDataBinding ethernetDB) {
        this.ethernetDB = ethernetDB;
    }

    /**
     * Return Mac address data binding model
     *
     * @return
     */
    public MacAddressDataBinding getMacDB() {
        return macDB;
    }

    /**
     * Set Mac address data binding model
     *
     * @param macDB
     */
    public void setMacDB(MacAddressDataBinding macDB) {
        this.macDB = macDB;
    }

    /**
     * Return IPV4 data binding model
     *
     * @return
     */
    public IPV4AddressDataBinding getIpv4DB() {
        return ipv4DB;
    }

    /**
     * Set IPV4 data binding model
     *
     * @param ipv4DB
     */
    public void setIpv4DB(IPV4AddressDataBinding ipv4DB) {
        this.ipv4DB = ipv4DB;
    }

    /**
     * Return TCP protocol data binding model
     *
     * @return
     */
    public TCPProtocolDataBinding getTcpProtocolDB() {
        return tcpProtocolDB;
    }

    /**
     * Set TCP protocol data binding model
     *
     * @param tcpProtocolDB
     */
    public void setTcpProtocolDB(TCPProtocolDataBinding tcpProtocolDB) {
        this.tcpProtocolDB = tcpProtocolDB;
    }

    /**
     * Return UDP protocol data binding model
     *
     * @return
     */
    public UDPProtocolDataBinding getUdpProtocolDB() {
        return udpProtocolDB;
    }

    /**
     * Set UDP protocol data binding model
     *
     * @param udpProtocolDB
     */
    public void setUdpProtocolDB(UDPProtocolDataBinding udpProtocolDB) {
        this.udpProtocolDB = udpProtocolDB;
    }

    /**
     * Return payload data binding model
     *
     * @return
     */
    public PayloadDataBinding getPayloadDB() {
        return payloadDB;
    }

    /**
     * Set payload data binding model
     *
     * @param payloadDB
     */
    public void setPayloadDB(PayloadDataBinding payloadDB) {
        this.payloadDB = payloadDB;
    }

    /**
     * Return list of vlan data binding model
     *
     * @return
     */
    public List<VlanDataBinding> getVlanDB() {
        if (vlanDB.isEmpty()) {
            vlanDB.add(new VlanDataBinding());
            vlanDB.add(new VlanDataBinding());
        }
        return vlanDB;
    }

    /**
     * Set list of vlan data binding model
     *
     * @param vlanDB
     */
    public void setVlanDB(List<VlanDataBinding> vlanDB) {
        this.vlanDB = vlanDB;
    }

    /**
     * Return cahce size
     * @return 
     */
    public AdvancedPropertiesDataBinding getAdvancedPropertiesDB() {
        return advancedPropertiesDB;
    }

    /**
     * Set cache size
     * @param advancedPropertiesDB 
     */
    public void setAdvancedPropertiesDB(AdvancedPropertiesDataBinding advancedPropertiesDB) {
        this.advancedPropertiesDB = advancedPropertiesDB;
    }
}

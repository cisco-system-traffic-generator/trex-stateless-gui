/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.simulator.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Ethernet data info model
 *
 * @author Georgekh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EthernetData {

    AddressInfo srcAddress;

    AddressInfo dstAddress;

    /**
     * Return source address
     *
     * @return
     */
    public AddressInfo getSrcAddress() {
        return srcAddress;
    }

    /**
     * Set source address
     *
     * @param srcAddress
     */
    public void setSrcAddress(AddressInfo srcAddress) {
        this.srcAddress = srcAddress;
    }

    /**
     * Return destination address
     *
     * @return
     */
    public AddressInfo getDstAddress() {
        return dstAddress;
    }

    /**
     * Set destination address
     *
     * @param dstAddress
     */
    public void setDstAddress(AddressInfo dstAddress) {
        this.dstAddress = dstAddress;
    }

}

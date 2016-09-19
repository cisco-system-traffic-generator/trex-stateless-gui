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
package com.exalttech.trex.simulator.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * IPV4 data info model
 *
 * @author Georgekh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPV4Data {

    boolean enable;
    AddressInfo srcAddress;
    AddressInfo dstAddress;

    /**
     * Return wither ipv4 is enable or not
     *
     * @return
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Set ipv4 enable
     *
     * @param enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

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
     * Set destination address
     *
     * @return
     */
    public AddressInfo getDstAddress() {
        return dstAddress;
    }

    /**
     * Return destination address
     *
     * @param dstAddress
     */
    public void setDstAddress(AddressInfo dstAddress) {
        this.dstAddress = dstAddress;
    }

}

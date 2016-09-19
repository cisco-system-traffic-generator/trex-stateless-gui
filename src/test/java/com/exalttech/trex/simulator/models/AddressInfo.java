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
 * Address info model
 *
 * @author Georgekh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressInfo {

    String address;
    String count;
    String type;
    String step;

    /**
     * Return address
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set address
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Return count
     *
     * @return
     */
    public String getCount() {
        return count;
    }

    /**
     * Set count
     *
     * @param count
     */
    public void setCount(String count) {
        this.count = count;
    }

    /**
     * Return type
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Set type
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Return step
     *
     * @return
     */
    public String getStep() {
        return step;
    }

    /**
     * Set step
     *
     * @param step
     */
    public void setStep(String step) {
        this.step = step;
    }

}

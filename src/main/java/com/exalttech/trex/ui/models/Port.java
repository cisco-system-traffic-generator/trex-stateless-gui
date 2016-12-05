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

import com.exalttech.trex.remote.models.params.Params;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

/**
 * Port model
 *
 * @author Georgekh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Port {

    int index;

    String status;

    String assigned;

    String profileAssigned;

    String owner;

    int speed;

    String driver;

    /**
     * Return index
     *
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * Port index to set
     *
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Return status
     *
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * Port status to set
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Return assigned
     *
     * @return
     */
    public String getAssigned() {
        return assigned;
    }

    /**
     * Assigned value to set
     *
     * @param assigned
     */
    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    /**
     * Return assigned profile
     *
     * @return
     */
    public String getProfileAssigned() {
        return profileAssigned;
    }

    /**
     * Assigned profile to set
     *
     * @param profileAssigned
     */
    public void setProfileAssigned(String profileAssigned) {
        this.profileAssigned = profileAssigned;
    }

    /**
     * Return port owner
     *
     * @return
     */
    public String getOwner() {

        return owner;
    }

    /**
     * Port owner to set
     *
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Return speed
     *
     * @return
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Speed to set
     *
     * @param speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Return driver
     *
     * @return
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Driver to set
     *
     * @param driver
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * Return port parameter
     * @return 
     */
    public PortParams getPortParam(){
        return new PortParams(index);
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /**
     * Port parameters model
     */
    public class PortParams extends Params {

        @JsonProperty("port_id")
        private Integer portId;

        /**
         * Constructor
         * @param portId 
         */
        public PortParams(int portId){
            this.portId = portId;
        }
        
        /**
         * 
         * @param portId 
         */
        @JsonProperty("port_id")
        public void setPortId(Integer portId) {
            this.portId = portId;
        }

        /**
         * 
         * @return 
         */
        @JsonProperty("port_id")
        public Integer getPortId() {
            return portId;
        }

    }
}

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
import com.exalttech.trex.ui.models.datastore.CaptureStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import javafx.beans.property.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Port model
 *
 * @author Georgekh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Port {

    String description;
    StringProperty descriptionProerty = new SimpleStringProperty();

    String driver;
    StringProperty driverProerty = new SimpleStringProperty();

    IntegerProperty indexProerty = new SimpleIntegerProperty();

    boolean is_fc_supported;
    BooleanProperty is_fc_supportedProerty = new SimpleBooleanProperty();

    boolean is_led_supported;
    BooleanProperty is_led_supportedProerty = new SimpleBooleanProperty();

    boolean is_link_supported;
    BooleanProperty is_link_supportedProerty = new SimpleBooleanProperty();

    boolean is_virtual;
    BooleanProperty is_virtualProerty = new SimpleBooleanProperty();

    int numa;
    IntegerProperty numaProerty = new SimpleIntegerProperty();

    String pci_addr;
    StringProperty pci_addrProerty = new SimpleStringProperty();

    PortRx rx;
    
    int[] supp_speeds;

    String status;
    StringProperty statusProerty = new SimpleStringProperty();

    String assigned;
    StringProperty assignedProerty = new SimpleStringProperty();

    String profileAssigned;
    StringProperty profileAssignedProerty = new SimpleStringProperty();

    String owner;
    StringProperty ownerProerty = new SimpleStringProperty();

    int speed;
    IntegerProperty speedProerty = new SimpleIntegerProperty();

    PortStatus.PortStatusResult.PortStatusResultAttr attr;

    PortStatus.PortStatusResult.PortStatusResultRxInfo rx_info;

    Map<String, Long> xstats;

    Map<String, Long> xstatsPinned;

    CaptureStatus[] captureStatus;

    boolean service;
    BooleanProperty serviceModeProperty = new SimpleBooleanProperty();
    private StringProperty captureStatusProperty = new SimpleStringProperty();

    public boolean getServiceMode() {
        return serviceModeProperty.get();
    }

    public void setService(boolean service) {
        this.serviceModeProperty.set(service);
    }

    public BooleanProperty serviceModeProperty() {
        return serviceModeProperty;
    }

    /**
     * Return index
     *
     * @return
     */
    public int getIndex() {
        return indexProerty.get();
    }

    /**
     * Port index to set
     *
     * @param index
     */
    public void setIndex(int index) {
        this.indexProerty.set(index);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        descriptionProerty.set(description);
    }

    public int getNuma() {
        return numa;
    }

    public void setNuma(int numa) {
        this.numa = numa;
        numaProerty.setValue(numa);
    }

    public String getPci_addr() {
        return pci_addr;
    }

    public void setPci_addr(String pci_addr) {
        this.pci_addr = pci_addr;
        pci_addrProerty.set(pci_addr);
    }

    public PortRx getRx() {
        return rx;
    }

    public void setRx(PortRx rx) {
        this.rx = rx;
    }

    public int[] getSupp_speeds() {
        return supp_speeds;
    }

    public void setSupp_speeds(int[] supp_speeds) {
        this.supp_speeds = supp_speeds;
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
        statusProerty.set(status);
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
        assignedProerty.set(assigned);
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
        ownerProerty.set(owner);
    }

    /**
     * Return port attr
     *
     * @return
     */
    public PortStatus.PortStatusResult.PortStatusResultAttr getAttr() {
        return attr;
    }

    /**
     * Port attr to set
     *
     * @param attr
     */
    public void setAttr(PortStatus.PortStatusResult.PortStatusResultAttr attr) {
        if (this.attr == null) {
            this.attr = attr;
        } else {
         this.attr.updateFrom(attr);
        }
    }

    public PortStatus.PortStatusResult.PortStatusResultRxInfo getRx_info() {
        return rx_info;
    }

    public void setRx_info(PortStatus.PortStatusResult.PortStatusResultRxInfo rx_info) {
        if (this.rx_info == null) {
            this.rx_info = rx_info;
        } else {
            this.rx_info.updateFrom(rx_info);
        }
    }

    public Map<String, Long> getXstats() {
        return xstats == null ? Collections.emptyMap() : xstats;
    }

    public void setXstats(Map<String, Long> xstats) {
        this.xstats = xstats;
    }

    public Map<String, Long> getXstatsPinned() {
        if (xstatsPinned==null) {
            xstatsPinned = new HashMap<>();
        }
        return xstatsPinned;
    }

    public void setXstatsPinned(Map<String, Long> xstatsPinned) {
        this.xstatsPinned = xstatsPinned;
    }

    private String getCaptureStatusString() {
        String status = "None";
        if (captureStatus != null && captureStatus.length > 0) {
            int port_index_mask = 1 << getIndex();
            boolean tx = false;
            boolean rx = false;

            for (int i = 0; i < captureStatus.length; i++) {
                if ((port_index_mask & captureStatus[i].getFilter().getRx()) != 0) {
                    rx = true;
                }
                if ((port_index_mask & captureStatus[i].getFilter().getTx()) != 0) {
                    tx = true;
                }
            }

            if (rx && tx) {
                status = "Rx + Tx";
            }
            else if (rx) {
                status = "Rx";
            }
            else if (tx) {
                status = "Tx";
            }
        }
        return status;
    }

    public String getCaptureStatus() {
        return captureStatusProperty.get();
    }
    
    public void setCaptureStatus(CaptureStatus[] captureStatus) {
        this.captureStatus = captureStatus;
        captureStatusProperty.set(getCaptureStatusString());
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
        speedProerty.set(speed);
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
        driverProerty.set(driver);
    }

    /**
     * Return port parameter
     * @return
     */
    public PortParams getPortParam(){
        return new PortParams(indexProerty.get());
    }

    /**
     * Return stream parameter
     * @return
     */
    public StreamParams getStreamParam(int streamId){
        return new StreamParams(indexProerty.get(), streamId);
    }


    public String getSrcMac() {
        return getAttr().getLayer_cfg().getEther().getSrc();
    }
    public String getDstMac() {
        return getAttr().getLayer_cfg().getEther().getDst();
    }
    public void setDstMac(String mac) {
        getAttr().getLayer_cfg().getEther().setDst(mac);
    }
    public String getSrcIp() {
        return getAttr().getLayer_cfg().getIpv4().getSrc();
    }
    public String getDstIp() {
        return getAttr().getLayer_cfg().getIpv4().getDst();
    }
    
    public boolean isIs_fc_supported() {
        return is_fc_supported;
    }

    public void setIs_fc_supported(boolean is_fc_supported) {
        this.is_fc_supported = is_fc_supported;
        is_fc_supportedProerty.set(is_fc_supported);
    }

    public boolean isIs_led_supported() {
        return is_led_supported;
    }

    public void setIs_led_supported(boolean is_led_supported) {
        this.is_led_supported = is_led_supported;
        is_led_supportedProerty.set(is_led_supported);
    }

    public boolean isIs_link_supported() {
        return is_link_supported;
    }

    public void setIs_link_supported(boolean is_link_supported) {
        this.is_link_supported = is_link_supported;
        is_link_supportedProerty.set(is_link_supported);
    }

    public boolean isIs_virtual() {
        return is_virtual;
    }

    public void setIs_virtual(boolean is_virtual) {
        this.is_virtual = is_virtual;
        is_virtualProerty.set(is_virtual);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public FlowControl getFlowControl() {
        FlowControl mode = FlowControl.NONE;
        switch(getAttr().getFc().getMode()) {
            case 1:
                mode = FlowControl.TX;
                break;
            case 2:
                mode = FlowControl.RX;
                break;
            case 3:
                mode = FlowControl.FULL;
                
        }
        return mode;
    }

    public boolean getLink() {
        return getAttr().getLink() != null && getAttr().getLink().getUp();
    }

    public boolean getLed() {
        return getAttr().getLed() != null && getAttr().getLed().getOn();
    }

    public IntegerProperty indexProperty() {
        return indexProerty;
    }

    public StringProperty driverProper() {
        return driverProerty;
    }

    public StringProperty rxFilterModeProperty() {
        return getAttr().rxFilterModeProperty();
    }

    public BooleanProperty multicastProperty() {
        return getAttr().getMulticast().enabledProperty();
    }

    public BooleanProperty promiscuousProperty() {
        return getAttr().getPromiscuous().enabledProperty();
    }

    public StringProperty ownerProperty() {
        return ownerProerty;
    }

    public IntegerProperty speedProperty() {
        return speedProerty;
    }

    public StringProperty statusProerty() {
        return statusProerty;
    }

    public StringProperty captureStatusProperty() {
        return captureStatusProperty;
    }

    public BooleanProperty linkProperty() {
        return is_link_supportedProerty;
    }

    public BooleanProperty ledProperty() {
        return is_led_supportedProerty;
    }

    public IntegerProperty numaProerty() {
        return numaProerty;
    }

    public StringProperty pciAddrProperty() {
        return pci_addrProerty;
    }

    public StringProperty rxQueueProperty() {
        return getRx_info().getQueue().statusProperty();
    }

    public BooleanProperty serviceModeProerty() {
        return serviceModeProperty;
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

    /**
     * Stream parameters model
     */
    public class StreamParams extends Params {

        @JsonProperty("port_id")
        private Integer portId;
        @JsonProperty("stream_id")
        private Integer streamId;

        /**
         * Constructor
         * @param portId
         * @param streamId
         */
        public StreamParams(int portId, int streamId){
            this.portId = portId;
            this.streamId = streamId;
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

        /**
         *
         * @param streamId
         */
        @JsonProperty("stream_id")
        public void setStreamId(Integer streamId) {
            this.streamId = streamId;
        }

        /**
         *
         * @return
         */
        @JsonProperty("stream_id")
        public Integer getStreamId() {
            return streamId;
        }

    }

    public class PortRx {

        @JsonProperty("caps")
        String[] caps;

        @JsonProperty("counters")
        int counters;

        @JsonProperty("caps")
        public String[] getCaps() {
            return caps;
        }

        @JsonProperty("caps")
        public void setCaps(String[] caps) {
            this.caps = caps;
        }

        @JsonProperty("counters")
        public int getCounters() {
            return counters;
        }

        @JsonProperty("counters")
        public void setCounters(int counters) {
            this.counters = counters;
        }

    }
}

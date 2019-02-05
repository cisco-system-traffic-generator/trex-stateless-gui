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
package com.exalttech.trex.remote.models.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "attr",
        "handler",
        "port_id"
})
public class PortAttrParams extends Params {

    @JsonProperty("attr")
    private Attr attr;
    @JsonProperty("handler")
    private String handler;
    @JsonProperty("port_id")
    private Integer portId;

    public PortAttrParams(Integer portId, String handler, Boolean link_status, Boolean promiscuous, Boolean led_status, Integer flow_ctrl_mode, Boolean multicast) {
        this.handler = handler;
        this.portId = portId;
        this.attr = new Attr(link_status, promiscuous, led_status, flow_ctrl_mode, multicast);
    }

    @JsonProperty("attr")
    public Attr getAttr() {
        return attr;
    }

    @JsonProperty("attr")
    public void setAttr(Attr attr) {
        this.attr = attr;
    }

    @JsonProperty("handler")
    public String getHandler() {
        return handler;
    }

    @JsonProperty("handler")
    public void setHandler(String handler) {
        this.handler = handler;
    }

    @JsonProperty("port_id")
    public Integer getPortId() {
        return portId;
    }

    @JsonProperty("port_id")
    public void setPortId(Integer portId) {
        this.portId = portId;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Generated("org.jsonschema2pojo")
    @JsonPropertyOrder({
            "link_status",
            "promiscuous",
            "led_status",
            "flow_ctrl_mode"
    })
    public class Attr {

        @JsonProperty("link_status")
        private LinkStatus link_status;
        @JsonProperty("promiscuous")
        private Promiscuous promiscuous;
        @JsonProperty("led_status")
        private LedStatus led_status;
        @JsonProperty("flow_ctrl_mode")
        private FlowCtrlMode flow_ctrl_mode;
        @JsonProperty("multicast")
        private Multicast multicast;

        /**
         * @param force
         * @param handler
         * @param mul
         * @param portId
         */
        public Attr(Boolean link_status, Boolean promiscuous, Boolean led_status, Integer flow_ctrl_mode, Boolean multicast) {
            this.link_status = link_status != null ? new LinkStatus(link_status) : null;
            this.promiscuous = promiscuous != null ? new Promiscuous(promiscuous) : null;
            this.led_status = led_status != null ? new LedStatus(led_status) : null;
            this.flow_ctrl_mode = flow_ctrl_mode != null ? new FlowCtrlMode(flow_ctrl_mode) : null;
            this.multicast = multicast != null ? new Multicast(multicast) : null;
        }

        @JsonProperty("link_status")
        public LinkStatus getLink_status() {
            return link_status;
        }

        @JsonProperty("link_status")
        public void setLink_status(LinkStatus link_status) {
            this.link_status = link_status;
        }

        @JsonProperty("promiscuous")
        public Promiscuous getPromiscuous() {
            return promiscuous;
        }

        @JsonProperty("promiscuous")
        public void setPromiscuous(Promiscuous promiscuous) {
            this.promiscuous = promiscuous;
        }

        @JsonProperty("led_status")
        public LedStatus getLed_status() {
            return led_status;
        }

        @JsonProperty("led_status")
        public void setLed_status(LedStatus led_status) {
            this.led_status = led_status;
        }

        @JsonProperty("flow_ctrl_mode")
        public FlowCtrlMode getFlow_ctrl_mode() {
            return flow_ctrl_mode;
        }

        @JsonProperty("flow_ctrl_mode")
        public void setFlow_ctrl_mode(FlowCtrlMode flow_ctrl_mode) {
            this.flow_ctrl_mode = flow_ctrl_mode;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Generated("org.jsonschema2pojo")
        @JsonPropertyOrder({
                "up"
        })
        private class LinkStatus {
            @JsonProperty("up")
            private boolean up;

            public LinkStatus(boolean up) {
                this.up = up;
            }

            public boolean isUp() {
                return up;
            }

            public void setUp(boolean up) {
                this.up = up;
            }

        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Generated("org.jsonschema2pojo")
        @JsonPropertyOrder({
                "enabled"
        })
        private class Promiscuous {
            @JsonProperty("enabled")
            private boolean enabled;

            public Promiscuous(boolean enabled) {
                this.enabled = enabled;
            }

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Generated("org.jsonschema2pojo")
        @JsonPropertyOrder({
                "enabled"
        })
        private class Multicast {
            @JsonProperty("enabled")
            private boolean enabled;

            public Multicast(boolean enabled) {
                this.enabled = enabled;
            }

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Generated("org.jsonschema2pojo")
        @JsonPropertyOrder({
                "on"
        })
        private class LedStatus {
            @JsonProperty("on")
            private boolean on;

            public LedStatus(boolean on) {
                this.on = on;
            }

            public boolean isOn() {
                return on;
            }

            public void setOn(boolean on) {
                this.on = on;
            }

        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Generated("org.jsonschema2pojo")
        @JsonPropertyOrder({
                "mode"
        })
        private class FlowCtrlMode {
            @JsonProperty("mode")
            private int mode;

            public FlowCtrlMode(int mode) {
                this.mode = mode;
            }

            public int getMode() {
                return mode;
            }

            public void setMode(int mode) {
                this.mode = mode;
            }
        }
    }
}
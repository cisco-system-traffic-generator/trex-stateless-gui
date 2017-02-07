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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.annotation.Generated;

/**
 * Port status response data model
 * @author GeorgeKH
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "jsonrpc",
    "result"
})
public class PortStatus {

    @JsonProperty("id")
    private String id;

    @JsonProperty("jsonrpc")
    private String jsonrpc;

    @JsonProperty("result")
    private PortStatusResult result;

    /**
     * 
     * @return 
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id 
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return 
     */
    @JsonProperty("jsonrpc")
    public String getJsonrpc() {
        return jsonrpc;
    }

    /**
     * 
     * @param jsonrpc 
     */
    @JsonProperty("jsonrpc")
    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    /**
     * 
     * @return 
     */
    @JsonProperty("result")
    public PortStatusResult getResult() {
        return result;
    }

    /**
     * 
     * @param result 
     */
    @JsonProperty("result")
    public void setResult(PortStatusResult result) {
        this.result = result;
    }

    /**
     * class present port status result model
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder({
            "attr",
            "max_stream_id",
            "owner",
            "state"
    })
    public class PortStatusResult {

        @JsonProperty("attr")
        private PortStatusResultAttr attr;

        @JsonProperty("max_stream_id")
        private String max_stream_id;

        @JsonProperty("owner")
        private String owner;

        @JsonProperty("state")
        private String state;

        /**
         *
         * @return
         */
        @JsonProperty("owner")
        public String getOwner() {
            return owner;
        }

        /**
         *
         * @param owner
         */
        @JsonProperty("owner")
        public void setOwner(String owner) {
            this.owner = owner;
        }

        /**
         *
         * @return
         */
        @JsonProperty("state")
        public String getState() {
            return state;
        }

        /**
         *
         * @param state
         */
        @JsonProperty("state")
        public void setState(String state) {
            this.state = state;
        }

        /**
         *
         * @return
         */
        @JsonProperty("attr")
        public PortStatusResultAttr getAttr() {
            return attr;
        }

        /**
         *
         * @param attr
         */
        @JsonProperty("attr")
        public void setAttr(PortStatusResultAttr attr) {
            this.attr = attr;
        }

        /**
         *
         * @return
         */
        @JsonProperty("max_stream_id")
        public String getMaxStreamId() {
            return max_stream_id;
        }

        /**
         *
         * @param max_stream_id
         */
        @JsonProperty("max_stream_id")
        public void setMaxStreamId(String max_stream_id) {
            this.max_stream_id = max_stream_id;
        }

        /**
         * class present port status result-attr model
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonPropertyOrder({
                "fc",
                "link",
                "promiscuous",
                "led"
        })
        public class PortStatusResultAttr {

            @JsonProperty("fc")
            private PortStatusResultAttrFc fc;
            @JsonProperty("link")
            private PortStatusResultAttrLink link;
            @JsonProperty("promiscuous")
            private PortStatusResultAttrPromiscuous promiscuous;
            @JsonProperty("led")
            private PortStatusResultAttrLed led;
            @JsonProperty("layer_cfg")
            private PortStatusResultAttrLayerCfg layer_cfg;

            /**
             *
             * @return
             */
            @JsonProperty("fc")
            public PortStatusResultAttrFc getFc() {
                return fc;
            }

            /**
             *
             * @param owner
             */
            @JsonProperty("fc")
            public void setFc(PortStatusResultAttrFc fc) {
                this.fc = fc;
            }

            /**
             *
             * @return
             */
            @JsonProperty("link")
            public PortStatusResultAttrLink getLink() {
                return link;
            }

            /**
             *
             * @param link
             */
            @JsonProperty("link")
            public void setLink(PortStatusResultAttrLink link) {
                this.link = link;
            }

            /**
             *
             * @return
             */
            @JsonProperty("promiscuous")
            public PortStatusResultAttrPromiscuous getPromiscuous() {
                return promiscuous;
            }

            /**
             *
             * @param promiscuous
             */
            @JsonProperty("promiscuous")
            public void setPromiscuous(PortStatusResultAttrPromiscuous promiscuous) {
                this.promiscuous = promiscuous;
            }

            /**
             *
             * @return
             */
            @JsonProperty("led")
            public PortStatusResultAttrLed getLed() {
                return led;
            }

            /**
             *
             * @param led
             */
            @JsonProperty("led")
            public void setLed(PortStatusResultAttrLed led) {
                this.led = led;
            }

            /**
             *
             * @return
             */
            @JsonProperty("layer_cfg")
            public PortStatusResultAttrLayerCfg getLayer_cfg() {
                return layer_cfg;
            }

            /**
             *
             * @param led
             */
            @JsonProperty("layer_cfg")
            public void setLayer_cfg(PortStatusResultAttrLayerCfg layer_cfg) {
                this.layer_cfg = layer_cfg;
            }



            /**
             * class present port status result-attr-fc-mode model
             */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonPropertyOrder({
                    "mode"
            })
            public class PortStatusResultAttrFc {

                @JsonProperty("mode")
                private int mode;

                /**
                 *
                 * @return
                 */
                @JsonProperty("mode")
                public int getMode() {
                    return mode;
                }

                /**
                 *
                 * @param mode
                 */
                @JsonProperty("mode")
                public void setMode(int mode) {
                    this.mode = mode;
                }

                public String toString() {
                    return mode==0
                            ? "none"
                            : mode==1
                                ? "tx"
                                : mode==2
                                    ? "rx"
                                    : mode==3 ? "full" : "ERROR";
                }

            }

            /**
             * class present port status result-attr-link-up model
             */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonPropertyOrder({
                    "up"
            })
            public class PortStatusResultAttrLink {

                @JsonProperty("up")
                private boolean up;

                /**
                 *
                 * @return
                 */
                @JsonProperty("up")
                public boolean getUp() {
                    return up;
                }

                /**
                 *
                 * @param up
                 */
                @JsonProperty("up")
                public void setUp(boolean up) {
                    this.up = up;
                }

                public String toString() {
                    if (up) return "up";
                    return "down";
                }

            }

            /**
             * class present port status result-attr-link-up model
             */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonPropertyOrder({
                    "enabled"
            })
            public class PortStatusResultAttrPromiscuous {

                @JsonProperty("enabled")
                private boolean enabled;

                /**
                 *
                 * @return
                 */
                @JsonProperty("enabled")
                public boolean getEnabled() {
                    return enabled;
                }

                /**
                 *
                 * @param enabled
                 */
                @JsonProperty("enabled")
                public void setEnabled(boolean enabled) {
                    this.enabled = enabled;
                }

                public String toString() {
                    if (enabled) return "enabled";
                    return "disabled";
                }

            }

            /**
             * class present port status result-attr-link-up model
             */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonPropertyOrder({
                    "enabled"
            })
            public class PortStatusResultAttrLed {

                @JsonProperty("on")
                private boolean on;

                /**
                 *
                 * @return
                 */
                @JsonProperty("on")
                public boolean getOn() {
                    return on;
                }

                /**
                 *
                 * @param on
                 */
                @JsonProperty("on")
                public void setOn(boolean on) {
                    this.on = on;
                }

                public String toString() {
                    if (on) return "on";
                    return "off";
                }
            }

            /**
             * class present port status result-attr-link-up model
             */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonPropertyOrder({
                    "ether",
                    "ipv4"
            })
            public class PortStatusResultAttrLayerCfg {

                @JsonProperty("ether")
                private PortStatusResultAttrLayerCfgEther ether;
                @JsonProperty("ipv4")
                private PortStatusResultAttrLayerCfgIPv4 ipv4;

                /**
                 *
                 * @return ether
                 */
                @JsonProperty("ether")
                public PortStatusResultAttrLayerCfgEther getEther() {
                    return ether;
                }

                /**
                 *
                 * @param ether
                 */
                @JsonProperty("ether")
                public void setEther(PortStatusResultAttrLayerCfgEther ether) {
                    this.ether = ether;
                }

                /**
                 *
                 * @return ipv4
                 */
                @JsonProperty("ipv4")
                public PortStatusResultAttrLayerCfgIPv4 getIpv4() {
                    return ipv4;
                }

                /**
                 *
                 * @param ipv4
                 */
                @JsonProperty("ipv4")
                public void setIpv4(PortStatusResultAttrLayerCfgIPv4 ipv4) {
                    this.ipv4 = ipv4;
                }

                /**
                 * class present port status result-attr-link-up model
                 */
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonPropertyOrder({
                        "dst",
                        "src",
                        "state"
                })
                public class PortStatusResultAttrLayerCfgEther {

                    @JsonProperty("dst")
                    private String dst;
                    @JsonProperty("src")
                    private String src;
                    @JsonProperty("state")
                    private String state;

                    @JsonProperty("dst")
                    public String getDst() {
                        return dst;
                    }

                    @JsonProperty("dst")
                    public void setDst(String dst) {
                        this.dst = dst;
                    }

                    @JsonProperty("src")
                    public String getSrc() {
                        return src;
                    }

                    @JsonProperty("src")
                    public void setSrc(String src) {
                        this.src = src;
                    }

                    @JsonProperty("state")
                    public String getState() {
                        return state;
                    }

                    @JsonProperty("state")
                    public void setState(String state) {
                        this.state = state;
                    }
                }

                /**
                 * class present port status result-attr-link-up model
                 */
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonPropertyOrder({
                        "dst",
                        "src",
                        "state"
                })
                public class PortStatusResultAttrLayerCfgIPv4 {

                    @JsonProperty("dst")
                    private String dst;
                    @JsonProperty("src")
                    private String src;
                    @JsonProperty("state")
                    private String state;

                    @JsonProperty("dst")
                    public String getDst() {
                        return dst;
                    }

                    @JsonProperty("dst")
                    public void setDst(String dst) {
                        this.dst = dst;
                    }

                    @JsonProperty("src")
                    public String getSrc() {
                        return src;
                    }

                    @JsonProperty("src")
                    public void setSrc(String src) {
                        this.src = src;
                    }

                    @JsonProperty("state")
                    public String getState() {
                        return state;
                    }

                    @JsonProperty("state")
                    public void setState(String state) {
                        this.state = state;
                    }
                }
            }
        }
    }
}

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
package com.exalttech.trex.remote.models.validate;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author Georgekh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "diff_bps_l1",
    "diff_bps_l2",
    "diff_pps",
    "stream_id",
    "time_usec"
})
public class Event {

    @JsonProperty("diff_bps_l1")
    private long diffBpsL1;
    @JsonProperty("diff_bps_l2")
    private long diffBpsL2;
    @JsonProperty("diff_pps")
    private long diffPps;
    @JsonProperty("stream_id")
    private Integer streamId;
    @JsonProperty("time_usec")
    private Long timeUsec;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return The diffBpsL1
     */
    @JsonProperty("diff_bps_l1")
    public long getDiffBpsL1() {
        return diffBpsL1;
    }

    /**
     *
     * @param diffBpsL1 The diff_bps_l1
     */
    @JsonProperty("diff_bps_l1")
    public void setDiffBpsL1(long diffBpsL1) {
        this.diffBpsL1 = diffBpsL1;
    }

    /**
     *
     * @return The diffBpsL2
     */
    @JsonProperty("diff_bps_l2")
    public long getDiffBpsL2() {
        return diffBpsL2;
    }

    /**
     *
     * @param diffBpsL2 The diff_bps_l2
     */
    @JsonProperty("diff_bps_l2")
    public void setDiffBpsL2(long diffBpsL2) {
        this.diffBpsL2 = diffBpsL2;
    }

    /**
     *
     * @return The diffPps
     */
    @JsonProperty("diff_pps")
    public long getDiffPps() {
        return diffPps;
    }

    /**
     *
     * @param diffPps The diff_pps
     */
    @JsonProperty("diff_pps")
    public void setDiffPps(long diffPps) {
        this.diffPps = diffPps;
    }

    /**
     *
     * @return The streamId
     */
    @JsonProperty("stream_id")
    public Integer getStreamId() {
        return streamId;
    }

    /**
     *
     * @param streamId The stream_id
     */
    @JsonProperty("stream_id")
    public void setStreamId(Integer streamId) {
        this.streamId = streamId;
    }

    /**
     *
     * @return The timeUsec
     */
    @JsonProperty("time_usec")
    public Long getTimeUsec() {
        return timeUsec;
    }

    /**
     *
     * @param timeUsec The time_usec
     */
    @JsonProperty("time_usec")
    public void setTimeUsec(Long timeUsec) {
        this.timeUsec = timeUsec;
    }

    /**
     *
     * @return
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @Override
    public String toString() {
        return "Event{" + "diffBpsL1=" + diffBpsL1 + ", diffBpsL2=" + diffBpsL2 + ", diffPps=" + diffPps + ", streamId=" + streamId + ", timeUsec=" + timeUsec + ", additionalProperties=" + additionalProperties + '}';
    }

    /**
     *
     * @param name
     * @param value
     */
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

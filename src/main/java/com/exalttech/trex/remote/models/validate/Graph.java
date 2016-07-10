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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author Georgekh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "events",
    "events_count",
    "expected_duration"
})
public class Graph {

    @JsonProperty("events")
    private List<Event> events = new ArrayList<Event>();
    @JsonProperty("events_count")
    private Integer eventsCount;
    @JsonProperty("expected_duration")
    private Integer expectedDuration;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return The events
     */
    @JsonProperty("events")
    public List<Event> getEvents() {
        return events;
    }

    /**
     *
     * @param events The events
     */
    @JsonProperty("events")
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     *
     * @return The eventsCount
     */
    @JsonProperty("events_count")
    public Integer getEventsCount() {
        return eventsCount;
    }

    /**
     *
     * @param eventsCount The events_count
     */
    @JsonProperty("events_count")
    public void setEventsCount(Integer eventsCount) {
        this.eventsCount = eventsCount;
    }

    /**
     *
     * @return The expectedDuration
     */
    @JsonProperty("expected_duration")
    public Integer getExpectedDuration() {
        return expectedDuration;
    }

    /**
     *
     * @param expectedDuration The expected_duration
     */
    @JsonProperty("expected_duration")
    public void setExpectedDuration(Integer expectedDuration) {
        this.expectedDuration = expectedDuration;
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
        return "Graph{" + "events=" + events + ", eventsCount=" + eventsCount + ", expectedDuration=" + expectedDuration + ", additionalProperties=" + additionalProperties + '}';
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

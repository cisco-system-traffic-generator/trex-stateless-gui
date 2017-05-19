package com.exalttech.trex.remote.models.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LatencyStat {
    @JsonProperty("er")
    private LatencyStatErr err;
    @JsonProperty("lat")
    private LatencyStatLat lat;

    @JsonProperty("er")
    public LatencyStatErr getErr() {
        return err;
    }

    @JsonProperty("er")
    public void setErr(final LatencyStatErr err) {
        this.err = err;
    }

    @JsonProperty("lat")
    public LatencyStatLat getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(final LatencyStatLat lat) {
        this.lat = lat;
    }
}

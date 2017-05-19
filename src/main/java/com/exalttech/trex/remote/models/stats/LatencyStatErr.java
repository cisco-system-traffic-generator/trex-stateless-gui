package com.exalttech.trex.remote.models.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LatencyStatErr {
    @JsonProperty("drp")
    private long drp;
    @JsonProperty("dup")
    private long dup;
    @JsonProperty("ooo")
    private long ooo;
    @JsonProperty("sth")
    private long sth;
    @JsonProperty("stl")
    private long stl;

    @JsonProperty("drp")
    public long getDrp() {
        return drp;
    }

    @JsonProperty("drp")
    public void setDrp(final long drp) {
        this.drp = drp;
    }

    @JsonProperty("dup")
    public long getDup() {
        return dup;
    }

    @JsonProperty("dup")
    public void setDup(final long dup) {
        this.dup = dup;
    }

    @JsonProperty("ooo")
    public long getOoo() {
        return ooo;
    }

    @JsonProperty("ooo")
    public void setOoo(final long ooo) {
        this.ooo = ooo;
    }

    @JsonProperty("sth")
    public long getSth() {
        return sth;
    }

    @JsonProperty("sth")
    public void setSth(final long sth) {
        this.sth = sth;
    }

    @JsonProperty("stl")
    public long getStl() {
        return stl;
    }

    @JsonProperty("stl")
    public void setStl(final long stl) {
        this.stl = stl;
    }
}

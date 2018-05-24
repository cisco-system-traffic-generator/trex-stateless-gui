package com.cisco.trex.stl.gui.models;

import com.cisco.trex.stateless.model.stats.LatencyStat;
import com.cisco.trex.stateless.model.stats.LatencyStatErr;
import com.cisco.trex.stateless.model.stats.LatencyStatLat;

import java.util.HashMap;
import java.util.Map;


public class LatencyStatPoint {
    private LatencyStat latencyStat;
    private double time;

    public LatencyStatPoint(final LatencyStat latencyStat, final double time) {
        this.latencyStat = latencyStat;
        this.time = time;
    }

    public LatencyStat getLatencyStat() {
        return this.latencyStat;
    }

    public double getTime() {
        return time;
    }

    public LatencyStatPoint subtractOffset(LatencyStatPoint offset) {
        LatencyStat newLatencyStat = subtractLatencyStat(this.latencyStat, offset.latencyStat);
        return new LatencyStatPoint(newLatencyStat, time);
    }

    private LatencyStat subtractLatencyStat(LatencyStat stat1, LatencyStat statOffset) {
        LatencyStatErr newErr = subtractLatencyStatErr(stat1.getErr(), statOffset.getErr());
        LatencyStatLat newLat = subtractLatencyStatLat(stat1.getLat(), statOffset.getLat());

        LatencyStat result = new LatencyStat();
        result.setErr(newErr);
        result.setLat(newLat);
        return result;
    }

    private LatencyStatLat subtractLatencyStatLat(LatencyStatLat lat1, LatencyStatLat latOffset) {
        LatencyStatLat result = new LatencyStatLat();
        result.setAverage(lat1.getAverage());
        result.setJit(lat1.getJit());
        result.setLastMax(lat1.getLastMax());
        result.setTotalMax(lat1.getTotalMax());

        Map<String, Long> shiftedHistogram = new HashMap<>();
        lat1.getHistogram().forEach((final String key, final Long value) -> {
            Long newvalue = value - latOffset.getHistogram().getOrDefault(key, 0L);
            shiftedHistogram.put(key, newvalue);
        });
        result.setHistogram(shiftedHistogram);

        return result;
    }

    private LatencyStatErr subtractLatencyStatErr(LatencyStatErr err1, LatencyStatErr errOffset) {
        LatencyStatErr result = new LatencyStatErr();
        result.setDrp(err1.getDrp() - errOffset.getDrp());
        result.setDup(err1.getDup() - errOffset.getDup());
        result.setOoo(err1.getOoo() - errOffset.getOoo());
        result.setSth(err1.getSth() - errOffset.getSth());
        result.setStl(err1.getStl() - errOffset.getStl());
        return result;
    }
}

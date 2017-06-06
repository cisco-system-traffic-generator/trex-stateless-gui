package com.cisco.trex.stateless.gui.models;

import com.cisco.trex.stateless.model.stats.FlowStat;


public class FlowStatPoint {
    private long rb;
    private double rbsL2;
    private long rp;
    private double rps;
    private long tb;
    private double tbsL1;
    private double tbsL2;
    private long tp;
    private double tps;
    private double time;

    public FlowStatPoint(final FlowStat flowStat, final double time) {
        rb = flowStat.getRb().values().stream().mapToLong(Long::longValue).sum();
        rbsL2 = flowStat.getRbs().values().stream().mapToDouble(Double::doubleValue).sum();
        rp = flowStat.getRp().values().stream().mapToLong(Long::longValue).sum();
        rps = flowStat.getRps().values().stream().mapToDouble(Double::doubleValue).sum();
        tb = flowStat.getTb().values().stream().mapToLong(Long::longValue).sum();
        tbsL2 = flowStat.getTbs().values().stream().mapToDouble(Double::doubleValue).sum();
        tp = flowStat.getTp().values().stream().mapToLong(Long::longValue).sum();
        tps = flowStat.getTps().values().stream().mapToDouble(Double::doubleValue).sum();
        tbsL1 = tbsL2 + 20 * tps * 8;
        this.time = time;
    }

    public long getRb() {
        return rb;
    }

    public double getRbsL2() {
        return rbsL2;
    }

    public long getRp() {
        return rp;
    }

    public double getRps() {
        return rps;
    }

    public long getTb() {
        return tb;
    }

    public double getTbsL1() {
        return tbsL1;
    }

    public double getTbsL2() {
        return tbsL2;
    }

    public long getTp() {
        return tp;
    }

    public double getTps() {
        return tps;
    }

    public double getTime() {
        return time;
    }
}

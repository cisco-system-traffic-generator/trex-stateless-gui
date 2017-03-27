package com.exalttech.trex.ui.models.stats.flow;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;


public class StatsFlowStream {
    private Map<Integer, Long> txPkts = new HashMap<>();
    private Map<Integer, Long> rxPkts = new HashMap<>();
    private Map<Integer, Long> txBytes = new HashMap<>();
    private Map<Integer, Long> rxBytes = new HashMap<>();
    private Map<Integer, Double> txPps = new HashMap<>();
    private Map<Integer, Double> rxPps = new HashMap<>();
    private Map<Integer, Double> txBpsL1 = new HashMap<>();
    private Map<Integer, Double> txBpsL2 = new HashMap<>();
    private Map<Integer, Double> rxBps = new HashMap<>();
    private double time = 0.0;

    public StatsFlowStream() {
        // Default constructor
    }

    public StatsFlowStream(
            double time
    ) {
        this.time = time;
    }

    public StatsFlowStream(
            Map<Integer, Long> txPkts,
            Map<Integer, Long> rxPkts,
            Map<Integer, Long> txBytes,
            Map<Integer, Long> rxBytes,
            double time
    ) {
        this.txPkts = txPkts;
        this.rxPkts = rxPkts;
        this.txBytes = txBytes;
        this.rxBytes = rxBytes;
        this.time = time;
    }

    public StatsFlowStream(
            StatsFlowStream prev,
            Map<Integer, Long> txPkts,
            Map<Integer, Long> rxPkts,
            Map<Integer, Long> txBytes,
            Map<Integer, Long> rxBytes,
            double time
    ) {
        this.txPkts = txPkts;
        this.rxPkts = rxPkts;
        this.txBytes = txBytes;
        this.rxBytes = rxBytes;
        this.time = time;

        final double timeDelta = this.time - prev.getTime();

        txPkts.keySet().forEach((Integer port) -> {
            this.txPps.put(port, calcPerSecond(prev.getTxPkts().get(port), this.txPkts.get(port), timeDelta));
        });

        rxPkts.keySet().forEach((Integer port) -> {
            this.rxPps.put(port, calcPerSecond(prev.getRxPkts().get(port), this.rxPkts.get(port), timeDelta));
        });

        txBytes.keySet().forEach((Integer port) -> {
            final double bpsL2 = calcPerSecond(prev.getTxBytes().get(port), this.txBytes.get(port), timeDelta);
            txBpsL2.put(port, bpsL2);
            if (bpsL2 == 0.0) {
                txBpsL1.put(port, 0.0);
                return;
            }
            final Double pps = this.txPps.get(port);
            if (pps == null) {
                txBpsL1.put(port, 0.0);
                return;
            }
            txBpsL1.put(port, bpsL2*(1 + (20/(bpsL2/pps))));
        });

        rxBytes.keySet().forEach((Integer port) -> {
            this.rxBps.put(port, calcPerSecond(prev.getRxBytes().get(port), this.rxBytes.get(port), timeDelta));
        });
    }

    public Map<Integer, Long> getTxPkts() {
        return txPkts;
    }

    public Map<Integer, Long> getRxPkts() {
        return rxPkts;
    }

    public Map<Integer, Long> getTxBytes() {
        return txBytes;
    }

    public Map<Integer, Long> getRxBytes() {
        return rxBytes;
    }

    public Map<Integer, Double> getTxPps() {
        return txPps;
    }

    public Map<Integer, Double> getRxPps() {
        return rxPps;
    }

    public Map<Integer, Double> getTxBpsL1() {
        return txBpsL1;
    }

    public Map<Integer, Double> getTxBpsL2() {
        return txBpsL2;
    }

    public Map<Integer, Double> getRxBps() {
        return rxBps;
    }

    public double getTime() {
        return time;
    }

    public long calcTotalTxPkts(Set<Integer> visiblePorts) {
        return calcTotalLong(txPkts, visiblePorts);
    }

    public long calcTotalRxPkts(Set<Integer> visiblePorts) {
        return calcTotalLong(rxPkts, visiblePorts);
    }

    public long calcTotalTxBytes(Set<Integer> visiblePorts) {
        return calcTotalLong(txBytes, visiblePorts);
    }

    public long calcTotalRxBytes(Set<Integer> visiblePorts) {
        return calcTotalLong(rxBytes, visiblePorts);
    }

    public double calcTotalTxPps(Set<Integer> visiblePorts) {
        return calcTotalDouble(txPps, visiblePorts);
    }

    public double calcTotalRxPps(Set<Integer> visiblePorts) {
        return calcTotalDouble(rxPps, visiblePorts);
    }

    public double calcTotalTxBpsL1(Set<Integer> visiblePorts) {
        return calcTotalDouble(txBpsL1, visiblePorts);
    }

    public double calcTotalTxBpsL2(Set<Integer> visiblePorts) {
        return calcTotalDouble(txBpsL2, visiblePorts);
    }

    public double calcTotalRxBps(Set<Integer> visiblePorts) {
        return calcTotalDouble(rxBps, visiblePorts);
    }

    public StatsFlowStream getZeroCopy() {
        final StatsFlowStream copy = new StatsFlowStream();
        copy.txPkts = new HashMap<>(txPkts);
        copy.txPkts.keySet().forEach((Integer key) -> {
            copy.txPkts.put(key, 0L);
        });
        copy.rxPkts = new HashMap<>(rxPkts);
        copy.rxPkts.keySet().forEach((Integer key) -> {
            copy.rxPkts.put(key, 0L);
        });
        copy.txBytes = new HashMap<>(txBytes);
        copy.txBytes.keySet().forEach((Integer key) -> {
            copy.txBytes.put(key, 0L);
        });
        copy.rxBytes = new HashMap<>(rxBytes);
        copy.rxBytes.keySet().forEach((Integer key) -> {
            copy.rxBytes.put(key, 0L);
        });
        copy.txPps = txPps;
        copy.rxPps = rxPps;
        copy.txBpsL1 = txBpsL1;
        copy.txBpsL2 = txBpsL2;
        copy.rxBps = rxBps;
        copy.time = time;
        return copy;
    }

    private static double calcPerSecond(Long prevValue, long currValue, double timeDelta) {
        if (prevValue == null) {
            prevValue = 0L;
        }
        return Math.max((currValue - prevValue)/timeDelta, 0.0);
    }

    private static long calcTotalLong(Map<Integer, Long> data, Set<Integer> visiblePorts) {
        AtomicLong total = new AtomicLong(0);
        data.forEach((Integer port, Long value) -> {
            if (visiblePorts == null || visiblePorts.contains(port)) {
                total.getAndAdd(value);
            }
        });
        return total.get();
    }

    private static double calcTotalDouble(Map<Integer, Double> data, Set<Integer> visiblePorts) {
        AtomicDouble total = new AtomicDouble(0);
        data.forEach((Integer port, Double value) -> {
            if (visiblePorts == null || visiblePorts.contains(port)) {
                total.getAndAdd(value);
            }
        });
        return total.get();
    }
}

package com.exalttech.trex.ui.models.stats.flow;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.HashMap;
import java.util.Map;
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
            final double bpsL2 = calcPerSecond(prev.getTxBytes().get(port), this.txBytes.get(port), timeDelta) * 8;
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
            txBpsL1.put(port, bpsL2 + 20 * pps * 8);
        });

        rxBytes.keySet().forEach((Integer port) -> {
            this.rxBps.put(port, calcPerSecond(prev.getRxBytes().get(port), this.rxBytes.get(port), timeDelta) * 8);
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

    public long calcTotalTxPkts() {
        return calcTotalLong(txPkts);
    }

    public long calcTotalRxPkts() {
        return calcTotalLong(rxPkts);
    }

    public long calcTotalTxBytes() {
        return calcTotalLong(txBytes);
    }

    public long calcTotalRxBytes() {
        return calcTotalLong(rxBytes);
    }

    public double calcTotalTxPps() {
        return calcTotalDouble(txPps);
    }

    public double calcTotalRxPps() {
        return calcTotalDouble(rxPps);
    }

    public double calcTotalTxBpsL1() {
        return calcTotalDouble(txBpsL1);
    }

    public double calcTotalTxBpsL2() {
        return calcTotalDouble(txBpsL2);
    }

    public double calcTotalRxBps() {
        return calcTotalDouble(rxBps);
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

    private static long calcTotalLong(Map<Integer, Long> data) {
        AtomicLong total = new AtomicLong(0);
        data.forEach((Integer port, Long value) -> {
            total.getAndAdd(value);
        });
        return total.get();
    }

    private static double calcTotalDouble(Map<Integer, Double> data) {
        AtomicDouble total = new AtomicDouble(0);
        data.forEach((Integer port, Double value) -> {
            total.getAndAdd(value);
        });
        return total.get();
    }
}

package com.exalttech.trex.ui.models.stats.latency;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;


public class StatsLatencyTableRow {
    private final SimpleStringProperty pgId = new SimpleStringProperty("");
    private final SimpleLongProperty txPkts = new SimpleLongProperty(0);
    private final SimpleLongProperty rxPkts = new SimpleLongProperty(0);
    private final SimpleIntegerProperty maxLatency = new SimpleIntegerProperty(0);
    private final SimpleDoubleProperty avgLatency = new SimpleDoubleProperty(0.0);
    private final SimpleIntegerProperty jitter = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty dropped = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty dup = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty outOfOrder = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty seqToHigh = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty seqToLow = new SimpleIntegerProperty(0);

    public StatsLatencyTableRow() {
        this("", 0, 0, 0, 0.0, 0, 0, 0, 0, 0);
    }

    public StatsLatencyTableRow(
            String pgId,
            long txPkts,
            long rxPkts,
            int maxLatency,
            double avgLatency,
            int jitter,
            int dropped,
            int dup,
            int seqToHigh,
            int seqToLow
    ) {
        setPgId(pgId);
        setTxPkts(txPkts);
        setRxPkts(rxPkts);
        setMaxLatency(maxLatency);
        setAvgLatency(avgLatency);
        setJitter(jitter);
        setDropped(dropped);
        setDup(dup);
        setSeqToHigh(seqToHigh);
        setSeqToLow(seqToLow);
    }

    public String getPgId() { return pgId.get(); }
    public void setPgId(String pgId) { this.pgId.set(pgId); }

    public long getTxPkts() { return txPkts.get(); }
    public void setTxPkts(long txPkts) { this.txPkts.set(txPkts); }

    public long getRxPkts() { return rxPkts.get(); }
    public void setRxPkts(long rxPkts) { this.rxPkts.set(rxPkts); }

    public int getMaxLatency() { return maxLatency.get(); }
    public void setMaxLatency(int maxLatency) { this.maxLatency.set(maxLatency); }

    public double getAvgLatency() { return avgLatency.get(); }
    public void setAvgLatency(double avgLatency) { this.avgLatency.set(avgLatency); }

    public int getJitter() { return jitter.get(); }
    public void setJitter(int jitter) { this.jitter.set(jitter); }

    public int getDropped() { return dropped.get(); }
    public void setDropped(int dropped) { this.dropped.set(dropped); }

    public int getDup() { return dup.get(); }
    public void setDup(int dup) { this.dup.set(dup); }

    public int getOutOfOrder() { return outOfOrder.get(); }
    public void setOutOfOrder(int outOfOrder) { this.outOfOrder.set(outOfOrder); }

    public int getSeqToHigh() { return seqToHigh.get(); }
    public void setSeqToHigh(int seqToHigh) { this.seqToHigh.set(seqToHigh); }

    public int getSeqToLow() { return seqToLow.get(); }
    public void setSeqToLow(int seqToLow) { this.seqToLow.set(seqToLow); }
}

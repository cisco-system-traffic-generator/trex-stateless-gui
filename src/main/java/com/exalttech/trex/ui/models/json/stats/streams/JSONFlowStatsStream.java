package com.exalttech.trex.ui.models.json.stats.streams;

import java.util.HashMap;


public class JSONFlowStatsStream {
    HashMap<Integer, Long> tx_pkts;
    HashMap<Integer, Long> tx_bytes;
    HashMap<Integer, Long> rx_pkts;
    HashMap<Integer, Long> rx_bytes;

    public JSONFlowStatsStream() {
        tx_pkts = new HashMap<>();
        tx_bytes = new HashMap<>();
        rx_pkts = new HashMap<>();
        rx_bytes = new HashMap<>();
    }

    public JSONFlowStatsStream(
            HashMap<Integer, Long> tx_pkts,
            HashMap<Integer, Long> tx_bytes,
            HashMap<Integer, Long> rx_pkts,
            HashMap<Integer, Long> rx_bytes
    ) {
        this.tx_pkts = tx_pkts;
        this.tx_bytes = tx_bytes;
        this.rx_pkts = rx_pkts;
        this.rx_bytes = rx_bytes;
    }

    public HashMap<Integer, Long> getTx_pkts() {
        return tx_pkts;
    }

    public void setTx_pkts(HashMap<Integer, Long> tx_pkts) {
        this.tx_pkts = tx_pkts;
    }

    public HashMap<Integer, Long> getTx_bytes() {
        return tx_bytes;
    }

    public void setTx_bytes(HashMap<Integer, Long> tx_bytes) {
        this.tx_bytes = tx_bytes;
    }

    public HashMap<Integer, Long> getRx_pkts() {
        return rx_pkts;
    }

    public void setRx_pkts(HashMap<Integer, Long> rx_pkts) {
        this.rx_pkts = rx_pkts;
    }

    public HashMap<Integer, Long> getRx_bytes() {
        return rx_bytes;
    }

    public void setRx_bytes(HashMap<Integer, Long> rx_bytes) {
        this.rx_bytes = rx_bytes;
    }
}

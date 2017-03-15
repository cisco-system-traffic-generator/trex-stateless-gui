package com.exalttech.trex.ui.models.json.stats.streams;

import java.util.HashMap;


public class JSONFlowStatsStream {
    private HashMap<Integer, Long> tx_pkts;
    private HashMap<Integer, Long> tx_bytes;
    private HashMap<Integer, Long> rx_pkts;
    private HashMap<Integer, Long> rx_bytes;
    private HashMap<Integer, Long> tx_err;

    public HashMap<Integer, Long> getTx_pkts() { return tx_pkts; }
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

    public HashMap<Integer, Long> getTx_err() { return tx_err; }
    public void setTx_err(HashMap<Integer, Long> tx_err) { this.tx_err = tx_err; }
}

package com.exalttech.trex.ui.models;

import java.util.HashMap;


public class RawFlowStatsData {
    HashMap<Integer,Integer> tx_pkts;
    HashMap<Integer,Integer> tx_bytes;
    HashMap<Integer,Integer> rx_pkts;
    HashMap<Integer,Integer> rx_bytes;

    public RawFlowStatsData() {
        tx_pkts = new HashMap<>();
        tx_bytes = new HashMap<>();
        rx_pkts = new HashMap<>();
        rx_bytes = new HashMap<>();
    }

    public RawFlowStatsData(
            HashMap<Integer,Integer> tx_pkts,
            HashMap<Integer,Integer> tx_bytes,
            HashMap<Integer,Integer> rx_pkts,
            HashMap<Integer,Integer> rx_bytes
    ) {
        this.tx_pkts = tx_pkts;
        this.tx_bytes = tx_bytes;
        this.rx_pkts = rx_pkts;
        this.rx_bytes = rx_bytes;
    }

    public HashMap<Integer, Integer> getTx_pkts() {
        return tx_pkts;
    }

    public void setTx_pkts(HashMap<Integer, Integer> tx_pkts) {
        this.tx_pkts = tx_pkts;
    }

    public HashMap<Integer, Integer> getTx_bytes() {
        return tx_bytes;
    }

    public void setTx_bytes(HashMap<Integer, Integer> tx_bytes) {
        this.tx_bytes = tx_bytes;
    }

    public HashMap<Integer, Integer> getRx_pkts() {
        return rx_pkts;
    }

    public void setRx_pkts(HashMap<Integer, Integer> rx_pkts) {
        this.rx_pkts = rx_pkts;
    }

    public HashMap<Integer, Integer> getRx_bytes() {
        return rx_bytes;
    }

    public void setRx_bytes(HashMap<Integer, Integer> rx_bytes) {
        this.rx_bytes = rx_bytes;
    }
}

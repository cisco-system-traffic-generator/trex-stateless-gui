package com.exalttech.trex.ui.models;

public enum ConfigurationMode {
    L2(2), L3(3);

    int layer;

    ConfigurationMode(int layer) {
        this.layer = layer;
    }
}

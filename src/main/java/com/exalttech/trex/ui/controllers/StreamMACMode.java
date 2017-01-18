package com.exalttech.trex.ui.controllers;

public class StreamMACMode {
    public String name;
    public Integer value;
    public Integer mask;
    
    public StreamMACMode(String name, Integer value, Integer mask) {
        this.name = name;
        this.value = value;
        this.mask = mask;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

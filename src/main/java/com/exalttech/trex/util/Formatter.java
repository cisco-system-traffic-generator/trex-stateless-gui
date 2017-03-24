package com.exalttech.trex.util;


public class Formatter {
    static private final int UNIT_VALUE = 1000;

    private int exp = 0;
    private double factor = 1.0;
    private String unitsPrefix = "";

    public void addValue(Number value) {
        final int newExp = (int) (Math.log(value.longValue()) / Math.log(UNIT_VALUE));
        if (newExp <= exp) {
            return;
        }
        exp = newExp;
        factor = 1.0 / Math.pow(UNIT_VALUE, exp);
        unitsPrefix = String.valueOf(("KMGTPE").charAt(exp - 1));
    }

    public Number getFormattedValue(Number value) {
        if (exp == 0) {
            return value;
        }
        return value.doubleValue() * factor;
    }

    public String getUnitsPrefix() {
        return unitsPrefix;
    }
}

package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import com.cisco.trex.stl.gui.models.FlowStatPoint;
import javafx.beans.property.IntegerProperty;


public class PacketLossController extends StreamLineChartController {
    public PacketLossController(final IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Packet loss";
    }

    protected String getYChartUnits() {
        return "p/s";
    }

    protected Number getValue(final FlowStatPoint point) {
        return point.getPacketLossPerSecond();
    }
}

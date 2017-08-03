package com.cisco.trex.stl.gui.controllers.capture;

import java.util.ArrayList;
import java.util.List;

public class AddRecordPojo {
    public List<Integer> rxPorts = new ArrayList<>();
    public List<Integer> txPorts = new ArrayList<>();
    public int pktLimit = 1000;
}

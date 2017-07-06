package com.cisco.trex.stl.gui.services.capture;

import com.cisco.trex.stateless.model.capture.CapturedPackets;

public interface PktDumpService {
    Process init(String wireSharkExecPath) throws PktDumpServiceInitException;

    void dump(CapturedPackets capturedPkts) throws PktDumpServiceException;

    void close();
}

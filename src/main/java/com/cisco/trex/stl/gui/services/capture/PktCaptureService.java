package com.cisco.trex.stl.gui.services.capture;

import com.cisco.trex.stateless.TRexClient;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.cisco.trex.stateless.model.capture.CaptureInfo;
import com.cisco.trex.stateless.model.capture.CaptureMonitor;
import com.cisco.trex.stateless.model.capture.CapturedPackets;
import com.exalttech.trex.core.ConnectionManager;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;

import java.util.List;

public class PktCaptureService extends ScheduledService<CapturedPackets> {
    private static Logger LOG = Logger.getLogger(PktCaptureService.class);
    
    private int currentActiveMonitorId = 0;
    
    private TRexClient tRexClient = ConnectionManager.getInstance().getTrexClient();
    
    @Override
    protected Task<CapturedPackets> createTask() {
        return new Task<CapturedPackets>() {
            @Override
            protected CapturedPackets call() throws Exception {
                if (currentActiveMonitorId == 0) {
                    return null;
                }
                try {
                    return fetchCapturedPkts(currentActiveMonitorId, 10);
                } catch (PktCaptureServiceException e) {
                    LOG.error("Unable to fetch pkts from monitor.", e);
                    return null;
                }
                
            }
        };
    }
    
    public boolean startMonitor(List<Integer> rx, List<Integer> tx) throws PktCaptureServiceException {
        TRexClientResult<CaptureMonitor> result = tRexClient.captureMonitorStart(rx, tx);
        guardNotFailed(result);
        currentActiveMonitorId = result.get().getCaptureId();
        start();
        return true;
    }

    synchronized public void stopMonitor() {
        tRexClient.captureMonitorStop(currentActiveMonitorId);
        tRexClient.captureMonitorRemove(currentActiveMonitorId);
    }

    synchronized public CapturedPackets fetchCapturedPkts(int captureId, int chunkSize) throws PktCaptureServiceException {
        TRexClientResult<CapturedPackets> result = tRexClient.captureFetchPkts(captureId, chunkSize);
        guardNotFailed(result);
        return result.get();
    }
    
    public CaptureMonitor addRecorder(List<Integer> rx, List<Integer> tx, int bufferSize) throws PktCaptureServiceException {
        TRexClientResult<CaptureMonitor> result = tRexClient.captureRecorderStart(rx, tx, bufferSize);
        guardNotFailed(result);
        return result.get();
    }
    
    public void getActiveCaptures() throws PktCaptureServiceException {
        TRexClientResult<CaptureInfo[]> result = tRexClient.getActiveCaptures();
        guardNotFailed(result);
    }
    
    private void guardNotFailed(TRexClientResult<?> result) throws PktCaptureServiceException {
        if (result.isFailed()) {
            throw new PktCaptureServiceException(result.getError());
        }
    }
}

package com.cisco.trex.stl.gui.services.capture;

import com.cisco.trex.stateless.TRexClient;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.cisco.trex.stateless.model.capture.CaptureInfo;
import com.cisco.trex.stateless.model.capture.CaptureMonitor;
import com.cisco.trex.stateless.model.capture.CaptureMonitorStop;
import com.cisco.trex.stateless.model.capture.CapturedPackets;
import com.exalttech.trex.core.ConnectionManager;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    
    public int startMonitor(List<Integer> rx, List<Integer> tx, boolean serviceEnable) throws PktCaptureServiceException {
        TRexClientResult<CaptureMonitor> result = tRexClient.captureMonitorStart(rx, tx);
        guardNotFailed(result);
        int captureId = result.get().getCaptureId();
        if (serviceEnable) {
            currentActiveMonitorId = captureId;
            start();
        }
        return captureId;
    }

    public int updateMonitor(List<Integer> rx, List<Integer> tx) throws PktCaptureServiceException {
        tRexClient.captureMonitorStop(currentActiveMonitorId);
        tRexClient.captureMonitorRemove(currentActiveMonitorId);
        TRexClientResult<CaptureMonitor> result = tRexClient.captureMonitorStart(rx, tx);
        guardNotFailed(result);
        int captureId = result.get().getCaptureId();
        currentActiveMonitorId = captureId;
        return captureId;
    }

    synchronized public void stopMonitor(int captureId) {
        if (currentActiveMonitorId == captureId) {
            currentActiveMonitorId = 0;
        }
        tRexClient.captureMonitorStop(captureId);
        tRexClient.captureMonitorRemove(captureId);
    }

    public CaptureMonitorStop stopRecorder(int caputureId) throws PktCaptureServiceException {
        TRexClientResult<CaptureMonitorStop> result = tRexClient.captureMonitorStop(caputureId);
        if (result.isFailed()) {
            LOG.error("Unable to stop monitor. "+result.getError());
            throw new PktCaptureServiceException(result.getError());
        }
        return result.get();
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
    
    public boolean removeRecorder(int recorderId) {
        return tRexClient.captureMonitorRemove(recorderId);
    }
    
    public List<CaptureInfo> getActiveCaptures() throws PktCaptureServiceException {
        TRexClientResult<CaptureInfo[]> result = tRexClient.getActiveCaptures();
        guardNotFailed(result);
        return Arrays.stream(result.get()).collect(Collectors.toList());
    }
    
    private void guardNotFailed(TRexClientResult<?> result) throws PktCaptureServiceException {
        if (result.isFailed()) {
            throw new PktCaptureServiceException(result.getError());
        }
    }
}

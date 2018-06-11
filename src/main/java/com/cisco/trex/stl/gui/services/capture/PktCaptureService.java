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

    public int getCurrentActiveMonitorId() {
        return currentActiveMonitorId;
    }
    
    public int startMonitor(
            List<Integer> rx,
            List<Integer> tx,
            String filter,
            boolean serviceEnable) throws PktCaptureServiceException {
        TRexClientResult<CaptureMonitor> result = getTrexClient().captureMonitorStart(rx, tx, filter);
        guardNotFailed(result);
        int captureId = result.get().getCaptureId();
        if (serviceEnable) {
            currentActiveMonitorId = captureId;
            start();
        }
        return captureId;
    }

    private TRexClient getTrexClient() {
        return ConnectionManager.getInstance().getTrexClient();
    }

    public int updateMonitor(
            List<Integer> rx,
            List<Integer> tx,
            String filter) throws PktCaptureServiceException {
        getTrexClient().captureMonitorStop(currentActiveMonitorId);
        getTrexClient().captureMonitorRemove(currentActiveMonitorId);
        TRexClientResult<CaptureMonitor> result = getTrexClient().captureMonitorStart(rx, tx, filter);
        guardNotFailed(result);
        int captureId = result.get().getCaptureId();
        currentActiveMonitorId = captureId;
        return captureId;
    }

    synchronized public void stopMonitor(int captureId) {
        if (currentActiveMonitorId == captureId) {
            currentActiveMonitorId = 0;
        }
        getTrexClient().captureMonitorStop(captureId);
        getTrexClient().captureMonitorRemove(captureId);
    }

    public CaptureMonitorStop stopRecorder(int caputureId) throws PktCaptureServiceException {
        TRexClientResult<CaptureMonitorStop> result = getTrexClient().captureMonitorStop(caputureId);
        if (result.isFailed()) {
            LOG.error("Unable to stop monitor. "+result.getError());
            throw new PktCaptureServiceException(result.getError());
        }
        return result.get();
    }

    synchronized public CapturedPackets fetchCapturedPkts(int captureId, int chunkSize) throws PktCaptureServiceException {
        TRexClientResult<CapturedPackets> result = getTrexClient().captureFetchPkts(captureId, chunkSize);
        guardNotFailed(result);
        return result.get();
    }
    
    public CaptureMonitor addRecorder(List<Integer> rx, List<Integer> tx, String filter, int bufferSize) throws PktCaptureServiceException {
        TRexClientResult<CaptureMonitor> result = getTrexClient().captureRecorderStart(rx, tx, filter, bufferSize);
        guardNotFailed(result);
        return result.get();
    }
    
    public boolean removeRecorder(int recorderId) {
        return getTrexClient().captureMonitorRemove(recorderId);
    }
    
    public List<CaptureInfo> getActiveCaptures() throws PktCaptureServiceException {
        TRexClientResult<CaptureInfo[]> result = getTrexClient().getActiveCaptures();
        guardNotFailed(result);
        return Arrays.stream(result.get()).collect(Collectors.toList());
    }
    
    private void guardNotFailed(TRexClientResult<?> result) throws PktCaptureServiceException {
        if (result.isFailed()) {
            throw new PktCaptureServiceException(result.getError());
        }
    }
}

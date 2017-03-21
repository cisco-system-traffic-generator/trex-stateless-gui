/**
 * *****************************************************************************
 * Copyright (c) 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************
 */
package com.exalttech.trex.ui;

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.views.services.UpdatePortStatusService;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Util;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.concurrent.WorkerStateEvent;
import javafx.util.Duration;

/**
 * Port Manager class implementation
 *
 * @author Georgekh
 */
public class PortsManager {

    private static PortsManager instance = null;

    /**
     * Return instance of Port manager
     *
     * @return
     */
    public static PortsManager getInstance() {
        if (instance == null) {
            instance = new PortsManager();
        }

        return instance;
    }
    private List<Port> portList;
    UpdatePortStatusService updatePortStatusService;
    PortManagerEventHandler portManagerHandler;

    /**
     * Protected constructor
     */
    protected PortsManager() {
        // constructor
    }

    /**
     * Get port list
     *
     * @return
     */
    public List<Port> getPortList() {
        if (hasPorts()) {
            return portList;
        }
        return new ArrayList<>();
    }

    /**
     * Set port list
     *
     * @param portList
     */
    public void setPortList(List<Port> portList) {
        this.portList = portList;
    }

    /**
     * Stop update port state scheduler service
     */
    public void stopPortStatusScheduler() {
        if (updatePortStatusService != null) {
            if (!updatePortStatusService.isRunning()) {
                updatePortStatusService.reset();
            }
            updatePortStatusService.cancel();
        }
    }

    /**
     * Set port handler
     *
     * @param portManagerHandler
     */
    public void setPortManagerHandler(PortManagerEventHandler portManagerHandler) {
        this.portManagerHandler = portManagerHandler;
    }

    /**
     * Start scheduler to update port state every 15sec
     */
    public void startPortStatusScheduler() {
        updatePortStatusService = new UpdatePortStatusService(portList);
        updatePortStatusService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        updatePortStatusService.setRestartOnFailure(false);
        updatePortStatusService.setOnSucceeded((WorkerStateEvent event) -> {
            portList = (List<Port>) event.getSource().getValue();
            boolean successfulyUpdate = portList != null && !portList.isEmpty();
            portManagerHandler.onPortListUpdated(successfulyUpdate);
        });
        updatePortStatusService.start();
    }

    /**
     * Force request to update port state
     */
    public void updatePortForce() {
        portList = updatePortStatusService.getUpdatedPortList();
        portManagerHandler.onPortListUpdated(true);
    }

    /**
     * Return port state scheduler instance
     *
     * @return
     */
    public UpdatePortStatusService getUpdatePortStatusService() {
        return updatePortStatusService;
    }

    /**
     * Return port size
     *
     * @param isOwnerFiltered
     * @return
     */
    public int getPortCount(boolean isOwnerFiltered) {
        int count = 0;
        if (hasPorts()) {
            if (!isOwnerFiltered) {
                return portList.size();
            }

            for (Port port : portList) {
                if (port.getOwner().equals(ConnectionManager.getInstance().getClientName())) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Return number of active port
     *
     * @return
     */
    public String getActivePort() {
        int count = 0;
        if (hasPorts()) {
            for (Port port : getPortList()) {
                if (PortState.getPortStatus(port.getStatus()) == PortState.TX) {
                    count++;
                }
            }
        }
        return String.valueOf(count);
    }

    /**
     * Return whether the current user is the port owner
     *
     * @param portIndex
     * @return
     */
    public boolean isCurrentUserOwner(int portIndex) {
        return portList.get(portIndex).getOwner().equals(ConnectionManager.getInstance().getClientName());
    }

    /**
     * Return whether the port list is exists or not
     */
    private boolean hasPorts() {
        return portList != null && !portList.isEmpty();
    }

    /**
     * Return whether the port is owned or free
     *
     * @param portIndex
     * @return
     */
    public boolean isPortFree(int portIndex) {
        return Util.isNullOrEmpty(portList.get(portIndex).getOwner());
    }

    /**
     * Return indexes of the ports
     *
     * @return
     */
    public List<Integer> getPortIndexes() {
        List<Integer> portIndexes = new LinkedList<Integer>();
        portList.forEach((Port port) -> {
            portIndexes.add(port.getIndex());
        });
        return portIndexes;
    }

    /**
     * Return indexes of the owned ports
     *
     * @return
     */
    public List<Integer> getOwnedPortIndexes() {
        List<Integer> ownedPortIndexes = new LinkedList<Integer>();
        portList.forEach((Port port) -> {
            int portIndex = port.getIndex();
            if (isCurrentUserOwner(portIndex)) {
                ownedPortIndexes.add(portIndex);
            }
        });
        return ownedPortIndexes;
    }
}

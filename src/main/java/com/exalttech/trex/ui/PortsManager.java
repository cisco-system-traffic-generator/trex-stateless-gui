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

import com.cisco.trex.stateless.model.port.PortVlan;
import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.ui.models.PortStatus;
import com.exalttech.trex.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Port Manager class implementation
 *
 * @author Georgekh
 */
public class PortsManager {

    public interface PortServiceModeChangedListener {
        void serviceModeChanged();
    }

    private static final Logger logger = Logger.getLogger(PortsManager.class);
    
    private static PortsManager instance = null;

    ObjectMapper mapper = new ObjectMapper();
    
    private List<PortServiceModeChangedListener> portServiceModeChangedListeners = Collections.synchronizedList(new ArrayList<>());
    
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
    PortManagerEventHandler portManagerHandler;

    private Map<Integer, PortModel> portModels = new HashMap<>();
    
    /**
     * Protected constructor
     */
    protected PortsManager() {
        // constructor
    }

    public void addPortServiceModeChangedListener(final PortServiceModeChangedListener listener) {
        portServiceModeChangedListeners.add(listener);
    }

    public void removePortServiceModeChangedListener(final PortServiceModeChangedListener listener) {
        portServiceModeChangedListeners.remove(listener);
    }
    
    public PortModel getPortModel(int portIndex) {
        PortModel model = portModels.get(portIndex);
        if (model == null) {
            model = PortModel.createModelFrom(portList.get(portIndex));
            model.serviceModeProperty().addListener((observable, oldVal, newVal) -> {
                synchronized (portServiceModeChangedListeners) {
                    portServiceModeChangedListeners.forEach(PortServiceModeChangedListener::serviceModeChanged);
                }
            });
            portModels.put(portIndex, model);
        }
        return model;
    }
    
    public void clearPorts() {
        portModels.clear();
        portList.clear();
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
     * Set port handler
     *
     * @param portManagerHandler
     */
    public void setPortManagerHandler(PortManagerEventHandler portManagerHandler) {
        this.portManagerHandler = portManagerHandler;
    }

    /**
     * Force request to update port state
     */
    public void updatePortForce() {
        updatedPorts(portList.stream().map(Port::getIndex).collect(Collectors.toList()));
    }
    
    public void updatedPorts(List<Integer> portIndexes) {
        List<Port> list = portList.stream()
                                  .filter(port -> portIndexes.contains(port.getIndex()))
                                  .collect(Collectors.toList());

        try {
            String response = ConnectionManager.getInstance().sendPortStatusRequest(list);
            if (response == null) {
                return;
            }
            List<PortStatus> portStatusList = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, PortStatus.class));
            for (Port port : list) {
                PortStatus.PortStatusResult portStatus = portStatusList.get(list.indexOf(port)).getResult();
                port.setOwner(portStatus.getOwner());
                port.setStatus(portStatus.getState());
                port.setAttr(portStatus.getAttr());
                port.setRx_info(portStatus.getRx_info());
                port.setService(portStatus.getService());
                port.linkProperty().set(portStatus.getAttr().getLink().getUp());
                updateModel(port.getIndex(), portStatus);
            }
            portManagerHandler.onPortListUpdated(true);
        } catch (Exception ex) {
            logger.error("Error reading port status", ex);
        }
    }

    private void updateModel(int portIdx, PortStatus.PortStatusResult portStatus) {
        PortModel model = getPortModel(portIdx);
        String vlan = "";
        PortVlan portVlan = portStatus.getAttr().getVlan();
        List<Integer> vlanIds = portVlan.getTags();
        if (!vlanIds.isEmpty()) {
            vlan = vlanIds.stream()
                          .map(String::valueOf)
                          .collect(Collectors.joining(" "));
        }
        model.setVlan(vlan);
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

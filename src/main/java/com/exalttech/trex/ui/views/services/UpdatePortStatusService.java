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
/*



 */
package com.exalttech.trex.ui.views.services;

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.models.PortStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GeorgeKh
 */
public class UpdatePortStatusService extends ScheduledService<List<Port>> {

    private static final Logger LOG = Logger.getLogger(UpdatePortStatusService.class.getName());
    List<Port> portList;
    Task portStatTask;
    ObjectMapper mapper = new ObjectMapper();

    /**
     *
     * @param portList
     */
    public UpdatePortStatusService(List<Port> portList) {
        this.portList = portList;
    }

    @Override
    protected Task<List<Port>> createTask() {
        return new Task<List<Port>>() {
            @Override
            protected List<Port> call() throws Exception {
                return updatePortList();
            }
        };
    }

    /**
     * Update port list
     *
     * @return
     */
    private List<Port> updatePortList() {
        try {
            String response = ConnectionManager.getInstance().sendPortStatusRequest(portList);
            if (response == null) {
                return new ArrayList<>();
            }
            List<PortStatus> portStatusList = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, PortStatus.class));
            PortStatus portStatus = null;
            for (Port port : portList) {
                portStatus = portStatusList.get(port.getIndex());
                port.setOwner(portStatus.getResult().getOwner());
                port.setStatus(portStatus.getResult().getState());
                port.setAttr(portStatus.getResult().getAttr());
                port.setRx_info(portStatus.getResult().getRx_info());
                port.setService(portStatus.getResult().getService());

//                String response1 = ConnectionManager.getInstance().sendRequest(Constants.CAPTURE_METHOD,
//                        "\"command\":\"status\"");
//                CaptureStatus[] capture = Util.getCaptureStatusFromJSONString(response1);
//                port.setCaptureStatus(null);
//                for (int i = 0; i < capture.length; i++) {
//                    int port_index_mask = 1 << port.getIndex();
//                    if ((capture[i].getFilter().getRx() & port_index_mask) != 0) {
//                        port.setCaptureStatus(capture);
//                        break;
//                    }
//                    if ((capture[i].getFilter().getTx() & port_index_mask) != 0) {
//                        port.setCaptureStatus(capture);
//                        break;
//                    }
//                }
//
//                String response2 = ConnectionManager.getInstance().sendPortXStatsNamesRequest(port);
//                String response3 = ConnectionManager.getInstance().sendPortXStatsValuesRequest(port);
//                Map<String, Long> loadedXStatsList = Util.getXStatsFromJSONString(response2, response3);
//                port.setXstats(loadedXStatsList);

                // Is it right ?
                port.setSpeed(port.getAttr().getSpeed());
            }
            return portList;
        } catch (Exception ex) {
            LOG.error("Error reading port status", ex);
            return new ArrayList<>();
        }
    }

    /**
     * Return updated port list
     *
     * @return
     */
    public List<Port> getUpdatedPortList() {
        return updatePortList();
    }

}

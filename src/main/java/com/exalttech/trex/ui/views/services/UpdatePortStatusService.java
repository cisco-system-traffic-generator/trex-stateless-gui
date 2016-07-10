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
import com.exalttech.trex.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 *
 * @author GeorgeKh
 */
public class UpdatePortStatusService extends ScheduledService<List<Port>> {

    List<Port> portList;
    Task portStatTask;

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
        for (Port port : portList) {
            String portStatus = ConnectionManager.getInstance().sendRequest("get_port_status", "\"port_id\": " + port.getIndex());
            if (portStatus == null) {
                return new ArrayList<>();
            }
            portStatus = Util.removeFirstBrackets(portStatus);
            portStatus = Util.fromJSONResult(portStatus, "result");
            Map<String, String> resutlSet = new HashMap<>();
            resutlSet.put("owner", "");
            resutlSet.put("state", "");
            Util.fromJSONResultSet(portStatus, resutlSet);
            port.setOwner(resutlSet.get("owner"));
            port.setStatus(resutlSet.get("state"));
        }

        return portList;
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

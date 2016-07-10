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

package com.exalttech.trex.ui.views;

import com.exalttech.trex.ui.TableCellElementGenerator;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.models.SystemInfoReq;
import javafx.scene.layout.GridPane;

/**
 * 
 * TODO internationalize labels once the project is internationalized.
 * 
 * Info Panes Generator (System / port information)
 * @author Georgekh
 */
public class InfoPaneGenerator {
    
    /**
     * Build and return Device table info
     * @param systemInfoReq
     * @return 
     */
    public GridPane generateSystemInfoPane(SystemInfoReq systemInfoReq) {
        GridPane systemInfoGridPane = new GridPane();
        systemInfoGridPane.getStyleClass().add("statsTable");
        systemInfoGridPane.setGridLinesVisible(false);
        int rowIndex = 0;
        systemInfoGridPane.add(TableCellElementGenerator.getTableHeaderElement("Counter"), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableHeaderElement("Value", 450), 1, rowIndex++);

        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement("ID", false, false), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement(systemInfoReq.getId(), false, 450, false), 1, rowIndex++);

        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement("jsonrpc", true, false), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement(systemInfoReq.getJsonrpc(), true, 450, false), 1, rowIndex++);

        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement("IP", false, false), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement(systemInfoReq.getIp(), false, 450, false), 1, rowIndex++);

        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement("Port", true, false), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement(systemInfoReq.getPort(), true, 450, false), 1, rowIndex++);

        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement("Core Type", false, false), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement(systemInfoReq.getResult().getCoreType(), false, 450, false), 1, rowIndex++);

        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement("Core Count", true, false), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement(systemInfoReq.getResult().getDpCoreCount(), true, 450, false), 1, rowIndex++);

        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement("Host Name", false, false), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement(systemInfoReq.getResult().getHostname(), false, 450, false), 1, rowIndex++);

        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement("Port Count", true, false), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement(String.valueOf(systemInfoReq.getResult().getPortCount()), true, 450, false), 1, rowIndex++);

        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement("Up Time", false, false), 0, rowIndex);
        systemInfoGridPane.add(TableCellElementGenerator.getTableCellElement(systemInfoReq.getResult().getUptime(), false, 450, false), 1, rowIndex++);

        return systemInfoGridPane;
    }
    
     /**
     * Build and return port detail table
     * @param port
     * @return 
     */
    public GridPane generatePortInfoPane(Port port) {
        GridPane portGridPane = new GridPane();
        portGridPane.getStyleClass().add("statsTable");
        portGridPane.setGridLinesVisible(false);
        int rowIndex = 0;

        portGridPane.add(TableCellElementGenerator.getTableHeaderElement("Counter"), 0, rowIndex);
        portGridPane.add(TableCellElementGenerator.getTableHeaderElement("Value"), 1, rowIndex++);

        portGridPane.add(TableCellElementGenerator.getTableCellElement("Port name", false, false), 0, rowIndex);
        portGridPane.add(TableCellElementGenerator.getTableCellElement("Port " + port.getIndex(), false, false), 1, rowIndex++);

        portGridPane.add(TableCellElementGenerator.getTableCellElement("Driver", true, false), 0, rowIndex);
        portGridPane.add(TableCellElementGenerator.getTableCellElement(port.getDriver(), true, false), 1, rowIndex++);

        portGridPane.add(TableCellElementGenerator.getTableCellElement("Index", false, false), 0, rowIndex);
        portGridPane.add(TableCellElementGenerator.getTableCellElement(String.valueOf(port.getIndex()), false, false), 1, rowIndex++);

        portGridPane.add(TableCellElementGenerator.getTableCellElement("Owner", true, false), 0, rowIndex);
        portGridPane.add(TableCellElementGenerator.getTableCellElement(port.getOwner(), true, false), 1, rowIndex++);

        portGridPane.add(TableCellElementGenerator.getTableCellElement("Speed", false, false), 0, rowIndex);
        portGridPane.add(TableCellElementGenerator.getTableCellElement(port.getSpeed() + " Gbps", false, false), 1, rowIndex++);

        portGridPane.add(TableCellElementGenerator.getTableCellElement("Status", true, false), 0, rowIndex);
        portGridPane.add(TableCellElementGenerator.getTableCellElement(port.getStatus(), true, false), 1, rowIndex++);
        
        return portGridPane;
    }
}

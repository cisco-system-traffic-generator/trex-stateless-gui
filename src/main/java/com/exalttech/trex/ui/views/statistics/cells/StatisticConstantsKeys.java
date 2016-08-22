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
package com.exalttech.trex.ui.views.statistics.cells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Constants class that contains statistics keys
 * @author Georgekh
 */
public class StatisticConstantsKeys {
     
    /**
     *
     */
    public static final List<String> PORT_STATS_ROW_NAME = Arrays.asList("Owner", "State", "Tx bps L2", "Tx pps", "Rx bps", "Rx pps", "opackets",
            "ipackets", "obytes", "ibytes", "tx-bytes", "rx-bytes", "tx-pkts", "rx-pkts", "oerrors", "ierrors");

    /**
     * 
     */
    public static final List<String> GLOBAL_STATS_ROW_NAME = Arrays.asList("Cpu Util", "Total Tx", "Total Rx", "Total Pps", 
            "Drop Rate", "Queue Full", "Active Ports");
    
    public static final List<String> PORT_ROW_NAME = Arrays.asList("Port name", "Driver", "Index", "Port Owner", 
            "Speed", "Status");
    public static final List<String> SYSTEM_INFO_ROW_NAME = Arrays.asList("ID", "jsonrpc", "IP", "Port", 
            "Core Type", "Core Count", "Host Name", "Port Count", "Up Time");
    
    public static final List<String> Stream_ROW_NAME = Arrays.asList("tx-bytes", "rx-bytes", "tx-pkts", "rx-pkts");
    
    public static final List<StatisticRow> PORT_STATS_KEY = new ArrayList();
    public static final List<StatisticRow> GLOBAL_STATS_KEY = new ArrayList();
    static{
        PORT_STATS_KEY.add(new StatisticRow("m_total_tx_bps", "m_total_tx_bps", CellType.ARROWS_CELL, false, "bps"));
        PORT_STATS_KEY.add(new StatisticRow("m_total_tx_pps", "m_total_tx_pps", CellType.ARROWS_CELL, false, "pps"));
        PORT_STATS_KEY.add(new StatisticRow("m_total_rx_bps", "m_total_rx_bps", CellType.ARROWS_CELL, false, "bps"));
        PORT_STATS_KEY.add(new StatisticRow("m_total_rx_pps", "m_total_rx_pps", CellType.ARROWS_CELL, false, "pps"));

        PORT_STATS_KEY.add(new StatisticRow("opackets", "opackets", CellType.DEFAULT_CELL, false, ""));
        PORT_STATS_KEY.add(new StatisticRow("ipackets", "ipackets", CellType.DEFAULT_CELL, false, ""));
        PORT_STATS_KEY.add(new StatisticRow("obytes", "obytes", CellType.DEFAULT_CELL, false, ""));
        PORT_STATS_KEY.add(new StatisticRow("ibytes", "ibytes", CellType.DEFAULT_CELL, false, ""));

        PORT_STATS_KEY.add(new StatisticRow("obytes-formated", "obytes", CellType.DEFAULT_CELL, true, "B"));
        PORT_STATS_KEY.add(new StatisticRow("ibytes-formated", "ibytes", CellType.DEFAULT_CELL, true, "B"));
        PORT_STATS_KEY.add(new StatisticRow("opackets-formated", "opackets", CellType.DEFAULT_CELL, true, "pkts"));
        PORT_STATS_KEY.add(new StatisticRow("ipackets-formated", "ipackets", CellType.DEFAULT_CELL, true, "pkts"));

        PORT_STATS_KEY.add(new StatisticRow("oerrors", "oerrors", CellType.ERROR_CELL, false, ""));
        PORT_STATS_KEY.add(new StatisticRow("ierrors", "ierrors", CellType.ERROR_CELL, false, ""));

        GLOBAL_STATS_KEY.add(new StatisticRow("m_cpu_util", "m_cpu_util", CellType.DEFAULT_CELL, true, "%"));
        GLOBAL_STATS_KEY.add(new StatisticRow("m_tx_bps", "m_tx_bps", CellType.DEFAULT_CELL, true, "b/sec"));
        GLOBAL_STATS_KEY.add(new StatisticRow("m_rx_bps", "m_rx_bps", CellType.DEFAULT_CELL, true, "b/sec"));
        GLOBAL_STATS_KEY.add(new StatisticRow("m_tx_pps", "m_tx_pps", CellType.DEFAULT_CELL, true, "pkt/sec"));
        GLOBAL_STATS_KEY.add(new StatisticRow("m_rx_drop_bps", "m_rx_drop_bps", CellType.DEFAULT_CELL, true, "b/sec"));
        GLOBAL_STATS_KEY.add(new StatisticRow("m_total_queue_full", "m_total_queue_full", CellType.DEFAULT_CELL, true, "pkts"));
        GLOBAL_STATS_KEY.add(new StatisticRow("active-port", "active-port", CellType.DEFAULT_CELL, false, ""));
    }
}

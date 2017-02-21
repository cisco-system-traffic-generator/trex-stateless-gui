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
package com.exalttech.trex.ui.views.statistics;

import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.models.SystemInfoReq;
import com.exalttech.trex.ui.views.statistics.cells.*;
import com.exalttech.trex.util.Util;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;

import java.beans.EventHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author GeorgeKh
 */
public class StatsTableGenerator {

    private static final Logger LOG = Logger.getLogger(StatsTableGenerator.class.getName());
    Map<String, String> currentStatsList;
    Map<String, String> cachedStatsList;
    Map<String, String> prevStatsList;
    Map<String, Long> totalValues = new HashMap<>();
    Map<String, Long> prevTotalValues = new HashMap<>();

    GridPane statTable = new GridPane();
    GridPane statXTable = new GridPane();

    Map<String, StatisticCell> gridCellsMap = new HashMap<>();
    StringBuilder keyBuffer = new StringBuilder(30);


    private static DataFormat dragDataFormat = new DataFormat("drag-n-drop-format");
    private static String dragTargetStyle = "-fx-border-style: solid inside; -fx-border-width: 2;" +
            "-fx-border-insets: 2; -fx-border-radius: 1; -fx-border-color: green;";

    private boolean odd;
    private int rowIndex;

    private static final int WIDTH_COL_0 = 145;
    private static final int WIDTH_COL_1 = 150;
    private static final int WIDTH_COL_PIN = 48;

    /**
     * Constructor
     */
    public StatsTableGenerator() {
        statTable = new GridPane();
        statTable.setCache(false);
        statTable.getStyleClass().add("statsTable");
        statTable.setGridLinesVisible(false);

        statXTable = new GridPane();
        statXTable.setCache(false);
        statXTable.getStyleClass().add("statsTable");
        statXTable.setGridLinesVisible(false);
    }

    /**
     *
     * @param cached
     * @param portIndex
     * @return
     */
    public GridPane getPortStatTable(Map<String, String> cached, int portIndex) {
        return getPortStatTable(cached, portIndex, false, WIDTH_COL_1, false);
    }

    /**
     * Build port stats table
     *
     * @param cached
     * @param portIndex
     * @param columnWidth
     * @param isMultiPort
     * @param ownerFilter
     * @return
     */
    public GridPane getPortStatTable(Map<String, String> cached, int portIndex, boolean isMultiPort, double columnWidth, boolean ownerFilter) {
        this.currentStatsList = StatsLoader.getInstance().getLoadedStatsList();
        this.prevStatsList = StatsLoader.getInstance().getPreviousStatsList();
        this.prevTotalValues = totalValues;
        this.cachedStatsList = cached;

        int startPortIndex = portIndex;
        int endPortIndex = portIndex + 1;
        if (isMultiPort) {
            startPortIndex = 0;
            endPortIndex = portIndex;
        }
        rowIndex = 0;
        totalValues = new HashMap<>();
        statTable.getChildren().clear();

        Util.optimizeMemory();

        addCounterColumn(StatisticConstantsKeys.PORT_STATS_ROW_NAME);

        int columnIndex = 1;
        for (int i = startPortIndex; i < endPortIndex; i++) {
            rowIndex = 0;
            odd = true;
            Port port = PortsManager.getInstance().getPortList().get(i);
            if (!ownerFilter || (ownerFilter && PortsManager.getInstance().isCurrentUserOwner(port.getIndex()))) {
                // add owner and port status
                addPortInfoCells(port, columnWidth, columnIndex);
                for (StatisticRow key : StatisticConstantsKeys.PORT_STATS_KEY) {
                    keyBuffer.setLength(0);
                    keyBuffer.append(key.getKey()).append("-").append(i);

                    StatisticCell cell = getGridCell(key, columnWidth, keyBuffer.toString());

                    cell.updateItem(getPrevValue(key, i, false), getStatValue(key, i));
                    statTable.getChildren().remove(cell);
                    statTable.add((Node) cell, columnIndex, rowIndex++);
                }
                columnIndex++;
            }
        }
        if (isMultiPort) {
            addTotalColumn(columnWidth, columnIndex);
        }
        return statTable;

    }

    /**
     * Add counter column
     *
     * @param counterList
     */
    private void addCounterColumn(List<String> counterList) {
        addCounterColumn(counterList, 0, "Counter", "Counter");
    }
    private void addCounterColumn(List<String> counterList, int columnIndex, String key, String header) {
        double firstColWidth = WIDTH_COL_0;
        rowIndex = 0;
        addHeaderCell(key, header, columnIndex, firstColWidth);
        odd = true;
        rowIndex = 1;
        for (String label : counterList) {
            addDefaultCell(label, label, firstColWidth, columnIndex);
        }
    }

    /**
     * Add counter column
     *
     * @param attrList
     */
    private void addAttrColumn(List<String> attrList) {
        addAttrColumn(statTable, attrList, "port-attrs", "Port attrs");
    }

    private void addAttrColumn(GridPane table, List<String> attrList, String key, String header) {
        double firstColWidth = WIDTH_COL_0;
        addHeaderCell(table, key, header, 0, firstColWidth, rowIndex++);
        odd = true;
        for (String label : attrList) {
            addDefaultCell(table, label, label, firstColWidth, 0);
        }
    }

    /**
     * Add Port info(port/status/owner) cells
     *
     * @param port
     * @param width
     * @param columnIndex
     */
    private void addPortInfoCells(Port port, double width, int columnIndex) {
        StatisticRow row;
        keyBuffer.setLength(0);
        keyBuffer.append("port-").append(port.getIndex());
        row = new StatisticRow(keyBuffer.toString(), "", CellType.HEADER_WITH_ICON, false, "");
        HeaderCellWithIcon headerCell = (HeaderCellWithIcon) getGridCell(row, width, row.getKey());
        headerCell.setTitle("Port " + port.getIndex());
        headerCell.updateItem("", port.getStatus());
        statTable.getChildren().remove(headerCell);
        statTable.add((Node) headerCell, columnIndex, rowIndex++);

        keyBuffer.setLength(0);
        keyBuffer.append("owner-").append(port.getIndex());
        row = new StatisticRow(keyBuffer.toString(), "owner", CellType.DEFAULT_CELL, false, "");
        StatisticCell cell = getGridCell(row, width, row.getKey());
        cell.updateItem("", port.getOwner());
        statTable.getChildren().remove(cell);
        statTable.add((Node) cell, columnIndex, rowIndex++);

        keyBuffer.setLength(0);
        keyBuffer.append("status-").append(port.getIndex());
        row = new StatisticRow(keyBuffer.toString(), "status", CellType.STATUS_CELL, false, "");
        cell = getGridCell(row, width, row.getKey());
        cell.updateItem("", port.getStatus());
        statTable.getChildren().remove(cell);
        statTable.add((Node) cell, columnIndex, rowIndex++);
        row = null;
    }

    /**
     * Return the cell if exists otherwise create it
     *
     * @param row
     * @param width
     * @param key
     * @return
     */
    private StatisticCell getGridCell(StatisticRow row, double width, String key) {
        StatisticCell cell = gridCellsMap.get(key);
        if (cell != null) {
            cell.setPrefWidth(width);
        } else {
            cell = createGridCell(row, width);
            gridCellsMap.put(key, cell);
        }
        return cell;
    }

    /**
     * Create cell
     *
     * @param row
     * @param width
     * @return
     */
    private StatisticCell createGridCell(StatisticRow row, double width) {
        switch (row.getCellType()) {
            case ERROR_CELL:
                odd = !odd;
                return new StatisticLabelCell(width, odd, CellType.ERROR_CELL, row.isRightPosition());
            case ATTR_CELL:
            case DEFAULT_CELL:
                odd = !odd;
                return new StatisticLabelCell(width, odd, CellType.DEFAULT_CELL, row.isRightPosition());
            case STATUS_CELL:
                odd = !odd;
                return new StatisticLabelCell(width, odd, CellType.STATUS_CELL, row.isRightPosition());
            case HEADER_CELL:
                return new HeaderCell(width);
            case HEADER_WITH_ICON:
                return new HeaderCellWithIcon(width);
            case ARROWS_CELL:
                odd = !odd;
                return new StatisticCellWithArrows(width, odd, row.getUnit());
        }
        return null;
    }

    /**
     * Add header cell
     *
     */
    private void addHeaderCell(String title, int columnIndex, double columnWidth) {
        addHeaderCell(statTable, title, title, columnIndex, columnWidth, 0);
    }

    private void addHeaderCell(String key, String title, int columnIndex, double columnWidth) {
        addHeaderCell(statTable, key, title, columnIndex, columnWidth, 0);
    }

    private void addHeaderCell(GridPane table, String key, String title, int columnIndex, double columnWidth) {
        addHeaderCell(table, key, title, columnIndex, columnWidth, 0);
    }

    private void addHeaderCell(String key, String title, int columnIndex, double columnWidth, int rowIndex) {
        addHeaderCell(statTable, key, title, columnIndex, columnWidth, rowIndex);
    }

    private void addHeaderCell(GridPane table, String key, String title, int columnIndex, double columnWidth, int rowIndex) {
        StatisticRow row = new StatisticRow(key, title, CellType.HEADER_CELL, false, "");
        StatisticCell cell = getGridCell(row, columnWidth, row.getKey());
        cell.updateItem("", title);
        table.getChildren().remove(cell);
        table.add((Node) cell, columnIndex, rowIndex);
    }

    /**
     * Return the equivalent cell value
     *
     * @param row
     * @param portIndex
     * @return
     */
    private String getStatValue(StatisticRow row, int portIndex) {
        keyBuffer.setLength(0);
        keyBuffer.append(row.getAttributeName()).append("-").append(portIndex);
        switch (row.getCellType()) {
            case ERROR_CELL:
                return calcTotal(row.getAttributeName(), String.valueOf(getStatsDifference(keyBuffer.toString())));
            case ATTR_CELL:
            case DEFAULT_CELL:
                if (row.isFormatted()) {
                    return Util.getFormatted(String.valueOf(getStatsDifference(keyBuffer.toString())), true, row.getUnit());
                }
                return calcTotal(row.getAttributeName(), String.valueOf(getStatsDifference(keyBuffer.toString())));
            case ARROWS_CELL:
                calcTotal(row.getAttributeName(), currentStatsList.get(keyBuffer.toString()));
                return currentStatsList.get(keyBuffer.toString());
        }
        return "";
    }

    /**
     * Return previous value
     *
     * @param row
     * @param portIndex
     * @param isTotal
     * @return
     */
    private String getPrevValue(StatisticRow row, int portIndex, boolean isTotal) {
        if (row.getCellType() == CellType.ARROWS_CELL || row.getCellType() == CellType.ERROR_CELL) {
            if (isTotal) {
                return String.valueOf(prevTotalValues.get(row.getAttributeName()));
            } else {
                keyBuffer.setLength(0);
                keyBuffer.append(row.getAttributeName()).append("-").append(portIndex);
                return prevStatsList.get(keyBuffer.toString());
            }
        }
        return "";
    }

    /**
     * Add total column
     *
     * @param columnIndex
     * @param columnWidth
     */
    private void addTotalColumn(double columnWidth, int columnIndex) {
        rowIndex = 1;
        odd = true;
        addHeaderCell("Total", columnIndex, columnWidth);
        addEmptyCell("total-owner", columnIndex, columnWidth);
        addEmptyCell("total-status", columnIndex, columnWidth);
        for (StatisticRow row : StatisticConstantsKeys.PORT_STATS_KEY) {
            StatisticCell cell = getGridCell(row, columnWidth, row.getKey() + "-total");
            cell.updateItem(getPrevValue(row, 0, true), getTotalValue(row));
            statTable.getChildren().remove(cell);
            statTable.add((Node) cell, columnIndex, rowIndex++);
        }
    }

    /**
     * return equivalent total value
     *
     * @param row
     * @return
     */
    private String getTotalValue(StatisticRow row) {
        String val = String.valueOf(totalValues.get(row.getAttributeName()));
        if (Util.isNullOrEmpty(val)) {
            return "0";
        }
        return val;
    }

    /**
     * Add cell of type default
     *
     * @param key
     * @param value
     * @param columnWidth
     * @param columnIndex
     */
    private StatisticCell addDefaultCell(String key, String value, double columnWidth, int columnIndex) {
        return addDefaultCell(statTable, key, value, columnWidth, columnIndex);
    }

    private StatisticCell addDefaultCell(GridPane table, String key, String value, double columnWidth, int columnIndex) {
        StatisticRow row = new StatisticRow(key, key, CellType.DEFAULT_CELL, false, "");
        row.setRightPosition(false);
        StatisticCell cell = getGridCell(row, columnWidth, key);
        cell.updateItem("", value);
        table.getChildren().remove(cell);
        table.add((Node) cell, columnIndex, rowIndex++);
        return cell;
    }

    /**
     * Add cell of type default
     *
     * @param key
     * @param value
     * @param columnWidth
     * @param columnIndex
     */
    private void addAttrCell(String key, String value, double columnWidth, int columnIndex) {
        addAttrCell(statTable, key, value, columnWidth, columnIndex);
    }

    private void addAttrCell(GridPane table
            , String key, String value, double columnWidth, int columnIndex)
    {
        StatisticRow row = new StatisticRow(key, key, CellType.ATTR_CELL, false, "");
        row.setRightPosition(false);
        StatisticCell cell = getGridCell(row, columnWidth, key);
        cell.updateItem("", value);
        table.getChildren().remove(cell);
        table.add((Node) cell, columnIndex, rowIndex++);
    }

    private void addAttrCell2(GridPane table
            , String key0, String value0, double columnWidth0, int columnIndex0
            , String key1, String value1, double columnWidth1, int columnIndex1)
    {
        addAttrCell2(table, null
                , key0, value0, columnWidth0, columnIndex0
                , key1, value1, columnWidth1, columnIndex1);
    }

    private void addAttrCell2(GridPane table, CheckBox checkbox
            , String key0, String value0, double columnWidth0, int columnIndex0
            , String key1, String value1, double columnWidth1, int columnIndex1)
    {
        boolean odd2 = odd;
        StatisticRow row0 = new StatisticRow(key0, key0, CellType.ATTR_CELL, false, "");
        row0.setRightPosition(false);
        StatisticCell cell0 = getGridCell(row0, columnWidth0, key0);
        cell0.updateItem("", value0);
        table.getChildren().remove(cell0);
        table.add((Node) cell0, columnIndex0, rowIndex);

        odd = odd2;
        StatisticRow row1 = new StatisticRow(key1, key1, CellType.ATTR_CELL, false, "");
        row1.setRightPosition(false);
        StatisticCell cell1 = getGridCell(row1, columnWidth1, key1);
        cell1.updateItem("", value1);

        table.getChildren().remove(cell1);
        table.add((Node) cell1, columnIndex1, rowIndex);
        if (checkbox != null) {
            table.add((Node) checkbox, columnIndex1, rowIndex);
        }

        rowIndex++;
    }

    private void addXstatRow(GridPane table
            , Consumer<MouseEvent> col2MouseHandler, String col2AddStyleClass, String col2RemoveStyleClass, Tooltip tooltip
            , String key0, String value0, double columnWidth0, int columnIndex0
            , String key1, String value1, double columnWidth1, int columnIndex1
            , String key2, String value2, double columnWidth2, int columnIndex2)
    {
        boolean odd2 = odd;
        StatisticRow row0 = new StatisticRow(key0, key0, CellType.ATTR_CELL, false, "");
        row0.setRightPosition(false);
        StatisticCell cell0 = getGridCell(row0, columnWidth0, key0);
        cell0.updateItem("", value0);
        table.getChildren().remove(cell0);
        table.add((Node) cell0, columnIndex0, rowIndex);

        odd = odd2;
        StatisticRow row1 = new StatisticRow(key1, key1, CellType.ATTR_CELL, false, "");
        row1.setRightPosition(false);
        StatisticCell cell1 = getGridCell(row1, columnWidth1, key1);
        cell1.updateItem("", value1);
        table.getChildren().remove(cell1);
        table.add((Node) cell1, columnIndex1, rowIndex);

        odd = odd2;
        StatisticRow row2 = new StatisticRow(key2, key2, CellType.ATTR_CELL, false, "");
        row2.setRightPosition(false);
        StatisticCell cell2 = getGridCell(row2, columnWidth2, key2);
        cell2.updateItem("", value2);
        table.getChildren().remove(cell2);
        table.add((Node) cell2, columnIndex2, rowIndex);
        GridPane.setHalignment((Node) cell2, HPos.CENTER);
        ((Node) cell2).getStyleClass().removeAll(col2RemoveStyleClass);
        ((Node) cell2).getStyleClass().add(col2AddStyleClass);
        ((Node) cell2).setOnMouseClicked((event) -> {
            col2MouseHandler.accept(event);
            event.consume();
        });

        rowIndex++;
    }

    /**
     * Return Difference between current and cached stats value
     *
     * @param key
     * @return
     */
    private long getStatsDifference(String key) {
        String cached = cachedStatsList.get(key);
        String current = currentStatsList.get(key);
        return calculateDiff(current, cached);
    }

    /**
     * Calculate difference between two values
     *
     * @param current
     * @param cached
     * @return
     */
    private long calculateDiff(String current, String cached) {
        try {
            if (Util.isNullOrEmpty(current)) {
                return 0;
            } else if (Util.isNullOrEmpty(cached)) {
                return Long.valueOf(current);
            } else {
                return Long.valueOf(current) - Long.valueOf(cached);
            }
        } catch (NumberFormatException ex) {
            LOG.warn("Error calculating difference", ex);
            return 0;
        }
    }

    /**
     * Add empty cell
     *
     * @param key
     * @param columnIndex
     * @param columnWidth
     */
    private StatisticCell addEmptyCell(String key, int columnIndex, double columnWidth) {
        return addDefaultCell(key, "", columnWidth, columnIndex++);
    }
    private void addEmptyColumn(int rowindex, int columnindex, int count) {
        int rowIndexSaved  = rowIndex;
        rowIndex = rowindex;
        for (int i = 0; i < count; i++) {
            odd = true;
            StatisticCell cell = addEmptyCell("empy-col-" + columnindex + "-" + rowIndex, columnindex, WIDTH_COL_0 / 3);
        }
        rowIndex = rowIndexSaved;
    }

    /**
     * calculate total value
     *
     * @param key
     * @param val
     * @return
     */
    private String calcTotal(String key, String val) {
        if (val == null) {
            val = "0";
        }
        if (!Util.isNullOrEmpty(val)) {
            long value = (long) Double.parseDouble(val);
            if (totalValues.get(key) != null) {
                totalValues.put(key, totalValues.get(key) + value);
            } else {
                totalValues.put(key, value);
            }
            return String.valueOf(value);
        }
        return "0";
    }

    /**
     * Reset to empty state
     */
    public void reset() {

        statTable.getChildren().clear();
        Util.optimizeMemory();

        statTable = null;

        gridCellsMap.clear();
        gridCellsMap = null;

        currentStatsList.clear();
        currentStatsList = null;

        prevStatsList.clear();
        prevStatsList = null;

        cachedStatsList.clear();
        cachedStatsList = null;

        totalValues.clear();
        totalValues = null;

        prevTotalValues.clear();
        prevStatsList = null;
    }

    /**
     * Build system info pane
     *
     * @param systemInfoReq
     * @return
     */
    public GridPane generateSystemInfoPane(SystemInfoReq systemInfoReq) {
        statTable.getChildren().clear();
        Util.optimizeMemory();

        double columnWidth = 450;
        addHeaderCell("Value", 1, columnWidth);
        addCounterColumn(StatisticConstantsKeys.SYSTEM_INFO_ROW_NAME);
        rowIndex = 1;
        odd = true;
        addDefaultCell("info-id", systemInfoReq.getId(), columnWidth, 1);
        addDefaultCell("info-jsonrpc", systemInfoReq.getJsonrpc(), columnWidth, 1);
        addDefaultCell("info-ip", systemInfoReq.getIp(), columnWidth, 1);
        addDefaultCell("info-port", systemInfoReq.getPort(), columnWidth, 1);
        addDefaultCell("info-core-type", systemInfoReq.getResult().getCoreType(), columnWidth, 1);
        addDefaultCell("info-core-count", systemInfoReq.getResult().getDpCoreCount(), columnWidth, 1);
        addDefaultCell("info-host-name", systemInfoReq.getResult().getHostname(), columnWidth, 1);
        addDefaultCell("info-port-count", String.valueOf(systemInfoReq.getResult().getPortCount()), columnWidth, 1);
        addDefaultCell("info-up-time", systemInfoReq.getResult().getUptime(), columnWidth, 1);
        addDefaultCell("info-api-version", systemInfoReq.getResult().getApiVersion(), columnWidth, 1);

        return statTable;
    }

    /**
     * Build port info pane
     *
     * @param port
     * @return
     */
    public GridPane generatePortInfoPane(Port port) {
        statTable.getChildren().clear();
        Util.optimizeMemory();

        double columnWidth = WIDTH_COL_1;
        addHeaderCell("Value", 1, columnWidth);
        addCounterColumn(StatisticConstantsKeys.PORT_ROW_NAME);
        rowIndex = 1;
        odd = true;
        addDefaultCell("port-name", "Port " + port.getIndex(), columnWidth, 1);
        addDefaultCell("port-driver", port.getDriver(), columnWidth, 1);
        addDefaultCell("port-index", String.valueOf(port.getIndex()), columnWidth, 1);
        addDefaultCell("port-owner", port.getOwner(), columnWidth, 1);
        addDefaultCell("port-speed", String.valueOf(port.getSpeed()), columnWidth, 1);
        addDefaultCell("port-status", port.getStatus(), columnWidth, 1);

        return statTable;
    }

    /**
     * Build global statistic pane
     *
     * @return
     */
    public GridPane generateGlobalStatPane() {
        Map<String, String> statsList = StatsLoader.getInstance().getLoadedStatsList();
        statTable.getChildren().clear();
        Util.optimizeMemory();

        double columnWidth = 150;
        addHeaderCell("Value", 1, columnWidth);
        addCounterColumn(StatisticConstantsKeys.GLOBAL_STATS_ROW_NAME);
        rowIndex = 1;
        odd = true;
        for (StatisticRow row : StatisticConstantsKeys.GLOBAL_STATS_KEY) {
            StatisticCell cell = getGridCell(row, columnWidth, row.getKey());
            ((StatisticLabelCell) cell).setLeftPosition();
            if (row.getAttributeName().equals("active-port")) {
                cell.updateItem("", PortsManager.getInstance().getActivePort());
            } else {
                String value = statsList.get(row.getAttributeName());
                if (row.isFormatted()) {
                    value = Util.getFormatted(value, true, row.getUnit());
                }
                cell.updateItem("", value);
            }
            statTable.getChildren().remove(cell);
            statTable.add((Node) cell, 1, rowIndex++);
        }
        return statTable;
    }

    public GridPane generateXStatPane(boolean full, Port port, boolean notempty, String filter) {
        if (full) {
            statXTable.getChildren().clear();
            Util.optimizeMemory();
        }
        Map<String, Integer> xstatsList = port.getXstats();
        Map<String, Integer> xstatsListPinned = port.getXstatsPinned();
        String pinnedChar = "\u2716";
        String notPinnedChar = "\u271a";
        //String pinnedChar = "\u2611";
        //String notPinnedChar = "\u2610";

        rowIndex = 0;
        addHeaderCell(statXTable, "xstats-header0", "Counter", 0, WIDTH_COL_0 * 1.5);
        addHeaderCell(statXTable, "xstats-header1", "Value", 1, WIDTH_COL_1);
        addHeaderCell(statXTable, "xstats-header2", "Pin", 2, WIDTH_COL_PIN);
        rowIndex = 1;
        odd = true;
        xstatsListPinned.forEach( (k,v) -> {
            if (v != null) {
                Node check = new Label(pinnedChar);
                GridPane.setHalignment(check, HPos.CENTER);
                addXstatRow(statXTable,
                        (event) -> {
                            xstatsListPinned.remove(k, v);
                        },
                        "xstat-red", "xstat-green",
                        new Tooltip("Click '" + pinnedChar + "' to un-pin the counter."),
                        "xstats-val-0-" + rowIndex, k, WIDTH_COL_0 * 1.5, 0,
                        "xstats-val-1-" + rowIndex, v.toString(), WIDTH_COL_1, 1,
                        "xstats-val-2-" + rowIndex, pinnedChar, WIDTH_COL_PIN, 2);
            }
        });
        xstatsList.forEach( (k,v) -> {
            if (v != null && (!notempty || (notempty && v != 0)) && xstatsListPinned.get(k) == null) {
                if ((filter == null || filter.trim().length() == 0) || k.contains(filter)) {
                    Node check = new Label(notPinnedChar);
                    GridPane.setHalignment(check, HPos.CENTER);
                    addXstatRow(statXTable,
                            (event) -> {
                                xstatsListPinned.put(k, v);
                            },
                            "xstat-green", "xstat-red",
                            new Tooltip("Click '" + notPinnedChar + "' to pin the counter.\nPinned counter is always visible."),
                            "xstats-val-0-" + rowIndex, k, WIDTH_COL_0 * 1.5, 0,
                            "xstats-val-1-" + rowIndex, v.toString(), WIDTH_COL_1, 1,
                            "xstats-val-2-" + rowIndex, notPinnedChar, WIDTH_COL_PIN, 2);
                }
            }
        });

        GridPane gp = new GridPane();
        gp.setGridLinesVisible(false);
        gp.add(statXTable, 1, 1, 1, 2);

        return gp;
    }
}

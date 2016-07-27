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

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.ui.PortState;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.TableCellElementGenerator;
import static com.exalttech.trex.ui.TableCellElementGenerator.getTableCellElement;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Util;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.apache.log4j.Logger;

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
    Map<String, TotalStatsArrow> totalArrow = new HashMap<>();
    String arrowIcon = "green_arrow.png";

    /**
     *
     * @param cached
     * @param portIndex
     * @return
     */
    public GridPane getPortStatTable(Map<String, String> cached, int portIndex) {
        return getPortStatTable(cached, portIndex, false, 150, false);
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
        this.cachedStatsList = cached;

        int startPortIndex = portIndex;
        int endPortIndex = portIndex + 1;

        if (isMultiPort) {
            startPortIndex = 0;
            endPortIndex = portIndex;
        }
        int rowIndex = 0;

        // Build rows statically
        GridPane statTable = new GridPane();
        statTable.getStyleClass().add("statsTable");
        statTable.setGridLinesVisible(false);
        double firstColWidth = 145;

        // build 1st column
        int index = 0;
        statTable.add(TableCellElementGenerator.getTableHeaderElement("Counter", firstColWidth), 0, rowIndex++);
        for (String label : Constants.PORT_STATS_ROW_NAME) {
            statTable.add(TableCellElementGenerator.getTableCellElement(label, index % 2 != 0, firstColWidth, false), 0, rowIndex++);
            index++;
        }

        int columnIndex = 1;
        totalValues = new HashMap<>();
        for (int i = startPortIndex; i < endPortIndex; i++) {
            rowIndex = 0;
            Port port = PortsManager.getInstance().getPortList().get(i);
            if (!ownerFilter || (ownerFilter && port.getOwner().equals(ConnectionManager.getInstance().getClientName()))) {
                // Fill rows with Data
                PortState state = PortState.getPortStatus(port.getStatus());
                statTable.add(TableCellElementGenerator.getTableHeaderElementWithIcon("Port " + i, columnWidth, state.getStatHeaderIcon()), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableCellElement(getOwnerValue(port.getOwner()), false, columnWidth, true), columnIndex, rowIndex++);
                statTable.add(getColoredStateTableValue(state, true, columnWidth), columnIndex, rowIndex++);

                statTable.add(getTableValueWithArrow(Util.getFormatted(calcTotal("m_total_tx_bps", currentStatsList.get("m_total_tx_bps-" + i)), true, "bps"), false, columnWidth, "m_total_tx_bps-" + i, false), columnIndex, rowIndex++);
                statTable.add(getTableValueWithArrow(Util.getFormatted(calcTotal("m_total_tx_pps", currentStatsList.get("m_total_tx_pps-" + i)), true, "pps"), true, columnWidth, "m_total_tx_pps-" + i, false), columnIndex, rowIndex++);
                statTable.add(getTableValueWithArrow(Util.getFormatted(calcTotal("m_total_rx_bps", currentStatsList.get("m_total_rx_bps-" + i)), true, "bps"), false, columnWidth, "m_total_rx_bps-" + i, false), columnIndex, rowIndex++);
                statTable.add(getTableValueWithArrow(Util.getFormatted(calcTotal("m_total_rx_pps", currentStatsList.get("m_total_rx_pps-" + i)), true, "pps"), true, columnWidth, "m_total_rx_pps-" + i, false), columnIndex, rowIndex++);

                statTable.add(TableCellElementGenerator.getTableCellElement(calcTotal("opackets", String.valueOf(getDiff("opackets-" + i))), false, columnWidth, true), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableCellElement(calcTotal("ipackets", String.valueOf(getDiff("ipackets-" + i))), true, columnWidth, true), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableCellElement(calcTotal("obytes", String.valueOf(getDiff("obytes-" + i))), false, columnWidth, true), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableCellElement(calcTotal("ibytes", String.valueOf(getDiff("ibytes-" + i))), true, columnWidth, true), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(String.valueOf(getDiff("obytes-" + i)), true, "B"), false, columnWidth, true), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(String.valueOf(getDiff("ibytes-" + i)), true, "B"), true, columnWidth, true), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(String.valueOf(getDiff("opackets-" + i)), true, "pkts"), false, columnWidth, true), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(String.valueOf(getDiff("ipackets-" + i)), true, "pkts"), true, columnWidth, true), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableErrorElementValue(calcTotal("oerrors", String.valueOf(getDiff("oerrors-" + i))) + "", false, columnWidth), columnIndex, rowIndex++);
                statTable.add(TableCellElementGenerator.getTableErrorElementValue(calcTotal("ierrors", String.valueOf(getDiff("ierrors-" + i))) + "", true, columnWidth), columnIndex++, rowIndex++);
            }
        }
        if (isMultiPort) {
            addTotalColumn(statTable, columnIndex, columnWidth);
        }

        // clear total arrow map
        totalArrow.clear();
        return statTable;
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
     * Return Dif between current and original stat
     *
     * @param key
     * @return
     */
    private long getDiff(String key) {
        try {
            String cached = cachedStatsList.get(key);
            String current = currentStatsList.get(key);
            long data;
            if (Util.isNullOrEmpty(cached)) {
                data = (long) Double.parseDouble(current);
            } else {
                data = (long) Double.parseDouble(current) - (long) Double.parseDouble(cached);
            }
            return data;
        } catch (NumberFormatException ex) {
            LOG.warn("Error calculating difference", ex);
            return 0;
        }
    }

    /**
     * Return owner value
     *
     * @param owner
     * @return
     */
    private String getOwnerValue(String owner) {
        if (Util.isNullOrEmpty(owner)) {
            return "";
        }
        return owner;
    }

    /**
     * get table view colored value widget
     *
     * @param portState
     * @param isOdd
     * @param width
     * @return
     */
    private Label getColoredStateTableValue(PortState portState, boolean isOdd, double width) {
        Label label = getTableCellElement(portState.getDisplayedState(), isOdd, width, true);
        label.getStyleClass().add(portState.getTextColor());
        return label;
    }

    /**
     * Return table view value widget with arrow
     *
     * @param displayedTxt
     * @param isOdd
     * @param width
     * @param key
     * @param isTotal
     * @return
     */
    private HBox getTableValueWithArrow(String displayedTxt, boolean isOdd, double width, String key, boolean isTotal) {
        HBox container = new HBox();
        container.setPrefSize(width, 22);
        container.setSpacing(5);
        container.setAlignment(Pos.CENTER_RIGHT);
        container.getStyleClass().add("statsTableColCell");
        if (isOdd) {
            container.getStyleClass().add("statsTableColCellOdd");
        }
        int numOfArrow = 0;
        if (!isTotal) {
            numOfArrow = calculateNumOfArrows(key);
            if (numOfArrow > 0) {
                totalArrow.put(key.substring(0, key.indexOf("-")), new TotalStatsArrow(numOfArrow, arrowIcon));
            }
        } else if (totalArrow.get(key) != null) {
            TotalStatsArrow statArrow = totalArrow.get(key);
            numOfArrow = statArrow.getCount();
            arrowIcon = statArrow.getIcon();
        }
        HBox arrowContainer = new HBox();
        arrowContainer.setPrefSize(36, 22);
        arrowContainer.setAlignment(Pos.CENTER);
        arrowContainer.setSpacing(0);
        for (int count = 0; count < numOfArrow && count < 3; count++) {
            ImageView imageView = new ImageView(new Image("/icons/" + arrowIcon));
            arrowContainer.getChildren().add(imageView);
        }
        container.getChildren().add(arrowContainer);
        Label label = new Label(displayedTxt);
        container.getChildren().add(label);
        return container;
    }

    /**
     * Do calculation to get number of displayed arrow
     *
     * @param key
     * @return
     */
    private int calculateNumOfArrows(String key) {
        String current = currentStatsList.get(key);
        String prev = prevStatsList.get(key);
        int numOfArrow = 0;
        arrowIcon = "green_arrow.png";
        if (!Util.isNullOrEmpty(current) && !Util.isNullOrEmpty(prev)) {
            double currentVal = Double.parseDouble(current);
            double prevVal = Double.parseDouble(prev);
            if (currentVal != 0 && prevVal != 0) {
                double diff = currentVal - prevVal;
                if (diff > 0) {
                    arrowIcon = "red_arrow.png";
                }
                double val = Math.abs((diff / prevVal) * 100.0);

                //change in 1% is not meaningful
                if (val < 1) {
                    numOfArrow = 0;
                } else if (val > 5) {
                    numOfArrow = 3;
                } else if (val > 2) {
                    numOfArrow = 2;
                } else {
                    numOfArrow = 1;
                }

            }
        }
        return numOfArrow;
    }

    /**
     * Add total column
     *
     * @param statTable
     * @param columnIndex
     * @param columnWidth
     */
    private void addTotalColumn(GridPane statTable, int columnIndex, double columnWidth) {
        int rowIndex = 0;
        statTable.add(TableCellElementGenerator.getTableHeaderElement("Total", columnWidth), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableCellElement(" ", false, columnWidth, true), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableCellElement(" ", true, columnWidth, true), columnIndex, rowIndex++);

        statTable.add(getTableValueWithArrow(Util.getFormatted(getTotalValue("m_total_tx_bps"), true, "bps"), false, columnWidth, "m_total_tx_bps", true), columnIndex, rowIndex++);
        statTable.add(getTableValueWithArrow(Util.getFormatted(getTotalValue("m_total_tx_pps"), true, "pps"), true, columnWidth, "m_total_tx_pps", true), columnIndex, rowIndex++);
        statTable.add(getTableValueWithArrow(Util.getFormatted(getTotalValue("m_total_rx_bps"), true, "bps"), false, columnWidth, "m_total_rx_bps", true), columnIndex, rowIndex++);
        statTable.add(getTableValueWithArrow(Util.getFormatted(getTotalValue("m_total_rx_pps"), true, "pps"), true, columnWidth, "m_total_rx_pps", true), columnIndex, rowIndex++);

        statTable.add(TableCellElementGenerator.getTableCellElement(getTotalValue("opackets"), false, columnWidth, true), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableCellElement(getTotalValue("ipackets"), true, columnWidth, true), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableCellElement(getTotalValue("obytes"), false, columnWidth, true), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableCellElement(getTotalValue("ibytes"), true, columnWidth, true), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(getTotalValue("obytes"), true, "B"), false, columnWidth, true), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(getTotalValue("ibytes"), true, "B"), true, columnWidth, true), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(getTotalValue("opackets"), true, "pkts"), false, columnWidth, true), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(getTotalValue("ipackets"), true, "pkts"), true, columnWidth, true), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableErrorElementValue(getTotalValue("oerrors"), false, columnWidth), columnIndex, rowIndex++);
        statTable.add(TableCellElementGenerator.getTableErrorElementValue(getTotalValue("ierrors"), true, columnWidth), columnIndex, rowIndex++);
    }

    /**
     * return equivalent total value
     *
     * @param key
     * @return
     */
    private String getTotalValue(String key) {
        String val = String.valueOf(totalValues.get(key));
        if (Util.isNullOrEmpty(val)) {
            return "0";
        }
        return val;
    }

    /**
     * Build global statistic table
     *
     * @return
     */
    public GridPane getGlobalStatTable() {
        GridPane statTable = new GridPane();
        statTable.getStyleClass().add("statsTable");
        statTable.setGridLinesVisible(false);

        Map<String, String> statsList = StatsLoader.getInstance().getLoadedStatsList();

        int rowIndex = 0;
        // add globla stats
        statTable.add(TableCellElementGenerator.getTableHeaderElement("Counter"), 0, rowIndex);
        statTable.add(TableCellElementGenerator.getTableHeaderElement("Value"), 1, rowIndex++);

        statTable.add(TableCellElementGenerator.getTableCellElement("Cpu Util", false, false), 0, rowIndex);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getEmptyValue(statsList.get("m_cpu_util")) + " %", false, false), 1, rowIndex++);

        statTable.add(TableCellElementGenerator.getTableCellElement("Total Tx", true, false), 0, rowIndex);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(statsList.get("m_tx_bps"), true, "b/sec"), true, false), 1, rowIndex++);

        statTable.add(TableCellElementGenerator.getTableCellElement("Total Rx", false, false), 0, rowIndex);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(statsList.get("m_rx_bps"), true, "b/sec"), false, false), 1, rowIndex++);

        statTable.add(TableCellElementGenerator.getTableCellElement("Total Pps", true, false), 0, rowIndex);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(statsList.get("m_tx_pps"), true, "pkt/sec"), true, false), 1, rowIndex++);

        statTable.add(TableCellElementGenerator.getTableCellElement("Drop Rate", false, false), 0, rowIndex);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(statsList.get("m_rx_drop_bps"), true, "b/sec"), false, false), 1, rowIndex++);

        statTable.add(TableCellElementGenerator.getTableCellElement("Queue Full", true, false), 0, rowIndex);
        statTable.add(TableCellElementGenerator.getTableCellElement(Util.getFormatted(statsList.get("m_total_queue_full"), true, "pkts"), true, false), 1, rowIndex++);

        statTable.add(TableCellElementGenerator.getTableCellElement("Active Ports", false, false), 0, rowIndex);
        statTable.add(TableCellElementGenerator.getTableCellElement(PortsManager.getInstance().getActivePort(), false, false), 1, rowIndex++);

        return statTable;
    }

    /**
     * Enumerator that present total stats arrow
     */
    private class TotalStatsArrow {

        int count;
        String icon;

        public TotalStatsArrow(int count, String icon) {
            this.count = count;
            this.icon = icon;
        }

        public int getCount() {
            return count;
        }

        public String getIcon() {
            return icon;
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.importPcap;

import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.components.TextFieldTableViewCell;
import com.exalttech.trex.ui.models.PacketInfo;
import com.exalttech.trex.ui.util.AlertUtils;
import com.exalttech.trex.ui.views.models.ImportPcapTableData;
import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.exalttech.trex.ui.views.streams.builder.VMInstructionBuilder;
import com.exalttech.trex.ui.views.streams.builder.VMInstructionBuilder.InstructionType;
import com.exalttech.trex.ui.views.streams.viewer.PacketParser;
import com.exalttech.trex.util.TrafficProfile;
import com.exalttech.trex.util.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import java.io.EOFException;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeoutException;


public class ImportedPacketTableView extends AnchorPane {
    private static final Logger LOG = Logger.getLogger(ImportedPacketTableView.class.getName());

    @FXML
    TableColumn selectedColumn;
    @FXML
    TableColumn nameColumn;
    @FXML
    TableColumn packetNumColumn;
    @FXML
    TableColumn lengthColumn;
    @FXML
    TableColumn macSrcColumn;
    @FXML
    TableColumn macDstColumn;
    @FXML
    TableColumn ipSrcColumn;
    @FXML
    TableColumn ipDstColumn;
    @FXML
    TableColumn packetTypeColumn;
    @FXML
    TableView<ImportPcapTableData> importedStreamTable;

    List<Profile> profilesList;
    List<String> existingNamesList = new ArrayList<>();
    TrafficProfile trafficProfile;
    String yamlFileName;
    ObservableList<ImportPcapTableData> tableDataList = FXCollections.observableArrayList();
    ObservableList<Integer> highlightRows = FXCollections.observableArrayList();
    CheckBox selectAll;
    HighlightedRowFactory<ImportPcapTableData> highlightedRowFactory;
    Map<String, ImportPcapTableData> duplicateRowNamesMap = new HashMap<>();
    int index = 0;
    ImportedPacketProperties propertiesBinder;
    ImportPcapTableData firstPacket = null;

    /**
     * Constructor
     *
     * @param profilesList
     * @param yamlFileName
     */
    public ImportedPacketTableView(List<Profile> profilesList, String yamlFileName) {
        this.profilesList = profilesList;
        this.yamlFileName = yamlFileName;
        trafficProfile = new TrafficProfile();
        highlightedRowFactory = new HighlightedRowFactory<>(highlightRows);
        initView();
    }

    /**
     * Initialize view
     */
    private void initView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ImportedPacketTableView.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();

            initTableRowsColumns();
            extractExistingNames();
        } catch (Exception ex) {
            LOG.error("Error setting UI", ex);
        }
    }

    /**
     * Set properties binder model
     *
     * @param propertiesBinder
     */
    public void setPropertiesBinder(ImportedPacketProperties propertiesBinder) {
        this.propertiesBinder = propertiesBinder;
        PacketUpdater.getInstance().setImportedProperties(propertiesBinder);
    }

    /**
     * Initialize table rows and columns
     */
    private void initTableRowsColumns() {

        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectAll = new CheckBox();
        selectAll.getStyleClass().add("selectAll");
        selectAll.setSelected(true);
        selectAll.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            selectAllRows();
        });
        selectedColumn.setGraphic(selectAll);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(new TextFieldTableViewCell());

        packetNumColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        macSrcColumn.setCellValueFactory(new PropertyValueFactory<>("macSrc"));
        macDstColumn.setCellValueFactory(new PropertyValueFactory<>("macDst"));
        ipSrcColumn.setCellValueFactory(new PropertyValueFactory<>("ipSrc"));
        ipDstColumn.setCellValueFactory(new PropertyValueFactory<>("ipDst"));
        packetTypeColumn.setCellValueFactory(new PropertyValueFactory<>("packetType"));

        importedStreamTable.setRowFactory(highlightedRowFactory);

    }

    /**
     * Select all rows
     */
    public void selectAllRows() {
        for (ImportPcapTableData item : tableDataList) {
            item.setSelected(selectAll.isSelected());
        }
    }

    /**
     * Extract stream names from existing profile
     */
    private void extractExistingNames() {
        for (Profile profile : profilesList) {
            existingNamesList.add(profile.getName());
        }
    }

    /**
     * Parse pcap file to get all streams
     *
     * @param pcapFile
     * @return
     */
    public boolean setPcapFile(File pcapFile) throws PcapNativeException,
                                                     TimeoutException,
                                                     NotOpenException {
        List<PacketInfo> packetInfoList = new ArrayList<>();
        PacketUpdater.getInstance().reset();
        PcapHandle handler = Pcaps.openOffline(pcapFile.getAbsolutePath());
        PacketParser parser = new PacketParser();
        Packet packet;
        while (true) {
            try {
                packet = handler.getNextPacketEx();
            } catch (EOFException e) {
                break;
            }
            if (!PacketUpdater.getInstance().validatePacket(packet)) {
                break;
            }
            PacketInfo packetInfo = new PacketInfo();
            packet = PacketUpdater.getInstance().updatePacketSrcDst(packet);
            packetInfo.setPacket(packet);
            packetInfo.setTimeStamp(handler.getTimestamp().getTime());
            parser.parsePacket(packet, packetInfo);
            packetInfoList.add(packetInfo);
        }
        setTableData(packetInfoList);
        return PacketUpdater.getInstance().isValidPacket();
    }

    /**
     * Set table data
     *
     * @param packetInfoList
     */
    private void setTableData(List<PacketInfo> packetInfoList) {

        index = 1;
        tableDataList.clear();
        for (PacketInfo packetInfo : packetInfoList) {
            ImportPcapTableData tableData = new ImportPcapTableData();
            tableData.setName("packet_" + index);
            tableData.setIndex(index);
            tableData.setLength(packetInfo.getPacket().length());
            tableData.setMacSrc(packetInfo.getSrcMac());
            tableData.setMacDst(packetInfo.getDestMac());
            tableData.setIpSrc(packetInfo.getSrcIpv4());
            tableData.setIpDst(packetInfo.getDestIpv4());
            tableData.setPacketType(trafficProfile.getPacketTypeText(packetInfo.getPacket()).getType());
            tableData.setPacket(packetInfo.getPacket());
            tableData.setHasVlan(packetInfo.hasVlan());
            tableData.setTimeStamp(packetInfo.getTimeStamp());
            tableDataList.add(tableData);
            index++;
        }
        importedStreamTable.setItems(tableDataList);
    }

    /**
     * Import pcap to current yaml file
     *
     * @return
     */
    public boolean doImport() {
        try {
            if (validateStreamNames()) {
                index = 0;
                ImportPcapTableData current = getNextSelectedPacket();
                firstPacket = current;
                ImportPcapTableData next = null;
                boolean firstStream = true;
                long diffTimeStamp = 1;
                while (index <= tableDataList.size()) {
                    if (current != null) {
                        Profile profile = new Profile();
                        profile.setName(current.getName());
                        profile.getStream().getMode().setType("single_burst");
                        String hexDataString = PacketBuilderHelper.getPacketHex(current.getPacket().getRawData());
                        profile.getStream().getPacket().setBinary(trafficProfile.encodeBinaryFromHexString(hexDataString));

                        // add vm
                        if (propertiesBinder.isDestinationEnabled() || propertiesBinder.isSourceEnabled()) {
                            profile.getStream().setAdditionalProperties(getVm(current));
                        }
                        // update pps/ISG
                        defineISG_PPSValues(profile, getIpg(diffTimeStamp, firstStream));
                        if (firstStream) {
                            firstStream = false;
                        }
                        // get next stream 
                        next = getNextSelectedPacket();
                        if (next != null && next != current) {
                            profile.setNext(next.getName());
                            diffTimeStamp = next.getTimeStamp() - current.getTimeStamp();
                        } else if (propertiesBinder.getCount() > 0) {
                            profile.setNext(firstPacket.getName());
                            profile.getStream().setActionCount(propertiesBinder.getCount());
                        }

                        current = next;
                        profilesList.add(profile);
                    }
                }

                // save yaml data
                String yamlData = trafficProfile.convertTrafficProfileToYaml(profilesList.toArray(new Profile[profilesList.size()]));
                FileUtils.writeStringToFile(new File(yamlFileName), yamlData);
                return true;
            }
        } catch (Exception ex) {
            LOG.error("Error saving Yaml file", ex);
        }
        return false;
    }

    /**
     * Get next selected stream
     *
     * @return
     */
    private ImportPcapTableData getNextSelectedPacket() {
        if (index == tableDataList.size()) {
            index++;
            return null;
        }
        ImportPcapTableData tableData = tableDataList.get(index);
        index++;
        if (tableData.isSelected()) {
            return tableData;
        }
        return getNextSelectedPacket();
    }

    /**
     * Validate stream names
     *
     * @return
     */
    private boolean validateStreamNames() {

        ObservableList<Integer> duplicateIndexesList = FXCollections.observableArrayList();
        duplicateRowNamesMap.clear();
        // validate saved stream names
        for (ImportPcapTableData tableData : tableDataList) {
            if (tableData.isSelected()) {
                // comparing with existing streams
                if ((existingNamesList.contains(tableData.getName()) || Util.isNullOrEmpty(tableData.getName().trim()))) {
                    addDuplicateIndex(duplicateIndexesList, tableData);
                }
                // compairing with other streams in list
                if (duplicateRowNamesMap.get(tableData.getName()) != null) {
                    // name is duplicate
                    addDuplicateIndex(duplicateIndexesList, tableData);
                    addDuplicateIndex(duplicateIndexesList, duplicateRowNamesMap.get(tableData.getName()));
                } else {
                    // add existing names in imported table
                    duplicateRowNamesMap.put(tableData.getName(), tableData);
                }
            }
        }

        highlightedRowFactory.getRowsToHighlight().setAll(duplicateIndexesList);
        boolean validNames = true;
        if (!duplicateIndexesList.isEmpty()) {
            validNames = false;
            Alert alert = Util.getAlert(Alert.AlertType.ERROR);
            alert.setContentText("Some packet names (highlighted in red) have the same names of exisiting packets !");
            alert.showAndWait();
        }
        return validNames;
    }

    /**
     * Add index to duplicate list
     * @param duplicateIndexesList
     * @param tableData
     */
    private void addDuplicateIndex(ObservableList<Integer> duplicateIndexesList,ImportPcapTableData tableData ){
        if(!duplicateIndexesList.contains(tableData.getIndex())){
            duplicateIndexesList.add(tableData.getIndex());
        }
    }
    
    /**
     * Build vm instructions for source/destination ipv4
     *
     * @param packetData
     * @return
     */
    public Map<String, Object> getVm(ImportPcapTableData packetData) {
        VMInstructionBuilder vmInstructionBuilder = new VMInstructionBuilder(packetData.hasVlan(), packetData.getPacketType().indexOf("UDP") != -1);
        ArrayList<Object> instructionsList = new ArrayList<>();

        if (propertiesBinder.isDestinationEnabled()) {
            instructionsList.addAll(vmInstructionBuilder.addVmInstruction(getInstructionType(packetData, propertiesBinder.getDstAddress()),
                    propertiesBinder.getDstMode(), propertiesBinder.getDstCount(), "1", propertiesBinder.getDstAddress()));
        }
        if (propertiesBinder.isSourceEnabled()) {
            instructionsList.addAll(vmInstructionBuilder.addVmInstruction(getInstructionType(packetData, propertiesBinder.getSrcAddress()),
                    propertiesBinder.getSrcMode(), propertiesBinder.getSrcCount(), "1", propertiesBinder.getSrcAddress()));
        }

        // add ipv4 checksum instructions
        instructionsList.addAll(vmInstructionBuilder.addChecksumInstruction());

        Map<String, Object> additionalProperties = new HashMap<>();

        LinkedHashMap<String, Object> vmBody = new LinkedHashMap<>();
        vmBody.put("split_by_var", vmInstructionBuilder.getSplitByVar());
        vmBody.put("instructions", instructionsList);

        // add cache size
        vmInstructionBuilder.addCacheSize(vmBody);

        additionalProperties.put("vm", vmBody);

        return additionalProperties;
    }

    /**
     * Return instruction type according to place of selected address
     *
     * @param packetData
     * @param ipAddress
     * @return
     */
    private InstructionType getInstructionType(ImportPcapTableData packetData, String ipAddress) {
        if (ipAddress.equals(packetData.getIpSrc())) {
            return InstructionType.IP_SRC;
        }
        return InstructionType.IP_DST;
    }

    /**
     * Define ISG/PPS values
     *
     * @param profile
     * @param ipg
     */
    private void defineISG_PPSValues(Profile profile, double ipg) {
        profile.getStream().setIsg(ipg * 1000);
        if (ipg == 0) {
            ipg = 1;
        }
        double ppsValue = Math.max(1.0, 1.0/ipg);
        profile.getStream().getMode().getRate().setValue(ppsValue);
    }

    /**
     * Calculate and return IPG value
     *
     * @param diffTimestamp
     * @param firstStream
     * @return
     */
    private double getIpg(long diffTimestamp, boolean firstStream) {
        if (propertiesBinder.isIPGSelected()) {
            return propertiesBinder.getIpg();

        } else if (firstStream) {
            return 1;
        } else {
            double ipg_usage = (double) diffTimestamp / 1000;
            double ipg = ipg_usage / propertiesBinder.getSpeedup();
            return ipg;
        }
    }

    /**
     * HighlightedRowFactory factory implementation
     *
     * @param <T>
     */
    public class HighlightedRowFactory<T> implements Callback<TableView<T>, TableRow<T>> {

        ObservableList<Integer> rowsToHighlight;

        public HighlightedRowFactory(ObservableList<Integer> rowsToHighlight) {
            this.rowsToHighlight = rowsToHighlight;
        }

        public ObservableList<Integer> getRowsToHighlight() {
            return rowsToHighlight;
        }

        @Override
        public TableRow call(TableView param) {
            TableRow row = new TableRow();

            row.itemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    updateRowStyle(row);
                }
            });

            rowsToHighlight.addListener(new ListChangeListener<Integer>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Integer> c) {
                    updateRowStyle(row);
                }
            });

            return row;
        }

        /**
         * Update row higlighted style
         *
         * @param row
         */
        private void updateRowStyle(TableRow row) {
            if (row != null && row.getItem() != null) {
                row.getStyleClass().remove("highlightedRow");
                int index = ((ImportPcapTableData) row.getItem()).getIndex();

                if (rowsToHighlight.contains(index)) {
                    row.getStyleClass().add("highlightedRow");
                }
            }
        }
    }
}

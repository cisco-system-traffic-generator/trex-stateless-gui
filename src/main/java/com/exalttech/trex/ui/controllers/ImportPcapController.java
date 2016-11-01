/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.components.TextFieldTableViewCell;
import com.exalttech.trex.ui.components.TextFieldTableViewCell.EnteredValueHandler;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.models.PacketInfo;
import com.exalttech.trex.ui.views.models.ImportPcapTableData;
import com.exalttech.trex.ui.views.streams.builder.PacketBuilderHelper;
import com.exalttech.trex.ui.views.streams.viewer.PacketParser;
import com.exalttech.trex.util.TrafficProfile;
import com.exalttech.trex.util.Util;
import java.io.EOFException;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

/**
 * FXML Controller class
 *
 * @author GeorgeKH
 */
public class ImportPcapController extends DialogView implements Initializable, EnteredValueHandler {

    private static final Logger LOG = Logger.getLogger(ImportPcapController.class.getName());

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
    ObservableList<Integer> duplicateRowNames = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        trafficProfile = new TrafficProfile();
        highlightedRowFactory = new HighlightedRowFactory<>(highlightRows);
        initTableRowsColumns();

    }

    /**
     * Load pcap file
     *
     * @param pcapFile
     * @param profilesList
     * @param yamlFileName
     */
    public void loadPcap(File pcapFile, List<Profile> profilesList, String yamlFileName) {
        this.profilesList = profilesList;
        this.yamlFileName = yamlFileName;
        extractExistingNames();
        parsePcapFile(pcapFile);
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
        nameColumn.setCellFactory(new TextFieldTableViewCell(this));

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
     */
    private void parsePcapFile(File pcapFile) {
        List<PacketInfo> packetInfoList = new ArrayList<>();
        try {
            PcapHandle handler = Pcaps.openOffline(pcapFile.getAbsolutePath());
            PacketParser parser = new PacketParser();
            Packet packet;
            while ((packet = handler.getNextPacketEx()) != null) {
                PacketInfo packetInfo = new PacketInfo();
                packetInfo.setPacket(packet);
                parser.parsePacket(packet, packetInfo);
                packetInfoList.add(packetInfo);
            }

        } catch (EOFException e) {
            LOG.info("End of pcap file");
        } catch (Exception ex) {
            LOG.error("Error parsing selectd pcap file", ex);
        }
        setTableData(packetInfoList);
    }

    /**
     * Set table data
     *
     * @param packetInfoList
     */
    private void setTableData(List<PacketInfo> packetInfoList) {

        int index = 1;
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
            tableDataList.add(tableData);
            index++;
        }
        importedStreamTable.setItems(tableDataList);
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        // nothing to do
    }

    /**
     * Handle cancel button clicked
     *
     * @param event
     */
    @FXML
    public void handleCancelButtonClicked(ActionEvent event) {
        closeDialog();
    }

    /**
     * Handle save button clicked
     *
     * @param event
     */
    @FXML
    public void handleSaveButtonClicked(ActionEvent event) {
        try {
            if (validateStreamNames()) {
                for (ImportPcapTableData tableData : tableDataList) {
                    if (tableData.isSelected()) {
                        Profile profile = new Profile();
                        profile.setName(tableData.getName());
                        profile.getStream().getMode().setType("continuous");
                        String hexDataString = PacketBuilderHelper.getPacketHex(tableData.getPacket().getRawData());
                        profile.getStream().getPacket().setBinary(trafficProfile.encodeBinaryFromHexString(hexDataString));
                        profilesList.add(profile);
                    }
                }

                // save yaml data
                String yamlData = trafficProfile.convertTrafficProfileToYaml(profilesList.toArray(new Profile[profilesList.size()]));
                FileUtils.writeStringToFile(new File(yamlFileName), yamlData);

                // close dialog
                closeDialog();
            }
        } catch (Exception ex) {
            LOG.error("Error saving Yaml file", ex);
        }
    }

    /**
     * Validate stream names
     *
     * @return
     */
    private boolean validateStreamNames() {
        boolean validNames = true;
        ObservableList<Integer> errorRows = FXCollections.observableArrayList();
        // validate saved stream names
        for (ImportPcapTableData tableData : tableDataList) {
            if (tableData.isSelected() && existingNamesList.contains(tableData.getName())) {
                validNames = false;
                errorRows.add(tableData.getIndex());
            }
        }

        // validate current list names
        if (!duplicateRowNames.isEmpty()) {
            errorRows.addAll(duplicateRowNames);
            validNames = false;
        }
        highlightedRowFactory.getRowsToHighlight().setAll(errorRows);
        if (!validNames) {
            Alert alert = Util.getAlert(Alert.AlertType.ERROR);
            alert.setContentText("Some packet names (highlighted in red) have the same names of exisiting packets !");
            alert.showAndWait();
        }
        return validNames;
    }

    /**
     * Close import dialog
     */
    private void closeDialog() {
        Stage currentStage = (Stage) importedStreamTable.getScene().getWindow();
        currentStage.fireEvent(new WindowEvent(currentStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Validate entered text
     *
     * @param item
     */
    @Override
    public void validateEnteredValue(Object item) {
        if (item != null) {
            ImportPcapTableData updatedRow = (ImportPcapTableData) item;
            if (duplicateRowNames.contains(updatedRow.getIndex())) {
                int index = duplicateRowNames.indexOf(updatedRow.getIndex());
                duplicateRowNames.remove(index);
            }
            for (ImportPcapTableData tableDataRow : tableDataList) {
                if (updatedRow.getName().equals(tableDataRow.getName()) && updatedRow.getIndex() != tableDataRow.getIndex()) {
                    if (!duplicateRowNames.contains(updatedRow.getIndex())) {
                        duplicateRowNames.add(updatedRow.getIndex());
                    }

                }
            }
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

        ;
            /**
             * Update row higlighted style
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

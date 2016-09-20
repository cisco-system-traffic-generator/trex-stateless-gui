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
package com.exalttech.trex.ui.views;

import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.StreamBuilderType;
import com.exalttech.trex.ui.components.CheckBoxTableViewCell;
import com.exalttech.trex.ui.components.CheckBoxTableViewCell.CheckBoxTableChangeHandler;
import com.exalttech.trex.ui.controllers.PacketBuilderHomeController;
import com.exalttech.trex.ui.controllers.ProfileStreamNameDialogController;
import com.exalttech.trex.ui.dialog.DialogWindow;
import com.exalttech.trex.ui.views.models.TableProfile;
import com.exalttech.trex.ui.views.models.TableProfileStream;
import com.exalttech.trex.ui.views.streamtable.StreamTableAction;
import com.exalttech.trex.ui.views.streamtable.StreamTableButton;
import com.exalttech.trex.util.TrafficProfile;
import com.exalttech.trex.util.Util;
import com.exalttech.trex.util.files.FileManager;
import com.exalttech.trex.util.files.FileType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.DataLinkType;

/**
 * Stream table view implementation
 *
 * @author Georgekh
 */
public class PacketTableView extends AnchorPane implements EventHandler<ActionEvent>, CheckBoxTableChangeHandler {

    private static final Logger LOG = Logger.getLogger(PacketTableView.class.getName());

    StreamTableButton addPacketBtn;
    StreamTableButton buildPacketBtn;
    StreamTableButton editPacketBtn;
    StreamTableButton deleteButtonBtn;
    StreamTableButton exportPcapButton;
    StreamTableButton exportToYaml;
    TableView<TableProfileStream> streamPacketTableView;

    double maxHight;

    TableProfile tabledata;
    private boolean streamEditingWindowOpen;
    PacketTableUpdatedHandler tableUpdateHandler;
    TrafficProfile trafficProfile;
    final KeyCombination copyCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
    final KeyCombination pasteCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
    private Profile selectedProfile = null;
    private String profileName;
    int numOfStreamLoaded = 0;
    int numOfEnabledStream = 0;
    private boolean doUpdate = false;
    ContextMenu rightClickMenu;

    /**
     * @param maxHight
     * @param tableUpdateHandler
     * @param addExportToYamlBtn
     */
    public PacketTableView(double maxHight, PacketTableUpdatedHandler tableUpdateHandler, boolean addExportToYamlBtn) {
        this.maxHight = maxHight;
        this.tableUpdateHandler = tableUpdateHandler;
        trafficProfile = new TrafficProfile();
        buildUI(addExportToYamlBtn);
    }

    /**
     *
     * @param maxHight
     */
    public PacketTableView(double maxHight) {
        this(maxHight, null, false);
    }

    /**
     * Build component UI
     */
    private void buildUI(boolean addExportToYamlBtn) {
        setTopAnchor(this, 0d);
        setLeftAnchor(this, 0d);
        setBottomAnchor(this, 0d);
        setRightAnchor(this, 0d);
        // build btn bar
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(5);
        addPacketBtn = new StreamTableButton(StreamTableAction.ADD);
        initializeStreamButtons(addPacketBtn, false);
        buttonContainer.getChildren().add(addPacketBtn);

        // add build stream btn
        buildPacketBtn = new StreamTableButton(StreamTableAction.BUILD);
        initializeStreamButtons(buildPacketBtn, false);
        buttonContainer.getChildren().add(buildPacketBtn);

        editPacketBtn = new StreamTableButton(StreamTableAction.EDIT);
        initializeStreamButtons(editPacketBtn, true);
        buttonContainer.getChildren().add(editPacketBtn);

        deleteButtonBtn = new StreamTableButton(StreamTableAction.DELETE);
        initializeStreamButtons(deleteButtonBtn, true);
        buttonContainer.getChildren().add(deleteButtonBtn);

        exportPcapButton = new StreamTableButton(StreamTableAction.EXPORT_TO_PCAP);
        initializeStreamButtons(exportPcapButton, true);
        buttonContainer.getChildren().add(exportPcapButton);

        if (addExportToYamlBtn) {
            exportToYaml = new StreamTableButton(StreamTableAction.EXPORT_TO_YAML);
            initializeStreamButtons(exportToYaml, false);
            buttonContainer.getChildren().add(exportToYaml);
        }
        getChildren().add(buttonContainer);
        setTopAnchor(buttonContainer, 5d);

        rightClickMenu = new ContextMenu();
        addMenuItem(StreamTableAction.EDIT);
        addMenuItem(StreamTableAction.DELETE);
        addMenuItem(StreamTableAction.EXPORT_TO_PCAP);
        addMenuItem(StreamTableAction.EXPORT_TO_YAML);

        // add table view
        streamPacketTableView = new TableView<>();

        streamPacketTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        streamPacketTableView.setFixedCellSize(32);

        streamPacketTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleTableRowClick(event);
            }
        });

        streamPacketTableView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TableProfileStream> observable, TableProfileStream oldValue, TableProfileStream newValue) -> {
            boolean notSelected = !(newValue != null);

            exportPcapButton.setDisable(notSelected);
            editPacketBtn.setDisable(notSelected);
            deleteButtonBtn.setDisable(notSelected);
        });

        streamPacketTableView.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (copyCombination.match(event)) {
                selectedProfile = tabledata.getProfiles().get(streamPacketTableView.getSelectionModel().getSelectedIndex());
            } else if (pasteCombination.match(event)) {
                handleDuplicateStream();
            } else if (event.getCode() == KeyCode.DELETE) {
                handleDeletePacket();
            }
        });
        streamPacketTableView.setEditable(true);
        initializeTableColumn();
        // disable table reordering
        streamPacketTableView.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) {
                TableHeaderRow header = (TableHeaderRow) streamPacketTableView.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });

        getChildren().add(streamPacketTableView);
        setTopAnchor(streamPacketTableView, 35d);
        setBottomAnchor(streamPacketTableView, 5d);
        setLeftAnchor(streamPacketTableView, 0d);
        setRightAnchor(streamPacketTableView, 0d);
    }

    /**
     * Initialize stream action buttons
     *
     * @param button
     * @param disable
     */
    private void initializeStreamButtons(Button button, boolean disable) {
        button.setDisable(disable);
        button.setOnAction(this);
    }

    /**
     * Handle stream button click event
     *
     * @param event
     */
    @Override
    public void handle(ActionEvent event) {
        StreamTableButton source = (StreamTableButton) event.getSource();
        handleStreamTableAction(source.getButtonActionType());
    }

    /**
     * Handle stream table action
     *
     * @param action
     */
    private void handleStreamTableAction(StreamTableAction action) {
        switch (action) {
            case ADD:
                viewStreamNameWindow(StreamBuilderType.ADD_STREAM);
                break;
            case BUILD:
                viewStreamNameWindow(StreamBuilderType.BUILD_STREAM);
                break;
            case EDIT:
                handleEditPacket();
                break;
            case DELETE:
                handleDeletePacket();
                break;
            case EXPORT_TO_PCAP:
                handleExportPcapFile();
                break;
            case EXPORT_TO_YAML:
                handleExportToYaml();
                break;
        }
    }

    /**
     * Handle Add packet button clicked
     *
     * @param streamName
     * @param type
     */
    public void handleAddPacket(String streamName, StreamBuilderType type) {
        doUpdate = false;
        TableProfileStream newRow = new TableProfileStream();
        newRow.setName(streamName);
        tabledata.getStreamsList().add(newRow);
        Profile newProfile = new Profile();
        newProfile.setName(streamName);
        tabledata.getProfiles().add(newProfile);
        streamPacketTableView.setItems(FXCollections.observableArrayList(tabledata.getStreamsList()));
        streamPacketTableView.getSelectionModel().select(newRow);
        openStreamDialog(type);
    }

    /**
     * Handle edit button clicked
     */
    public void handleEditPacket() {
        openStreamDialog(StreamBuilderType.EDIT_STREAM);
    }

    /**
     * Handle delete button clicked
     */
    public void handleDeletePacket() {
        try {
            int selectedIndex = streamPacketTableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex > -1 && Util.isConfirmed("Are you sure you want to delete this stream?")) {
                tabledata.getStreamsList().remove(selectedIndex);
                tabledata.getProfiles().remove(selectedIndex);
                saveChangesToYamlFile(tabledata.getProfiles().toArray(new Profile[tabledata.getProfiles().size()]));
                if (tableUpdateHandler != null) {
                    tableUpdateHandler.onStreamTableChanged();
                }
            }
        } catch (IOException ex) {
            LOG.error("Error deleting stream", ex);
        }
    }

    /**
     * Export stream to pcap file
     */
    public void handleExportPcapFile() {
        try {
            Profile p = tabledata.getProfiles().get(streamPacketTableView.getSelectionModel().getSelectedIndex());
            String packetBinary = p.getStream().getPacket().getBinary();

            byte[] pkt = Base64.decodeBase64(packetBinary);
            Packet packet = EthernetPacket.newPacket(pkt, 0, pkt.length);
            File pcapFile = File.createTempFile("temp-file-name", ".pcap");

            PcapHandle handle = Pcaps.openDead(DataLinkType.EN10MB, 65536);
            PcapDumper dumper = handle.dumpOpen(pcapFile.getAbsolutePath());
            Timestamp ts = new Timestamp(0);
            dumper.dump(packet, ts);
            dumper.close();
            handle.close();

            String fileName = p.getName() + ".pcap";
            Window owner = streamPacketTableView.getScene().getWindow();
            FileManager.exportFile("Save Pcap File", fileName, pcapFile, owner, FileType.PCAP);
        } catch (IllegalRawDataException | IOException | PcapNativeException | NotOpenException ex) {
            LOG.error("Error during generate JSON file", ex);
        }
    }

    /**
     * Handle duplicate stream
     */
    public void handleDuplicateStream() {
        try {
            if (selectedProfile != null) {
                Profile clonedProfile = (Profile) selectedProfile.clone();
                clonedProfile.setName(selectedProfile.getName() + "_" + Util.getRandomNumericID(3));
                tabledata.getProfiles().add(clonedProfile);

                Profile[] newProfileDataList = tabledata.getProfiles().toArray(new Profile[tabledata.getProfiles().size()]);
                tabledata.setStreamsList(trafficProfile.convertProfilesToTableData(newProfileDataList));
                saveChangesToYamlFile(newProfileDataList);
            }
        } catch (CloneNotSupportedException | IOException ex) {
            LOG.error("Error duplicationg stream", ex);
        }
    }

    /**
     * Save profile changes to yaml file
     *
     * @param profiles
     * @throws JsonProcessingException
     * @throws IOException
     */
    private void saveChangesToYamlFile(Profile[] profiles) throws JsonProcessingException, IOException {
        String yamlData = trafficProfile.convertTrafficProfileToYaml(profiles);
        FileUtils.writeStringToFile(new File(tabledata.getYamlFileName()), yamlData);
        streamPacketTableView.setItems(FXCollections.observableArrayList(tabledata.getStreamsList()));
        if (tableUpdateHandler != null && doUpdate) {
            tableUpdateHandler.onStreamUpdated();
        }
    }

    /**
     * initialize packet table columns
     */
    private void initializeTableColumn() {

        // add enable column
        streamPacketTableView.getColumns().add(createStaticTableColumn("", "enabledProperty", 30, true));

        // index column
        streamPacketTableView.getColumns().add(createStaticTableColumn("Index", "indexProperty", 50, false));

        streamPacketTableView.getColumns().add(createTableColumn("Name", "nameProperty", .17));
        streamPacketTableView.getColumns().add(createTableColumn("Packet Type", "packetTypeProperty", .25));
        TableColumn lengthColumn = createTableColumn("Length", "lengthProperty", .115);
        lengthColumn.setId("alignedColumn");
        streamPacketTableView.getColumns().add(lengthColumn);
        streamPacketTableView.getColumns().add(createTableColumn("Mode", "modeProperty", .175));
        TableColumn rateColumn = createTableColumn("Rate", "rateProperty", .115);
        rateColumn.setId("alignedColumn");
        streamPacketTableView.getColumns().add(rateColumn);

        TableColumn nextStreamCol = createTableColumn("Next Stream", "nextStreamProperty", .174);
        nextStreamCol.setCellFactory(new ImageCellFactory());
        streamPacketTableView.getColumns().add(nextStreamCol);

    }

    /**
     * Create and return table column
     *
     * @param title
     * @param propertyName
     * @param width
     * @return
     */
    private TableColumn createTableColumn(String title, String propertyName, double width) {
        TableColumn col = new TableColumn(title);
        col.prefWidthProperty().bind((streamPacketTableView.widthProperty().subtract(86)).multiply(width));
        col.setResizable(false);
        col.setSortable(false);
        col.setEditable(false);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        return col;
    }

    /**
     * Create and return static width table column
     *
     * @param title
     * @param propertyName
     * @param width
     * @param hasCheckbox
     * @return
     */
    private TableColumn createStaticTableColumn(String title, String propertyName, double width, boolean hasCheckbox) {
        TableColumn col = new TableColumn(title);
        col.setPrefWidth(width);
        col.setResizable(false);
        col.setEditable(false);
        col.setSortable(false);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        if (hasCheckbox) {
            col.setEditable(true);
            col.setCellFactory(new CheckBoxTableViewCell(this));
        }
        return col;
    }

    /**
     * Return table packet data
     *
     * @return
     */
    public List<TableProfileStream> getPacketDataList() {
        return tabledata.getStreamsList();
    }

    /**
     * Set table packet data
     *
     * @param tableData
     */
    public void setPacketData(TableProfile tableData) {
        this.tabledata = tableData;
        numOfStreamLoaded = 0;
        doUpdate = false;
        updateNumberOfEnabledStream();
        streamPacketTableView.setItems(FXCollections.observableArrayList(tableData.getStreamsList()));
    }

    private void updateNumberOfEnabledStream() {
        numOfEnabledStream = 0;
        for (Profile profile : tabledata.getProfiles()) {
            if (profile.getStream().isEnabled()) {
                numOfEnabledStream++;
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isStreamEditingWindowOpen() {
        return streamEditingWindowOpen;
    }

    /**
     *
     * @param streamEditingWindowOpen
     */
    public void setStreamEditingWindowOpen(boolean streamEditingWindowOpen) {
        this.streamEditingWindowOpen = streamEditingWindowOpen;
    }

    /**
     * Handle table row click event
     *
     * @param event
     */
    public void handleTableRowClick(MouseEvent event) {
        streamPacketTableView.setContextMenu(null);
        if (event.getClickCount() == 2 && streamPacketTableView.getSelectionModel().getSelectedItem() != null) {
            openStreamDialog(StreamBuilderType.EDIT_STREAM);
        } else if (event.getButton() == MouseButton.SECONDARY && streamPacketTableView.getSelectionModel().getSelectedItem() != null) {
            streamPacketTableView.setContextMenu(rightClickMenu);
        }
    }

    /**
     * open stream properties
     */
    private void openStreamDialog(StreamBuilderType type) {
        try {
            TableProfileStream data = streamPacketTableView.getSelectionModel().getSelectedItem();
            if ("0".equals(data.getLength()) && !Util.isConfirmed("Problem reading file, Do you want to continue anyway ?")) {
                return;
            }
            setStreamEditingWindowOpen(true);
            Stage currentStage = (Stage) streamPacketTableView.getScene().getWindow();
            String windowTitle = "Edit Stream (" + data.getName() + ")";
            DialogWindow srteamWindow = new DialogWindow("PacketBuilderHome.fxml", windowTitle, 40, 30, false, currentStage);
            PacketBuilderHomeController controller = (PacketBuilderHomeController) srteamWindow.getController();
            switch (type) {
                case EDIT_STREAM:
                    controller.initStreamBuilder(data.getPcapBinary(), tabledata.getProfiles(), streamPacketTableView.getSelectionModel().getSelectedIndex(), tabledata.getYamlFileName(), StreamBuilderType.EDIT_STREAM);
                    break;
                case ADD_STREAM:
                    controller.initStreamBuilder(null, tabledata.getProfiles(), streamPacketTableView.getSelectionModel().getSelectedIndex(), tabledata.getYamlFileName(), StreamBuilderType.ADD_STREAM);
                    break;
                case BUILD_STREAM:
                    controller.initStreamBuilder(null, tabledata.getProfiles(), streamPacketTableView.getSelectionModel().getSelectedIndex(), tabledata.getYamlFileName(), StreamBuilderType.BUILD_STREAM);
                    break;
                default:
                    break;
            }

            srteamWindow.show(true);
        } catch (IOException ex) {
            LOG.error("Error opening file", ex);
        }
    }

    /**
     * reset table data
     */
    public void reset() {
        tabledata = null;
        streamPacketTableView.getItems().clear();
    }

    /**
     * View stream name window
     *
     * @param type
     */
    private void viewStreamNameWindow(StreamBuilderType type) {
        try {
            Stage currentStage = (Stage) streamPacketTableView.getScene().getWindow();
            DialogWindow profileNameWindow = new DialogWindow("ProfileStreamNameDialog.fxml", "Add Stream", 150, 100, false, currentStage);
            ProfileStreamNameDialogController controller = (ProfileStreamNameDialogController) profileNameWindow.getController();
            controller.setProfileList(tabledata.getProfiles());
            controller.setProfileWindow(false);
            profileNameWindow.show(true);
            if (controller.isDataAvailable()) {
                String streamName = controller.getName();
                handleAddPacket(streamName, type);
            }

        } catch (IOException ex) {
            LOG.error("Error adding new stream", ex);
        }
    }

    /**
     * Load stream table data
     *
     * @param fileToLoad
     * @return
     * @throws Exception
     */
    public Profile[] loadStreamTable(File fileToLoad) throws Exception {
        doUpdate = false;
        Profile[] profileList;
        this.profileName = fileToLoad.getName();
        try {
            profileList = trafficProfile.getTrafficProfile(fileToLoad);
        } catch (IOException ex) {
            LOG.warn("Profile does not have any streams", ex);
            profileList = new Profile[0];
        }
        List<TableProfileStream> packetDataList = trafficProfile.convertProfilesToTableData(profileList);
        TableProfile tableData = new TableProfile(new ArrayList<>(Arrays.asList(profileList)), packetDataList, fileToLoad.getPath());
        setPacketData(tableData);
        return profileList;
    }

    /**
     * Export current load profile to yaml
     */
    private void handleExportToYaml() {
        Window owner = this.getScene().getWindow();
        Profile[] profilesList = tabledata.getProfiles().toArray(new Profile[tabledata.getProfiles().size()]);
        trafficProfile.exportProfileToYaml(owner, profilesList, profileName);
    }

    /**
     *
     * @param index
     * @param newValue
     */
    @Override
    public void stateChanged(int index, boolean newValue) {
        try {
            if (++numOfStreamLoaded > numOfEnabledStream) {
                doUpdate = true;
            }
            tabledata.getProfiles().get(index).getStream().setEnabled(newValue);
            saveChangesToYamlFile(tabledata.getProfiles().toArray(new Profile[tabledata.getProfiles().size()]));
        } catch (IOException ex) {
            LOG.error("Error updating profile", ex);
        }
    }

    /**
     * Add menu item to table rightclickMenu
     *
     * @param action
     */
    private void addMenuItem(StreamTableAction action) {
        MenuItem item = new MenuItem(action.getTitle());
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleStreamTableAction(action);
            }
        });
        rightClickMenu.getItems().add(item);
    }

    /**
     * Class present cell with image factory
     */
    private class ImageCellFactory implements Callback<TableColumn, TableCell> {

        @Override
        public TableCell call(TableColumn p) {
            return new TableCell<Object, String>() {
                private final ImageView imageview;

                {
                    imageview = new ImageView();
                    imageview.setFitHeight(16);
                    imageview.setFitWidth(16);
                    imageview.setCache(true);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        imageview.setImage(new Image(item));
                        setGraphic(imageview);
                    }
                }
            };
        }
    }
}

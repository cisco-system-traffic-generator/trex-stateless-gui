/**
 * *****************************************************************************
 * Copyright (c) 2016
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package com.exalttech.trex.ui.controllers;

import com.cisco.trex.stateless.TRexClient;
import com.cisco.trex.stl.gui.storages.StatsStorage;
import com.cisco.trex.stl.gui.util.RunningConfiguration;
import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.core.AsyncResponseManager;
import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.core.TrexEvent;
import com.exalttech.trex.remote.exceptions.IncorrectRPCMethodException;
import com.exalttech.trex.remote.exceptions.InvalidRPCResponseException;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.remote.exceptions.TrafficException;
import com.exalttech.trex.remote.models.profiles.FlowStats;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.remote.models.validate.StreamValidation;
import com.exalttech.trex.ui.MultiplierType;
import com.exalttech.trex.ui.PortManagerEventHandler;
import com.exalttech.trex.ui.PortState;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.components.CustomTreeItem;
import com.exalttech.trex.ui.components.CustomTreeItem.TreeItemType;
import com.exalttech.trex.ui.components.NotificationPanel;
import com.exalttech.trex.ui.controllers.ports.PortView;
import com.exalttech.trex.ui.dialog.DialogManager;
import com.exalttech.trex.ui.dialog.DialogWindow;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.ui.models.SystemInfoReq;
import com.exalttech.trex.ui.models.datastore.Connection;
import com.exalttech.trex.ui.models.datastore.ConnectionsWrapper;
import com.exalttech.trex.ui.util.AlertUtils;
import com.exalttech.trex.ui.views.MultiplierOptionChangeHandler;
import com.exalttech.trex.ui.views.MultiplierView;
import com.exalttech.trex.ui.views.PacketTableUpdatedHandler;
import com.exalttech.trex.ui.views.PacketTableView;
import com.exalttech.trex.ui.views.logs.LogType;
import com.exalttech.trex.ui.views.logs.LogsController;
import com.exalttech.trex.ui.views.models.AssignedProfile;
import com.exalttech.trex.ui.views.models.ProfileMultiplier;
import com.exalttech.trex.ui.views.services.CountdownService;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.ProfileManager;
import com.exalttech.trex.util.Util;
import com.exalttech.trex.util.files.XMLFileManager;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xored.javafx.packeteditor.events.InitPacketEditorEvent;
import com.xored.javafx.packeteditor.events.ScapyClientNeedConnectEvent;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Main view FXML controller
 *
 * @author Georgekh
 */
public class MainViewController implements Initializable, EventHandler<KeyEvent>,
    MultiplierOptionChangeHandler, PortManagerEventHandler, PacketTableUpdatedHandler {

    private static final Logger LOG = Logger.getLogger(MainViewController.class.getName());
    private final RPCMethods serverRPCMethods = TrexApp.injector.getInstance(RPCMethods.class);
    private final RunningConfiguration runningConfiguration = TrexApp.injector.getInstance(RunningConfiguration.class);
    private static final String DISABLED_MULTIPLIER_MSG = "Multiplier is disabled because all streams have latency enabled";

    @FXML
    TreeView devicesTree;
    @FXML
    ScrollPane statTableContainer;
    @FXML
    AnchorPane statTableWrapper;
    @FXML
    AnchorPane profileContainer;
    @FXML
    ComboBox<String> profileListBox;
    @FXML
    Label profileDetailLabel;
    @FXML
    Label disableProfileNote;
    @FXML
    AnchorPane profileDetailContainer;
    @FXML
    AnchorPane profileTableViewContainer;
    @FXML
    AnchorPane mainViewContainer;
    @FXML
    MenuItem connectMenuItem;
    @FXML
    Label serverStatusLabel;
    @FXML
    ImageView serverStatusIcon;
    @FXML
    MenuItem statsMenuItem;
    @FXML
    MenuItem captureMenuItem;

    @FXML
    AnchorPane multiplierOptionContainer;
    @FXML
    Pane notificationPanelHolder;

    @FXML
    Button updateBtn;
    @FXML
    Button stopUpdateBtn;
    @FXML
    Button newProfileBtn;
    @FXML
    Button duplicateProfileBtn;


    @FXML
    AnchorPane logContainer;

    @FXML
    Label startStream;
    @FXML
    Label startAllStream;

    @FXML
    Label stopStream;
    @FXML
    Label stopAllStream;
    @FXML
    Label pauseStream;
    @FXML
    Label acquirePort;
    @FXML
    Label releasePort;
    @FXML
    Label connectIcon;
    @FXML
    Label clearCache;

    @FXML
    Label countdownValue;
    @FXML
    TabPane logsContainer;
    @FXML
    Tab consoleLogTab;
    @FXML
    Tab logTab;
    @FXML
    Button copyToClipboardBtn;
    @FXML
    Label dashboardIcon;
    @FXML
    Tooltip connectDixconnectTooltip;
    @FXML
    ImageView devicesTreeArrowContainer;
    @FXML
    SplitPane mainViewSplitPanel;

    @FXML
    PortView portView;

    @FXML
    Label serviceModeLabel;

    BooleanProperty portViewVisibilityProperty = new SimpleBooleanProperty(false);
    BooleanProperty systemInfoVisibilityProperty = new SimpleBooleanProperty(true);

    private Optional<ContextMenu> rightClickDevicesTreeMenu = Optional.empty();
    private ContextMenu rightClickPortMenu;
    private ContextMenu rightClickProfileMenu;
    private ContextMenu rightClickGlobalMenu;

    private SystemInfoReq systemInfoReq = null;
    private PacketTableView tableView;
    private RefreshingService refreshStatsService = new RefreshingService();
    private final Map<Integer, CustomTreeItem> portTreeItemMap = new HashMap<>();
    private final BooleanProperty updateProfileListProperty = new SimpleBooleanProperty();
    private final Map<Integer, AssignedProfile> assignedPortProfileMap = new HashMap<>();

    private Profile[] loadedProfiles;
    private String currentSelectedProfile;
    private MultiplierView multiplierView;
    private NotificationPanel notificationPanel;
    boolean needReassignProfile = false;
    private CountdownService countdownService;
    private PortsManager portManager;
    private final BooleanProperty disableProfileProperty = new SimpleBooleanProperty();
    StatsTableGenerator statsTableGenerator;
    private boolean allStreamWithLatency;
    private boolean isFirstPortStatusRequest = true;
    private static final String DISCONNECT_MENU_ITEM_TITLE = "Disconnect";
    private static final String CONNECT_MENU_ITEM_TITLE = "Connect";

    private int lastSelectedPortIndex = -1;
    private int previousSelectedPortIndex = -1; //TODO reorganize usage of this field
    private boolean profileLoaded = false;

    private Image leftArrow;
    private Image rightArrow;
    private boolean treeviewOpened = true;

    private EventBus eventBus;
    private boolean resetAppInProgress;
    private BooleanProperty trafficProfileLoadedProperty = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        portManager = PortsManager.getInstance();
        portManager.setPortManagerHandler(this);
        statsTableGenerator = new StatsTableGenerator();
        leftArrow = new Image("/icons/arrow_left.png");
        rightArrow = new Image("/icons/arrow_right.png");
        initializeInlineComponent();
        logsContainer.setDisable(false);
        eventBus = TrexApp.injector.getInstance(EventBus.class);
        portView.visibleProperty().bind(portViewVisibilityProperty);
        statTableContainer.visibleProperty().bindBidirectional(systemInfoVisibilityProperty);

        ConnectionManager.getInstance().addDisconnectListener(() -> resetApplication(true));
        // Handle update port state event
        AsyncResponseManager.getInstance().asyncEventObjectProperty().addListener((observable, oldValue, newVal) -> {

            switch (newVal.getType()) {
                case PORT_RELEASED:
                case PORT_ACQUIRED:
                case PORT_ATTR_CHANGED:
                case PORT_STARTED:
                case PORT_STOPPED:
                    final int portId = newVal.getData().getAsJsonPrimitive("port_id").getAsInt();
                    PortModel portModel = portManager.getPortModel(portId);

                    if (ConnectionManager.getInstance().isConnected()) {
                        Platform.runLater(() -> {
                            portManager.updatedPorts(Arrays.asList(portModel.getIndex()));
                            onPortListUpdated(true);
                        });
                    }
                    break;
                case SERVER_STOPPED:
                    resetApplication(true);
                    break;
            }
        });
    }

    /**
     * Handle connect menu item clicked
     *
     * @param event
     */
    @FXML
    public void handleConnectMenuItemClicked(ActionEvent event) {
        doConnectDisconnect();
    }

    /**
     * Handle connect button clicked
     *
     * @param event
     */
    @FXML
    public void handleConnectDisconnectBtnClicked(MouseEvent event) {
        doConnectDisconnect();
    }

    /**
     * Connect/Disconnect to TRex server
     */
    private void doConnectDisconnect() {
        if (ConnectionManager.getInstance().isConnected()) {
            resetApplication(false);
        } else {
            openConnectDialog();
        }
    }

    /**
     * Handle Exit menu item click
     *
     * @param event
     */
    @FXML
    public void handleExitMenuItemClick(ActionEvent event) {
        // release all port
        handleAppClose();
        System.exit(0);
    }

    /**
     * Open connect dialog
     */
    private void openConnectDialog() {
        try {
            DialogWindow connectWindow = new DialogWindow("ConnectDialog.fxml", "Connect", 300, 100, false, TrexApp.getPrimaryStage());
            connectWindow.show(true);
            if (ConnectionManager.getInstance().isConnected()) {
                serverRPCMethods.serverApiSync();

                loadSystemInfo();
                StatsLoader.getInstance().start();
                StatsStorage.getInstance().startPolling();
                portManager.updatePortForce();

                serverStatusLabel.setText("Connected");
                serverStatusIcon.setImage(new Image("/icons/connectedIcon.gif"));
                connectIcon.getStyleClass().add("disconnectIcon");
                connectDixconnectTooltip.setText("Disconnect from TRex server");
                connectMenuItem.setText(DISCONNECT_MENU_ITEM_TITLE);
                statsMenuItem.setDisable(false);
                captureMenuItem.setDisable(false);
                clearCache.setDisable(false);
                logsContainer.setDisable(false);
                copyToClipboardBtn.setDisable(false);
                dashboardIcon.setDisable(false);
            }
        } catch (IOException ex) {
            LOG.error("Error while Connecting", ex);
        }
    }

    /**
     * Run loading system info thread
     */
    private void loadSystemInfo() {
        LogsController.getInstance().appendText(LogType.INFO, "Loading port information");
        String data = ConnectionManager.getInstance().sendRequest("get_system_info", "");
        data = Util.removeFirstBrackets(data);
        systemInfoReq = (SystemInfoReq) Util.fromJSONString(data, SystemInfoReq.class);
        systemInfoReq.setIp(ConnectionManager.getInstance().getIPAddress());
        systemInfoReq.setPort(ConnectionManager.getInstance().getRpcPort());

        List<Port> ports = systemInfoReq.getResult().getPorts();
        Collections.sort(ports, (p1, p2) -> Integer.compare(p1.getIndex(), p2.getIndex()));
        portManager.setPortList(ports);

        String versionResponse = ConnectionManager.getInstance().sendRequest("get_version", "");
        Gson gson = new Gson();
        JsonObject version = gson.fromJson(versionResponse, JsonArray.class).get(0).getAsJsonObject();
        systemInfoReq.getResult().setApiVersion(version.getAsJsonObject("result").getAsJsonPrimitive("version").getAsString());
        LogsController.getInstance().appendText(LogType.INFO, "Loading port information complete");
    }

    /**
     * Open manage traffic profile dialog
     *
     * @param event
     * @throws IOException
     */
    @FXML
    public void openTrafficProfile(ActionEvent event) throws IOException {
        DialogWindow trafficWindow = new DialogWindow("TrafficProfileDialog.fxml", "Traffic Profiles", 10, 50, true, TrexApp.getPrimaryStage());
        TrafficProfileDialogController controller = (TrafficProfileDialogController) trafficWindow.getController();
        controller.init();
        trafficWindow.show(true);
    }

    /**
     * Load and render devices tree
     *
     * @throws UnsupportedEncodingException
     */
    private void updateDevicesTree() {

        if (devicesTree.getRoot() == null) {
            TreeItem root = new CustomTreeItem("TRex-" + ConnectionManager.getInstance().getIPAddress(), TreeItemType.DEVICES, rightClickGlobalMenu);
            root.setExpanded(true);
            portManager.getPortList().stream().forEach(port -> {
                // build port tree item
                root.getChildren().add(createPortItem(port));
            });
            devicesTree.setRoot(root);
            devicesTree.getSelectionModel().select(0);
        } else {
            portManager.getPortList().stream().forEach(port -> {
                if (portTreeItemMap.get(port.getIndex()) != null) {
                    CustomTreeItem item = portTreeItemMap.get(port.getIndex());
                    item.setTooltipText(port.getStatus());
                    item.updateItemValue("Port " + port.getIndex(), port.getOwner(), PortState.getPortStatus(port.getStatus()).getIcon());
                } else {
                    devicesTree.getRoot().getChildren().add(createPortItem(port));
                }
            });
        }
    }

    /**
     * Render port tree item
     *
     * @param port
     * @return
     */
    private TreeItem createPortItem(Port port) {
        CustomTreeItem root = new CustomTreeItem("Port " + port.getIndex(), port.getOwner(), PortState.getPortStatus(port.getStatus()).getIcon(), TreeItemType.PORT);
        root.setTooltipText(port.getStatus());
        root.setMenu(rightClickPortMenu);
        root.setReturnedValue(String.valueOf(port.getIndex()));
        portTreeItemMap.put(port.getIndex(), root);
        CustomTreeItem profileItem = new CustomTreeItem("Profile", null, null, TreeItemType.PORT_PROFILE);
        profileItem.setMenu(rightClickProfileMenu);
        profileItem.setReturnedValue(String.valueOf(port.getIndex()));
        root.getChildren().add(profileItem);
        return root;
    }

    /**
     * Handle tree item clicked
     *
     * @param mouseEvent
     */
    @FXML
    public void handleTreeClicked(MouseEvent mouseEvent) {
        rightClickDevicesTreeMenu.ifPresent(ContextMenu::hide);
        CustomTreeItem selected = (CustomTreeItem) devicesTree.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                updateContextMenuState();
                rightClickDevicesTreeMenu = Optional.ofNullable(selected.getMenu());
                rightClickDevicesTreeMenu.ifPresent(contextMenu -> {
                    contextMenu.show(devicesTree, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                });
            }
        }
    }

    /**
     * Handle treeitem selection changed
     */
    private void handleTreeItemSelectionChanged() {
        updateHeaderBtnStat();
        CustomTreeItem selected = (CustomTreeItem) devicesTree.getSelectionModel().getSelectedItem();
        // update acquire/release port icon state
        updateAcquireReleaseBtnState(true);
        if (selected != null) {

            if (profileLoaded) {
                updateCurrentProfile();
            }

            try {
                stopRefreshingService();
                // show table container by default
                hideShowStatTable(true);
                portViewVisibilityProperty.setValue(false);
                systemInfoVisibilityProperty.setValue(false);
                serviceModeLabel.visibleProperty().unbind();
                serviceModeLabel.setVisible(false);
                switch (selected.getTreeItemType()) {
                    case DEVICES:
                        systemInfoVisibilityProperty.setValue(true);
                        buildSystemInfoTable();
                        break;
                    case PORT:
                        updateAcquireReleaseBtnState(false);
                        loadPortModel();
                        portViewVisibilityProperty.setValue(true);
                        break;
                    case PORT_PROFILE:
                        viewProfile();
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                LOG.error(ex);
            }
        }
    }

    /**
     * Update current loaded profile
     */
    private void updateCurrentProfile() { //TODO get rid of this method and implement with tryUpdateProfile(...)
        profileLoaded = false;
        AssignedProfile assignedProf = assignedPortProfileMap.get(lastSelectedPortIndex);
        if (assignedProf != null) {
            assignedProf.setHasDuration(multiplierView.isDurationEnable());
            updateMultiplierValues(assignedProf);
        }
        if (isWaitingUpdate()) {
            tryUpdateProfile(false, true);
        }
    }

    /**
     * Update right click menu enabling state
     */
    private void updateContextMenuState() {
        int portId = getSelectedPortIndex();
        if (portId != -1) {
            Port port = portManager.getPortByIndex(portId);

            boolean isOwned = port.getOwner().equals(ConnectionManager.getInstance().getClientName());

            setDisableContextMenuItem("Acquire", !Util.isNullOrEmpty(port.getOwner()));
            setDisableContextMenuItem("Release Acquire", !isOwned);

            boolean enableServiceMenuItemDisabled = true;
            boolean disableServiceMenuItemDisabled = true;
            if (isOwned) {
                enableServiceMenuItemDisabled = port.getServiceMode();
                disableServiceMenuItemDisabled = !port.getServiceMode();
            }
            setDisableContextMenuItem("Enable Service Mode", enableServiceMenuItemDisabled);
            setDisableContextMenuItem("Disable Service Mode", disableServiceMenuItemDisabled);

        }
    }

    private void setDisableContextMenuItem(String item, boolean disable) {
        Optional<MenuItem> disableServiceModeOptional = getItemByName(rightClickPortMenu, item);
        disableServiceModeOptional.ifPresent(menuItem -> menuItem.setDisable(disable));
    }

    private Optional<MenuItem> getItemByName(ContextMenu menu, String text) {
        return menu.getItems()
            .stream()
            .filter(menuItem -> menuItem.getText().equalsIgnoreCase(text))
            .findFirst();
    }

    /**
     * Reset the application to initial state
     */
    private void resetApplication(boolean didServerCrash) {
        if (!didServerCrash) {
            releaseAllPort(false);
        }

        DialogManager.getInstance().closeAll();
        portManager.clearPorts();

        Platform.runLater(() -> {
            StatsStorage.getInstance().stopPolling();
            shutdownRunningServices();
            LogsController.getInstance().getView().clear();

            resetAppInProgress = true;
            profileListBox.getSelectionModel().select(Constants.SELECT_PROFILE);
            // clear tree
            devicesTree.setRoot(null);
            // hide all right side views
            statTableWrapper.setVisible(false);
            profileContainer.setVisible(false);
            serviceModeLabel.visibleProperty().unbind();
            serviceModeLabel.setVisible(false);

            connectMenuItem.setText(CONNECT_MENU_ITEM_TITLE);
            statsMenuItem.setDisable(true);
            captureMenuItem.setDisable(true);
            dashboardIcon.setDisable(true);
            serverStatusIcon.setImage(new Image("/icons/offline.png"));
            serverStatusLabel.setText("Disconnected");
            connectIcon.getStyleClass().remove("disconnectIcon");
            connectDixconnectTooltip.setText("Connect to TRex server");

            // reset Header btns
            startStream.setDisable(true);
            startAllStream.setDisable(true);
            stopStream.setDisable(true);
            stopAllStream.setDisable(true);
            pauseStream.setDisable(true);
            clearCache.setDisable(true);
            logsContainer.setDisable(false);
            copyToClipboardBtn.setDisable(true);
            acquirePort.setDisable(true);
            releasePort.setDisable(true);
            assignedPortProfileMap.clear();

            portViewVisibilityProperty.setValue(false);

            resetAppInProgress = false;

            ConnectionManager.getInstance().disconnect();

            if (didServerCrash) {
                AlertUtils.construct(
                    Alert.AlertType.ERROR,
                    "Disconnected from TRex",
                    "Disconnected from TRex server",
                    "Make sure TRex server is online and reachable.\n" +
                        "If you have a slow connection network connection you can change timeout options in the application connection settings.\n")
                    .showAndWait();

                openConnectDialog();
            }
        });
    }

    /**
     * Build Device table info
     */
    private void buildSystemInfoTable() {
        statTableContainer.setContent(statsTableGenerator.generateSystemInfoPane(systemInfoReq));
    }

    /**
     * Build port detail table
     */
    private void loadPortModel() {
        PortModel model = portManager.getPortModel(getSelectedPortIndex());

        serviceModeLabel.visibleProperty().bind(model.serviceModeProperty());

        portManager.updatedPorts(Arrays.asList(getSelectedPortIndex()));
        Optional<Integer> optional = portManager.getOwnedPortIndexes().stream().filter(idx -> idx.equals(getSelectedPortIndex())).findFirst();
        optional.ifPresent(val -> model.setIsOwned(true));
        portView.loadModel(model);
    }

    /**
     * Hide and show stat table
     *
     * @TODO need to get rid off this method
     *
     * @param showStatTable
     */
    private void hideShowStatTable(boolean showStatTable) {
        statTableContainer.setVisible(true);
        statTableWrapper.setVisible(showStatTable);
        profileContainer.setVisible(!showStatTable);
    }

    /**
     * Show profile view
     */
    private void viewProfile() {
        try {
            hideShowStatTable(false);
            int portIndex = getSelectedPortIndex();
            lastSelectedPortIndex = portIndex;
            profileLoaded = true;
            disableProfileProperty.set(!portManager.isCurrentUserOwner(portIndex));
            disableProfileNote.visibleProperty().bind(disableProfileProperty);
            AssignedProfile assigned = assignedPortProfileMap.get(portIndex);
            if (assigned == null || !portManager.isCurrentUserOwner(portIndex)) {
                assigned = new AssignedProfile();
                assignedPortProfileMap.put(portIndex, assigned);
            }

            if (assigned.isProfileAssigned()) {
                profileListBox.getSelectionModel().select(assigned.getProfileName());
                profileDetailContainer.setVisible(true);
            } else {
                profileDetailContainer.setVisible(false);
                profileListBox.getSelectionModel().select(Constants.SELECT_PROFILE);
            }

            tableView.reset();

            if (!Util.isNullOrEmpty(assigned.getProfileName())) {
                loadStreamTable(assigned.getProfileName());
                // fill multiplier values
                multiplierView.fillAssignedProfileValues(assigned);
            }

            previousSelectedPortIndex = portIndex;
        } catch (Exception ex) {
            LOG.error("Error loading profile", ex);
        }
    }

    /**
     * Assign profile to selected port
     *
     * @param profileName
     */
    private void assignProfile(String profileName, double currentBandwidth, boolean assignPrevBandwidth, int portID) {
        try {
            // update selected profile
            AssignedProfile assignedProf = assignedPortProfileMap.get(portID);
            if (assignedProf == null) {
                return;
            }
            assignedProf.setProfileName(profileName);
            assignedProf.setAllStreamsWithLatency(allStreamWithLatency);
            PortModel port = PortsManager.getInstance().getPortModel(portID);
            String portState = port.getPortStatus();
            StreamValidation streamValidationGraph = serverRPCMethods.assignTrafficProfile(portID, loadedProfiles);

            portManager.getPortModel(portID).setStreamLoaded(true);
            startStream.setDisable(false);
            // update current multiplier data
            assignedProf.setRate(streamValidationGraph.getResult().getRate());
            multiplierView.assignNewProfile(assignedProf);
            // update multiplier value according to previous bandwidth value
            if (assignPrevBandwidth) {
                multiplierView.setSliderValue(currentBandwidth);
            }
            updateMultiplierValues(assignedProf);
            if (portState.equalsIgnoreCase("tx")) {
                startTraffic(portID);
            }
        } catch (IOException | InvalidRPCResponseException | IncorrectRPCMethodException ex) {
            startStream.setDisable(true);
            portManager.getPortModel(portID).setStreamLoaded(false);
            LOG.error("Failed to load Stream", ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            portManager.updatedPorts(Arrays.asList(portID));
        }
    }

    /**
     * View YAML file stream table data
     *
     * @param fileName
     */
    private void loadStreamTable(String fileName) {
        if (loadProfile(fileName)) {
            allStreamWithLatency = false;
            boolean flowStatsEnabled = false;
            boolean latencyEnabled = false;
            if (loadedProfiles.length > 0) {
                allStreamWithLatency = true;
                for (Profile profile : loadedProfiles) {
                    FlowStats flowStats = profile.getStream().getFlowStats();
                    allStreamWithLatency = allStreamWithLatency && flowStats.isLatencyEnabled();
                    flowStatsEnabled = flowStatsEnabled || flowStats.getEnabled();
                    latencyEnabled = latencyEnabled || flowStats.isLatencyEnabled();
                }
            }
            runningConfiguration.flowStatsEnabledProperty().set(flowStatsEnabled);
            runningConfiguration.latencyEnabledProperty().set(latencyEnabled);
            multiplierView.setDisable(allStreamWithLatency);
            notificationPanelHolder.setVisible(allStreamWithLatency);
        }
    }

    private boolean loadProfile(String fileName) {
        try {
            File selectedFile = new File(ProfileManager.getInstance().getProfileFilePath(fileName));
            loadedProfiles = tableView.loadStreamTable(selectedFile);
            return true;
        } catch (Exception e) {
            LOG.error("Error loading stream table", e);
            LogsController.getInstance().appendText(LogType.ERROR, "Unable to load profile " + fileName);
            return false;
        }
    }

    /**
     * Initialize in-line built component
     */
    private void initializeInlineComponent() {
        updateBtn.setGraphic(new ImageView(new Image("/icons/apply.png")));
        newProfileBtn.setGraphic(new ImageView(new Image("/icons/add_profile.png")));
        duplicateProfileBtn.setGraphic(new ImageView(new Image("/icons/clone_profile.png")));
        stopUpdateBtn.setGraphic(new ImageView(new Image("/icons/stop_update.png")));
        devicesTreeArrowContainer.setImage(leftArrow);
        // mapped profiles enabling with property
        profileListBox.disableProperty().bind(disableProfileProperty);
        newProfileBtn.disableProperty().bind(disableProfileProperty);
        duplicateProfileBtn.disableProperty().bind(disableProfileProperty);
        profileDetailLabel.disableProperty().bind(disableProfileProperty);
        profileListBox.getItems().clear();
        profileListBox.setItems(FXCollections.observableArrayList(getProfilesNameList()));

        profileListBox.valueProperty().addListener(new UpdateProfileListener<>(profileListBox.getSelectionModel()));
        updateProfileListProperty.bind(ProfileManager.getInstance().getUpdatedProperty());
        updateProfileListProperty.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            List<String> profiles = getProfilesNameList();
            profileListBox.setItems(FXCollections.observableArrayList(profiles));
            if (!profiles.contains(currentSelectedProfile)) {
                tableView.reset();
                profileDetailContainer.setVisible(false);
            }
        });
        tableView = new PacketTableView(230, this, true);
        profileTableViewContainer.getChildren().add(tableView);
        serverStatusIcon.setImage(new Image("/icons/offline.png"));

        // initialize right click menu
        rightClickPortMenu = new ContextMenu();
        addMenuItem(rightClickPortMenu, "Acquire", ContextMenuClickType.ACQUIRE, false);
        addMenuItem(rightClickPortMenu, "Force Acquire", ContextMenuClickType.FORCE_ACQUIRE, false);
        addMenuItem(rightClickPortMenu, "Release Acquire", ContextMenuClickType.RELEASE_ACQUIRE, false);
        addMenuItem(rightClickPortMenu, "Enable Service Mode", ContextMenuClickType.ENABLE_SERVICE, false);
        addMenuItem(rightClickPortMenu, "Disable Service Mode", ContextMenuClickType.DISABLE_SERVICE, false);

        rightClickProfileMenu = new ContextMenu();
        addMenuItem(rightClickProfileMenu, "Play", ContextMenuClickType.PLAY, false);
        addMenuItem(rightClickProfileMenu, "Pause", ContextMenuClickType.PAUSE, false);
        addMenuItem(rightClickProfileMenu, "Stop", ContextMenuClickType.STOP, false);

        rightClickGlobalMenu = new ContextMenu();
        addMenuItem(rightClickGlobalMenu, "Release All Ports", ContextMenuClickType.RELEASE_ALL, false);
        addMenuItem(rightClickGlobalMenu, "Acquire All Ports", ContextMenuClickType.ACQUIRE_ALL, false);
        addMenuItem(rightClickGlobalMenu, "Force Acquire All Ports", ContextMenuClickType.FORCE_ACQUIRE_ALL, false);
        addMenuItem(rightClickGlobalMenu, "Re-Acquire my Ports", ContextMenuClickType.ACQUIRE_MY_PORT, false);

        // initialize multiplexer
        multiplierView = new MultiplierView(this);
        multiplierOptionContainer.getChildren().add(multiplierView);
        notificationPanel = new NotificationPanel();
        notificationPanel.setNotificationMsg(DISABLED_MULTIPLIER_MSG);
        notificationPanelHolder.getChildren().add(notificationPanel);
        // add close
        TrexApp.getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // handle aplpication close
                DialogManager.getInstance().closeAll();
                handleAppClose();
            }
        });
        TrexApp.getPrimaryStage().setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                TrexApp.getPrimaryStage().focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue && tableView.isStreamEditingWindowOpen()) {
                        tableView.setStreamEditingWindowOpen(false);
                        streamTableUpdated();
                    }
                });
            }
        });
        TrexApp.getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        logContainer.getChildren().add(LogsController.getInstance().getView());

        // initialize countdown service
        countdownService = new CountdownService();
        countdownService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        countdownService.setRestartOnFailure(false);
        countdownService.setOnSucceeded((WorkerStateEvent event) -> {
            int count = (int) event.getSource().getValue();
            countdownValue.setText(String.valueOf(count) + " Sec");
            if (count == 0) {
                doUpdateAssignedProfile();
            }
        });

        devicesTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                handleTreeItemSelectionChanged();
            }

        });
    }

    /**
     * Handle add new profile
     *
     * @param ev
     */
    @FXML
    public void handleAddNewProfile(ActionEvent ev) {
        try {
            String newProfileName = ProfileManager.getInstance().createNewProfile(TrexApp.getPrimaryStage());
            if (!Util.isNullOrEmpty(newProfileName)) {
                profileListBox.getSelectionModel().select(newProfileName);
            }
        } catch (IOException ex) {
            LOG.error("Error creating new profile", ex);
        }
    }

    /**
     * Handle add new profile
     *
     * @param ev
     */
    @FXML
    public void handleDuplicateProfile(ActionEvent ev) {
        try {
            String assignedProfile = String.valueOf(profileListBox.getValue());
            if (Util.isNullOrEmpty(assignedProfile) || Constants.SELECT_PROFILE.equals(assignedProfile)) {
                return;
            }

            String duplicatedProfileName = ProfileManager.getInstance().duplicateProfile(TrexApp.getPrimaryStage(), assignedProfile);
            if (Util.isNullOrEmpty(duplicatedProfileName)) {
                return;
            }

            int portID = getSelectedPortIndex();

            if (portID > -1) {
                if (PortState.getPortStatus(portManager.getPortByIndex(portID).getStatus()) == PortState.PAUSE
                    && !Util.isNullOrEmpty(duplicatedProfileName)) {
                    profileListBox.getSelectionModel().select(duplicatedProfileName);
                }
            }
        } catch (IOException ex) {
            LOG.error("Error duplicating profile", ex);
        }
    }


    /**
     * Add menu item
     *
     * @param menu
     * @param text
     * @param type
     * @param isDisable
     */
    private MenuItem addMenuItem(ContextMenu menu, String text, ContextMenuClickType type, boolean isDisable) {
        MenuItem item = new MenuItem(text);
        item.setDisable(isDisable);
        item.setOnAction(event -> handleContextMenuItemCLicked(type));
        menu.getItems().add(item);
        return item;
    }

    /**
     * Clear stat cache
     *
     * @param event
     */
    @FXML
    public void clearStatCache(MouseEvent event) {
        // TODO: fill shadow counters
    }

    /**
     * Handle About tree item clicked
     *
     * @param event
     * @throws java.lang.Exception
     */
    @FXML
    public void handleAboutTreeItemClicked(ActionEvent event) throws Exception {
        DialogWindow statsWindow = new DialogWindow("AboutWindowView.fxml", "TRex", 200, 100, false, TrexApp.getPrimaryStage());
        statsWindow.show(true);
    }

    /**
     * Handle stats menu item clicked
     *
     * @param event
     */
    @FXML
    public void handleStatMenuItemClicked(ActionEvent event) {
        openStateDialog();
    }

    /**
     * Handle stats menu item clicked
     *
     * @param event
     */
    @FXML
    public void handleCaptureItemClicked(ActionEvent event) {
        final String currentUser = ConnectionManager.getInstance().getClientName();
        Optional<Port> atLeastOneEnabledServiceMode = PortsManager.getInstance()
            .getPortList()
            .stream()
            .filter(port -> port.getOwner().equalsIgnoreCase(currentUser) && port.getServiceMode())
            .findAny();
        if (atLeastOneEnabledServiceMode.isPresent()) {
            try {
                DialogWindow statsWindow = new DialogWindow(
                    "pkt_capture/Layout.fxml",
                    "Packet Capture",
                    50,
                    10,
                    1200,
                    700,
                    true,
                    TrexApp.getPrimaryStage()
                );
                statsWindow.show(false);
            } catch (IOException ex) {
                LOG.error("Error opening dashboard view", ex);
            }
        } else {
            AlertUtils.construct(
                Alert.AlertType.ERROR,
                "Capture error",
                "Unable to open capture window",
                "At least one port with enabled service mode should be acquired")
                .show();
        }
    }

    /**
     * Handle stats menu item clicked
     *
     * @param event
     */
    @FXML
    public void handleCaptureSettingsItemClicked(ActionEvent event) {
    }

    /**
     * Open statistic dashboard view
     */
    private void openStateDialog() {
        try {
            if (DialogManager.getInstance().getNumberOfOpenedDialog() < 4) {
                DialogWindow statsWindow = new DialogWindow(
                    "dashboard/Dashboard.fxml",
                    "Dashboard",
                    50,
                    10,
                    1210,
                    740,
                    true,
                    TrexApp.getPrimaryStage()
                );
                statsWindow.show(false);
            }
        } catch (IOException ex) {
            LOG.error("Error opening dashboard view", ex);
        }
    }

    /**
     * Handle key down event
     *
     * @param event
     */
    @Override
    public void handle(KeyEvent event) {
        if (!event.getCharacter().matches("[0-9]") && event.getCode() != KeyCode.BACK_SPACE) {
            event.consume();
        }
    }

    /**
     * start transit button click handler
     *
     * @param event
     */
    @FXML
    public void startTransitBtnCLicked(MouseEvent event) {
        int portID = getSelectedPortIndex();
        LOG.trace("Clicked on the Start Transit Button with selectedPort [" + portID + "]");
        if (portID > -1) {
            doStartResume(portID);
        }
    }

    /**
     * Do start/Resume port
     *
     * @param portID
     */
    private void doStartResume(int portID) {
        // disable start button to avoid another quick click
        startStream.setDisable(true);
        if (PortState.getPortStatus(portManager.getPortByIndex(portID).getStatus()) == PortState.PAUSE) {
            serverRPCMethods.resumeTraffic(portID);
        } else {
            startTraffic(portID);
        }
        portManager.updatedPorts(Arrays.asList(portID));
    }

    /**
     * start all transit button click handler
     *
     * @param event
     */
    @FXML
    public void startAllTransitBtnCLicked(MouseEvent event) {
        LOG.trace("Clicked on the Start All Transit Button");
        portManager.getPortList().stream().forEach(new Consumer<Port>() {
            @Override
            public void accept(Port port) {
                PortState portState = PortState.getPortStatus(port.getStatus());
                if (portManager.isCurrentUserOwner(port.getIndex())
                    && portState != PortState.TX && portState != PortState.IDLE) {
                    doStartResume(port.getIndex());
                }
            }
        });
    }

    /**
     * Start traffic on port
     *
     * @param portID
     */
    private void startTraffic(int portID) {
        try {
            AssignedProfile assignedProf = assignedPortProfileMap.get(portID);

            if (assignedProf != null && assignedProf.isAllStreamsWithLatency()) {
                serverRPCMethods.startTraffic(portID, false, "percentage", 100, multiplierView.getDuration());
            } else {
                serverRPCMethods.startTraffic(portID, false, "pps", multiplierView.getPPSValue(), multiplierView.getDuration());
            }

            if (assignedProf != null) {
                assignedProf.setStreamStarted(true);
                assignedProf.setHasDuration(multiplierView.isDurationEnable());
                updateMultiplierValues(assignedProf);
            }
        } catch (TrafficException ex) {
            // re-enable start button in case of errors
            startStream.setDisable(false);
            LOG.error("Error starting traffic", ex);
        }
    }

    /**
     * stop transit btn clicked
     *
     * @param event
     */
    @FXML
    public void stopTransitBtnCLicked(MouseEvent event) {
        int portID = getSelectedPortIndex();
        LOG.trace("Clicked on the Stop Transit Button with selectedPort [" + portID + "]");
        if (portID > -1) {
            serverRPCMethods.stopPortTraffic(portID);
            portManager.updatedPorts(Arrays.asList(portID));
            if (isWaitingUpdate() && !needReassignProfile) {
                cancelUpdate();
            }
        }
    }

    /**
     * stop all transit btn clicked
     *
     * @param event
     */
    @FXML
    public void stopAllTransitBtnCLicked(MouseEvent event) {
        LOG.trace("Clicked on the Stop All Transit Button ");
        portManager.getPortList().stream().forEach(port -> {
            PortState portState = PortState.getPortStatus(port.getStatus());
            if (portManager.isCurrentUserOwner(port.getIndex()) && portState == PortState.TX) {
                serverRPCMethods.stopPortTraffic(port.getIndex());
                portManager.updatedPorts(Arrays.asList(port.getIndex()));
            }
        });
        enableUpdateBtn(false, false);
    }

    /**
     * pause transit btn clicked
     *
     * @param event
     */
    @FXML
    public void pauseTransitBtnCLicked(MouseEvent event) {
        int portID = getSelectedPortIndex();
        LOG.trace("Clicked on the Pause Transit Button with selectedPort [" + portID + "]");
        if (portID > -1) {
            if (PortState.getPortStatus(portManager.getPortByIndex(portID).getStatus()) == PortState.PAUSE) {
                serverRPCMethods.resumeTraffic(portID);
            } else {
                enableUpdateBtn(false, false);
                serverRPCMethods.pauseTraffic(portID);
            }
            portManager.updatedPorts(Arrays.asList(portID));
        }
    }

    /**
     * Return port index related to selected treeItem
     *
     * @return
     */
    private int getSelectedPortIndex() {
        CustomTreeItem selected = (CustomTreeItem) devicesTree.getSelectionModel().getSelectedItem();
        if (selected != null && !Util.isNullOrEmpty(selected.getReturnedValue())) {
            return Integer.parseInt(selected.getReturnedValue());
        }
        return -1;
    }

    /**
     * Handle context menu item clicked
     *
     * @param type
     */
    private void handleContextMenuItemCLicked(ContextMenuClickType type) {
        try {
            Integer portIndex = getSelectedPortIndex();
            switch (type) {
                case ENABLE_SERVICE:
                    PortsManager.getInstance().getPortModel(portIndex).serviceModeProperty().set(true);
                    break;
                case DISABLE_SERVICE:
                    PortsManager.getInstance().getPortModel(portIndex).serviceModeProperty().set(false);
                    break;
                case ACQUIRE:
                    acquirePort();
                    break;
                case FORCE_ACQUIRE:
                    serverRPCMethods.acquireServerPort(portIndex, true);
                    portManager.getPortModel(portIndex).setIsOwned(true);
                    portManager.updatePortForce();
                    break;
                case RELEASE_ACQUIRE:
                    releasePort(portIndex, true, true);
                    break;
                case PLAY:
                    doStartResume(portIndex);
                    break;
                case PAUSE:
                    serverRPCMethods.pauseTraffic(portIndex);
                    portManager.updatedPorts(Arrays.asList(portIndex));
                    break;
                case STOP:
                    serverRPCMethods.stopPortTraffic(portIndex);
                    portManager.updatedPorts(Arrays.asList(portIndex));
                    break;
                case ACQUIRE_ALL:
                    acquireAllPorts(false, false);
                    break;
                case FORCE_ACQUIRE_ALL:
                    acquireAllPorts(true, false);
                    break;
                case ACQUIRE_MY_PORT:
                    acquireAllPorts(true, true);
                    break;
                case RELEASE_ALL:
                    releaseAllPort(true);
                    portManager.updatePortForce();
                    break;

                case UNLOAD_PROFILE:
                    profileListBox.setValue(Constants.SELECT_PROFILE);
                    break;
                default:
                    break;
            }
        } catch (PortAcquireException ex) {
            LOG.error("Error handling context menu item clicked", ex);
        }
    }

    /**
     * Acquire selected port
     */
    private void acquirePort() {
        try {
            serverRPCMethods.acquireServerPort(getSelectedPortIndex(), false);
            portManager.updatePortForce();
            portManager.getPortModel(getSelectedPortIndex()).setIsOwned(true);
        } catch (PortAcquireException ex) {
            LOG.error("Error acquiring port", ex);
        }
    }

    /**
     * Release selected port
     */
    private void releasePort(int portIndex, boolean forceUpdatePort, boolean stopTraffic) {
        serverRPCMethods.releasePort(portIndex, stopTraffic);
        // remove saved assigned profile
        if (assignedPortProfileMap.get(portIndex) != null) {
            assignedPortProfileMap.remove(portIndex);
        }
        if (!resetAppInProgress) {
            final int selectedPortIndex = getSelectedPortIndex();
            if (selectedPortIndex != -1) {
                portManager.getPortModel(selectedPortIndex).setIsOwned(false);
            }
        }
        if (forceUpdatePort) {
            portManager.updatePortForce();
        }
    }

    /**
     * Release all ports
     */
    private void releaseAllPort(boolean stopTraffic) {
        portManager.getPortList().stream().forEach(port -> {
            if (PortsManager.getInstance().isCurrentUserOwner(port.getIndex())) {
                releasePort(port.getIndex(), false, stopTraffic);
            }
        });
    }

    /**
     * Acquire all ports
     *
     * @param force
     * @param acquireOwnedOnly
     * @throws PortAcquireException
     */
    private void acquireAllPorts(boolean force, boolean acquireOwnedOnly) {
        for (Port port : portManager.getPortList()) {
            try {
                if (!acquireOwnedOnly || (acquireOwnedOnly && portManager.isCurrentUserOwner(port.getIndex()))) {
                    serverRPCMethods.acquireServerPort(port.getIndex(), force);
                }
            } catch (PortAcquireException ex) {
                LOG.error("Error handling context menu item clicked", ex);
            }
        }
        portManager.updatePortForce();
    }

    /**
     * Handle update button click
     *
     * @param event
     */
    @FXML
    public void handleUpdateBtnClicked(ActionEvent event) {
        doUpdateAssignedProfile();
    }

    /**
     * Update current port
     */
    private void doUpdateAssignedProfile() {
        try {
            if (needReassignProfile) {
                needReassignProfile = false;
                String assignedProfile = String.valueOf(profileListBox.getValue());
                assignProfile(assignedProfile, multiplierView.getSliderValue(), true, lastSelectedPortIndex);
            } else {
                serverRPCMethods.updateTraffic(lastSelectedPortIndex, false, MultiplierType.pps.name(), multiplierView.getPPSValue());
                // update assigned profile multiplier
                AssignedProfile assignedProf = assignedPortProfileMap.get(lastSelectedPortIndex);
                updateMultiplierValues(assignedProf);
            }
            updateHeaderBtnStat();
        } catch (TrafficException ex) {
            LOG.error("Error updating port", ex);
        }
        enableUpdateBtn(false, false);
    }

    /**
     * Update multiplier values
     *
     * @param assignedProf
     */
    private void updateMultiplierValues(AssignedProfile assignedProf) {
        ProfileMultiplier multiplier = new ProfileMultiplier();
        multiplier.setDuration(multiplierView.getDuration());
        multiplier.setValue(multiplierView.getPPSValue());
        multiplier.setSelectedType(multiplierView.getType());
        multiplier.setType("pps");
        multiplier.setUnit("pps");
        assignedProf.setMultiplier(multiplier);
    }

    /**
     * Handle application close
     */
    private void handleAppClose() {
        try {
            // stop running thread
            shutdownRunningServices();
            if (!portManager.getPortList().isEmpty() && !portManager.getPortList().isEmpty()) {
                releaseAllPort(false);
            }
            // stop async subscriber
            if (ConnectionManager.getInstance().isConnected()) {
                ConnectionManager.getInstance().disconnect();
            }
        } catch (Exception ex) {
            LOG.error("Error closing the application", ex);
            System.exit(0);
        }
    }

    /**
     * Return profile name list
     *
     * @return
     */
    private List<String> getProfilesNameList() {
        List<String> profilenameList = ProfileManager.getInstance().loadProfiles();
        profilenameList.add(0, Constants.SELECT_PROFILE);
        return profilenameList;
    }

    /**
     * Update header stream buttons state
     */
    private void updateHeaderBtnStat() {
        int portIndex = getSelectedPortIndex();
        resetBtnState();
        if (portIndex != -1) {
            Port port = portManager.getPortByIndex(portIndex);
            PortState state = PortState.getPortStatus(port.getStatus());

            // enable state btn btn according to owner 
            boolean isOwner = portManager.isCurrentUserOwner(portIndex);
            switch (state) {
                case STREAMS:
                    startStream.setDisable(!isOwner || !portManager.getPortModel(portIndex).isStreamLoaded());
                    break;
                case TX:
                    pauseStream.setDisable(!isOwner);
                    stopStream.setDisable(!isOwner);
                    break;
                case PAUSE:
                    startStream.setDisable(!isOwner);
                    pauseStream.setDisable(!isOwner);
                    stopStream.setDisable(!isOwner);
                    pauseStream.getStyleClass().add("pauseIconPressed");
                    break;
                case IDLE:
                    AssignedProfile portProfile = assignedPortProfileMap.get(portIndex);
                    if (portProfile != null) {
                        startStream.setDisable(!(isOwner && portProfile.isProfileAssigned()));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Reset stream header buttons state
     */
    private void resetBtnState() {
        startStream.setDisable(true);
        stopStream.setDisable(true);
        pauseStream.setDisable(true);
        pauseStream.getStyleClass().remove("pauseIconPressed");
    }

    /**
     *
     * @param event
     */
    @FXML
    public void handlePreferencesMenuItemClicked(ActionEvent event) {
        openPreferencesWindow();
    }

    /**
     * Open preferences window
     */
    private void openPreferencesWindow() {
        try {
            DialogWindow statsWindow = new DialogWindow("Preferences.fxml", "Preferences", 100, 50, true, TrexApp.getPrimaryStage());
            statsWindow.show(true);
        } catch (IOException ex) {
            LOG.error("Error opening preferences window", ex);

        }
    }


    /**
     * Tries to update profile for last selected port. If
     * this port is transmitting traffic the update will
     * be delayed ("Update" and "Cancel" buttons will appear),
     * otherwise update will be executed immediately.
     * @param enqueueNeedReassign if true, then profile will be reassigned
     *                     on update, if false -- only trafficUpdate
     *                     will be called. If there is already need
     *                     to reassign profile and enqueueNeedReassign is false
     *                     it still will be reassigned on next update
     * @param forceUpdate if true the update will be executed immediately
     *              ignoring other conditions
     */
    private void tryUpdateProfile(boolean enqueueNeedReassign, boolean forceUpdate) {
        needReassignProfile |= enqueueNeedReassign;
        boolean shouldDelay = portManager.getPortModel(lastSelectedPortIndex).transmitStateProperty().get() && isContinuousStream();
        if(shouldDelay && !forceUpdate) {
            enableUpdateBtn(true, true);
        } else {
            doUpdateAssignedProfile();
        }
    }

    /**
     * Overloaded version of {@link #tryUpdateProfile(boolean, boolean)} method
     * with parameter forceUpdate set to false by default.
     * @param enqueueNeedReassign
     */
    private void tryUpdateProfile(boolean enqueueNeedReassign) {
        tryUpdateProfile(enqueueNeedReassign, false);
    }

    /**
     * Cancels update if it was delayed inside {@link #tryUpdateProfile(boolean)} method
     */
    private void cancelUpdate() {
        enableUpdateBtn(false, false);
    }

    /**
     * Uses with {@link #tryUpdateProfile(boolean)} method
     * @return true if profile updating was delayed and not yet executed,
     * returns false otherwise
     */
    private boolean isWaitingUpdate() {
        return !updateBtn.isDisabled();
    }

    /**
     * Enable update/Stop button button
     */
    private void enableUpdateBtn(boolean enableCounter, boolean enableUpdate) {
        stopUpdateBtn.setVisible(enableCounter);
        countdownValue.setVisible(enableCounter);
        updateBtn.setDisable(!enableUpdate);
        if (enableCounter) {
            countdownService.resetCounter();
            countdownService.restart();
        } else if (countdownService.isRunning()) {
            countdownService.cancel();
            countdownValue.setText("");
        }
    }

    /**
     * Return True if all stream is continuous, otherwise return false
     *
     * @return
     */
    private boolean isContinuousStream() {
        for (Profile pro : loadedProfiles) {
            if (!"continuous".equals(pro.getStream().getMode().getType())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Multiplier option value changed handler
     */
    @Override
    public void optionValueChanged() {
        if(portManager.getPortModel(getSelectedPortIndex()).transmitStateProperty().get())
            tryUpdateProfile(false);
    }

    /**
     * Handle dashboard button clicked
     *
     * @param event
     */
    @FXML
    public void handleDashboardBtnClicked(MouseEvent event) {
        openStateDialog();
    }

    /**
     * Handle Stop button clicked
     *
     * @param event
     */
    @FXML
    public void hanldeStopBtnClicked(ActionEvent event) {
        enableUpdateBtn(false, true);
    }

    /**
     *
     * @param successfullyUpdated
     */
    @Override
    public void onPortListUpdated(boolean successfullyUpdated) {
        if (successfullyUpdated) {
            updateDevicesTree();
            updateHeaderBtnStat();
            enableDisableStartStopAllBtn();
            if (isFirstPortStatusRequest) {
                isFirstPortStatusRequest = false;
                reAcquireOwnedPorts();
            }
            CustomTreeItem selected = (CustomTreeItem) devicesTree.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getTreeItemType() == TreeItemType.PORT) {
                updateAcquireReleaseBtnState(false);
            } else if (selected != null && selected.getTreeItemType() == TreeItemType.PORT_PROFILE
                && !portManager.isCurrentUserOwner(getSelectedPortIndex())) {
                viewProfile();
            }
        } else {
            LOG.info("Server went down ... handling it");
            resetApplication(true);
        }
    }

    /**
     * Re-acquire owned port on login
     */
    private void reAcquireOwnedPorts() {
        try {
            for (Port port : portManager.getPortList()) {
                if (portManager.isCurrentUserOwner(port.getIndex())) {
                    serverRPCMethods.acquireServerPort(port.getIndex(), true);
                }
            }
        } catch (PortAcquireException ex) {
            LOG.error("Error re-acquiring port", ex);
        }
    }

    /**
     * Enable/Disable start/stop all button according to port state
     */
    private void enableDisableStartStopAllBtn() {
        boolean disableStartStopAll = true;
        for (Port port : portManager.getPortList()) {
            if (PortState.getPortStatus(port.getStatus()) != PortState.IDLE) {
                disableStartStopAll = false;
                break;
            }
        }
        startAllStream.setDisable(disableStartStopAll);
        stopAllStream.setDisable(disableStartStopAll);
    }

    /**
     * Copy to clipboard button clicked handler
     *
     * @param event
     */
    @FXML
    public void copyToClipboard(ActionEvent event) {
        if (logTab.isSelected()) {
            LogsController.getInstance().getView().copyToClipboard();
            return;
        }
    }

    /**
     * Stop running services
     */
    private void shutdownRunningServices() {
        portView.stopPolling();
        stopRefreshingService();
    }

    /**
     * Stop refresh service
     */
    private void stopRefreshingService() {
        if (refreshStatsService.isRunning()) {
            refreshStatsService.cancel();
        }
        Util.optimizeMemory();
    }

    /**
     * re-assign profile update button enabled when stream is deleted
     */
    @Override
    public void onStreamUpdated() {
        tryUpdateProfile(true);
    }

    /**
     * Stream table changed handler
     */
    @Override
    public void onStreamTableChanged() {
        streamTableUpdated();
    }

    /**
     * Reload stream table
     */
    private void streamTableUpdated() {
        try {
            String fileName = String.valueOf(profileListBox.getValue());
            loadStreamTable(fileName);
            // assigned profile may changed need to re-assign
            tryUpdateProfile(true);
        } catch (Exception ex) {
            LOG.error("Error reloading table", ex);
        }
    }

    /**
     * Handle acquire port button clicked
     *
     * @param event
     */
    @FXML
    public void handleAcquireBtnClicked(MouseEvent event) {
        acquirePort();
    }

    /**
     * Handle release port button clicked
     *
     * @param event
     */
    @FXML
    public void handleReleaseBtnClicked(MouseEvent event) {
        releasePort(getSelectedPortIndex(), true, true);
    }

    /**
     * Update acquire/release port button enabling state
     */
    private void updateAcquireReleaseBtnState(boolean forceDisable) {
        int selectedPort = getSelectedPortIndex();
        if (selectedPort == -1) {
            return;
        }
        acquirePort.setDisable(!(!forceDisable && portManager.isPortFree(selectedPort)));
        releasePort.setDisable(!(!forceDisable && portManager.isCurrentUserOwner(selectedPort)));
    }

    /**
     * Handle devicestree arrow clicking
     *
     * @param event
     */
    @FXML
    public void handleDevicesTreeArrowClicked(MouseEvent event) {
        if (treeviewOpened) {
            mainViewSplitPanel.setDividerPosition(0, 0);
            devicesTreeArrowContainer.setImage(rightArrow);
        } else {
            mainViewSplitPanel.setDividerPosition(0, 1);
            devicesTreeArrowContainer.setImage(leftArrow);
        }
        treeviewOpened = !treeviewOpened;
    }

    /**
     * Enumerator that present Context menu item type
     */
    private enum ContextMenuClickType {
        ACQUIRE,
        FORCE_ACQUIRE,
        RELEASE_ACQUIRE,
        ENABLE_SERVICE,
        DISABLE_SERVICE,
        PLAY,
        STOP,
        UNLOAD_PROFILE,
        PAUSE,
        ACQUIRE_ALL,
        FORCE_ACQUIRE_ALL,
        RELEASE_ALL,
        ACQUIRE_MY_PORT
    }

    @Subscribe
    public void handleScapyClientNeedConnectEvent(ScapyClientNeedConnectEvent event) {
        LogsController.getInstance().getView().setDisable(false);
        if (!ConnectionManager.getInstance().isScapyConnected()) {
            ConnectionsWrapper connection = (ConnectionsWrapper) XMLFileManager.loadXML("connections.xml", ConnectionsWrapper.class);
            Connection lastUsed = null;
            for (Connection con : connection.getConnectionList()) {
                if (con.isLastUsed()) {
                    lastUsed = con;
                }
            }
            if (lastUsed == null) {
                ConnectionManager.getInstance().connectScapy();
            } else {
                ConnectionManager.getInstance().connectScapy(lastUsed.getIp(), lastUsed.getScapyPort());
            }

            if (!ConnectionManager.getInstance().isScapyConnected()) {
                openConnectDialog();
            }
            if (ConnectionManager.getInstance().isScapyConnected()) {
                eventBus.post(new InitPacketEditorEvent());
                return;
            }
        }
    }

    public class UpdateProfileListener<T> implements ChangeListener<T> {

        private final SelectionModel<T> selectionModel;
        private boolean reverting = false;

        public UpdateProfileListener(SelectionModel<T> selectionModel) {
            if (selectionModel == null) {
                throw new IllegalArgumentException();
            }
            this.selectionModel = selectionModel;
        }

        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
            if (reverting || lastSelectedPortIndex != previousSelectedPortIndex) {
                return;
            }

            if (!resetAppInProgress && !isAllowed()) {
                reverting = true;
                Platform.runLater(() -> {
                    selectionModel.select(oldValue);
                    reverting = false;
                });
                return;
            }

            try {
                if (isWaitingUpdate()) {
                    tryUpdateProfile(false, true);
                }
                String profileName = String.valueOf(newValue);
                profileDetailContainer.setVisible(false);
                if (!Util.isNullOrEmpty(profileName) && !Constants.SELECT_PROFILE.equals(profileName)) {
                    profileDetailContainer.setVisible(true);
                    currentSelectedProfile = profileName;
                    loadStreamTable(profileName);
                    tryUpdateProfile(true, true);
                    trafficProfileLoadedProperty.set(true);
                } else {
                    unloadProfile();
                }
            } catch (Exception ex) {
                LOG.error("Error loading profile", ex);
            }
        }

        protected boolean isAllowed() {
            int portIndex = getSelectedPortIndex();
            PortModel currentPortModel = portManager.getPortModel(portIndex);
            boolean isPortTransmit = currentPortModel.transmitStateProperty().get();

            if (!isPortTransmit && Constants.SELECT_PROFILE.equals(selectionModel.getSelectedItem())) {
                return true;
            }

            if (isPortTransmit) {
                String header = "Port " + portIndex + " in TX mode";
                String content = "Assigning another profile to the port will stop it. Proceed?";
                Optional result = runConfirmationDialog(header, content);
                return result.get() == ButtonType.OK;
            }
            return true;
        }
    }

    private void unloadProfile() {
        if (currentSelectedProfile == null) {
            return;
        }
        Task<Void> unloadProfileTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                TRexClient trexClient = ConnectionManager.getInstance().getTrexClient();
                trexClient.stopTraffic(lastSelectedPortIndex);
                trexClient.removeAllStreams(lastSelectedPortIndex);
                return null;
            }
        };

        unloadProfileTask.setOnSucceeded(event -> {
            LogsController.getInstance().appendText(LogType.INFO, currentSelectedProfile + " profile unloaded");
            currentSelectedProfile = Constants.SELECT_PROFILE;
            assignedPortProfileMap.put(lastSelectedPortIndex, new AssignedProfile());
            trafficProfileLoadedProperty.set(false);
            portManager.updatedPorts(Arrays.asList(lastSelectedPortIndex));
        });
        LogsController.getInstance().appendText(LogType.INFO, "Unloading " + currentSelectedProfile + " profile");
        new Thread(unloadProfileTask).start();
    }

    private Optional runConfirmationDialog(String header, String content) {
        Dialog alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStyleClass().add("warning");
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}

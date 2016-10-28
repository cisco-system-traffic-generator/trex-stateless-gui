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
package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.core.AsyncResponseManager;
import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.remote.exceptions.IncorrectRPCMethodException;
import com.exalttech.trex.remote.exceptions.InvalidRPCResponseException;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.remote.exceptions.TrafficException;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.remote.models.validate.StreamValidation;
import com.exalttech.trex.ui.MultiplierType;
import com.exalttech.trex.ui.PortManagerEventHandler;
import com.exalttech.trex.ui.PortState;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.components.CustomTreeItem;
import com.exalttech.trex.ui.components.CustomTreeItem.TreeItemType;
import com.exalttech.trex.ui.components.NotificationPanel;
import com.exalttech.trex.ui.dialog.DialogManager;
import com.exalttech.trex.ui.dialog.DialogWindow;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.models.SystemInfoReq;
import com.exalttech.trex.ui.views.MultiplierOptionChangeHandler;
import com.exalttech.trex.ui.views.MultiplierView;
import com.exalttech.trex.ui.views.PacketTableUpdatedHandler;
import com.exalttech.trex.ui.views.PacketTableView;
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
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import com.xored.javafx.packeteditor.TRexPacketCraftingTool;

/**
 * Main view FXML controller
 *
 * @author Georgekh
 */
public class MainViewController implements Initializable, EventHandler<KeyEvent>,
        MultiplierOptionChangeHandler, PortManagerEventHandler, PacketTableUpdatedHandler {

    private static final Logger LOG = Logger.getLogger(MainViewController.class.getName());
    private final RPCMethods serverRPCMethods = new RPCMethods();
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
    ComboBox profileListBox;
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
    MenuItem trafficProfileMenu;

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
    AnchorPane logContainer;
    @FXML
    AnchorPane consoleLogContainer;

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
    
    private ContextMenu rightClickPortMenu;
    private ContextMenu rightClickProfileMenu;
    private ContextMenu rightClickGlobalMenu;

    private SystemInfoReq systemInfoReq = null;
    private PacketTableView tableView;
    private RefreshingService refreshStatsService = new RefreshingService();
    private Map<String, String> cachedStatsList = new HashMap<>();
    private final Map<Integer, CustomTreeItem> portTreeItemMap = new HashMap<>();
    private final BooleanProperty updateProfileListProperty = new SimpleBooleanProperty();
    private final Map<Integer, AssignedProfile> assignedPortProfileMap = new HashMap<>();

    private Profile[] loadedProfiles;
    private String currentSelectedProfile;
    private MultiplierView multiplierView;
    private NotificationPanel notificationPanel;
    boolean reAssign = false;
    private CountdownService countdownService;
    private PortsManager portManager;
    private final BooleanProperty disableProfileProperty = new SimpleBooleanProperty();
    StatsTableGenerator statsTableGenerator;
    boolean doAssignProfile = true;
    KeyCombination openDashboardCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    KeyCombination connectCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
    KeyCombination openPreferencesCombination = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    KeyCombination quiteCombination = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
    private boolean allStreamWithLatency;
    private boolean isFirstPortStatusRequest = true;
    private static final String DISCONNECT_MENU_ITEM_TITLE = "  Disconnect";
    private static final String CONNECT_MENU_ITEM_TITLE = "  Connect               Ctrl+C";

    private int lastLoadedPortPtofileIndex = -1;
    private boolean profileLoaded = false;
    
    private Image leftArrow;
    private Image rightArrow;
    private boolean treeviewOpened = true;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        portManager = PortsManager.getInstance();
        portManager.setPortManagerHandler(this);
        statsTableGenerator = new StatsTableGenerator();
        leftArrow = new Image("/icons/arrow_left.png");
        rightArrow = new Image("/icons/arrow_right.png");
        initializeInlineComponent();
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
        Platform.exit();
    }

    /**
     * Open connect dialog
     */
    private void openConnectDialog() {
        try {
            DialogWindow connectWindow = new DialogWindow("ConnectDialog.fxml", "Connect", 300, 100, false, TrexApp.getPrimaryStage());
            connectWindow.show(true);
            if (ConnectionManager.getInstance().isConnected()) {
                StatsLoader.getInstance().start();
                serverStatusLabel.setText("Connected");
                serverStatusIcon.setImage(new Image("/icons/connectedIcon.gif"));
                connectIcon.getStyleClass().add("disconnectIcon");
                connectDixconnectTooltip.setText("Disconnect from TRex server");
                connectMenuItem.setText(DISCONNECT_MENU_ITEM_TITLE);
                statsMenuItem.setDisable(false);
                trafficProfileMenu.setDisable(false);
                clearCache.setDisable(false);
                logsContainer.setDisable(false);
                copyToClipboardBtn.setDisable(false);
                dashboardIcon.setDisable(false);
                cachedStatsList = new HashMap<>();
                serverRPCMethods.serverApiSync();
                loadSystemInfo();
            }
        } catch (IOException ex) {
            LOG.error("Error while Connecting", ex);
        }
    }

    /**
     * Run loading system info thread
     */
    private void loadSystemInfo() {
        String data = ConnectionManager.getInstance().sendRequest("get_system_info", "");
        data = Util.removeFirstBrackets(data);
        systemInfoReq = (SystemInfoReq) Util.fromJSONString(data, SystemInfoReq.class);
        systemInfoReq.setIp(ConnectionManager.getInstance().getIPAddress());
        systemInfoReq.setPort(ConnectionManager.getInstance().getRpcPort());
        portManager.setPortList(systemInfoReq.getResult().getPorts());
        portManager.startPortStatusScheduler();
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
            root.getChildren().add(new CustomTreeItem("Global Stats", TreeItemType.GLOBAL_STAT));
            root.getChildren().add(new CustomTreeItem("Port Statistics", TreeItemType.TUI));
            portManager.getPortList().stream().forEach(new Consumer<Port>() {
                @Override
                public void accept(Port port) {
                    // build port tree item
                    root.getChildren().add(createPortItem(port));
                }
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
        CustomTreeItem portStatItem = new CustomTreeItem("Port stats", TreeItemType.PORT_STATS);
        portStatItem.setReturnedValue(String.valueOf(port.getIndex()));
        root.getChildren().add(portStatItem);
        return root;
    }

    /**
     * Handle tree item clicked
     *
     * @param mouseEvent
     */
    @FXML
    public void handleTreeClicked(MouseEvent mouseEvent) {
        
        CustomTreeItem selected = (CustomTreeItem) devicesTree.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // mouse left button clicked
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                viewTreeContextMenu(selected);
            }
            
        }
    }

    /**
     * Handle treeitem selection changed
     */
    private void handleTreeItemSelectionChanged(){
        updateHeaderBtnStat();
        CustomTreeItem selected = (CustomTreeItem) devicesTree.getSelectionModel().getSelectedItem();
        if (selected != null) {
            
            if (profileLoaded) {
                updateCurrentProfileMultiplier();
            }

            try {
                stopRefreshingService();
                // update aquire/release port icon state
                updateAcquireReleaseBtnState(true);
                // show table container by default
                hideShowStatTable(true);
                switch (selected.getTreeItemType()) {
                    case DEVICES:
                        buildSystemInfoTable();
                        break;
                    case GLOBAL_STAT:
                        viewGlobalStatsTable();
                        break;
                    case PORT:
                        updateAcquireReleaseBtnState(false);
                        buildPortInfoTable();
                        break;
                    case PORT_PROFILE:
                        viewProfile();
                        break;
                    case PORT_STATS:
                        getPortStats();
                        break;
                    case TUI:
                        viewTUITable();
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
     * View treeitem context menu
     * @param selected
     */
    private void viewTreeContextMenu(CustomTreeItem selected) {
        devicesTree.setContextMenu(null);
        updateContextMenuState();
        if (selected.getMenu() != null) {
            devicesTree.setContextMenu(selected.getMenu());
        }
    }

    /**
     * Update current loaded profile multiplier
     */
    private void updateCurrentProfileMultiplier() {
        profileLoaded = false;
        AssignedProfile assignedProf = assignedPortProfileMap.get(lastLoadedPortPtofileIndex);
        if (assignedProf != null) {
            assignedProf.setHasDuration(multiplierView.isDurationEnable());
            updateMultiplierValues(assignedProf);
        }
    }

    /**
     * Update right click menu enabling state
     */
    private void updateContextMenuState() {
        int portId = getSelectedPortIndex();
        if (portId != -1) {
            Port port = portManager.getPortList().get(portId);
            // enable acquire option if port not owned
            rightClickPortMenu.getItems().get(0).setDisable(!Util.isNullOrEmpty(port.getOwner()));
            // enable release option if port owned by loggin-user    
            rightClickPortMenu.getItems().get(2).setDisable(!port.getOwner().equals(ConnectionManager.getInstance().getClientName()));
        }
    }

    /**
     * View TUI stats table
     */
    private void viewTUITable() {
        getAsyncStatList(AsyncStatsType.TUI);
    }

    /**
     * View Global stats table
     */
    private void viewGlobalStatsTable() {
        getAsyncStatList(AsyncStatsType.GLOBAL);
    }

    /**
     * Build port stats table
     *
     * @param portIndex
     */
    private void buildPortStatTable(int portIndex) {
        statTableContainer.setContent(null);

        if (portIndex == -1) {
            statTableContainer.setContent(statsTableGenerator.getPortStatTable(cachedStatsList, portManager.getPortList().size(), true, 150, false));
            return;
        }
        statTableContainer.setContent(statsTableGenerator.getPortStatTable(cachedStatsList, portIndex));
    }

    /**
     * Build global statistic table
     */
    private void buildGlobalStat() {
        statTableContainer.setContent(statsTableGenerator.generateGlobalStatPane());
    }

    /**
     * Request and return portStats
     *
     */
    private void getPortStats() {
        getAsyncStatList(AsyncStatsType.PORT, getSelectedPortIndex());
    }

    /**
     * Return async stats list
     *
     * @param type
     * @param keysList
     * @param additionalKeyStr
     */
    private void getAsyncStatList(AsyncStatsType type) {
        getAsyncStatList(type, 0);
    }

    /**
     * Return async stats list
     *
     * @param type
     * @param addData
     */
    private void getAsyncStatList(final AsyncStatsType type, final int addData) {
        refreshStatsService = new RefreshingService();
        refreshStatsService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        refreshStatsService.setOnSucceeded((WorkerStateEvent event) -> {
            switch (type) {
                case GLOBAL:
                    buildGlobalStat();
                    break;
                case PORT:
                    buildPortStatTable(addData);
                    break;
                case TUI:
                    buildPortStatTable(-1);
                    break;
                default:
                    break;
            }
        });
        refreshStatsService.start();
    }

    /**
     * Reset the application to initial state
     */
    private void resetApplication(boolean didServerCrash) {
        if (!didServerCrash) {
            ConnectionManager.getInstance().setConnected(false);
            // release all port
            releaseAllPort(false);
        }

        // shutdown running services
        shutdownRunningServices();

        // close all open dialog
        DialogManager.getInstance().closeAll();

        // clear tree
        devicesTree.setRoot(null);

        // hide all right side views
        statTableWrapper.setVisible(false);
        profileContainer.setVisible(false);
        cachedStatsList = new HashMap<>();
        connectMenuItem.setText(CONNECT_MENU_ITEM_TITLE);
        statsMenuItem.setDisable(true);
        trafficProfileMenu.setDisable(true);
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
        logsContainer.setDisable(true);
        copyToClipboardBtn.setDisable(true);
        acquirePort.setDisable(true);
        releasePort.setDisable(true);
        assignedPortProfileMap.clear();

        // stop async subscriber
        ConnectionManager.getInstance().disconnectSubscriber();
        ConnectionManager.getInstance().disconnectRequester();
        ConnectionManager.getInstance().disconnectScapyClient();

        if (didServerCrash) {
            openConnectDialog();
        }
    }

    /**
     * Restart application
     */
    private void restartApplication() {
        try {
            new ProcessBuilder(Util.getApplicationPath()).start();
            TrexApp.getPrimaryStage().fireEvent(new WindowEvent(TrexApp.getPrimaryStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
        } catch (IOException ex) {
            LOG.error("Error restarting application", ex);
        }
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
    private void buildPortInfoTable() {
        Port port = portManager.getPortList().get(getSelectedPortIndex());
        statTableContainer.setContent(statsTableGenerator.generatePortInfoPane(port));
    }

    /**
     * Hide and show stat table
     *
     * @param showStatTable
     */
    private void hideShowStatTable(boolean showStatTable) {
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
            lastLoadedPortPtofileIndex = portIndex;
            profileLoaded = true;
            disableProfileProperty.set(!portManager.isCurrentUserOwner(portIndex));
            disableProfileNote.visibleProperty().bind(disableProfileProperty);
            AssignedProfile assigned = assignedPortProfileMap.get(portIndex);
            if (assigned == null || !portManager.isCurrentUserOwner(portIndex)) {
                assigned = new AssignedProfile();
                assignedPortProfileMap.put(portIndex, assigned);
            }
            fillAssignedProfileData(assigned);
        } catch (IOException ex) {
            LOG.error("Error loading profile", ex);
        }
    }

    /**
     * Fill port assigned profile selection
     *
     * @param assigned
     * @throws IOException
     */
    private void fillAssignedProfileData(AssignedProfile assigned) throws IOException {

        if (assigned.isProfileAssigned()) {
            doAssignProfile = false;
            profileListBox.getSelectionModel().select(assigned.getProfileName());
        } else {
            profileDetailContainer.setVisible(false);
            profileListBox.getSelectionModel().select(Constants.SELECT_PROFILE);
            doAssignProfile = true;
        }
        tableView.reset();
        if (!Util.isNullOrEmpty(assigned.getProfileName())) {
            loadStreamTable(assigned.getProfileName());
            // fill multiplier values
            multiplierView.fillAssignedProfileValues(assigned);
        }

    }

    /**
     * Assign profile to selected port
     *
     * @param profileName
     */
    private void assignProfile(String profileName, double currentBandwidth, boolean assignPrevBandwidth) {
        try {
            int portID = getSelectedPortIndex();
            // update selected profile
            AssignedProfile assignedProf = assignedPortProfileMap.get(getSelectedPortIndex());
            assignedProf.setProfileName(profileName);
            assignedProf.setAllStreamsWithLatency(allStreamWithLatency);
            StreamValidation streamValidationGraph = serverRPCMethods.assignTrafficProfile(portID, loadedProfiles);
            startStream.setDisable(false);
            // update current multiplier data 
            assignedProf.setRate(streamValidationGraph.getResult().getRate());
            multiplierView.assignNewProfile(assignedProf);
            // update multiplier value according to previous bandwidth value
            if (assignPrevBandwidth) {
                multiplierView.setSliderValue(currentBandwidth);
            }
            updateMultiplierValues(assignedProf);
        } catch (IOException | InvalidRPCResponseException | IncorrectRPCMethodException ex) {
            LOG.error("Failed to load Stream", ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * View YAML file stream table data
     *
     * @param fileName
     */
    private void loadStreamTable(String fileName) {
        try {
            File selectedFile = new File(ProfileManager.getInstance().getProfileFilePath(fileName));
            loadedProfiles = tableView.loadStreamTable(selectedFile);

            allStreamWithLatency = true;
            for (Profile profile : loadedProfiles) {
                allStreamWithLatency = allStreamWithLatency && profile.getStream().getFlowStats().isEnabled();
            }
            multiplierView.setDisable(allStreamWithLatency);
            notificationPanelHolder.setVisible(allStreamWithLatency);

        } catch (Exception ex) {
            LOG.error("Error loading stream table", ex);
        }
    }

    /**
     * Initialize in-line built component
     */
    private void initializeInlineComponent() {
        updateBtn.setGraphic(new ImageView(new Image("/icons/apply.png")));
        newProfileBtn.setGraphic(new ImageView(new Image("/icons/add_profile.png")));
        stopUpdateBtn.setGraphic(new ImageView(new Image("/icons/stop_update.png")));
        devicesTreeArrowContainer.setImage(leftArrow);
        // mapped profiles enabling with property
        profileListBox.disableProperty().bind(disableProfileProperty);
        newProfileBtn.disableProperty().bind(disableProfileProperty);
        profileDetailLabel.disableProperty().bind(disableProfileProperty);
        profileListBox.getItems().clear();
        profileListBox.setItems(FXCollections.observableArrayList(getProfilesNameList()));
        profileListBox.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            try {
                String profileName = String.valueOf(newValue);
                profileDetailContainer.setVisible(false);
                if (!"".equals(profileName) && profileName != null && !Constants.SELECT_PROFILE.equals(profileName)) {
                    profileDetailContainer.setVisible(true);
                    currentSelectedProfile = profileName;
                    loadStreamTable(profileName);
                    if (loadedProfiles.length > 0 && doAssignProfile) {
                        // assign profile to selected port
                        assignProfile(profileName, 0, false);
                    }

                    doAssignProfile = true;

                }
            } catch (Exception ex) {
                LOG.error("Error loading profile", ex);
            }
        });
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

        rightClickProfileMenu = new ContextMenu();
        addMenuItem(rightClickProfileMenu, "Play", ContextMenuClickType.PLAY, false);
        addMenuItem(rightClickProfileMenu, "Pause", ContextMenuClickType.PAUSE, false);
        addMenuItem(rightClickProfileMenu, "Stop", ContextMenuClickType.STOP, false);

        rightClickGlobalMenu = new ContextMenu();
        addMenuItem(rightClickGlobalMenu, "Release All Ports", ContextMenuClickType.RELEASE_ALL, false);
        addMenuItem(rightClickGlobalMenu, "Acquire All ports", ContextMenuClickType.ACQUIRE_ALL, false);
        addMenuItem(rightClickGlobalMenu, "Force acquire All ports", ContextMenuClickType.FORCE_ACQUIRE_ALL, false);
        addMenuItem(rightClickGlobalMenu, "Re-acquire my ports", ContextMenuClickType.ACQUIRE_MY_PORT, false);

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
        TrexApp.getPrimaryStage().addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (openDashboardCombination.match(event) && ConnectionManager.getInstance().isConnected()) {
                openStateDialog();
            } else if (openPreferencesCombination.match(event)) {
                openPreferencesWindow();
            } else if (connectCombination.match(event) && !ConnectionManager.getInstance().isConnected()) {
                openConnectDialog();
            } else if (quiteCombination.match(event)) {
                if (Util.isConfirmed("Are you sure you want to close the application?")) {
                    TrexApp.getPrimaryStage().fireEvent(new WindowEvent(TrexApp.getPrimaryStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            }
        });
        // bind async trex event property
        AsyncResponseManager.getInstance().getTrexEventProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // update
                Platform.runLater(() -> {
                    if (portManager.getUpdatePortStatusService() != null && ConnectionManager.getInstance().isConnected()) {
                        portManager.updatePortForce();
                    }
                });
            }
        });

        logContainer.getChildren().add(LogsController.getInstance().getView());
        consoleLogContainer.getChildren().add(LogsController.getInstance().getConsoleLogView());

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
        
        devicesTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
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
     * Add menu item
     *
     * @param menu
     * @param text
     * @param type
     * @param isDisable
     */
    private void addMenuItem(ContextMenu menu, String text, ContextMenuClickType type, boolean isDisable) {
        MenuItem item = new MenuItem(text);
        item.setDisable(isDisable);
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleContextMenuItemCLicked(type);
            }
        });
        menu.getItems().add(item);
    }

    /**
     * Clear stat cache
     *
     * @param event
     */
    @FXML
    public void clearStatCache(MouseEvent event) {
        cachedStatsList = StatsLoader.getInstance().getLoadedStatsList();
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
     * Open statistic dashboard view
     */
    private void openStateDialog() {
        try {
            if (DialogManager.getInstance().getNumberOfOpenedDialog() < 4) {
                DialogWindow statsWindow = new DialogWindow("Dashboard.fxml", "Dashboard", 50, 10, true, TrexApp.getPrimaryStage());
                statsWindow.setMinSize(380, 400);
                DashboardController dashboardController = (DashboardController) statsWindow.getController();
                dashboardController.init();
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
        if (PortState.getPortStatus(portManager.getPortList().get(portID).getStatus()) == PortState.PAUSE) {
            serverRPCMethods.resumeTraffic(portID);
        } else {
            startTraffic(portID);
        }
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
            enableUpdateBtn(false, false);
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
            if (PortState.getPortStatus(portManager.getPortList().get(portID).getStatus()) == PortState.PAUSE) {
                serverRPCMethods.resumeTraffic(portID);
            } else {
                enableUpdateBtn(false, false);
                serverRPCMethods.pauseTraffic(portID);
            }
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
            switch (type) {
                case ACQUIRE:
                    acquirePort();
                    break;
                case FORCE_ACQUIRE:
                    serverRPCMethods.acquireServerPort(getSelectedPortIndex(), true);
                    portManager.updatePortForce();
                    break;
                case RELEASE_ACQUIRE:
                    releasePort(getSelectedPortIndex(), true, true);
                    break;
                case PLAY:
                    doStartResume(getSelectedPortIndex());
                    break;
                case PAUSE:
                    serverRPCMethods.pauseTraffic(getSelectedPortIndex());
                    break;
                case STOP:
                    serverRPCMethods.stopPortTraffic(getSelectedPortIndex());
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
        } catch (PortAcquireException ex) {
            LOG.error("Error aquiring port", ex);
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
            if (reAssign) {
                reAssign = false;
                String assignedProfile = String.valueOf(profileListBox.getValue());
                assignProfile(assignedProfile, multiplierView.getSliderValue(), true);
            } else {
                serverRPCMethods.updateTraffic(getSelectedPortIndex(), false, MultiplierType.pps.name(), multiplierView.getPPSValue());
                // update assigned profile multiplier
                AssignedProfile assignedProf = assignedPortProfileMap.get(getSelectedPortIndex());
                updateMultiplierValues(assignedProf);
            }
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
                ConnectionManager.getInstance().disconnectSubscriber();
                ConnectionManager.getInstance().disconnectRequester();
            }
        } catch (Exception ex) {
            LOG.error("Error closing the application", ex);
            Platform.exit();
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
            Port port = portManager.getPortList().get(portIndex);
            PortState state = PortState.getPortStatus(port.getStatus());

            // enable state btn btn according to owner 
            boolean isOwner = portManager.isCurrentUserOwner(portIndex);
            switch (state) {
                case STREAMS:
                    startStream.setDisable(!isOwner);
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
            DialogWindow statsWindow = new DialogWindow("Preferences.fxml", "Preferences", 100, 50, false, TrexApp.getPrimaryStage());
            statsWindow.show(true);
        } catch (IOException ex) {
            LOG.error("Error opening preferences window", ex);

        }
    }

    /**
     * Enable update/Stop button button
     *
     * @param enable
     */
    private void enableUpdateBtn(boolean enableCounter, boolean enableUpdate) {
        Port currentPort = portManager.getPortList().get(getSelectedPortIndex());
        boolean enableUpdateBtn = enableUpdate && (reAssign || PortState.getPortStatus(currentPort.getStatus()) == PortState.TX && isContinuousStream());
        boolean startCounting = enableCounter && (reAssign || PortState.getPortStatus(currentPort.getStatus()) == PortState.TX && isContinuousStream());
        stopUpdateBtn.setVisible(startCounting);
        updateBtn.setDisable(!enableUpdateBtn);
        if (startCounting) {
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
        countdownService.resetCounter();
        enableUpdateBtn(true, true);
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
                buildPortInfoTable();
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
        LogsController.getInstance().getConsoleLogView().copyToClipboard();
    }

    /**
     * Stop running services
     */
    private void shutdownRunningServices() {
        stopRefreshingService();
        // stop port status sceduler
        portManager.stopPortStatusScheduler();
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
        reAssign = true;
        enableUpdateBtn(true, true);
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
            reAssign = true;
            enableUpdateBtn(true, true);
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
        acquirePort.setDisable(!(!forceDisable && portManager.isPortFree(selectedPort)));
        releasePort.setDisable(!(!forceDisable && portManager.isCurrentUserOwner(selectedPort)));
    }
    /**
     * Handle devicestree arrow clicking
     * @param event 
     */
    @FXML
    public void handleDevicesTreeArrowClicked(MouseEvent event){
        if(treeviewOpened){
            mainViewSplitPanel.setDividerPosition(0, 0);
            devicesTreeArrowContainer.setImage(rightArrow);
        }else{
            mainViewSplitPanel.setDividerPosition(0, 1);
            devicesTreeArrowContainer.setImage(leftArrow);
        }
        treeviewOpened = !treeviewOpened;
    }

    /**
     * Enumerator that present async stats data type
     */
    private enum AsyncStatsType {
        PORT,
        TUI,
        GLOBAL;
    }

    /**
     * Enumerator that present Context menu item type
     */
    private enum ContextMenuClickType {
        ACQUIRE,
        FORCE_ACQUIRE,
        RELEASE_ACQUIRE,
        PLAY,
        STOP,
        PAUSE,
        ACQUIRE_ALL,
        FORCE_ACQUIRE_ALL,
        RELEASE_ALL,
        ACQUIRE_MY_PORT;
    }
}

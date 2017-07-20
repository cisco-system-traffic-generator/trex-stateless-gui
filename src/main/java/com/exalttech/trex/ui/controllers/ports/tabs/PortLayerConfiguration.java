package com.exalttech.trex.ui.controllers.ports.tabs;

import com.cisco.trex.stateless.IPv6NeighborDiscoveryService;
import com.cisco.trex.stateless.TRexClient;
import com.cisco.trex.stateless.exception.ServiceModeRequiredException;
import com.cisco.trex.stateless.model.Ipv6Node;
import com.cisco.trex.stl.gui.models.IPv6Host;
import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.core.AsyncResponseManager;
import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.ui.models.ConfigurationMode;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.ui.views.logs.LogType;
import com.exalttech.trex.ui.views.logs.LogsController;
import com.exalttech.trex.util.Initialization;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import org.apache.log4j.Logger;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.namednumber.IcmpV4Type;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PortLayerConfiguration extends AnchorPane {
    @FXML
    private AnchorPane root;
    @FXML
    private ToggleGroup mode;

    @FXML
    private TextField l2Source;
    @FXML
    private TextField l3Source;

    @FXML
    private TextField l2Destination;

    @FXML
    private TextField l3Destination;

    @FXML
    private Label pingLabel;

    @FXML
    private TextField pingDestination;

    @FXML
    private Button pingCommandBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Button startScanIpv6Btn;

    @FXML
    private Button clearIpv6HostsBtn;

    @FXML
    private Label arpStatus;

    @FXML
    private Label arpLabel;

    @FXML
    RadioButton l2Mode;

    @FXML
    RadioButton l3Mode;

    @FXML
    private TableView<IPv6Host> ipv6Hosts;

    @FXML
    private Label ipv6HostsPlaceholder;
    private Label ipv6HostsDefaultPlaceholder;

    @FXML
    private TableColumn<IPv6Host, String> macColumn;
    
    @FXML
    private TableColumn<IPv6Host, String> ipv6Column;

    private IPv6NeighborDiscoveryService iPv6NDService;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    Clipboard clipboard = Clipboard.getSystemClipboard();
    
    private PortModel model;
    private LogsController guiLogger = LogsController.getInstance();
    private RPCMethods serverRPCMethods = TrexApp.injector.getInstance(RPCMethods.class);
    private static final Logger logger = Logger.getLogger(PortLayerConfiguration.class);
    private ChangeListener<ConfigurationMode> configurationModeChangeListener = (observable, prevMode, mode) -> updateControlsState();
    private Label ipv6HostsNotFoundPlaceholder = new Label("Zero IPv6 hosts found. Try to scan once again.");

    private void updateControlsState() {
        Arrays.asList(l2Source, l2Destination, l3Source, l3Destination).forEach(textField -> {
            textField.setVisible(false);
            textField.setManaged(false);
        });
        
        if (ConfigurationMode.L2.equals(model.getLayerMode())) {
            l2Source.setVisible(true);
            l2Destination.setVisible(true);
            l2Source.setManaged(true);
            l2Destination.setManaged(true);
            
            l2Mode.setSelected(true);
            arpStatus.setVisible(false);
            arpLabel.setVisible(false);
        } else {
            l3Source.setVisible(true);
            l3Destination.setVisible(true);
            l3Source.setManaged(true);
            l3Destination.setManaged(true);
            arpStatus.textProperty().bindBidirectional(model.getL3LayerConfiguration().stateProperty());
            l3Mode.setSelected(true);
            arpLabel.setVisible(true);
            arpStatus.setVisible(true);
        }
    }

    public PortLayerConfiguration() {
        Initialization.initializeFXML(this, "/fxml/ports/PortLayerConfiguration.fxml");
        
        l2Mode.setOnAction(event -> model.setLayerMode(ConfigurationMode.L2));
        
        l3Mode.setOnAction(event -> model.setLayerMode(ConfigurationMode.L3));
        
        pingCommandBtn.setOnAction(this::runPingCmd);
        
        saveBtn.setOnAction(this::saveConfiguration);
        
        macColumn.setCellValueFactory(cellData -> cellData.getValue().macAddressProperty());

        ipv6Column.setCellValueFactory(cellData -> cellData.getValue().ipAddressProperty());

        ipv6Hosts.setRowFactory( tv -> {
            TableRow<IPv6Host> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    IPv6Host rowData = row.getItem();
                    this.setAsL2DstAction(rowData);
                }
            });
            ContextMenu ctxMenu = new ContextMenu();
            
            ctxMenu.getItems().addAll(createMenuItems(row));
            row.setContextMenu(ctxMenu);

            row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                            .then(ctxMenu)
                            .otherwise((ContextMenu)null));
            return row;
        });
        
        startScanIpv6Btn.setOnAction(this::handleStartIPv6Scan);
        
        clearIpv6HostsBtn.setOnAction(e -> {
            ipv6Hosts.getItems().clear();
            ipv6Hosts.setPlaceholder(ipv6HostsDefaultPlaceholder);
        });

        ipv6HostsDefaultPlaceholder = ipv6HostsPlaceholder;
    }

    private ObservableList<MenuItem> createMenuItems(TableRow<IPv6Host> row) {
        ObservableList<MenuItem> ctxMenuItems = FXCollections.observableArrayList();
        
        MenuItem setAsL2DstMenuItem = new MenuItem("Set as L2 destination");
        setAsL2DstMenuItem.setOnAction(e -> setAsL2DstAction(row.getItem()));

        MenuItem copyMacMenuItem = new MenuItem("Copy MAC");
        copyMacMenuItem.setOnAction(e -> {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(row.getItem().getMacAddress());
            clipboard.setContent(clipboardContent);
        });
        MenuItem copyIPMenuItem = new MenuItem("Copy IP");
        copyIPMenuItem.setOnAction(e -> {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(row.getItem().getIpAddress());
            clipboard.setContent(clipboardContent);
        });

        
        
        ctxMenuItems.addAll(setAsL2DstMenuItem, copyMacMenuItem, copyIPMenuItem);
        
        return ctxMenuItems;
    }

    private void setAsL2DstAction(IPv6Host iPv6Host) {
        l2Mode.setSelected(true);
        model.setLayerMode(ConfigurationMode.L2);
        l2Destination.setText(iPv6Host.getMacAddress());
    }


    private void handleStartIPv6Scan(ActionEvent actionEvent) {
        LogsController.getInstance().appendText(LogType.INFO, "Start scanning IPv6 neighbor hosts.");
        AsyncResponseManager.getInstance().muteLogger();
        AsyncResponseManager.getInstance().suppressIncomingEvents(true);
        if (iPv6NDService == null) {
            iPv6NDService = new IPv6NeighborDiscoveryService(ConnectionManager.getInstance().getTrexClient());
        }
        startScanIpv6Btn.setDisable(true);
        ipv6Hosts.getItems().clear();
        ipv6Hosts.setPlaceholder(new Label("Scanning in progress..."));
        Task<Optional<Map<String, Ipv6Node>>> scanIpv6NeighborsTask = new Task<Optional<Map<String, Ipv6Node>>>() {
            @Override
            public Optional<Map<String, Ipv6Node>> call(){
                try {
                    return Optional.of(iPv6NDService.scan(model.getIndex(), 10));
                } catch (ServiceModeRequiredException e) {
                    AsyncResponseManager.getInstance().unmuteLogger();
                    AsyncResponseManager.getInstance().suppressIncomingEvents(false);
                    Platform.runLater(() -> {
                        ipv6Hosts.setPlaceholder(ipv6HostsDefaultPlaceholder);
                        LogsController.getInstance().appendText(LogType.ERROR, "Service mode is not enabled for port: " + model.getIndex() + ". Enable Service Mode in Control tab.");
                    });
                }
                return Optional.empty();
            }
        };
        scanIpv6NeighborsTask.setOnSucceeded(e -> {
            AsyncResponseManager.getInstance().unmuteLogger();
            AsyncResponseManager.getInstance().suppressIncomingEvents(false);
            startScanIpv6Btn.setDisable(false);
            
            Optional<Map<String, Ipv6Node>> result = scanIpv6NeighborsTask.getValue();
            result.ifPresent((hosts) -> {
                ipv6Hosts.getItems().addAll(
                        hosts.entrySet().stream()
                                .map(entry -> new IPv6Host(entry.getValue().getMac(), entry.getValue().getIp()))
                                .collect(Collectors.toList())
                );
                
                if (hosts.isEmpty()) {
                    ipv6Hosts.setPlaceholder(ipv6HostsNotFoundPlaceholder);
                }
                LogsController.getInstance().appendText(LogType.INFO, "Found " + hosts.size() + " nodes.");
                LogsController.getInstance().appendText(LogType.INFO, "Scanning complete.");
            });
        });
        
        executorService.submit(scanIpv6NeighborsTask);
    }

    private void saveConfiguration(Event event) {
        
        if (model.getPortStatus().equalsIgnoreCase("tx")) {
            guiLogger.appendText(LogType.ERROR, "Port " + model.getIndex() + " is in TX mode. Please stop traffic first.");
            return;
        }
        
        if (l2Mode.isSelected()) {
            if (Strings.isNullOrEmpty(l2Destination.getText())) {
                guiLogger.appendText(LogType.ERROR, "Destination MAC is empty. ");
                return;
            }
        } else {
            if (!validateIpAddress(l3Source.getText())) {
                return;
            }
            if(!validateIpAddress(l3Destination.getText())) {
                return;
            }
            
        }
        
        saveBtn.setDisable(true);
        saveBtn.setText("Applying...");
        Task saveConfigurationTask = new Task<Optional<String>>() {
            @Override
            public Optional<String> call(){
                TRexClient trexClient = ConnectionManager.getInstance().getTrexClient();
                if (l2Mode.isSelected()) {
                    String dstMac = l2Destination.getText();
                    try {
                        serverRPCMethods.setSetL2(model.getIndex(), dstMac);
                        guiLogger.appendText(LogType.INFO, "L2 mode configured for " + model.getIndex());
                        
                    } catch (Exception e1) {
                        logger.error("Failed to set L2 mode: " + e1.getMessage());
                    }
                } else if (l3Mode.isSelected()) {
                    try {
                        AsyncResponseManager.getInstance().muteLogger();
                        String portSrcIP = l3Source.getText();
                        String portDstIP = l3Destination.getText();
                        trexClient.serviceMode(model.getIndex(), true);
                        trexClient.setL3Mode(model.getIndex(), null, portSrcIP, portDstIP);
                        String nextHopMac = trexClient.resolveArp(model.getIndex(), portSrcIP, portDstIP);
                        if (nextHopMac != null) {
                            trexClient.setL3Mode(model.getIndex(), nextHopMac, portSrcIP, portDstIP);
                            AsyncResponseManager.getInstance().unmuteLogger();
                        }
                        return nextHopMac == null ? Optional.empty() : Optional.of(nextHopMac);
                    } catch (Exception e) {
                        logger.error("Failed to set L3 mode: " + e.getMessage());
                    } finally {
                        trexClient.serviceMode(model.getIndex(), false);
                    }
                }
                return Optional.empty();
            }
        };

        saveConfigurationTask.setOnSucceeded(e -> {
            saveBtn.setText("Apply");
            saveBtn.setDisable(false);
            Optional result = (Optional)(saveConfigurationTask.getValue());
            if (l3Mode.isSelected()) {
                String status = "unresolved";
                if (result.isPresent()) {
                    status = "resolved";
                    guiLogger.appendText(LogType.INFO, "ARP resolution for " + l3Destination.getText() +" is " + result.get());
                } else {
                    guiLogger.appendText(LogType.ERROR, "ARP resolution status: FAILED");
                }
                arpStatus.setText(status);
            }
        });

        new Thread(saveConfigurationTask).start();
    }
    
    private boolean validateIpAddress(String ip) {
        if (Strings.isNullOrEmpty(ip)) {
            guiLogger.appendText(LogType.ERROR, "Empty IP address.");
            return false;
        }
        try{
            InetAddresses.forString(ip);
            return true;
        } catch(IllegalArgumentException e) {
            guiLogger.appendText(LogType.ERROR, "Malformed IP address.");
        }
        return false;
    }
    
    private void runPingCmd(Event event) {
        if (model.getPortStatus().equalsIgnoreCase("tx")) {
            guiLogger.appendText(LogType.ERROR, "Port " + model.getIndex() + " is in TX mode. Please stop traffic first.");
            return;
        }
        
        if (Strings.isNullOrEmpty(pingDestination.getText())) {
            guiLogger.appendText(LogType.ERROR, "Empty ping destination address.");
            return;
        }
        if (!model.getL3LayerConfiguration().getState().equalsIgnoreCase("resolved")) {
            guiLogger.appendText(LogType.ERROR, "ARP resolution required. Configure L3 mode properly.");
            return;
        }

        pingCommandBtn.setDisable(true);
        
        Task<Void> pingTask = new Task<Void>() {
            @Override
            public Void call(){
                TRexClient trexClient = ConnectionManager.getInstance().getTrexClient();
                trexClient.serviceMode(model.getIndex(), true);
                String savedPingIPv4 = pingDestination.getText();
                guiLogger.appendText(LogType.PING, " Start ping " + savedPingIPv4 + ":");
                
                AsyncResponseManager.getInstance().muteLogger();
                try {
                    int icmp_id = new Random().nextInt(100);
                    for(int icmp_sec = 1; icmp_sec < 6; icmp_sec++) {
                        EthernetPacket reply = trexClient.sendIcmpEcho(model.getIndex(), savedPingIPv4, icmp_id, icmp_sec, 1000);
                        if (reply != null) {
                            IpV4Packet ip = reply.get(IpV4Packet.class);
                            String ttl = String.valueOf(ip.getHeader().getTtlAsInt());
                            IcmpV4CommonPacket echoReplyPacket = reply.get(IcmpV4CommonPacket.class);
                            IcmpV4Type replyType = echoReplyPacket.getHeader().getType();
                            if (IcmpV4Type.ECHO_REPLY.equals(replyType)) {
                                guiLogger.appendText(LogType.PING, " Reply from " + savedPingIPv4 + " size=" + reply.getRawData().length + " ttl=" + ttl + " icmp_sec=" + icmp_sec);
                            } else if (IcmpV4Type.DESTINATION_UNREACHABLE.equals(replyType)) {
                                guiLogger.appendText(LogType.PING, " Destination host unreachable");
                            }
                        } else {
                            guiLogger.appendText(LogType.PING, " Request timeout for icmp_seq " + icmp_sec);
                        }
                    }
                    guiLogger.appendText(LogType.PING, " Ping finished.");
                } catch (UnknownHostException e) {
                    guiLogger.appendText(LogType.PING, " Unknown host");
                } finally {
                    pingCommandBtn.setDisable(false);
                    trexClient.serviceMode(model.getIndex(), false);
                    AsyncResponseManager.getInstance().unmuteLogger();
                }
                return null;
            }
        };
        
        pingTask.setOnSucceeded(e -> pingCommandBtn.setDisable(false));
        
        new Thread(pingTask).start();
    }
    
    public void bindModel(PortModel model) {
        unbindAll();
        this.model = model;
        
        l2Destination.textProperty().bindBidirectional(this.model.getL2LayerConfiguration().dstProperty());
        l2Source.textProperty().bindBidirectional(this.model.getL2LayerConfiguration().srcProperty());
        
        l3Destination.textProperty().bindBidirectional(this.model.getL3LayerConfiguration().dstProperty());
        l3Source.textProperty().bindBidirectional(this.model.getL3LayerConfiguration().srcProperty());
        
        updateControlsState();
        
        this.model.layerConfigurationTypeProperty().addListener(configurationModeChangeListener);
    }

    private void unbindAll() {
        if(model == null) {
            return;
        }
        
        l2Destination.textProperty().unbindBidirectional(this.model.getL2LayerConfiguration().dstProperty());
        l2Source.textProperty().unbindBidirectional(this.model.getL2LayerConfiguration().srcProperty());

        l3Destination.textProperty().unbindBidirectional(this.model.getL3LayerConfiguration().dstProperty());
        l3Source.textProperty().unbindBidirectional(this.model.getL3LayerConfiguration().srcProperty());
        
        arpStatus.textProperty().unbindBidirectional(model.getL3LayerConfiguration().stateProperty());
        model.layerConfigurationTypeProperty().removeListener(configurationModeChangeListener);
    }
}

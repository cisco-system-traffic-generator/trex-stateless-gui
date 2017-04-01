package com.exalttech.trex.ui.controllers.ports.tabs;

import com.cisco.trex.stateless.TRexClient;
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
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.apache.log4j.Logger;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.namednumber.IcmpV4Type;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public class PortLayerConfiguration extends BorderPane {
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
    private Label arpStatus;

    @FXML
    private Label arpLabel;

    @FXML
    RadioButton l2Mode;

    @FXML
    RadioButton l3Mode;
    private PortModel model;
    private LogsController guiLogger = LogsController.getInstance();
    private RPCMethods serverRPCMethods = TrexApp.injector.getInstance(RPCMethods.class);
    private static final Logger logger = Logger.getLogger(PortLayerConfiguration.class);
    private ChangeListener<ConfigurationMode> configurationModeChangeListener = (observable, prevMode, mode) -> updateControlsState();

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
            pingLabel.setVisible(false);
            pingDestination.setVisible(false);
            pingCommandBtn.setVisible(false);
        } else {
            l3Source.setVisible(true);
            l3Destination.setVisible(true);
            l3Source.setManaged(true);
            l3Destination.setManaged(true);
            arpStatus.textProperty().bindBidirectional(model.getL3LayerConfiguration().stateProperty());
            l3Mode.setSelected(true);
            arpLabel.setVisible(true);
            arpStatus.setVisible(true);
            pingLabel.setVisible(true);
            pingDestination.setVisible(true);
            pingCommandBtn.setVisible(true);
        }
    }

    public PortLayerConfiguration() {
        Initialization.initializeFXML(this, "/fxml/ports/PortLayerConfiguration.fxml");
        
        l2Mode.setOnAction(event -> model.setLayerMode(ConfigurationMode.L2));
        
        l3Mode.setOnAction(event -> model.setLayerMode(ConfigurationMode.L3));
        
        pingCommandBtn.setOnAction(this::runPingCmd);
        
        saveBtn.setOnAction(this::saveConfiguration);
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
        
        l2Destination.textProperty().unbind();
        l2Source.textProperty().unbind();
        l3Destination.textProperty().bindBidirectional(this.model.getL3LayerConfiguration().dstProperty());
        l3Source.textProperty().unbind();
        arpStatus.textProperty().unbindBidirectional(model.getL3LayerConfiguration().stateProperty());
        model.layerConfigurationTypeProperty().removeListener(configurationModeChangeListener);
    }
}

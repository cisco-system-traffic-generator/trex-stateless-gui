package com.cisco.trex.stl.gui.controllers.capture;

import com.cisco.trex.stateless.model.capture.CapturedPkt;
import com.cisco.trex.stl.gui.models.CapturedPktModel;
import com.cisco.trex.stl.gui.services.capture.PktCaptureService;
import com.cisco.trex.stl.gui.services.capture.PktCaptureServiceException;
import com.exalttech.trex.util.Initialization;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class MonitorController extends BorderPane {

    @FXML
    private Button startStopBtn;
    
    @FXML
    private Button stopBtn;
    
    @FXML
    private Button clearBtn;
    
    @FXML
    private PortFilterController portFilter;
    
    @FXML
    private TableView<CapturedPktModel> capturedPkts;

    @FXML
    private TableColumn<CapturedPktModel, String> number;
    
    @FXML
    private TableColumn<CapturedPktModel, String> port;
    
    @FXML
    private TableColumn<CapturedPktModel, String> mode;
    
    @FXML
    private TableColumn<CapturedPktModel, String> time;
    
    @FXML
    private TableColumn<CapturedPktModel, String> ipDst;
    
    @FXML
    private TableColumn<CapturedPktModel, String> ipSrc;
    
    @FXML
    private TableColumn<CapturedPktModel, String> type;
    
    @FXML
    private TableColumn<CapturedPktModel, String> length;
    
    @FXML
    private TableColumn<CapturedPktModel, String> info;
    
    private PktCaptureService pktCaptureService = new PktCaptureService();
    
    private double starTs = 0;
    
    private int monitorId = 0;
    
    public MonitorController() {
        Initialization.initializeFXML(this, "/fxml/pkt_capture/Monitor.fxml");

        number.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asString());
        port.setCellValueFactory(cellData -> cellData.getValue().portProperty().asString());
        mode.setCellValueFactory(cellData -> cellData.getValue().modeProperty());
        time.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        ipDst.setCellValueFactory(cellData -> cellData.getValue().dstProperty());
        ipSrc.setCellValueFactory(cellData -> cellData.getValue().srcProperty());
        type.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        length.setCellValueFactory(cellData -> cellData.getValue().lengthProperty().asString());
        info.setCellValueFactory(cellData -> cellData.getValue().infoProperty());
        
        pktCaptureService.setOnSucceeded(this::handleOnPktsReceived);
        
        startStopBtn.setOnAction(this::handleStartStopMonitorAction);
        clearBtn.setOnAction(this::handleClearMonitorAction);
    }

    private void handleOnPktsReceived(WorkerStateEvent workerStateEvent) {
        synchronized (capturedPkts) {
            if (starTs == 0) {
                starTs = pktCaptureService.getValue().getStartTimeStamp();
            }
            pktCaptureService.getValue().getPkts().stream()
                                        .map(this::toModel)
                                        .filter(Objects::nonNull)
                                        .forEach(pktModel -> capturedPkts.getItems().add(pktModel));
        }
    }

    public void handleStartStopMonitorAction(ActionEvent event) {
        try {
            if(monitorId != 0) {
                pktCaptureService.stopMonitor();
                pktCaptureService.cancel();
                starTs = 0;
                startStopBtn.setText("Start");
                portFilter.setDisable(false);
                monitorId = 0;
            } else {
                List<Integer> rxPorts = portFilter.getRxPorts();
                List<Integer> txPorts = portFilter.getTxPorts();
                
                if (rxPorts.isEmpty() && txPorts.isEmpty()) {
                    showError("Zero ports selected. To capture packets please specify ports.");
                    return;
                }
                
                pktCaptureService.reset();
                monitorId = pktCaptureService.startMonitor(rxPorts, txPorts);
                portFilter.setDisable(true);
                startStopBtn.setText("Stop");
            }
            
        } catch (PktCaptureServiceException e) {
            // TODO: logger
        }
    }
    
    public void handleClearMonitorAction(ActionEvent event) {
        synchronized (capturedPkts) {
            capturedPkts.getItems().clear();
        }
    }
    
    private CapturedPktModel toModel(CapturedPkt pkt) {
        byte[] pktBin;
        try {
            pktBin = Base64.getDecoder().decode(pkt.getBinary());

            Stack<String> headers = new Stack<>();
            headers.push("Ether");
            EthernetPacket etherPkt = EthernetPacket.newPacket(pktBin, 0, pktBin.length);

            Map<String, Object> info = new HashMap<>();
            info.put("info", "");
            
            EtherType l3Type = etherPkt.getHeader().getType();
            if(l3Type.equals(EtherType.ARP)) {
                headers.push("ARP");
                info = parseARP((EthernetPacket) etherPkt.getPayload());
            } else if (l3Type.equals(EtherType.DOT1Q_VLAN_TAGGED_FRAMES)) {
                headers.push("Dot1Q");
                info = parseDot1Q(headers, (Dot1qVlanTagPacket) etherPkt.getPayload());
            } else if (l3Type.equals(EtherType.IPV4)) {
                headers.push("IPv4");
                info = parseIP(headers, (IpV4Packet) etherPkt.getPayload());
            } else if (l3Type.equals(EtherType.IPV6)) {
                headers.push("IPv6");
                info = parseIP(headers, (IpV6Packet) etherPkt.getPayload());
            }
            
            Double time = abs(starTs - pkt.getTimeStamp());
            
            return new CapturedPktModel(pkt.getIndex(),
                                        pkt.getPort(),
                                        pkt.getOrigin(),
                                        time,
                                        (String) info.get("dst"),
                                        (String) info.get("src"),
                                        headers.peek(),
                                        pktBin.length,
                                        (String) info.get("info"));
        } catch (Exception e) {
            return null;
        }
    }
    
    private Map<String, Object> parseARP(EthernetPacket pkt) {
        Map<String, Object> pktInfo = new HashMap<>();

        pktInfo.put("src", pkt.getHeader().getSrcAddr().toString());
        pktInfo.put("dst", pkt.getHeader().getDstAddr().toString());
        ArpPacket.ArpHeader arp = (ArpPacket.ArpHeader) pkt.getPayload().getHeader();
        if (arp.getOperation().equals(ArpOperation.REQUEST)) {
            pktInfo.put("info", String.format("[Request] Who has %s tell %s", arp.getDstProtocolAddr().toString().substring(1), arp.getSrcProtocolAddr().toString().substring(1)));
        } else if (arp.getOperation().equals(ArpOperation.REPLY)) {
            pktInfo.put("info", String.format("[Reply] %s is at %s", arp.getSrcProtocolAddr().toString().substring(1), arp.getSrcHardwareAddr().toString()));
        }
        return pktInfo;
    }
    
    private Map<String, Object> parseDot1Q(Stack<String> headers, Dot1qVlanTagPacket pkt) {
        headers.push("Dot1Q");
        
        EtherType etherType = pkt.getHeader().getType();
        
        if(EtherType.DOT1Q_VLAN_TAGGED_FRAMES.equals(etherType)) {
            return parseDot1Q(headers, (Dot1qVlanTagPacket) pkt.getPayload());
        } else if (EtherType.IPV4.equals(etherType)) {
            return parseIP(headers, (IpV4Packet) pkt.getPayload());
        } else {
            return parseIP(headers, (IpV6Packet) pkt.getPayload());
        }

    }
    
    private <T extends IpPacket> Map<String, Object> parseIP(Stack<String> headers, T pkt) {
        headers.push(pkt instanceof IpV4Packet ? "IPv4" : "IPv6");
        Map<String, Object> result = new HashMap<>();
        String src = pkt.getHeader().getSrcAddr().toString();
        String dst = pkt.getHeader().getDstAddr().toString();
        result.put("src", pkt instanceof IpV4Packet ? src.substring(1) : src);
        result.put("dst", pkt instanceof IpV4Packet ? dst.substring(1) : dst);

        IpNumber protocol = pkt.getHeader().getProtocol();

        if (IpNumber.UDP.equals(protocol)) {
            result.putAll(parseUDP(headers, (UdpPacket) pkt.getPayload()));
        } else if (IpNumber.TCP.equals(protocol)) {
            result.putAll(parseTCP(headers, (TcpPacket) pkt.getPayload()));
        }

        return result;
    }
    
    private Map<String, Object> parseTCP(Stack<String> headers, TcpPacket pkt) {
        headers.push("TCP");
        Map<String, Object> result = new HashMap<>();

        TcpPacket.TcpHeader tcpHeader = pkt.getHeader();
        List<String> enabledFlags = new ArrayList<>();
        if (tcpHeader.getUrg()) {
            enabledFlags.add("URG");
        }
        if (tcpHeader.getAck()) {
            enabledFlags.add("ACK");
        }
        if (tcpHeader.getPsh()) {
            enabledFlags.add("PSH");
        }
        if (tcpHeader.getRst()) {
            enabledFlags.add("RST");
        }
        if (tcpHeader.getSyn()) {
            enabledFlags.add("SYN");
        }
        if (tcpHeader.getFin()) {
            enabledFlags.add("FIN");
        }
        
        String flags = enabledFlags.stream().collect(Collectors.joining(", "));
        
        String info = String.format("%s -> %s [%s] Seq=%s Win=%s Ack=%s Len=%s",
                                    tcpHeader.getSrcPort().valueAsInt(),
                                    tcpHeader.getDstPort().valueAsInt(),
                                    flags,
                                    tcpHeader.getSequenceNumber(),
                                    tcpHeader.getWindowAsInt(),
                                    tcpHeader.getAcknowledgmentNumber(),
                                    tcpHeader.length());
        result.put("info", info);
        return result;
    }
    
    private Map<String, Object> parseUDP(Stack<String> headers, UdpPacket pkt) {
        headers.push("UDP");
        Map<String, Object> result = new HashMap<>();
        String info = String.format("Source port: %s Destination port: %s", pkt.getHeader().getSrcPort().toString(), pkt.getHeader().getDstPort().toString());
        result.put("info", info);
        return result;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();

    }
}

package com.exalttech.trex.ui.models;

import com.cisco.trex.stateless.TRexClient;
import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.remote.exceptions.PortAcquireException;
import com.exalttech.trex.ui.views.logs.LogType;
import com.exalttech.trex.ui.views.logs.LogsController;
import javafx.application.Platform;
import javafx.beans.property.*;
import org.testng.util.Strings;

import java.util.HashMap;
import java.util.Map;

public class PortModel {

    private IntegerProperty index = new SimpleIntegerProperty();
    private StringProperty portDriver = new SimpleStringProperty();
    private StringProperty rxFilterMode = new SimpleStringProperty();
    private StringProperty portSpeed = new SimpleStringProperty();
    private StringProperty portStatus = new SimpleStringProperty();
    private StringProperty capturingMode = new SimpleStringProperty();
    
    private BooleanProperty linkStatus = new SimpleBooleanProperty();
    private BooleanProperty linkControlSupport = new SimpleBooleanProperty();

    private BooleanProperty multicast = new SimpleBooleanProperty();
    private BooleanProperty multicastSupport = new SimpleBooleanProperty(true);
    
    private BooleanProperty promiscuousMode = new SimpleBooleanProperty();
    private BooleanProperty promiscuousSupport = new SimpleBooleanProperty(true);
    
    private BooleanProperty ledControl = new SimpleBooleanProperty();
    private BooleanProperty ledControlSupport = new SimpleBooleanProperty();
    
    private ObjectProperty<FlowControl> flowControl = new SimpleObjectProperty<>(FlowControl.NONE);
    private BooleanProperty flowControlSupport = new SimpleBooleanProperty();
    
    Map<String, BooleanProperty> supportCapabilities = new HashMap<>(4);
    
    private StringProperty owner = new SimpleStringProperty();
    
    private StringProperty numaMode = new SimpleStringProperty();
    private StringProperty pciAddress = new SimpleStringProperty();
    private StringProperty rxQueueing = new SimpleStringProperty();
    private StringProperty gratARP = new SimpleStringProperty();
    private StringProperty vlan = new SimpleStringProperty();
    
    private ObjectProperty<ConfigurationMode> layerConfigurationType = new SimpleObjectProperty<>();
    
    private PortLayerConfigurationModel l2Configuration;
    private PortLayerConfigurationModel l3Configuration;
    
    private BooleanProperty serviceModeProperty = new SimpleBooleanProperty(false);
    
    private BooleanProperty isOwnedProperty = new SimpleBooleanProperty(false);
    private RPCMethods serverRPCMethods;
    private LogsController guiLogger;
    private boolean streamLoaded;
    private BooleanProperty isTransmitProperty = new SimpleBooleanProperty(false);

    private PortModel() {
        guiLogger = LogsController.getInstance();
        serverRPCMethods = TrexApp.injector.getInstance(RPCMethods.class);
        supportCapabilities.put("link", linkControlSupport);
        supportCapabilities.put("led", ledControlSupport);
        supportCapabilities.put("flowControl", flowControlSupport);
        supportCapabilities.put("multicast", multicastSupport);
        supportCapabilities.put("promiscuousMode", multicastSupport);
        supportCapabilities.put("serviceMode", new SimpleBooleanProperty(true));
    }
    
    public static PortModel createModelFrom(Port port) {
        PortModel model = new PortModel();
        model.index.bindBidirectional(port.indexProperty());
        model.portDriver.bindBidirectional(port.driverProper());
        model.rxFilterMode.bindBidirectional(port.rxFilterModeProperty());
        model.multicast.bindBidirectional(port.multicastProperty());
        model.promiscuousMode.bindBidirectional(port.promiscuousProperty());
        
        model.owner.bindBidirectional(port.ownerProperty());
        port.ownerProerty.addListener((observable, oldVal, newVal) -> {
            String currentUser = ConnectionManager.getInstance().getClientName();
            model.setIsOwned(currentUser.equalsIgnoreCase(newVal));
        });
        
        model.portSpeed.bind(port.getAttr().speedProperty().asString());
        model.portStatus.addListener((_o, o, n) -> model.isTransmitProperty.set(n.equalsIgnoreCase("tx") || n.equalsIgnoreCase("pause")));
        model.portStatus.bindBidirectional(port.statusProerty());
        model.capturingMode.bind(port.captureStatusProperty());
        model.linkStatus.bindBidirectional(port.linkProperty());
        model.ledControl.bindBidirectional(port.ledProperty());
        model.numaMode.bind(port.numaProerty().asString());
        model.pciAddress.bind(port.pciAddrProperty());
        model.rxQueueing.bind(port.rxQueueProperty());
        PortStatus.PortStatusResult.PortStatusResultRxInfo.PortStatusResultRxInfoGratArp grat_arp = port.getRx_info().getGrat_arp();
        model.gratARP.bind(grat_arp.stateProperty());
        
        model.flowControl.setValue(port.getFlowControl());
        model.serviceModeProperty.bindBidirectional(port.serviceModeProerty());

        model.initHandlers(port);
        
        PortStatus.PortStatusResult.PortStatusResultAttr.PortStatusResultAttrLayerCfg layerConfiguration = port.getAttr().getLayer_cfg();
        
        PortStatus.PortStatusResult.PortStatusResultAttr.PortStatusResultAttrLayerCfg.PortStatusResultAttrLayerCfgEther l2 = layerConfiguration.getEther();
        model.l2Configuration = new PortLayerConfigurationModel(ConfigurationMode.L2, l2.getSrc(), l2.getDst(), l2.getState());
        model.l2Configuration.dstProperty().bindBidirectional(port.getAttr().getLayer_cfg().getEther().dstProperty());

        PortStatus.PortStatusResult.PortStatusResultAttr.PortStatusResultAttrLayerCfg.PortStatusResultAttrLayerCfgIPv4 l3 = layerConfiguration.getIpv4();
        model.l3Configuration = new PortLayerConfigurationModel(ConfigurationMode.L3, l3.getSrc(), l3.getDst(), l3.getState());
        model.l3Configuration.srcProperty().bindBidirectional(port.getAttr().getLayer_cfg().getIpv4().srcProperty());
        model.l3Configuration.dstProperty().bindBidirectional(port.getAttr().getLayer_cfg().getIpv4().dstProperty());

        if (Strings.isNullOrEmpty(l3.getSrc()) && Strings.isNullOrEmpty(l3.getDst())) {
            model.layerConfigurationType.setValue(ConfigurationMode.L2);
        } else {
            model.layerConfigurationType.setValue(ConfigurationMode.L3);
        }
        model.linkControlSupportProperty().set(port.is_link_supported);
        model.ledControlProperty().set(port.is_led_supported);
        model.flowControlSupportProperty().set(port.is_fc_supported);
        return model;
    }

    private void initHandlers(Port port) {
        int portIndex = index.get();
        linkStatus.addListener((observable, oldValue, newValue) -> {
            try {
                if (!isOwnedProperty.get()) {
                    return;
                }
                serverRPCMethods.setPortAttribute(portIndex, newValue, null, null, null, null);
                port.getAttr().getLink().setUp(newValue);
            } catch (Exception e) {
                guiLogger.appendText(LogType.ERROR, "Filed to set attributes for port " + portIndex);
            }
        });

        ledControl.addListener((observable, oldValue, newValue) -> {
            try {
                if (!isOwnedProperty.get()) {
                    return;
                }
                serverRPCMethods.setPortAttribute(portIndex, null, null, newValue, null, null);
                port.getAttr().getLed().setOn(newValue);
            } catch (Exception e) {
                guiLogger.appendText(LogType.ERROR, "Filed to set attributes for port " + portIndex);
            }
        });

        multicast.addListener((observable, oldValue, newValue) -> {
            try {
                if (!isOwnedProperty.get()) {
                    return;
                }
                serverRPCMethods.setPortAttribute(portIndex, null, null, null, null, newValue);
                port.getAttr().getMulticast().setEnabled(newValue);
            } catch (Exception e) {
                guiLogger.appendText(LogType.ERROR, "Filed to set attributes for port " + portIndex);
            }
        });

        promiscuousMode.addListener((observable, oldValue, newValue) -> {
            try {
                if (!isOwnedProperty.get()) {
                    return;
                }
                serverRPCMethods.setPortAttribute(portIndex, null, newValue, null, null, null);
                port.getAttr().getPromiscuous().setEnabled(newValue);
            } catch (Exception e) {
                guiLogger.appendText(LogType.ERROR, "Filed to set attributes for port " + portIndex);
            }
        });

        flowControl.addListener((observable, oldValue, newValue) -> {
            try {
                serverRPCMethods.setPortAttribute(portIndex, null, null, null, newValue.getVal(), null);
                port.getAttr().getFc().setMode(newValue.getVal());
            } catch (Exception e) {
                guiLogger.appendText(LogType.ERROR, "Filed to set attributes for port " + portIndex);
            }
        });

        serviceModeProperty.addListener((observable, oldValue, newValue) -> {
            if (!isOwnedProperty.get()) {
                return;
            }
            com.cisco.trex.stateless.model.PortStatus status = ConnectionManager.getInstance().getTrexClient().serviceMode(portIndex, newValue);
            if (!newValue.equals(status.service)) {
                guiLogger.appendText(LogType.ERROR, "Filed to change service mode for port " + portIndex+". Check active capturing mode or enabled rx queues.");
                Platform.runLater(() -> serviceModeProperty.set(oldValue));
            } else {
                port.setService(status.service);
            }
        });
    }

    public void acquire() throws PortAcquireException {
        serverRPCMethods.acquireServerPort(getIndex(), false);
        setIsOwned(true);
    }

    public void release() throws PortAcquireException {
        serverRPCMethods.releasePort(getIndex(), false);
        setIsOwned(false);
    }

    public String getVlan() {
        return vlan.get();
    }

    public StringProperty vlanProperty() {
        return vlan;
    }

    public void setVlan(String vlan) {
        this.vlan.set(vlan);
    }

    public boolean getServiceMode() {
        return serviceModeProperty.get();
    }

    public BooleanProperty serviceModeProperty() {
        return serviceModeProperty;
    }

    public BooleanProperty getSupport(String capId) {
        BooleanProperty cap = supportCapabilities.get(capId); 
        return cap != null ? cap : new SimpleBooleanProperty(false);
    }
    
    public BooleanProperty linkControlSupportProperty() {
        return linkControlSupport;
    }

    public BooleanProperty multicastSupportProperty() {
        return multicastSupport;
    }

    public BooleanProperty promiscuousSupportProperty() {
        return promiscuousSupport;
    }

    public BooleanProperty ledControlSupportProperty() {
        return ledControlSupport;
    }

    public BooleanProperty flowControlSupportProperty() {
        return flowControlSupport;
    }

    public int getIndex() {
        return index.get();
    }

    public IntegerProperty indexProperty() {
        return index;
    }
    
    public BooleanProperty isOwnedProperty() {
        return isOwnedProperty;
    }
    
    public void setIsOwned(boolean isOwned) {
        isOwnedProperty.set(isOwned);
    }

    public String getPortDriver() {
        return portDriver.get();
    }

    public StringProperty portDriverProperty() {
        return portDriver;
    }

    public String getRxFilterMode() {
        return rxFilterMode.get();
    }

    public StringProperty rxFilterModeProperty() {
        return rxFilterMode;
    }

    public boolean getMulticast() {
        return multicast.get();
    }

    public BooleanProperty multicastProperty() {
        return multicast;
    }

    public boolean getPromiscuousMode() {
        return promiscuousMode.get();
    }

    public BooleanProperty promiscuousModeProperty() {
        return promiscuousMode;
    }

    public String getOwner() {
        return owner.get();
    }

    public StringProperty ownerProperty() {
        return owner;
    }

    public String getPortSpeed() {
        return portSpeed.get();
    }

    public StringProperty portSpeedProperty() {
        return portSpeed;
    }

    public String getPortStatus() {
        return portStatus.get();
    }

    public StringProperty portStatusProperty() {
        return portStatus;
    }

    public String getCapturingMode() {
        return capturingMode.get();
    }

    public StringProperty capturingModeProperty() {
        return capturingMode;
    }

    public boolean getLinkStatus() {
        return linkStatus.get();
    }

    public BooleanProperty linkStatusProperty() {
        return linkStatus;
    }

    public boolean getLedControl() {
        return ledControl.get();
    }

    public BooleanProperty ledControlProperty() {
        return ledControl;
    }

    public String getNumaMode() {
        return numaMode.get();
    }

    public StringProperty numaModeProperty() {
        return numaMode;
    }

    public String getPciAddress() {
        return pciAddress.get();
    }

    public StringProperty pciAddressProperty() {
        return pciAddress;
    }

    public String getRxQueueing() {
        return rxQueueing.get();
    }

    public StringProperty rxQueueingProperty() {
        return rxQueueing;
    }

    public String getGratARP() {
        return gratARP.get();
    }

    public StringProperty gratARPProperty() {
        return gratARP;
    }

    public FlowControl getFlowControl() {
        return flowControl.get();
    }

    public ObjectProperty<FlowControl> flowControlProperty() {
        return flowControl;
    }
    
    public void setLayerMode(ConfigurationMode mode) {
        layerConfigurationType.setValue(mode);
    }
    
    public ConfigurationMode getLayerMode() {
        return layerConfigurationType.get();
    }
    
    public ObjectProperty<ConfigurationMode> layerConfigurationTypeProperty() {
        return layerConfigurationType;
    }
    
    public PortLayerConfigurationModel getL2LayerConfiguration() {
        return l2Configuration;
    }
    
    public PortLayerConfigurationModel getL3LayerConfiguration() {
        return l3Configuration;
    }

    public void setStreamLoaded(boolean streamLoaded) {
        this.streamLoaded = streamLoaded;
    }
    
    public boolean isStreamLoaded() {
        return streamLoaded;
    }

    public BooleanProperty transmitStateProperty() {
        return isTransmitProperty;
    }
}

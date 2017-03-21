package com.exalttech.trex.ui.models;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.ui.views.logs.LogType;
import com.exalttech.trex.ui.views.logs.LogsController;
import javafx.beans.property.*;

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
    
    private ObjectProperty<ConfigurationMode> layerConfigurationType = new SimpleObjectProperty<>();
    
    private PortLayerConfigurationModel l2Configuration;
    private PortLayerConfigurationModel l3Configuration;
    
    private BooleanProperty isOwnedProperty = new SimpleBooleanProperty(false);
    private RPCMethods serverRPCMethods;
    private LogsController guiLogger;

    private PortModel() {
        guiLogger = LogsController.getInstance();
        serverRPCMethods = TrexApp.injector.getInstance(RPCMethods.class);
        supportCapabilities.put("link", linkControlSupport);
        supportCapabilities.put("led", ledControlSupport);
        supportCapabilities.put("flowControl", flowControlSupport);
        supportCapabilities.put("multicast", multicastSupport);
        supportCapabilities.put("promiscuousMode", multicastSupport);
    }
    
    public static PortModel createModelFrom(Port port) {
        PortModel model = new PortModel();
        model.index.setValue(port.getIndex());
        model.portDriver.setValue(port.getDriver());
        model.rxFilterMode.setValue(port.getAttr().getRx_filter_mode());
        model.multicast.setValue(port.getAttr().getMulticast().getEnabled());
        model.promiscuousMode.setValue(port.getAttr().getPromiscuous().getEnabled());
        model.owner.setValue(port.getOwner());
        model.portSpeed.setValue(String.valueOf(port.getSpeed()));
        model.portStatus.setValue(port.getStatus());
        model.capturingMode.setValue(port.getCaptureStatus());
        model.linkStatus.setValue(port.getLink());
        model.ledControl.setValue(port.getLed());
        model.numaMode.set(String.valueOf(port.getNuma()));
        model.pciAddress.setValue(port.getPci_addr());
        model.rxQueueing.setValue(port.getRx_info().getQueue().isIs_active() ? "On" : "Off");
        model.gratARP.setValue(port.getRx_info().getGrat_arp().isIs_active() ? "On" : "Off");
        model.flowControl.setValue(port.getFlowControl());

        model.initHandlers(port);
        
        PortStatus.PortStatusResult.PortStatusResultAttr.PortStatusResultAttrLayerCfg layerConfiguration = port.getAttr().getLayer_cfg();
        
        PortStatus.PortStatusResult.PortStatusResultAttr.PortStatusResultAttrLayerCfg.PortStatusResultAttrLayerCfgEther l2 = layerConfiguration.getEther();
        model.l2Configuration = new PortLayerConfigurationModel(ConfigurationMode.L2, l2.getSrc(), l2.getDst(), l2.getState());

        PortStatus.PortStatusResult.PortStatusResultAttr.PortStatusResultAttrLayerCfg.PortStatusResultAttrLayerCfgIPv4 l3 = layerConfiguration.getIpv4();
        model.l3Configuration = new PortLayerConfigurationModel(ConfigurationMode.L3, l3.getSrc(), l3.getDst(), l3.getState());

        if (l3.getSrc() == null && l3.getDst() == null) {
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
                serverRPCMethods.setPortAttribute(portIndex, newValue, null, null, null, null);
                port.getAttr().getLink().setUp(newValue);
            } catch (Exception e) {
                guiLogger.appendText(LogType.ERROR, "Filed to set attributes for port " + portIndex);
            }
        });

        ledControl.addListener((observable, oldValue, newValue) -> {
            try {
                serverRPCMethods.setPortAttribute(portIndex, null, null, newValue, null, null);
                port.getAttr().getLed().setOn(newValue);
            } catch (Exception e) {
                guiLogger.appendText(LogType.ERROR, "Filed to set attributes for port " + portIndex);
            }
        });

        multicast.addListener((observable, oldValue, newValue) -> {
            try {
                serverRPCMethods.setPortAttribute(portIndex, null, null, null, null, newValue);
                port.getAttr().getMulticast().setEnabled(newValue);
            } catch (Exception e) {
                guiLogger.appendText(LogType.ERROR, "Filed to set attributes for port " + portIndex);
            }
        });

        promiscuousMode.addListener((observable, oldValue, newValue) -> {
            try {
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
}

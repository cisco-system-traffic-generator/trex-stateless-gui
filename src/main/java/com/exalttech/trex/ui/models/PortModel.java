package com.exalttech.trex.ui.models;

import javafx.beans.property.*;

public class PortModel {

    private StringProperty portDriver = new SimpleStringProperty();
    private StringProperty rxFilterMode = new SimpleStringProperty();
    private BooleanProperty multicast = new SimpleBooleanProperty();
    private BooleanProperty promiscuousMode = new SimpleBooleanProperty();
    private StringProperty owner = new SimpleStringProperty();
    private StringProperty portSpeed = new SimpleStringProperty();
    private StringProperty portStatus = new SimpleStringProperty();
    private StringProperty capturingMode = new SimpleStringProperty();
    private BooleanProperty linkStatus = new SimpleBooleanProperty();
    private BooleanProperty ledStatus = new SimpleBooleanProperty();
    private StringProperty numaMode = new SimpleStringProperty();
    private StringProperty pciAddress = new SimpleStringProperty();
    private StringProperty rxQueueing = new SimpleStringProperty();
    private StringProperty gratARP = new SimpleStringProperty();
    private ObjectProperty<FlowControl> flowControl = new SimpleObjectProperty<>(FlowControl.NONE);
    
    private PortModel() {}
    
    public static PortModel createModelFrom(Port port) {
        PortModel model = new PortModel();
        model.portDriver.setValue(port.getDriver());
        model.rxFilterMode.setValue(port.getAttr().getRx_filter_mode());
        model.multicast.setValue(port.getAttr().getMulticast().getEnabled());
        model.promiscuousMode.setValue(port.getAttr().getPromiscuous().getEnabled());
        model.owner.setValue(port.getOwner());
        model.portSpeed.setValue(String.valueOf(port.getSpeed()));
        model.portStatus.setValue(port.getStatus());
        model.capturingMode.setValue(port.getCaptureStatus());
        model.linkStatus.setValue(port.getAttr().getLink().getUp());
        model.ledStatus.setValue(port.getAttr().getLed().getOn());
        model.numaMode.set(String.valueOf(port.getNuma()));
        model.pciAddress.setValue(port.getPci_addr());
        model.rxQueueing.setValue(port.getRx_info().getQueue().isIs_active() ? "On" : "Off");
        model.gratARP.setValue(port.getRx_info().getGrat_arp().isIs_active() ? "On" : "Off");
        model.flowControl.setValue(port.getFlowControl());
        return model;
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

    public boolean getLedStatus() {
        return ledStatus.get();
    }

    public BooleanProperty ledStatusProperty() {
        return ledStatus;
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
}

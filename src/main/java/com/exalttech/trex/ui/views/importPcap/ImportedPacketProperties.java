/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.importPcap;

import com.exalttech.trex.ui.views.streams.builder.IPV4Type;
import com.exalttech.trex.ui.views.streams.builder.StreamBuilderConstants;
import com.exalttech.trex.util.Util;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Imported packet properties binder model
 *
 * @author GeorgeKH
 */
public class ImportedPacketProperties {

    BooleanProperty enabledDstProperty = new SimpleBooleanProperty(false);
    StringProperty dstAddressProperty = new SimpleStringProperty(StreamBuilderConstants.DEFAULT_DST_IP_ADDRESS);
    StringProperty dstModeProperty = new SimpleStringProperty(IPV4Type.FIXED.getTitle());
    StringProperty dstCountProperty = new SimpleStringProperty("16");

    BooleanProperty enabledSrcProperty = new SimpleBooleanProperty(false);
    StringProperty srcAddressProperty = new SimpleStringProperty(StreamBuilderConstants.DEFAULT_SRC_IP_ADDRESS);
    StringProperty srcModeProperty = new SimpleStringProperty(IPV4Type.FIXED.getTitle());
    StringProperty srcCountProperty = new SimpleStringProperty("16");

    StringProperty speedupProperty = new SimpleStringProperty("1");
    StringProperty ipgProperty = new SimpleStringProperty("1");
    BooleanProperty ipgSelectionProperty = new SimpleBooleanProperty(false);

    StringProperty countProperty = new SimpleStringProperty("0");

    /**
     * Get destination enabled property
     *
     * @return
     */
    public BooleanProperty getEnabledDstProperty() {
        return enabledDstProperty;
    }

    /**
     * Return true if destination enabled, otherwise return false
     *
     * @return
     */
    public boolean isDestinationEnabled() {
        return enabledDstProperty.get();
    }

    /**
     * Get destination address property
     *
     * @return
     */
    public StringProperty getDstAddressProperty() {
        return dstAddressProperty;
    }

    /**
     * Get destination address value
     *
     * @return
     */
    public String getDstAddress() {
        return dstAddressProperty.get();
    }

    /**
     * Get destination mode property
     *
     * @return
     */
    public StringProperty getDstModeProperty() {
        return dstModeProperty;
    }

    /**
     * Get destination mode value
     *
     * @return
     */
    public String getDstMode() {
        return dstModeProperty.get();
    }

    /**
     * Get destination count property
     *
     * @return
     */
    public StringProperty getDstCountProperty() {
        return dstCountProperty;
    }

    /**
     * Get destination count value
     *
     * @return
     */
    public String getDstCount() {
        return dstCountProperty.get();
    }

    /**
     * Get source enabled property
     *
     * @return
     */
    public BooleanProperty getEnabledSrcProperty() {
        return enabledSrcProperty;
    }

    /**
     * Return true if source is enabled otherwise return false
     *
     * @return
     */
    public boolean isSourceEnabled() {
        return enabledSrcProperty.get();
    }

    /**
     * Get source address property
     *
     * @return
     */
    public StringProperty getSrcAddressProperty() {
        return srcAddressProperty;
    }

    /**
     * Get source address value
     *
     * @return
     */
    public String getSrcAddress() {
        return srcAddressProperty.get();
    }

    /**
     * Get source mode property
     *
     * @return
     */
    public StringProperty getSrcModeProperty() {
        return srcModeProperty;
    }

    /**
     * Get source mode value
     *
     * @return
     */
    public String getSrcMode() {
        return srcModeProperty.get();
    }

    /**
     * Get source count property
     *
     * @return
     */
    public StringProperty getSrcCountProperty() {
        return srcCountProperty;
    }

    /**
     * Get source count value
     *
     * @return
     */
    public String getSrcCount() {
        return srcCountProperty.get();
    }

    /**
     * Get speedup property
     *
     * @return
     */
    public StringProperty getSpeedupProperty() {
        return speedupProperty;
    }

    /**
     * Get speedup value
     *
     * @return
     */
    public double getSpeedup() {
        return Double.parseDouble(speedupProperty.get());
    }

    /**
     * Get IPG property
     *
     * @return
     */
    public StringProperty getIpgProperty() {
        return ipgProperty;
    }

    /**
     * Get IPG value
     *
     * @return
     */
    public double getIpg() {
        return Double.parseDouble(ipgProperty.get());
    }

    /**
     * Get count property
     *
     * @return
     */
    public StringProperty getCountProperty() {
        return countProperty;
    }

    /**
     * Get count value
     *
     * @return
     */
    public int getCount() {
        return Util.getIntFromString(countProperty.get());
    }

    /**
     * Get IPG selection property
     *
     * @return
     */
    public BooleanProperty getIpgSelectionProperty() {
        return ipgSelectionProperty;
    }

    /**
     * Return true id IPG is selected, otherwise return false
     * @return 
     */
    public boolean isIPGSelected() {
        return ipgSelectionProperty.get();
    }

}

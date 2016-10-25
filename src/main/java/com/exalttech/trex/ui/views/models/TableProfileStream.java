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
package com.exalttech.trex.ui.views.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Profile stream table model
 *
 * @author GeorgeKh
 */
public class TableProfileStream {

    private static final String NEXT_STREAM_ICON = "/icons/nextStream.png";
    private static final String NO_STREAM_ICON = "/icons/noStream.png";

    private SimpleStringProperty nameProperty = new SimpleStringProperty("");
    private SimpleStringProperty packetTypeProperty = new SimpleStringProperty("");
    private SimpleStringProperty lengthProperty = new SimpleStringProperty("");
    private SimpleStringProperty modeProperty = new SimpleStringProperty("");
    private SimpleStringProperty rateProperty = new SimpleStringProperty("");
    private SimpleStringProperty nextStreamProperty = new SimpleStringProperty(NEXT_STREAM_ICON);
    private SimpleStringProperty indexProperty = new SimpleStringProperty("");
    private BooleanProperty enabledProperty = new SimpleBooleanProperty();

    private String pcapBinary;
    private String pktModel;

    /**
     * Set name
     *
     * @param name
     */
    public void setName(String name) {
        this.nameProperty.set(name);
    }

    /**
     * Return name
     *
     * @return
     */
    public String getName() {
        return nameProperty.getValue();
    }

    /**
     * Set packet type
     *
     * @param packetType
     */
    public void setPacketType(String packetType) {
        this.packetTypeProperty.set(packetType);
    }

    /**
     * Set length
     *
     * @param length
     */
    public void setLength(String length) {
        this.lengthProperty.set(length);
    }

    /**
     * Return length
     *
     * @return
     */
    public String getLength() {
        return lengthProperty.getValue();
    }

    /**
     * Set mode
     *
     * @param mode
     */
    public void setMode(String mode) {
        this.modeProperty.set(mode);
        if (mode != null && !"".equals(mode)) {
            String first = mode.substring(0, 1);
            this.modeProperty.set(mode.replaceFirst(first, first.toUpperCase()));
        }
    }

    /**
     * Set rate
     *
     * @param rate
     */
    public void setRate(String rate) {
        this.rateProperty.set(rate);
    }

    /**
     * Set next stream
     *
     * @param nextStream
     */
    public void setNextStream(String nextStream) {
        String nextStreamIcon = NEXT_STREAM_ICON;
        if ("None".equals(nextStream)) {
            nextStreamIcon = NO_STREAM_ICON;
        }
        this.nextStreamProperty.set(nextStreamIcon);
    }

    /**
     * Return name property value
     *
     * @return
     */
    public String getNameProperty() {
        return nameProperty.getValue();
    }

    /**
     * Return packet type property value
     *
     * @return
     */
    public String getPacketTypeProperty() {
        return packetTypeProperty.getValue();
    }

    /**
     * Return length property value
     *
     * @return
     */
    public String getLengthProperty() {
        return lengthProperty.getValue();
    }

    /**
     * Return mode property value
     *
     * @return
     */
    public String getModeProperty() {
        return modeProperty.getValue();
    }

    /**
     * Return rate property value
     *
     * @return
     */
    public String getRateProperty() {
        return rateProperty.getValue();
    }

    /**
     * Return next stream property value
     *
     * @return
     */
    public String getNextStreamProperty() {
        return nextStreamProperty.getValue();
    }

    /**
     * Set stream enable
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        enabledProperty.setValue(enabled);
    }

    /**
     * Return enable property value
     *
     * @return
     */
    public BooleanProperty enabledPropertyProperty() {
        return enabledProperty;
    }

    /**
     * Return enable property
     *
     * @return
     */
    public BooleanProperty getEnabledProperty() {
        return enabledProperty;
    }

    /**
     * Return pcap binary
     *
     * @return
     */
    public String getPcapBinary() {
        return pcapBinary;
    }

    /**
     * Set pcap binary
     *
     * @param pcapBinary
     */
    public void setPcapBinary(String pcapBinary) {
        this.pcapBinary = pcapBinary;
    }

    /**
     * Return pkt model serialized to JSON
     *
     * @return
     */
    public String getPktModel() {
        return pktModel;
    }

    /**
     * Set pcap binary
     *
     * @param pktModel
     */
    public void setPktModel(String pktModel) {
        this.pktModel = pktModel;
    }

    /**
     * Set stream index
     *
     * @param index
     */
    public void setIndex(String index) {
        this.indexProperty.set(index);
    }

    /**
     *
     * @return
     */
    public int getIndex() {
        return Integer.valueOf(indexProperty.get());
    }

    /**
     * Return index property value
     *
     * @return
     */
    public String getIndexProperty() {
        return indexProperty.getValue();
    }

}

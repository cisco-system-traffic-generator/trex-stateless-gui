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
package com.exalttech.trex.ui.views.streams.binders;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ProtocolSelectionDataBinding implements Externalizable {
    @JsonIgnore
    private BooleanProperty ipv4Property = new SimpleBooleanProperty(true);
    @JsonIgnore
    private BooleanProperty tcpProperty = new SimpleBooleanProperty(true);
    @JsonIgnore
    private BooleanProperty udpProperty = new SimpleBooleanProperty(false);
    @JsonIgnore
    private BooleanProperty patternProperty = new SimpleBooleanProperty(true);
    @JsonIgnore
    private BooleanProperty taggedVlanProperty = new SimpleBooleanProperty(false);
    @JsonIgnore
    private BooleanProperty stackedVlanProperty = new SimpleBooleanProperty(false);

    @JsonIgnore
    private StringProperty frameLengthTypeProperty = new SimpleStringProperty("Fixed");
    @JsonIgnore
    private StringProperty frameLengthProperty = new SimpleStringProperty("64");
    @JsonIgnore
    private StringProperty minLengthProperty = new SimpleStringProperty("64");
    @JsonIgnore
    private StringProperty maxLengthProperty = new SimpleStringProperty("1518");

    @JsonIgnore
    public BooleanProperty getIpv4Property() {
        return ipv4Property;
    }

    @JsonIgnore
    public BooleanProperty getTcpProperty() {
        return tcpProperty;
    }

    @JsonIgnore
    public BooleanProperty getUdpProperty() {
        return udpProperty;
    }

    @JsonIgnore
    public BooleanProperty getPatternProperty() {
        return patternProperty;
    }

    @JsonIgnore
    public BooleanProperty getTaggedVlanProperty() {
        return taggedVlanProperty;
    }

    @JsonIgnore
    public BooleanProperty getStackedVlanProperty() {
        return stackedVlanProperty;
    }

    @JsonProperty("is_ipv4_selected")
    public boolean isIPV4Selected() {
        return ipv4Property.get();
    }

    @JsonProperty("is_ipv4_selected")
    public void setIPV4Selected(final boolean isIPV4Selected) {
        ipv4Property.set(isIPV4Selected);
    }

    @JsonProperty("is_tcp_selected")
    public boolean isTCPSelected() {
        return tcpProperty.get();
    }

    @JsonProperty("is_tcp_selected")
    public void setTCPSelected(final boolean isTCPSelected) {
        tcpProperty.set(isTCPSelected);
    }

    @JsonProperty("is_udp_selected")
    public boolean isUDPSelected() {
        return udpProperty.get();
    }

    @JsonProperty("is_udp_selected")
    public void setUDPSelected(final boolean isUDPSelected) {
        udpProperty.set(isUDPSelected);
    }

    @JsonProperty("is_pattern_selected")
    public boolean isPatternSelected() {
        return patternProperty.get();
    }

    @JsonProperty("is_pattern_selected")
    public void setPatternSelected(final boolean isPatternSelected) {
        patternProperty.set(isPatternSelected);
    }

    @JsonProperty("is_tagged_vlan_selected")
    public boolean isTaggedVlanSelected() {
        return taggedVlanProperty.get();
    }

    @JsonProperty("is_tagged_vlan_selected")
    public void setTaggedVlanSelected(final boolean isTaggedVlanSelected) {
        taggedVlanProperty.set(isTaggedVlanSelected);
    }

    @JsonProperty("is_stacked_vlan_selected")
    public boolean isStackedVlanSelected() {
        return stackedVlanProperty.get();
    }

    @JsonProperty("is_stacked_vlan_selected")
    public void setStackedVlanSelected(final boolean isStackedVlanSelected) {
        stackedVlanProperty.set(isStackedVlanSelected);
    }

    @JsonIgnore
    public StringProperty getFrameLengthTypeProperty() {
        return frameLengthTypeProperty;
    }

    @JsonIgnore
    public StringProperty getFrameLengthProperty() {
        return frameLengthProperty;
    }

    @JsonIgnore
    public StringProperty getMinLengthProperty() {
        return minLengthProperty;
    }

    @JsonIgnore
    public StringProperty getMaxLengthProperty() {
        return maxLengthProperty;
    }

    @JsonProperty("frame_length_type")
    public String getFrameLengthType() {
        return frameLengthTypeProperty.get();
    }

    @JsonProperty("frame_length_type")
    public void setFrameLengthType(final String frameLengthType) {
        frameLengthTypeProperty.set(frameLengthType);
    }

    @JsonProperty("frame_length")
    public String getFrameLength() {
        return String.valueOf(getValidLength(this.frameLengthProperty.get()));
    }

    @JsonProperty("frame_length")
    public void setFrameLength(final String frameLength) {
        frameLengthProperty.set(frameLength);
    }

    @JsonProperty("min_length")
    public String getMinLength() {
        int validLength = getValidLength(this.minLengthProperty.get());
        if (validLength == 9238) {
            validLength = 9233;
        }
        return String.valueOf(validLength);
    }

    @JsonProperty("min_length")
    public void setMinLength(final String minLength) {
        minLengthProperty.set(minLength);
    }

    @JsonProperty("max_length")
    public String getMaxLength() {
        int validLength = getValidLength(this.maxLengthProperty.get());
        int minLength = Integer.parseInt(getMinLength());
        if (validLength - 5 <= minLength) {
            validLength = minLength + 5;
        }
        return String.valueOf(validLength);
    }

    @JsonProperty("max_length")
    public void setMaxLength(final String maxLength) {
        maxLengthProperty.set(maxLength);
    }

    @JsonIgnore
    private int getValidLength(String value) {
        try {
            int length = Integer.parseInt(value);
            if (length < 64) {
                return 64;
            } else if (length > 9238) {
                return 9238;
            }
            return length;
        } catch (NumberFormatException ex) {
            return 64;
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(ipv4Property.get());
        out.writeBoolean(tcpProperty.get());
        out.writeBoolean(udpProperty.get());
        out.writeBoolean(patternProperty.get());
        out.writeBoolean(taggedVlanProperty.get());
        out.writeBoolean(stackedVlanProperty.get());

        out.writeObject(frameLengthTypeProperty.get());
        out.writeObject(frameLengthProperty.get());
        out.writeObject(minLengthProperty.get());
        out.writeObject(maxLengthProperty.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        getIpv4Property().set(in.readBoolean());
        getTcpProperty().set(in.readBoolean());
        getUdpProperty().set(in.readBoolean());
        getPatternProperty().set(in.readBoolean());
        getTaggedVlanProperty().set(in.readBoolean());
        getStackedVlanProperty().set(in.readBoolean());

        getFrameLengthTypeProperty().set((String) in.readObject());
        getFrameLengthProperty().set((String) in.readObject());
        getMinLengthProperty().set((String) in.readObject());
        getMaxLengthProperty().set((String) in.readObject());
    }
}

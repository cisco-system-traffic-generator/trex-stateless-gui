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
package com.exalttech.trex.ui.views.streams;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * Model present selected protocol
 *
 * @author GeorgeKh
 */
public class ProtocolSelection implements Externalizable {

    BooleanProperty ipv4Property = new SimpleBooleanProperty(true);
    BooleanProperty tcpProperty = new SimpleBooleanProperty(true);
    BooleanProperty udpProperty = new SimpleBooleanProperty(false);
    BooleanProperty patternProperty = new SimpleBooleanProperty(true);
    BooleanProperty taggedVlanProperty = new SimpleBooleanProperty(false);
    BooleanProperty stackedVlanProperty = new SimpleBooleanProperty(false);

    StringProperty frameLengthTypeProperty = new SimpleStringProperty("Fixed");
    StringProperty frameLengthProperty = new SimpleStringProperty("64");
    StringProperty minLengthProperty = new SimpleStringProperty("64");
    StringProperty maxLengthProperty = new SimpleStringProperty("1518");

    /**
     * Return IPV4 property
     *
     * @return
     */
    public BooleanProperty getIpv4Property() {
        return ipv4Property;
    }

    /**
     * Return TCP property
     *
     * @return
     */
    public BooleanProperty getTcpProperty() {
        return tcpProperty;
    }

    /**
     * Return UDP property
     *
     * @return
     */
    public BooleanProperty getUdpProperty() {
        return udpProperty;
    }

    /**
     * Return pattern property
     *
     * @return
     */
    public BooleanProperty getPatternProperty() {
        return patternProperty;
    }

    /**
     * Return tagged vlan property
     *
     * @return
     */
    public BooleanProperty getTaggedVlanProperty() {
        return taggedVlanProperty;
    }

    /**
     * Return stacked vlan property
     *
     * @return
     */
    public BooleanProperty getStackedVlanProperty() {
        return stackedVlanProperty;
    }

    /**
     * Return true if IPv4 is selected, otherwise return false
     *
     * @return
     */
    public boolean isIPV4Selected() {
        return ipv4Property.getValue();
    }

    /**
     * Return true if tcp is selected, otherwise return false
     *
     * @return
     */
    public boolean isTCPSelected() {
        return tcpProperty.getValue();
    }

    /**
     * Return true if udp is selected, otherwise return false
     *
     * @return
     */
    public boolean isUDPSelected() {
        return udpProperty.getValue();
    }

    /**
     * Return true if payload pattern is selected, otherwise return false
     *
     * @return
     */
    public boolean isPatternSelected() {
        return patternProperty.getValue();
    }

    /**
     * Return true if tagged vlan is selected, otherwise return false
     *
     * @return
     */
    public boolean isTaggedVlanSelected() {
        return taggedVlanProperty.getValue();
    }

    /**
     * Return true if stacked vlan is selected, otherwise return false
     *
     * @return
     */
    public boolean isStackedVlanSelected() {
        return stackedVlanProperty.getValue();
    }

    /**
     * Return frame length type property
     *
     * @return
     */
    public StringProperty getFrameLengthTypeProperty() {
        return frameLengthTypeProperty;
    }

    /**
     * Return frame length property
     *
     * @return
     */
    public StringProperty getFrameLengthProperty() {
        return frameLengthProperty;
    }

    /**
     * Return min length property
     *
     * @return
     */
    public StringProperty getMinLengthProperty() {
        return minLengthProperty;
    }

    /**
     * Return max length property
     *
     * @return
     */
    public StringProperty getMaxLengthProperty() {
        return maxLengthProperty;
    }

    /**
     * Return frame length type
     *
     * @return
     */
    public String getFrameLengthType() {
        return frameLengthTypeProperty.getValue();
    }

    /**
     * Return frame length
     *
     * @return
     */
    public String getFrameLength() {
        return String.valueOf(getValidLength(this.frameLengthProperty.getValue()));
    }

    /**
     * Return min length
     *
     * @return
     */
    public String getMinLength() {
        int validLength = getValidLength(this.minLengthProperty.getValue());
        if (validLength == 9238) {
            validLength = 9233;
        }
        return String.valueOf(validLength);
    }

    /**
     * Return max length
     *
     * @return
     */
    public String getMaxLength() {
        int validLength = getValidLength(this.maxLengthProperty.getValue());
        int minLength = Integer.parseInt(getMinLength());
        if (validLength - 5 <= minLength) {
            validLength = minLength + 5;
        }
        return String.valueOf(validLength);
    }

    /**
     * Return valid length
     *
     * @param value
     * @return
     */
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

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

import com.exalttech.trex.ui.views.streams.buildstream.StreamBuilderConstants;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * UDP protocol data binding model
 *
 * @author Georgekh
 */
public class UDPProtocolDataBinding extends AbstractStreamDataBinding implements Externalizable {

    StringProperty srcPort = new SimpleStringProperty();
    StringProperty dstPort = new SimpleStringProperty();
    BooleanProperty overrideSrcPort = new SimpleBooleanProperty();
    BooleanProperty overrideDstPort = new SimpleBooleanProperty();
    StringProperty length = new SimpleStringProperty();
    BooleanProperty overrideLength = new SimpleBooleanProperty();
    StringProperty checksum = new SimpleStringProperty();
    BooleanProperty overrideChecksum = new SimpleBooleanProperty();

    /**
     * Constructor
     */
    public UDPProtocolDataBinding() {
        setInitialValues();
    }

    /**
     * Return source port property
     *
     * @return
     */
    public StringProperty getSrcPort() {
        return srcPort;
    }

    /**
     * Set source port property
     *
     * @param srcPort
     */
    public void setSrcPort(StringProperty srcPort) {
        this.srcPort = srcPort;
    }

    /**
     * Return destination port property
     *
     * @return
     */
    public StringProperty getDstPort() {
        return dstPort;
    }

    /**
     * Return destination port property
     *
     * @param dstPort
     */
    public void setDstPort(StringProperty dstPort) {
        this.dstPort = dstPort;
    }

    /**
     * Return override source port property
     *
     * @return
     */
    public BooleanProperty getOverrideSrcPort() {
        return overrideSrcPort;
    }

    /**
     * Set override source port property
     *
     * @param overrideSrcPort
     */
    public void setOverrideSrcPort(BooleanProperty overrideSrcPort) {
        this.overrideSrcPort = overrideSrcPort;
    }

    /**
     * Return override destination port property
     *
     * @return
     */
    public BooleanProperty getOverrideDstPort() {
        return overrideDstPort;
    }

    /**
     * Set override destination port property
     *
     * @param overrideDstPort
     */
    public void setOverrideDstPort(BooleanProperty overrideDstPort) {
        this.overrideDstPort = overrideDstPort;
    }

    /**
     * Return length property
     *
     * @return
     */
    public StringProperty getLength() {
        return length;
    }

    /**
     * Set length property
     *
     * @param length
     */
    public void setLength(StringProperty length) {
        this.length = length;
    }

    /**
     * Return override length property
     *
     * @return
     */
    public BooleanProperty getOverrideLength() {
        return overrideLength;
    }

    /**
     * Set override length property
     *
     * @param overrideLength
     */
    public void setOverrideLength(BooleanProperty overrideLength) {
        this.overrideLength = overrideLength;
    }

    /**
     * Return checksum property
     *
     * @return
     */
    public StringProperty getChecksum() {
        return checksum;
    }

    /**
     * Set checksum property
     *
     * @param checksum
     */
    public void setChecksum(StringProperty checksum) {
        this.checksum = checksum;
    }

    /**
     * Return override checksum property
     *
     * @return
     */
    public BooleanProperty getOverrideChecksum() {
        return overrideChecksum;
    }

    /**
     * Set override checksum property
     *
     * @param overrideChecksum
     */
    public void setOverrideChecksum(BooleanProperty overrideChecksum) {
        this.overrideChecksum = overrideChecksum;
    }

    /**
     * Initialize properties
     */
    @Override
    protected void setInitialValues() {
        srcPort.set(StreamBuilderConstants.DEFAULT_SRC_PORT);
        dstPort.set(StreamBuilderConstants.DEFAULT_DST_PORT);
        overrideSrcPort.set(false);
        overrideDstPort.set(false);
        length.set("26");
        overrideLength.set(false);
        checksum.set("FFBA");
        overrideChecksum.set(false);
    }

    /**
     * Write serialized properties
     *
     * @param out
     * @throws IOException
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(srcPort.get());
        out.writeObject(dstPort.get());
        out.writeBoolean(overrideSrcPort.get());
        out.writeBoolean(overrideDstPort.get());
        out.writeObject(length.get());
        out.writeBoolean(overrideLength.get());
        out.writeObject(checksum.get());
        out.writeBoolean(overrideChecksum.get());
    }

    /**
     * Read serialized properties
     *
     * @throws java.lang.ClassNotFoundException
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        srcPort.set((String) in.readObject());
        dstPort.set((String) in.readObject());
        overrideSrcPort.set(in.readBoolean());
        overrideDstPort.set(in.readBoolean());
        length.set((String) in.readObject());
        overrideLength.set(in.readBoolean());
        checksum.set((String) in.readObject());
        overrideChecksum.set(in.readBoolean());
    }

}

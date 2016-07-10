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

import com.exalttech.trex.ui.views.streams.builder.StreamBuilderConstants;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * TCP protocol data binding model
 *
 * @author Georgekh
 */
public class TCPProtocolDataBinding extends AbstractStreamDataBinding implements Externalizable {

    private StringProperty srcPort = new SimpleStringProperty();
    private StringProperty dstPort = new SimpleStringProperty();
    private BooleanProperty overrideSrcPort = new SimpleBooleanProperty();
    private BooleanProperty overrideDstPort = new SimpleBooleanProperty();
    private StringProperty sequeceNumber = new SimpleStringProperty();
    private StringProperty ackNumber = new SimpleStringProperty();
    private StringProperty window = new SimpleStringProperty();
    private BooleanProperty overrideChecksum = new SimpleBooleanProperty();
    private StringProperty checksum = new SimpleStringProperty();
    private StringProperty urgentPointer = new SimpleStringProperty();
    private BooleanProperty urg = new SimpleBooleanProperty();
    private BooleanProperty ack = new SimpleBooleanProperty();
    private BooleanProperty psh = new SimpleBooleanProperty();
    private BooleanProperty rst = new SimpleBooleanProperty();
    private BooleanProperty syn = new SimpleBooleanProperty();
    private BooleanProperty fin = new SimpleBooleanProperty();

    /**
     * Constructor
     */
    public TCPProtocolDataBinding() {
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
     * Set destination port property
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
     * @param ovverideDstPort
     */
    public void setOverrideDstPort(BooleanProperty ovverideDstPort) {
        this.overrideDstPort = ovverideDstPort;
    }

    /**
     * Return sequence number property
     *
     * @return
     */
    public StringProperty getSequeceNumber() {
        return sequeceNumber;
    }

    /**
     * Set sequence number property
     *
     * @param sequeceNumber
     */
    public void setSequeceNumber(StringProperty sequeceNumber) {
        this.sequeceNumber = sequeceNumber;
    }

    /**
     * Return Acknowledge number property
     *
     * @return
     */
    public StringProperty getAckNumber() {
        return ackNumber;
    }

    /**
     * Set acknowledge number property
     *
     * @param ackNumber
     */
    public void setAckNumber(StringProperty ackNumber) {
        this.ackNumber = ackNumber;
    }

    /**
     * Return window property
     *
     * @return
     */
    public StringProperty getWindow() {
        return window;
    }

    /**
     * Set Window property
     *
     * @param window
     */
    public void setWindow(StringProperty window) {
        this.window = window;
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
     * Return urgent pointer property
     *
     * @return
     */
    public StringProperty getUrgentPointer() {
        return urgentPointer;
    }

    /**
     * Set urgent pointer property
     *
     * @param urgentPointer
     */
    public void setUrgentPointer(StringProperty urgentPointer) {
        this.urgentPointer = urgentPointer;
    }

    /**
     * Return URG property
     *
     * @return
     */
    public BooleanProperty getUrg() {
        return urg;
    }

    /**
     * Set URG property
     *
     * @param urg
     */
    public void setUrg(BooleanProperty urg) {
        this.urg = urg;
    }

    /**
     * Return ACK property
     *
     * @return
     */
    public BooleanProperty getAck() {
        return ack;
    }

    /**
     * Set ACK property
     *
     * @param ack
     */
    public void setAck(BooleanProperty ack) {
        this.ack = ack;
    }

    /**
     * Return PSH property
     *
     * @return
     */
    public BooleanProperty getPsh() {
        return psh;
    }

    /**
     * Set PSH property
     *
     * @param psh
     */
    public void setPsh(BooleanProperty psh) {
        this.psh = psh;
    }

    /**
     * Return RST property
     *
     * @return
     */
    public BooleanProperty getRst() {
        return rst;
    }

    /**
     * Set RST property
     *
     * @param rst
     */
    public void setRst(BooleanProperty rst) {
        this.rst = rst;
    }

    /**
     * Return SYN property
     *
     * @return
     */
    public BooleanProperty getSyn() {
        return syn;
    }

    /**
     * Set SYN property
     *
     * @param syn
     */
    public void setSyn(BooleanProperty syn) {
        this.syn = syn;
    }

    /**
     * Return FIN property
     *
     * @return
     */
    public BooleanProperty getFin() {
        return fin;
    }

    /**
     * Set FIN property
     *
     * @param fin
     */
    public void setFin(BooleanProperty fin) {
        this.fin = fin;
    }

    /**
     * Set initial values
     */
    @Override
    protected void setInitialValues() {
        srcPort.set(StreamBuilderConstants.DEFAULT_SRC_PORT);
        overrideSrcPort.set(false);
        dstPort.set(StreamBuilderConstants.DEFAULT_DST_PORT);
        overrideDstPort.set(false);
        sequeceNumber.set("129018");
        ackNumber.set("0");
        window.set("1024");
        checksum.set("B3E3");
        overrideChecksum.set(false);
        urgentPointer.set("0");
        urg.set(false);
        ack.set(false);
        psh.set(false);
        rst.set(false);
        syn.set(false);
        fin.set(false);
    }

    /**
     * Write the serialized properties
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
        out.writeObject(sequeceNumber.get());
        out.writeObject(ackNumber.get());
        out.writeObject(window.get());
        out.writeBoolean(overrideChecksum.get());
        out.writeObject(checksum.get());
        out.writeObject(urgentPointer.get());
        out.writeBoolean(urg.get());
        out.writeBoolean(ack.get());
        out.writeBoolean(psh.get());
        out.writeBoolean(rst.get());
        out.writeBoolean(syn.get());
        out.writeBoolean(fin.get());
    }

    /**
     * Read the serialized property
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        srcPort.set((String) in.readObject());
        dstPort.set((String) in.readObject());
        overrideSrcPort.set(in.readBoolean());
        overrideDstPort.set(in.readBoolean());
        sequeceNumber.set((String) in.readObject());
        ackNumber.set((String) in.readObject());
        window.set((String) in.readObject());
        overrideChecksum.set(in.readBoolean());
        checksum.set((String) in.readObject());
        urgentPointer.set((String) in.readObject());
        urg.set(in.readBoolean());
        ack.set(in.readBoolean());
        psh.set(in.readBoolean());
        rst.set(in.readBoolean());
        syn.set(in.readBoolean());
        fin.set(in.readBoolean());
    }
}

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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.exalttech.trex.ui.views.streams.builder.StreamBuilderConstants;


public class TCPProtocolDataBinding extends AbstractStreamDataBinding {
    private StringProperty srcPortProperty = new SimpleStringProperty();
    private StringProperty dstPortProperty = new SimpleStringProperty();
    private BooleanProperty overrideSrcPortProperty = new SimpleBooleanProperty();
    private BooleanProperty overrideDstPortProperty = new SimpleBooleanProperty();
    private StringProperty sequenceNumberProperty = new SimpleStringProperty();
    private StringProperty ackNumberProperty = new SimpleStringProperty();
    private StringProperty windowProperty = new SimpleStringProperty();
    private BooleanProperty overrideChecksumProperty = new SimpleBooleanProperty();
    private StringProperty checksumProperty = new SimpleStringProperty();
    private StringProperty urgentPointerProperty = new SimpleStringProperty();
    private BooleanProperty urgProperty = new SimpleBooleanProperty();
    private BooleanProperty ackProperty = new SimpleBooleanProperty();
    private BooleanProperty pshProperty = new SimpleBooleanProperty();
    private BooleanProperty rstProperty = new SimpleBooleanProperty();
    private BooleanProperty synProperty = new SimpleBooleanProperty();
    private BooleanProperty finProperty = new SimpleBooleanProperty();

    public TCPProtocolDataBinding() {
        setInitialValues();
    }

    @JsonIgnore
    public StringProperty getSrcPortProperty() {
        return srcPortProperty;
    }

    @JsonIgnore
    public StringProperty getDstPortProperty() {
        return dstPortProperty;
    }

    @JsonIgnore
    public BooleanProperty getOverrideSrcPortProperty() {
        return overrideSrcPortProperty;
    }

    @JsonIgnore
    public BooleanProperty getOverrideDstPortProperty() {
        return overrideDstPortProperty;
    }

    @JsonIgnore
    public StringProperty getSequenceNumberProperty() {
        return sequenceNumberProperty;
    }

    @JsonIgnore
    public StringProperty getAckNumberProperty() {
        return ackNumberProperty;
    }

    @JsonIgnore
    public StringProperty getWindowProperty() {
        return windowProperty;
    }

    @JsonIgnore
    public BooleanProperty getOverrideChecksumProperty() {
        return overrideChecksumProperty;
    }

    @JsonIgnore
    public StringProperty getChecksumProperty() {
        return checksumProperty;
    }

    @JsonIgnore
    public StringProperty getUrgentPointerProperty() {
        return urgentPointerProperty;
    }

    @JsonIgnore
    public BooleanProperty getUrgProperty() {
        return urgProperty;
    }

    @JsonIgnore
    public BooleanProperty getAckProperty() {
        return ackProperty;
    }

    @JsonIgnore
    public BooleanProperty getPshProperty() {
        return pshProperty;
    }

    @JsonIgnore
    public BooleanProperty getRstProperty() {
        return rstProperty;
    }

    @JsonIgnore
    public BooleanProperty getSynProperty() {
        return synProperty;
    }

    @JsonIgnore
    public BooleanProperty getFinProperty() {
        return finProperty;
    }

    @JsonProperty("src_port")
    public String getSrcPort() {
        return srcPortProperty.get();
    }

    @JsonProperty("src_port")
    public void setSrcPort(final String srcPort) {
        srcPortProperty.set(srcPort);
    }

    @JsonProperty("dst_port")
    public String getDstPort() {
        return dstPortProperty.get();
    }

    @JsonProperty("dst_port")
    public void setDstPort(final String dstPort) {
        dstPortProperty.set(dstPort);
    }

    @JsonProperty("is_override_src_port")
    public boolean isOverrideSrcPort() {
        return overrideSrcPortProperty.get();
    }

    @JsonProperty("is_override_src_port")
    public void setOverrideSrcPort(final boolean isOverrideSrcPort) {
        overrideSrcPortProperty.set(isOverrideSrcPort);
    }

    @JsonProperty("is_override_dst_port")
    public boolean isOverrideDstPort() {
        return overrideDstPortProperty.get();
    }

    @JsonProperty("is_override_dst_port")
    public void setOverrideDstPortProperty(final boolean isOverrideDstPort) {
        overrideDstPortProperty.set(isOverrideDstPort);
    }

    @JsonProperty("sequence_number")
    public String getSequenceNumber() {
        return sequenceNumberProperty.get();
    }

    @JsonProperty("sequence_number")
    public void setSequenceNumber(final String sequenceNumber) {
        sequenceNumberProperty.set(sequenceNumber);
    }

    @JsonProperty("ack_number")
    public String getAckNumber() {
        return ackNumberProperty.get();
    }

    @JsonProperty("ack_number")
    public void setAckNumber(final String ackNumber) {
        ackNumberProperty.set(ackNumber);
    }

    @JsonProperty("window")
    public String getWindow() {
        return windowProperty.get();
    }

    @JsonProperty("window")
    public void setWindow(final String window) {
        windowProperty.set(window);
    }

    @JsonProperty("is_override_checksum")
    public boolean isOverrideChecksum() {
        return overrideChecksumProperty.get();
    }

    @JsonProperty("is_override_checksum")
    public void setOverrideChecksum(final boolean isOverrideChecksum) {
        overrideChecksumProperty.set(isOverrideChecksum);
    }

    @JsonProperty("checksum")
    public String getChecksum() {
        return checksumProperty.get();
    }

    @JsonProperty("checksum")
    public void setChecksum(final String checksum) {
        checksumProperty.set(checksum);
    }

    @JsonProperty("urgent_pointer")
    public String getUrgentPointer() {
        return urgentPointerProperty.get();
    }

    @JsonProperty("urgent_pointer")
    public void setUrgentPointer(final String urgentPointer) {
        urgentPointerProperty.set(urgentPointer);
    }

    @JsonProperty("is_urg")
    public boolean isUrg() {
        return urgProperty.get();
    }

    @JsonProperty("is_urg")
    public void setUrg(final boolean isUrg) {
        urgProperty.set(isUrg);
    }

    @JsonProperty("is_ack")
    public boolean isAck() {
        return ackProperty.get();
    }

    @JsonProperty("is_ack")
    public void setAck(final boolean isAck) {
        ackProperty.set(isAck);
    }

    @JsonProperty("is_psh")
    public boolean isPsh() {
        return pshProperty.get();
    }

    @JsonProperty("is_psh")
    public void setPsh(final boolean isPsh) {
        pshProperty.set(isPsh);
    }

    @JsonProperty("is_rst")
    public boolean isRst() {
        return rstProperty.get();
    }

    @JsonProperty("is_rst")
    public void setRst(final boolean isRst) {
        rstProperty.set(isRst);
    }

    @JsonProperty("is_sync")
    public boolean isSyn() {
        return synProperty.get();
    }

    @JsonProperty("is_sync")
    public void setSyn(final boolean isSync) {
        synProperty.set(isSync);
    }

    @JsonProperty("is_fin")
    public boolean isFin() {
        return finProperty.get();
    }

    @JsonProperty("is_fin")
    public void setFin(final boolean isFin) {
        finProperty.set(isFin);
    }

    @Override
    protected void setInitialValues() {
        srcPortProperty.set(StreamBuilderConstants.DEFAULT_SRC_PORT);
        overrideSrcPortProperty.set(false);
        dstPortProperty.set(StreamBuilderConstants.DEFAULT_DST_PORT);
        overrideDstPortProperty.set(false);
        sequenceNumberProperty.set("129018");
        ackNumberProperty.set("0");
        windowProperty.set("1024");
        checksumProperty.set("B3E3");
        overrideChecksumProperty.set(false);
        urgentPointerProperty.set("0");
        urgProperty.set(false);
        ackProperty.set(false);
        pshProperty.set(false);
        rstProperty.set(false);
        synProperty.set(false);
        finProperty.set(false);
    }
}

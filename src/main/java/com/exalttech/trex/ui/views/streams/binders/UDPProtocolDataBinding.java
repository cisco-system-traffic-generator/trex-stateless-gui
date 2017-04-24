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


public class UDPProtocolDataBinding extends AbstractStreamDataBinding {
    private StringProperty srcPortProperty = new SimpleStringProperty();
    private StringProperty dstPortProperty = new SimpleStringProperty();
    private BooleanProperty overrideSrcPortProperty = new SimpleBooleanProperty();
    private BooleanProperty overrideDstPortProperty = new SimpleBooleanProperty();
    private StringProperty lengthProperty = new SimpleStringProperty();
    private BooleanProperty overrideLengthProperty = new SimpleBooleanProperty();
    private StringProperty checksumProperty = new SimpleStringProperty();
    private BooleanProperty overrideChecksumProperty = new SimpleBooleanProperty();

    public UDPProtocolDataBinding() {
        setInitialValues();
    }

    @JsonIgnore
    public StringProperty getSrcPortProperty() {
        return srcPortProperty;
    }

    @JsonIgnore
    public void setSrcPortProperty(StringProperty srcPortProperty) {
        this.srcPortProperty = srcPortProperty;
    }

    @JsonIgnore
    public StringProperty getDstPortProperty() {
        return dstPortProperty;
    }

    @JsonIgnore
    public void setDstPortProperty(StringProperty dstPortProperty) {
        this.dstPortProperty = dstPortProperty;
    }

    @JsonIgnore
    public BooleanProperty getOverrideSrcPortProperty() {
        return overrideSrcPortProperty;
    }

    @JsonIgnore
    public void setOverrideSrcPortProperty(BooleanProperty overrideSrcPortProperty) {
        this.overrideSrcPortProperty = overrideSrcPortProperty;
    }

    @JsonIgnore
    public BooleanProperty getOverrideDstPortProperty() {
        return overrideDstPortProperty;
    }

    @JsonIgnore
    public void setOverrideDstPortProperty(BooleanProperty overrideDstPortProperty) {
        this.overrideDstPortProperty = overrideDstPortProperty;
    }

    @JsonIgnore
    public StringProperty getLengthProperty() {
        return lengthProperty;
    }

    @JsonIgnore
    public void setLengthProperty(StringProperty lengthProperty) {
        this.lengthProperty = lengthProperty;
    }

    @JsonIgnore
    public BooleanProperty getOverrideLengthProperty() {
        return overrideLengthProperty;
    }

    @JsonIgnore
    public void setOverrideLengthProperty(BooleanProperty overrideLengthProperty) {
        this.overrideLengthProperty = overrideLengthProperty;
    }

    @JsonIgnore
    public StringProperty getChecksumProperty() {
        return checksumProperty;
    }

    @JsonIgnore
    public void setChecksumProperty(StringProperty checksumProperty) {
        this.checksumProperty = checksumProperty;
    }

    @JsonIgnore
    public BooleanProperty getOverrideChecksumProperty() {
        return overrideChecksumProperty;
    }

    @JsonIgnore
    public void setOverrideChecksumProperty(BooleanProperty overrideChecksumProperty) {
        this.overrideChecksumProperty = overrideChecksumProperty;
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

    @JsonProperty("length")
    public String getLength() {
        return lengthProperty.get();
    }

    @JsonProperty("length")
    public void setLength(final String length) {
        lengthProperty.set(length);
    }

    @JsonProperty("is_override_length")
    public boolean isOverrideLength() {
        return overrideLengthProperty.get();
    }

    @JsonProperty("is_override_length")
    public void setOverrideLength(final boolean isOverrideLength) {
        overrideLengthProperty.set(isOverrideLength);
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

    @Override
    protected void setInitialValues() {
        srcPortProperty.set(StreamBuilderConstants.DEFAULT_SRC_PORT);
        dstPortProperty.set(StreamBuilderConstants.DEFAULT_DST_PORT);
        overrideSrcPortProperty.set(false);
        overrideDstPortProperty.set(false);
        lengthProperty.set("26");
        overrideLengthProperty.set(false);
        checksumProperty.set("FFBA");
        overrideChecksumProperty.set(false);
    }
}

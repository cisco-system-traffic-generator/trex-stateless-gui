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


public class VlanDataBinding extends AbstractStreamDataBinding implements Externalizable {
    @JsonIgnore
    private BooleanProperty overrideTPIdProperty = new SimpleBooleanProperty();
    @JsonIgnore
    private StringProperty tpIdProperty = new SimpleStringProperty();
    @JsonIgnore
    private StringProperty priorityProperty = new SimpleStringProperty();
    @JsonIgnore
    private StringProperty cfiProperty = new SimpleStringProperty();
    @JsonIgnore
    private StringProperty vIdProperty = new SimpleStringProperty();

    public VlanDataBinding() {
        setInitialValues();
    }

    @JsonIgnore
    public BooleanProperty getOverrideTPIdProperty() {
        return overrideTPIdProperty;
    }

    @JsonIgnore
    public void setOverrideTPIdProperty(BooleanProperty overrideTPIdProperty) {
        this.overrideTPIdProperty = overrideTPIdProperty;
    }

    @JsonIgnore
    public StringProperty getTpIdProperty() {
        return tpIdProperty;
    }

    @JsonIgnore
    public void setTpIdProperty(StringProperty tpIdProperty) {
        this.tpIdProperty = tpIdProperty;
    }

    @JsonIgnore
    public StringProperty getPriorityProperty() {
        return priorityProperty;
    }

    @JsonIgnore
    public void setPriorityProperty(StringProperty priorityProperty) {
        this.priorityProperty = priorityProperty;
    }

    @JsonIgnore
    public StringProperty getCfiProperty() {
        return cfiProperty;
    }

    @JsonIgnore
    public void setCfiProperty(StringProperty cfiProperty) {
        this.cfiProperty = cfiProperty;
    }

    @JsonIgnore
    public StringProperty getVIdProperty() {
        return vIdProperty;
    }

    @JsonIgnore
    public void setVIdProperty(StringProperty vIdProperty) {
        this.vIdProperty = vIdProperty;
    }

    @JsonProperty("is_override_tp_id")
    public boolean isOverrideTPId() {
        return overrideTPIdProperty.get();
    }

    @JsonProperty("is_override_tp_id")
    public void setOverrideTPId(final boolean isOverrideTPId) {
        overrideTPIdProperty.set(isOverrideTPId);
    }

    @JsonProperty("tp_id")
    public String getTpId() {
        return tpIdProperty.get();
    }

    @JsonProperty("tp_id")
    public void setTpId(final String tpId) {
        tpIdProperty.set(tpId);
    }

    @JsonProperty("priority")
    public String getPriority() {
        return priorityProperty.get();
    }

    @JsonProperty("priority")
    public void setPriority(final String priority) {
        priorityProperty.set(priority);
    }

    @JsonProperty("cfi")
    public String getCfi() {
        return cfiProperty.get();
    }

    @JsonProperty("cfi")
    public void setCfi(final String cfi) {
        cfiProperty.set(cfi);
    }

    @JsonProperty("v_id")
    public String getVId() {
        return vIdProperty.get();
    }

    @JsonProperty("v_id")
    public void setVId(final String vId) {
        vIdProperty.set(vId);
    }

    @Override
    protected void setInitialValues() {
        overrideTPIdProperty.set(false);
        tpIdProperty.setValue("FFFF");
        priorityProperty.set("0");
        cfiProperty.set("0");
        vIdProperty.set("0");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(overrideTPIdProperty.get());
        out.writeObject(tpIdProperty.get());
        out.writeObject(priorityProperty.get());
        out.writeObject(cfiProperty.get());
        out.writeObject(vIdProperty.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        overrideTPIdProperty.set(in.readBoolean());
        tpIdProperty.set((String) in.readObject());
        priorityProperty.set((String) in.readObject());
        cfiProperty.set((String) in.readObject());
        vIdProperty.set((String) in.readObject());
    }
}

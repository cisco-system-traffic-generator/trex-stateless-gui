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


public class VlanDataBinding extends AbstractStreamDataBinding {
    private BooleanProperty overrideTPIdProperty = new SimpleBooleanProperty();
    private StringProperty tpIdProperty = new SimpleStringProperty();
    private StringProperty priorityProperty = new SimpleStringProperty();
    private StringProperty cfiProperty = new SimpleStringProperty();
    private StringProperty vIdProperty = new SimpleStringProperty();

    public VlanDataBinding() {
        setInitialValues();
    }

    @JsonIgnore
    public BooleanProperty getOverrideTPIdProperty() {
        return overrideTPIdProperty;
    }

    @JsonIgnore
    public StringProperty getTpIdProperty() {
        return tpIdProperty;
    }

    @JsonIgnore
    public StringProperty getPriorityProperty() {
        return priorityProperty;
    }

    @JsonIgnore
    public StringProperty getCfiProperty() {
        return cfiProperty;
    }

    @JsonIgnore
    public StringProperty getVIdProperty() {
        return vIdProperty;
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
}

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


public class EthernetDataBinding extends AbstractStreamDataBinding implements Externalizable {
    @JsonIgnore
    private BooleanProperty overrideProperty = new SimpleBooleanProperty();
    @JsonIgnore
    private StringProperty typeProperty = new SimpleStringProperty();

    public EthernetDataBinding() {
        setInitialValues();
    }

    @JsonIgnore
    public BooleanProperty getOverrideProperty() {
        return overrideProperty;
    }

    @JsonIgnore
    public void setOverrideProperty(BooleanProperty overrideProperty) {
        this.overrideProperty = overrideProperty;
    }

    @JsonIgnore
    public StringProperty getTypeProperty() {
        return typeProperty;
    }

    @JsonIgnore
    public void setTypeProperty(StringProperty typeProperty) {
        this.typeProperty = typeProperty;
    }

    @JsonProperty("is_override")
    public boolean isOverride() {
        return overrideProperty.get();
    }

    @JsonProperty("is_override")
    public void setOverride(final boolean isOverride) {
        overrideProperty.set(isOverride);
    }

    @JsonProperty("type")
    public String getType() {
        return typeProperty.get();
    }

    @JsonProperty("type")
    public void setType(final String type) {
        typeProperty.set(type);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(overrideProperty.get());
        out.writeObject(typeProperty.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        getOverrideProperty().set(in.readBoolean());
        getTypeProperty().set((String) in.readObject());
    }

    @Override
    protected void setInitialValues() {
        overrideProperty.set(false);
        typeProperty.set("0800");
    }
}

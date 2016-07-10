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

/**
 *
 * Ethernet data binding model
 *
 * @author Georgekh
 */
public class EthernetDataBinding extends AbstractStreamDataBinding implements Externalizable {

    BooleanProperty overrideProperty = new SimpleBooleanProperty();
    StringProperty typeProperty = new SimpleStringProperty();

    /**
     *
     */
    public EthernetDataBinding() {
        setInitialValues();
    }

    /**
     * Return override type property
     *
     * @return
     */
    public BooleanProperty getOverrideProperty() {
        return overrideProperty;
    }

    /**
     * Set override type property
     *
     * @param overrideProperty
     */
    public void setOverrideProperty(BooleanProperty overrideProperty) {
        this.overrideProperty = overrideProperty;
    }

    /**
     * Return type property
     *
     * @return
     */
    public StringProperty getTypeProperty() {
        return typeProperty;
    }

    /**
     * Set type property
     *
     * @param typeProperty
     */
    public void setTypeProperty(StringProperty typeProperty) {
        this.typeProperty = typeProperty;
    }

    /**
     * Write serialized data
     *
     * @param out
     * @throws IOException
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(overrideProperty.get());
        out.writeObject(typeProperty.get());
    }

    /**
     * Read serialized data
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        getOverrideProperty().set(in.readBoolean());
        getTypeProperty().set((String) in.readObject());
    }

    /**
     * Initialize Ethernet properties values
     */
    @Override
    protected void setInitialValues() {
        overrideProperty.set(false);
        typeProperty.set("0800");
    }

}

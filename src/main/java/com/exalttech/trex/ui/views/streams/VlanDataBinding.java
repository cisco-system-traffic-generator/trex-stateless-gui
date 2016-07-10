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
 * Vlan data binding model
 *
 * @author Georgekh
 */
public class VlanDataBinding extends AbstractStreamDataBinding implements Externalizable {

    BooleanProperty overrideTPID = new SimpleBooleanProperty();
    StringProperty tpid = new SimpleStringProperty();
    StringProperty priority = new SimpleStringProperty();
    StringProperty cfi = new SimpleStringProperty();
    StringProperty vID = new SimpleStringProperty();

    /**
     *
     */
    public VlanDataBinding() {
        setInitialValues();
    }

    /**
     * Return override tpID property
     *
     * @return
     */
    public BooleanProperty getOverrideTPID() {
        return overrideTPID;
    }

    /**
     * Set override tpID property
     *
     * @param overrideTPID
     */
    public void setOverrideTPID(BooleanProperty overrideTPID) {
        this.overrideTPID = overrideTPID;
    }

    /**
     * Return tpID property
     *
     * @return
     */
    public StringProperty getTpid() {
        return tpid;
    }

    /**
     * Set tpID property
     *
     * @param tpid
     */
    public void setTpid(StringProperty tpid) {
        this.tpid = tpid;
    }

    /**
     * Return priority property
     *
     * @return
     */
    public StringProperty getPriority() {
        return priority;
    }

    /**
     * Set priority property
     *
     * @param priority
     */
    public void setPriority(StringProperty priority) {
        this.priority = priority;
    }

    /**
     * Return CFI property
     *
     * @return
     */
    public StringProperty getCfi() {
        return cfi;
    }

    /**
     * Set CFI property
     *
     * @param cfi
     */
    public void setCfi(StringProperty cfi) {
        this.cfi = cfi;
    }

    /**
     * Return vID property
     *
     * @return
     */
    public StringProperty getvID() {
        return vID;
    }

    /**
     * Set vID property
     *
     * @param vID
     */
    public void setvID(StringProperty vID) {
        this.vID = vID;
    }

    /**
     * Initialize property values
     */
    @Override
    protected void setInitialValues() {
        overrideTPID.set(false);
        tpid.setValue("FFFF");
        priority.set("0");
        cfi.set("0");
        vID.set("0");
    }

    /**
     * Write serialized property values
     *
     * @param out
     * @throws IOException
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(overrideTPID.get());
        out.writeObject(tpid.get());
        out.writeObject(priority.get());
        out.writeObject(cfi.get());
        out.writeObject(vID.get());
    }

    /**
     * Read serialized property values
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        overrideTPID.set(in.readBoolean());
        tpid.set((String) in.readObject());
        priority.set((String) in.readObject());
        cfi.set((String) in.readObject());
        vID.set((String) in.readObject());
    }

}

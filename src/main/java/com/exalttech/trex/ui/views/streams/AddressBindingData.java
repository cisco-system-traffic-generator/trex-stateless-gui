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
import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Abstract class that present Address data binding model
 *
 * @author Georgekh
 */
public abstract class AddressBindingData extends AbstractStreamDataBinding implements Serializable {

    AddressInfo source;
    AddressInfo destination;

    /**
     *
     */
    public AddressBindingData() {
        setInitialValues();
    }

    /**
     * Set destination info
     *
     * @param destination
     */
    public void setDestination(AddressInfo destination) {
        this.destination = destination;
    }

    /**
     * Return destination info
     *
     * @return
     */
    public AddressInfo getDestination() {
        if (destination == null) {
            destination = new AddressInfo();
        }
        return destination;
    }

    /**
     * Set source info
     *
     * @param source
     */
    public void setSource(AddressInfo source) {
        this.source = source;
    }

    /**
     * Return source info
     *
     * @return
     */
    public AddressInfo getSource() {
        if (source == null) {
            source = new AddressInfo();
        }
        return source;
    }

    /**
     * Address info data binding model
     */
    public static class AddressInfo implements Externalizable {

        StringProperty addressProperty = new SimpleStringProperty();
        StringProperty modeProperty = new SimpleStringProperty();
        StringProperty countProperty = new SimpleStringProperty();
        StringProperty stepProperty = new SimpleStringProperty();

        /**
         * Constructor
         */
        public AddressInfo() {
            // constructor
        }

        /**
         * Return Address property
         *
         * @return
         */
        public StringProperty getAddressProperty() {
            return addressProperty;
        }

        /**
         * Set Address property
         *
         * @param addressProperty
         */
        public void setAddressProperty(StringProperty addressProperty) {
            this.addressProperty = addressProperty;
        }

        /**
         * Return Mode property
         *
         * @return
         */
        public StringProperty getModeProperty() {
            return modeProperty;
        }

        /**
         * Set Mode property
         *
         * @param modeProperty
         */
        public void setModeProperty(StringProperty modeProperty) {
            this.modeProperty = modeProperty;
        }

        /**
         * Return Count property
         *
         * @return
         */
        public StringProperty getCountProperty() {
            return countProperty;
        }

        /**
         * Set count property
         *
         * @param countProperty
         */
        public void setCountProperty(StringProperty countProperty) {
            this.countProperty = countProperty;
        }

        /**
         * Return Step property
         *
         * @return
         */
        public StringProperty getStepProperty() {
            return stepProperty;
        }

        /**
         * Set step property
         *
         * @param stepProperty
         */
        public void setStepProperty(StringProperty stepProperty) {
            this.stepProperty = stepProperty;
        }

        /**
         * Write serialized binding data
         *
         * @param out
         * @throws IOException
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(getAddressProperty().get());
            out.writeObject(getModeProperty().get());
            out.writeObject(getCountProperty().get());
            out.writeObject(getStepProperty().get());
        }

        /**
         * Read serialized binding data
         *
         * @param in
         * @throws IOException
         * @throws ClassNotFoundException
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            getAddressProperty().set((String) in.readObject());
            getModeProperty().set((String) in.readObject());
            getCountProperty().set((String) in.readObject());
            getStepProperty().set((String) in.readObject());
        }

        /**
         * Reset address info property values
         *
         * @param address
         * @param mode
         */
        public void resetModel(String address, String mode) {
            addressProperty.set(address);
            modeProperty.set(mode);
            countProperty.set("16");
            stepProperty.set("1");
        }
    }
}

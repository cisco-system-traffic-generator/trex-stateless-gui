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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public abstract class AddressDataBinding extends AbstractStreamDataBinding {
    private AddressInfo source = new AddressInfo();
    private AddressInfo destination = new AddressInfo();

    public AddressDataBinding() {
        setInitialValues();
    }

    @JsonProperty("destination")
    public void setDestination(AddressInfo destination) {
        this.destination = destination;
    }

    @JsonProperty("destination")
    public AddressInfo getDestination() {
        if (destination == null) {
            destination = new AddressInfo();
        }
        return destination;
    }

    @JsonProperty("source")
    public void setSource(AddressInfo source) {
        this.source = source;
    }

    @JsonProperty("source")
    public AddressInfo getSource() {
        if (source == null) {
            source = new AddressInfo();
        }
        return source;
    }

    public static class AddressInfo {
        private StringProperty addressProperty = new SimpleStringProperty();
        private StringProperty modeProperty = new SimpleStringProperty();
        private StringProperty countProperty = new SimpleStringProperty();
        private StringProperty stepProperty = new SimpleStringProperty();

        public AddressInfo() {
            // constructor
        }

        @JsonIgnore
        public StringProperty getAddressProperty() {
            return addressProperty;
        }

        @JsonIgnore
        public StringProperty getModeProperty() {
            return modeProperty;
        }

        @JsonIgnore
        public StringProperty getCountProperty() {
            return countProperty;
        }

        @JsonIgnore
        public StringProperty getStepProperty() {
            return stepProperty;
        }

        @JsonProperty("address")
        public String getAddress() {
            return addressProperty.get();
        }

        @JsonProperty("address")
        public void setAddress(final String address) {
            addressProperty.set(address);
        }

        @JsonProperty("mode")
        public String getMode() {
            return modeProperty.get();
        }

        @JsonProperty("mode")
        public void setMode(final String mode) {
            modeProperty.set(mode);
        }

        @JsonProperty("count")
        public String getCount() {
            return countProperty.get();
        }

        @JsonProperty("count")
        public void setCount(final String count) {
            countProperty.set(count);
        }

        @JsonProperty("step")
        public String getStep() {
            return stepProperty.get();
        }

        @JsonProperty("step")
        public void setStep(final String step) {
            stepProperty.set(step);
        }

        public void resetModel(String address, String mode) {
            addressProperty.set(address);
            modeProperty.set(mode);
            countProperty.set("16");
            stepProperty.set("1");
        }
    }
}

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
package com.exalttech.trex.ui.views.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * IPv4/MAC address protocol data
 *
 * @author Georgekh
 */
public class AddressProtocolData {

    private ObjectProperty<String> addressProperty = new SimpleObjectProperty<>();
    private ObjectProperty<String> typeProperty = new SimpleObjectProperty<>();
    private ObjectProperty<String> countProperty = new SimpleObjectProperty<>();
    private ObjectProperty<String> stepProperty = new SimpleObjectProperty<>();

    /**
     * Return address property
     *
     * @return
     */
    public ObjectProperty<String> getAddressProperty() {
        return addressProperty;
    }

    /**
     * Return type property
     *
     * @return
     */
    public ObjectProperty<String> getTypeProperty() {
        return typeProperty;
    }

    /**
     * Return count property
     *
     * @return
     */
    public ObjectProperty<String> getCountProperty() {
        return countProperty;
    }

    /**
     * Return step property
     *
     * @return
     */
    public ObjectProperty<String> getStepProperty() {
        return stepProperty;
    }

    /**
     * Return address
     *
     * @return
     */
    public String getAddress() {
        return addressProperty.getValue();
    }

    /**
     * Return count
     *
     * @return
     */
    public String getCount() {
        return countProperty.getValue();
    }

    /**
     * Return type
     *
     * @return
     */
    public String getType() {
        return typeProperty.getValue();
    }

    /**
     * Return step
     *
     * @return
     */
    public String getStep() {
        return stepProperty.getValue();
    }

}

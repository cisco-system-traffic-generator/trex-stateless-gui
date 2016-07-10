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

/**
 * Profile multiplier model
 *
 * @author GeorgeKh
 */
public class ProfileMultiplier {

    String type;

    double value;

    int duration;

    String unit;

    String selectedType;

    /**
     *
     */
    public ProfileMultiplier() {
        this.type = "percentage";
        this.value = 1.0;
        this.duration = 0;
        unit = " ";
    }

    /**
     * Return multiplier type
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Set multiplier type
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Return multiplier value
     *
     * @return
     */
    public double getValue() {
        return value;
    }

    /**
     * Set multiplier value
     *
     * @param value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Return multiplier duration
     *
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Return multiplier duration
     *
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     */
    public String getUnit() {
        return unit;
    }

    /**
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Set selected type option
     *
     * @param selectedType
     */
    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

    /**
     * Return selection type option
     *
     * @return
     */
    public String getSelectedType() {
        return selectedType;
    }

}

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
package com.exalttech.trex.simulator.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Packet length info model
 *
 * @author Georgekh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PacketLength {

    int frameLength;

    String lengthType;

    int minLength;

    int maxLength;

    /**
     * Return frame length
     *
     * @return
     */
    public int getFrameLength() {
        return frameLength;
    }

    /**
     * Set frame length
     *
     * @param frameLength
     */
    public void setFrameLength(int frameLength) {
        this.frameLength = frameLength;
    }

    /**
     * Return length type
     *
     * @return
     */
    public String getLengthType() {
        return lengthType;
    }

    /**
     * Set length type
     *
     * @param lengthType
     */
    public void setLengthType(String lengthType) {
        this.lengthType = lengthType;
    }

    /**
     * Return min length
     *
     * @return
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Set min length
     *
     * @param minLength
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /**
     * Return max length
     *
     * @return
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Set max length
     *
     * @param maxLength
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

}

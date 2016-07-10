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
package com.exalttech.trex.ui.views.streams.builder;

import java.math.BigInteger;

/**
 * Model present payload
 *
 * @author Georgekh
 */
public class Payload {

    String payloadPattern;

    PayloadType payloadType;

    /**
     * Return payload pattern
     *
     * @return
     */
    public String getPayloadPattern() {
        return payloadPattern;
    }

    /**
     * Set payload pattern
     *
     * @param payloadPattern
     */
    public void setPayloadPattern(String payloadPattern) {
        this.payloadPattern = payloadPattern;
    }

    /**
     * Return payload type
     *
     * @return
     */
    public PayloadType getPayloadType() {
        return payloadType;
    }

    /**
     * Set payload type
     *
     * @param payloadType
     */
    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    /**
     * Return payload pad
     *
     * @param payloadString
     * @param length
     * @return
     */
    public byte[] getPayloadPad(String payloadString, int length) {
        byte[] pad = new BigInteger(payloadString, 16).toByteArray();
        if (pad.length == 1 && pad[0] == 0) {
            pad = new byte[length];
        }

        return pad;
    }
}

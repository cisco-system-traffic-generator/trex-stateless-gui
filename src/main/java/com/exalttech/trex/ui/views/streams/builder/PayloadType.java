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
import java.util.Random;

/**
 * Enumerator present payload type
 *
 * @author Georgekh
 */
public enum PayloadType {

    /**
     * Fix word payload type
     */
    FIXED_WORD {
        @Override
        public String getPadPayloadString(String payloadPattern, int length) {
            return fixHexLength(payloadPattern, length);
        }
    },
    /**
     * Increment byte payload type
     */
    INCREMENT_BYTE {
        @Override
        public String getPadPayloadString(String payloadPattern, int length) {
            String s = "01";
            BigInteger decimal = new BigInteger(s, 16);
            StringBuilder resultHex = new StringBuilder(s);
            for (int i = 0; i < 1000; i++) {
                decimal = decimal.add(BigInteger.ONE);
                String myString = String.format("%02X", decimal);
                if (myString.length() < 3) {
                    resultHex.append(myString);
                } else {
                    break;
                }
            }
            return fixHexLength(resultHex.toString(), length);
        }
    },
    /**
     * Decrement type payload type
     */
    DECREMENT_BYTE {
        @Override
        public String getPadPayloadString(String payloadPattern, int length) {
            String s = "FF";
            BigInteger decimal = new BigInteger(s, 16);
            StringBuilder resultHex = new StringBuilder(s);
            for (int i = 0; i < 1000; i++) {
                decimal = decimal.subtract(BigInteger.ONE);
                String myString = String.format("%02X", decimal);
                if (!myString.contains("-")) {
                    resultHex.append(myString);
                } else {
                    break;
                }
            }
            return fixHexLength(resultHex.toString(), length);
        }
    },
    /**
     * Random byte payload type
     */
    RANDOM {
        @Override
        public String getPadPayloadString(String payloadPattern, int length) {
            Random r = new Random();
            StringBuilder sb = new StringBuilder("43");
            while (sb.length() < length + 6) {
                sb.append(Integer.toHexString(r.nextInt()));
            }
            return sb.toString().substring(0, length);
        }
    };

    /**
     * Return pad payload string
     *
     * @param payloadPattern
     * @param length
     * @return
     */
    public abstract String getPadPayloadString(String payloadPattern, int length);

    /**
     * Return corrected payload string with specific length
     *
     * @param partialHex
     * @param hexLength
     * @return
     */
    private static String fixHexLength(String partialHex, int hexLength) {
        int length = hexLength;
        if (String.valueOf(partialHex.charAt(0)).matches("[abcdefABCDEF]")) {
            length -= 1;
        }
        int maxLoop = ((length - partialHex.length()) / partialHex.length()) + 1;

        StringBuilder resultHex = new StringBuilder(partialHex);
        for (int i = 0; i < maxLoop; i++) {

            resultHex.append(partialHex);
        }
        return resultHex.toString().substring(0, length);
    }

    /**
     * Return payload type
     *
     * @param type
     * @return
     */
    public static PayloadType getPayloadType(String type) {
        String payload = type.replaceAll(" ", "_").toUpperCase();
        return PayloadType.valueOf(payload);
    }
}

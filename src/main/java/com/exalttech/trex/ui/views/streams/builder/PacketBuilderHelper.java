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

import org.apache.log4j.Logger;

/**
 *
 * @author Georgekh
 */
public class PacketBuilderHelper {

    private static final Logger LOG = Logger.getLogger(PacketBuilderHelper.class.getName());

    /**
     *
     * @param rawData
     * @return
     */
    public static String getPacketHex(byte[] rawData) {
        String packetHex = "";
        try {
            StringBuilder myString = new StringBuilder();
            for (byte b : rawData) {
                myString.append(String.format("%02X", b));
            }
            packetHex = myString.toString();
        } catch (Exception ex) {
            LOG.error("Error generating packet hex", ex);
        }
        return packetHex;
    }

    /**
     *
     */
    private PacketBuilderHelper() {
        // private constructor
    }

}

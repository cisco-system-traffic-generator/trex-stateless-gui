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
package com.exalttech.trex.remote.models.profiles;

/**
 * Class that present packet info model
 *
 * @author GeorgeKh
 */
public class PacketInfo {

    int length;

    String type;

    /**
     * Return packet length
     *
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * Set packet length
     *
     * @param length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Return packet type
     *
     * @return
     */
    public String getType() {
        if (type == null) {
            return "";
        }
        return type;
    }

    /**
     * Set packet type
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PacketInfo{" + "length=" + length + ", type=" + type + '}';
    }

}

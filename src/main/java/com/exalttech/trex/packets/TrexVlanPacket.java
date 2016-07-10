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
package com.exalttech.trex.packets;

import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.packet.Dot1qVlanTagPacket;
import org.pcap4j.packet.namednumber.EtherType;

/**
 *
 * @author GeorgeKh
 */
public class TrexVlanPacket {

    private Dot1qVlanTagPacket packet;
    private byte priority;
    private boolean cfi;
    private short vid;
    private EtherType type;
    private Dot1qVlanTagPacket.Builder db;

    private boolean overrideType = false;

    /**
     *
     */
    public TrexVlanPacket() {
        this.priority = (byte) 0;
        this.cfi = false;
        this.vid = (short) 0;
        this.type = EtherType.getInstance((short) 0x0000);
    }

    /**
     *
     * @return
     */
    public Dot1qVlanTagPacket.Builder getBuilder() {
        return db;
    }

    /**
     *
     * @param builder
     */
    public void buildPacket(AbstractPacket.AbstractBuilder builder) {

        if (!this.type.equals(EtherType.getInstance((short) 0x0000))) {
            // do nothing if type not 0x0000
        } else if (builder == null) {
            this.type = EtherType.getInstance((short) 0xFFFF);
        } else {
            this.type = EtherType.IPV4;
        }
        db = new Dot1qVlanTagPacket.Builder();
        db.priority(priority)
                .cfi(cfi)
                .vid(vid)
                .type(type)
                .payloadBuilder(builder);
        this.packet = db.build();
    }

    /**
     *
     * @return
     */
    public Dot1qVlanTagPacket getPacket() {
        return packet;
    }

    /**
     *
     * @param packet
     */
    public void setPacket(Dot1qVlanTagPacket packet) {
        this.packet = packet;
    }

    /**
     *
     * @return
     */
    public byte getPriority() {
        return priority;
    }

    /**
     *
     * @param priority
     */
    public void setPriority(byte priority) {
        this.priority = priority;
    }

    /**
     *
     * @return
     */
    public boolean isCfi() {
        return cfi;
    }

    /**
     *
     * @param cfi
     */
    public void setCfi(boolean cfi) {
        this.cfi = cfi;
    }

    /**
     *
     * @return
     */
    public short getVid() {
        return vid;
    }

    /**
     *
     * @param vid
     */
    public void setVid(short vid) {
        this.vid = vid;
    }

    /**
     *
     * @return
     */
    public EtherType getType() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(short type) {
        this.type = EtherType.getInstance(type);
    }

    /**
     * Set override type indecator
     *
     * @param overrideType
     */
    public void setOverrideType(boolean overrideType) {
        this.overrideType = overrideType;
    }

    /**
     * Return true for override Type otherwise return false
     *
     * @return
     */
    public boolean isOverrideType() {
        return overrideType;
    }

}

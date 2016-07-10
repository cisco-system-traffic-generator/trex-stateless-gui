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
package com.exalttech.trex.ui;

/**
 * Enumerator that present port status
 *
 * @author GeorgeKh
 */
public enum PortState {

    /**
     *
     */
    DOWN("offline.png", "DOWN", "statsTableGreyValue", "offline.png"),
    /**
     *
     */
    IDLE("idle.png", "IDLE", "statsTableYellowValue", "idle.png"),
    /**
     *
     */
    STREAMS("idle.png", "IDLE", "statsTableYellowValue", "idle.png"),
    /**
     *
     */
    TX("stream.gif", "ACTIVE", "statsTableGreenValue", "stats_header_tx.gif"),
    /**
     *
     */
    PAUSE("pause.png", "PAUSE", "statsTablePurpleValue", "pause.png");

    String icon;
    String displayedState;
    String textColor;
    String statHeaderIcon;

    private PortState(String styleName, String displayedState, String textColor, String statHeaderIcon) {
        this.icon = styleName;
        this.displayedState = displayedState;
        this.textColor = textColor;
        this.statHeaderIcon = statHeaderIcon;
    }

    /**
     * Return state icon
     *
     * @return
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Return port displayed state
     *
     * @return
     */
    public String getDisplayedState() {
        return displayedState;
    }

    /**
     * Return port state text color
     *
     * @return
     */
    public String getTextColor() {
        return textColor;
    }

    /**
     * Return stats header icon
     *
     * @return
     */
    public String getStatHeaderIcon() {
        return statHeaderIcon;
    }

    /**
     * Return equivalent POrtState
     *
     * @param value
     * @return
     */
    public static PortState getPortStatus(String value) {
        return PortState.valueOf(value.toUpperCase());
    }
}

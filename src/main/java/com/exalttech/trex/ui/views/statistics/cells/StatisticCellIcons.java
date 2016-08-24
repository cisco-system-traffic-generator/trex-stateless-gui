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
package com.exalttech.trex.ui.views.statistics.cells;

import com.exalttech.trex.ui.PortState;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

/**
 * Singleton class to return statistic image
 * @author Georgekh
 */
public class StatisticCellIcons {

    Image noArrowIcon = new Image("/icons/no_arrow.png");
    private static StatisticCellIcons instance = null;

    ArrowTypeIcons greenIcons;
    ArrowTypeIcons redIcons;
    // port state icon
    Map<String, Image> imagesMap = new HashMap<>();

    public static StatisticCellIcons getInstance() {
        if (instance == null) {
            instance = new StatisticCellIcons();
        }
        return instance;
    }

    protected StatisticCellIcons() {
        imagesMap.put(PortState.DOWN.name(), new Image("/icons/" + PortState.DOWN.getIcon()));
        imagesMap.put(PortState.IDLE.name(), new Image("/icons/" + PortState.IDLE.getIcon()));
        imagesMap.put(PortState.PAUSE.name(), new Image("/icons/" + PortState.PAUSE.getIcon()));
        imagesMap.put(PortState.STREAMS.name(), new Image("/icons/" + PortState.STREAMS.getIcon()));
        imagesMap.put(PortState.TX.name(), new Image("/icons/" + PortState.TX.getIcon()));

        greenIcons = new ArrowTypeIcons(new Image("/icons/one_down_green.png"), new Image("/icons/two_down_green.png"), new Image("/icons/three_down_green.png"));
        redIcons = new ArrowTypeIcons(new Image("/icons/one_up_red.png"), new Image("/icons/two_up_red.png"), new Image("/icons/three_up_red.png"));
    }

    /**
     * Return no arrow icon
     * @return 
     */
    public Image getNoArrowIcon() {
        return noArrowIcon;
    }

    /**
     * Return port state icon
     * @param state
     * @return 
     */
    public Image getPortStateIcon(String state) {
        return imagesMap.get(state);
    }

    /**
     * Return green arrows icon
     * @return 
     */
    public ArrowTypeIcons getGreenIcons() {
        return greenIcons;
    }

    /**
     * Return red arrows icon
     * @return 
     */
    public ArrowTypeIcons getRedIcons() {
        return redIcons;
    }

}

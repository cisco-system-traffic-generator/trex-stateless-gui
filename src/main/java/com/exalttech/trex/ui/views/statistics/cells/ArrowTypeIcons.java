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

import javafx.scene.image.Image;

/**
 * Arrow type icon model
 * @author Georgekh
 */
public class ArrowTypeIcons {

    Image oneArrow;
    Image twoArrows;
    Image threeArrows;

    public ArrowTypeIcons(Image oneArrow, Image twoArrows, Image threeArrows) {
        this.oneArrow = oneArrow;
        this.twoArrows = twoArrows;
        this.threeArrows = threeArrows;
    }

    /**
     * Return icon with one arrow
     * @return 
     */
    public Image getOneArrow() {
        return oneArrow;
    }

    /**
     * Return icon with two arrows
     * @return 
     */
    public Image getTwoArrows() {
        return twoArrows;
    }

    /**
     * Return icon with three arrows
     * @return 
     */
    public Image getThreeArrows() {
        return threeArrows;
    }
}

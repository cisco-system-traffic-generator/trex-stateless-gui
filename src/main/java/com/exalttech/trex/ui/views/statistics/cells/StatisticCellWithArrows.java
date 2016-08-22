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

import com.exalttech.trex.util.Util;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Statistical cell with icon implementation
 *
 * @author Georgekh
 */
public class StatisticCellWithArrows extends HBox implements StatisticCell {

    ImageView imageView;
    Label value;
    String unit;

    public StatisticCellWithArrows(double width, boolean odd, String unit) {
        this.unit = unit;
        setPrefSize(width, 22);
        setSpacing(5);
        setAlignment(Pos.CENTER_RIGHT);
        getStyleClass().add("statsTableColCell");
        if (odd) {
            getStyleClass().add("statsTableColCellOdd");
        }
        imageView = new ImageView();
        getChildren().add(imageView);
        value = new Label();
        getChildren().add(value);
    }

    /**
     * Update cell values
     *
     * @param oldValue
     * @param newValue
     */
    @Override
    public void updateItem(String oldValue, String newValue) {
        value.setText(Util.getFormatted(newValue, true, unit));
        ArrowTypeIcons arrowType = StatisticCellIcons.getInstance().getGreenIcons();
        // reset image view
        imageView.setImage(StatisticCellIcons.getInstance().getNoArrowIcon());
        if (!Util.isNullOrEmpty(newValue) && !Util.isNullOrEmpty(oldValue)) {
            double currentVal = Double.parseDouble(newValue);
            double prevVal = Double.parseDouble(oldValue);
            if (currentVal != 0 && prevVal != 0) {
                double diff = currentVal - prevVal;
                if (diff > 0) {
                    arrowType = StatisticCellIcons.getInstance().getRedIcons();
                }
                double val = Math.abs((diff / prevVal) * 100.0);
                //change in 1% is not meaningful
                if (val < 1) {
                    imageView.setImage(StatisticCellIcons.getInstance().getNoArrowIcon());
                } else if (val > 5) {
                    imageView.setImage(arrowType.getThreeArrows());
                } else if (val > 2) {
                    imageView.setImage(arrowType.getTwoArrows());
                } else {
                    imageView.setImage(arrowType.getOneArrow());
                }
            }
        }
    }
}

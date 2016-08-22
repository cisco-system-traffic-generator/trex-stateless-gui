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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Header cell with icon implementation
 * 
 * @author Georgekh
 */
public class HeaderCellWithIcon extends HBox implements StatisticCell {
    
    ImageView statusView;
    Label titleLabel;
    
    public HeaderCellWithIcon(double width) {
        getStyleClass().add("statsTableColHeader");
        setPrefSize(width, 25);
        setSpacing(5);
        setAlignment(Pos.CENTER);
        statusView = new ImageView();
        getChildren().add(statusView);
        titleLabel = new Label();
        titleLabel.getStyleClass().add("statHeaderWithIcon");
        titleLabel.setAlignment(Pos.CENTER);
        getChildren().add(titleLabel);

    }
    
    /**
     * Set title
     * @param title 
     */
    public void setTitle(String title){
        titleLabel.setText(title);
    }
    
    /**
     * Update cell values
     * @param cachedValue
     * @param newValue 
     */
    @Override
    public void updateItem(String cachedValue, String newValue) {
        statusView.setImage(StatisticCellIcons.getInstance().getPortStateIcon(newValue.toUpperCase()));
    }
    
}

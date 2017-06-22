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

/**
 * Header cell implementation
 * @author Georgekh
 */
public class HeaderCell extends Label implements StatisticCell{

    public HeaderCell(double width){
        setPrefSize(width, 25);
        setAlignment(Pos.CENTER);
        getStyleClass().add("statsTableColHeader");
    }

    public HeaderCell(double width, String value){
        this(width);
        setText(value);
    }

    public HeaderCell(final double width, final String value, final boolean isStopped) {
        this(width, value);
        if (isStopped) {
            getStyleClass().add("stats-table-marked-cell");
        }
    }

    public HeaderCell(String value){
        setAlignment(Pos.CENTER);
        setText(value);
    }

    /**
     * Update cell values
     * @param cachedValue
     * @param newValue
     */
    @Override
    public void updateItem(String cachedValue, String newValue) {
        setText(newValue);
    }
    
}

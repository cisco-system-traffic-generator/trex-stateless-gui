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

/**
 * Statistical row cell implementation
 * @author Georgekh
 */
public class StatisticRow {
    
    String key;
    
    String attributeName;
    
    CellType cellType;

    boolean formatted = false;
    
    boolean rightPosition = true;
    
    String unit;
    
    public StatisticRow(String key, String attributeName, CellType cellType, boolean formatted, String unit) {
        this.key = key;
        this.attributeName = attributeName;
        this.cellType = cellType;
        this.formatted = formatted;
        this.unit = unit;
    }

    /**
     * 
     * @return 
     */
    public String getKey() {
        return key;
    }

    /**
     * 
     * @return 
     */
    public CellType getCellType() {
        return cellType;
    }

    /**
     * 
     * @return 
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * 
     * @return 
     */
    public boolean isFormatted() {
        return formatted;
    }

    /**
     * 
     * @return 
     */
    public boolean isRightPosition() {
        return rightPosition;
    }

    /**
     * 
     * @param rightPosition 
     */
    public void setRightPosition(boolean rightPosition) {
        this.rightPosition = rightPosition;
    }

    /**
     * 
     * @return 
     */
    public String getUnit() {
        return unit;
    }

}

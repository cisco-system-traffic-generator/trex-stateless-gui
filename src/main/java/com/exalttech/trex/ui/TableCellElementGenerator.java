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

import com.exalttech.trex.util.Util;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Helper class for build stats table
 *
 * @author GeorgeKh
 */
public class TableCellElementGenerator {

    /**
     * Return table cell element
     *
     * @param text
     * @param isOdd
     * @param width
     * @param isRight
     * @return
     */
    public static Label getTableCellElement(String text, boolean isOdd, double width, boolean isRight) {
        Label label = new Label(text);
        label.setPrefSize(width, 25);
        label.setAlignment(Pos.CENTER_LEFT);
        if (isRight) {
            label.setAlignment(Pos.CENTER_RIGHT);
        }
        label.getStyleClass().add("statsTableColCell");
        if (isOdd) {
            label.getStyleClass().add("statsTableColCellOdd");
        }
        return label;
    }

    /**
     * Return table cell element
     *
     * @param text
     * @param isOdd
     * @param isRight
     * @return
     */
    public static Label getTableCellElement(String text, boolean isOdd, boolean isRight) {
        return getTableCellElement(text, isOdd, 150, isRight);
    }

    /**
     * Return error table element cell
     *
     * @param text
     * @param isOdd
     * @param width
     * @return
     */
    public static Label getTableErrorElementValue(String text, boolean isOdd, double width) {

        Label label = getTableCellElement(text, isOdd, width, true);
        String valueColor = "statsTableGreenValue";
        if (!Util.isNullOrEmpty(text) && Double.parseDouble(text) > 0) {
            valueColor = "statsTableRedValue";
        }
        label.getStyleClass().add(valueColor);
        return label;
    }

    /**
     * Return table header cell element
     *
     * @param text
     * @param width
     * @return
     */
    public static Label getTableHeaderElement(String text, double width) {
        Label label = new Label(text);
        label.setPrefSize(width, 25);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("statsTableColHeader");
        return label;
    }

    /**
     * Return table header cell element
     *
     * @param text
     * @return
     */
    public static Label getTableHeaderElement(String text) {
        return getTableHeaderElement(text, 150);
    }

    /**
     * Return table header cell element with icon
     *
     * @param text
     * @param width
     * @param icon
     * @return
     */
    public static HBox getTableHeaderElementWithIcon(String text, double width, String icon) {
        HBox header = new HBox();
        header.getStyleClass().add("statsTableColHeader");
        header.setPrefSize(width, 25);
        header.setSpacing(5);
        header.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView(new Image("/icons/" + icon));
        header.getChildren().add(imageView);
        Label label = new Label(text);
        label.getStyleClass().add("statHeaderWithIcon");
        label.setAlignment(Pos.CENTER);
        header.getChildren().add(label);
        return header;
    }

    /**
     *
     */
    private TableCellElementGenerator() {
        // private constructor
    }

}

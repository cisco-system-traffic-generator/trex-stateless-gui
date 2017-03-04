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
/*



 */
package com.exalttech.trex.ui.views;

import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 *
 * Class that present stats info view
 *
 * @author GeorgeKh
 */
public class StatsInfoView extends AnchorPane {

    Label valueLabel;
    Label unitLabel;
    private final boolean isColored;

    /**
     *
     * @param title
     */
    public StatsInfoView(@NamedArg("title") String title) {
        this(title, false);
    }

    /**
     *
     * @param title
     * @param isColored
     */
    public StatsInfoView(@NamedArg("title") String title, @NamedArg("isColored") boolean isColored) {
        setTopAnchor(this, 0d);
        setLeftAnchor(this, 0d);
        setBottomAnchor(this, 0d);
        setRightAnchor(this, 0d);
        this.isColored = isColored;
        buildUI(title);
    }

    /**
     * Build UI
     *
     * @param title
     */
    private void buildUI(String title) {
        HBox valueContainer = new HBox();
        valueContainer.setAlignment(Pos.CENTER);
        valueContainer.setSpacing(5);
        valueLabel = new Label();
        valueLabel.getStyleClass().add("dashboardStatsValue");
        if (isColored) {
            valueLabel.getStyleClass().add("statsTableGreenValue");
        }
        valueContainer.getChildren().add(valueLabel);
        unitLabel = new Label();
        unitLabel.getStyleClass().add("dashboardStatsUnit");
        valueContainer.getChildren().add(unitLabel);

        getChildren().add(valueContainer);
        setTopAnchor(valueContainer, 30d);
        setLeftAnchor(valueContainer, 0d);
        setRightAnchor(valueContainer, 0d);

        HBox titleContainer = new HBox();
        titleContainer.setAlignment(Pos.CENTER);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dashboardStatsTitle");
        titleContainer.getChildren().add(titleLabel);
        getChildren().add(titleContainer);
        setTopAnchor(titleContainer, 55d);
        setLeftAnchor(titleContainer, 0d);
        setRightAnchor(titleContainer, 0d);
    }

    /**
     * Set value
     *
     * @param value
     */
    public void setValue(String value) {
        valueLabel.getStyleClass().remove("statsTableRedValue");
        if (value.indexOf(" ") != -1) {
            String[] splitValue = value.split(" ");
            String data = splitValue[0];
            valueLabel.setText(data);
            unitLabel.setText(splitValue[1]);
            if (isColored && Double.parseDouble(data) > 0) {
                valueLabel.getStyleClass().add("statsTableRedValue");
            }
        } else {
            valueLabel.setText(value);
        }
    }
}

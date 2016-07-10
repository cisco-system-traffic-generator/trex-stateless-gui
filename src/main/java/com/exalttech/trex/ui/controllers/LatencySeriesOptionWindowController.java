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
package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.models.ChartSeries;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Georgekh
 */
public class LatencySeriesOptionWindowController extends DialogView implements Initializable {

    @FXML
    VBox optionContainer;
    private BooleanProperty updateChartProperty;
    @FXML
    ToggleGroup latencyInterval;
    private StringProperty intervalProperty;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 
    }

    /**
     *
     * @param chartSeriesList
     * @param updateChartProperty
     * @param intervalProperty
     */
    public void initOption(List<ChartSeries> chartSeriesList, BooleanProperty updateChartProperty, StringProperty intervalProperty) {
        this.updateChartProperty = updateChartProperty;
        this.intervalProperty = intervalProperty;
        updateIntervalSelection();
        optionContainer.getChildren().clear();
        for (ChartSeries series : chartSeriesList) {
            CheckBox showSeriesCB = new CheckBox(series.getName());
            showSeriesCB.selectedProperty().bindBidirectional(series.getShowSeriesProperty());
            optionContainer.getChildren().add(showSeriesCB);
        }
    }

    /**
     * Update interval selection
     */
    private void updateIntervalSelection() {
        for (Toggle toggle : latencyInterval.getToggles()) {
            if (((RadioButton) toggle).getText().equals(intervalProperty.get())) {
                latencyInterval.selectToggle(toggle);
                return;
            }
        }
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        close(stage);
    }

    private void close(Stage stage) {
        stage.hide();
    }

    /**
     *
     * @param event
     */
    @FXML
    public void handleOKButtonClicked(ActionEvent event) {
        intervalProperty.set(((RadioButton) latencyInterval.getSelectedToggle()).getText());
        updateChartProperty.set(!updateChartProperty.get());
        Button source = (Button) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        close(stage);
    }
}

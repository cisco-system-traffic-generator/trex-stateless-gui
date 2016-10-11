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

import com.exalttech.trex.remote.models.validate.Rate;
import com.exalttech.trex.ui.MultiplierType;
import com.exalttech.trex.ui.components.MultiplierOption;
import com.exalttech.trex.ui.components.events.MultiplierSelectionEvent;
import com.exalttech.trex.ui.views.models.AssignedProfile;
import com.exalttech.trex.util.Util;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.layout.AnchorPane.setBottomAnchor;
import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setRightAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

/**
 * Multiplier option container view implementation
 *
 * @author GeorgeKh
 */
public class MultiplierView extends AnchorPane implements MultiplierSelectionEvent {
    
    DecimalFormat FRACTION_FORMATTER = new DecimalFormat("#0.#######");
    Map<MultiplierType, MultiplierOption> multiplierOptionMap = new HashMap<>();
    ToggleGroup group;
    MultiplierOption currentSelected;
    MultiplierOption notToUpdateField;
    Rate rate;
    Slider slider;

    CheckBox durationCB;
    TextField durationTF;
    boolean updateAll = true;
    MultiplierOptionChangeHandler optionValueChangeHandler;
    private boolean fireUpdateCommand = true;

    /**
     *
     * @param optionValueChangeHandler
     */
    public MultiplierView(MultiplierOptionChangeHandler optionValueChangeHandler) {
        this.optionValueChangeHandler = optionValueChangeHandler;
        setTopAnchor(this, 0d);
        setLeftAnchor(this, 0d);
        setBottomAnchor(this, 0d);
        setRightAnchor(this, 0d);

        buildUI();
    }

    /**
     * Build UI
     */
    private void buildUI() {
        // add slider area
        addLabel("Bandwidth", 7, 336);
        addLabel("0", 22, 287);
        addLabel("100", 22, 454);

        slider = new Slider(0, 100, 1);
        slider.setDisable(true);
        getChildren().add(slider);
        MultiplierView.setTopAnchor(slider, 22d);
        MultiplierView.setLeftAnchor(slider, 304d);

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // check min value for slider
                if((double)newValue< MultiplierType.percentage.getMinRate(rate)){
                    slider.setValue(MultiplierType.percentage.getMinRate(rate));
                }
                updateOptionsValues(slider.getValue(), updateAll);
                if (fireUpdateCommand) {
                    optionValueChangeHandler.optionValueChanged();
                }
            }
        });

        // add separator
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefHeight(1);
        getChildren().add(separator);
        MultiplierView.setTopAnchor(separator, 50d);
        MultiplierView.setLeftAnchor(separator, 15d);
        MultiplierView.setRightAnchor(separator, 15d);

        group = new ToggleGroup();
        int index = 0;
        double prevComponentWidth = 0;
        AnchorPane optionContainer = new AnchorPane();
        getChildren().add(optionContainer);
        MultiplierView.setTopAnchor(optionContainer, 51d);
        MultiplierView.setLeftAnchor(optionContainer, 15d);
        MultiplierView.setRightAnchor(optionContainer, 150d);

        for (MultiplierType type : MultiplierType.values()) {
            MultiplierOption option = new MultiplierOption(type.getTitle(), group, type, this);
            option.setMultiplierSelectionEvent(this);
            optionContainer.getChildren().add(option);
            double leftSpace = prevComponentWidth + (index * 15);
            MultiplierView.setLeftAnchor(option, leftSpace);
            MultiplierView.setTopAnchor(option, 0d);
            MultiplierView.setBottomAnchor(option, 0d);
            multiplierOptionMap.put(type, option);
            index++;
            prevComponentWidth += option.getComponentWidth();
        }
        multiplierOptionMap.get(MultiplierType.pps).setSelected();

        // add suration
        Separator vSeparator = new Separator(Orientation.VERTICAL);
        vSeparator.setPrefWidth(1);
        getChildren().add(vSeparator);
        MultiplierView.setTopAnchor(vSeparator, 93d);
        MultiplierView.setLeftAnchor(vSeparator, 560d);
        MultiplierView.setBottomAnchor(vSeparator, 15d);

        addLabel("Duration", 70, 606);

        durationCB = new CheckBox();
        getChildren().add(durationCB);
        MultiplierView.setTopAnchor(durationCB, 94d);
        MultiplierView.setLeftAnchor(durationCB, 581d);
        MultiplierView.setBottomAnchor(durationCB, 20d);

        durationTF = new TextField("0");
        durationTF.setPrefSize(70, 22);
        durationTF.setDisable(true);
        getChildren().add(durationTF);
        MultiplierView.setTopAnchor(durationTF, 93d);
        MultiplierView.setLeftAnchor(durationTF, 606d);
        MultiplierView.setBottomAnchor(durationTF, 15d);
        durationTF.disableProperty().bind(durationCB.selectedProperty().not());
        durationTF.setTextFormatter(Util.getNumberFilter(4));
    }

    /**
     * Add label widget
     *
     * @param title
     * @param top
     * @param left
     */
    private void addLabel(String title, double top, double left) {
        Label label = new Label(title);
        getChildren().add(label);
        MultiplierView.setTopAnchor(label, top);
        MultiplierView.setLeftAnchor(label, left);
    }

    /**
     * Handling multiplier select event
     *
     * @param type
     */
    @Override
    public void onMultiplierSelect(MultiplierType type) {
        currentSelected = multiplierOptionMap.get(type);
    }

    /**
     * Return PPS value
     *
     * @return
     */
    public double getPPSValue() {
        MultiplierOption option = multiplierOptionMap.get(MultiplierType.pps);
        validateSelectedMultiplierValue();
        // force PPS value to 1 if it less than 1
        if (option.getMultiplierValue() < 1 && MultiplierType.pps.getMaxRate(rate) > 0) {
            option.setValue(1);
            updateAll(option);
        }
        return option.getMultiplierValue();
    }

    private void validateSelectedMultiplierValue(){
        if(currentSelected.getValue() < currentSelected.getType().getMinRate(rate)){
            currentSelected.setValue(currentSelected.getType().getMinRate(rate));
        }
    }
    /**
     * Return multiplier type
     *
     * @return
     */
    public String getType() {
        return currentSelected.getType().name();
    }

    /**
     * Set value
     *
     * @param value
     */
    public void setValue(double value) {
        currentSelected.setValue(value);
    }

    /**
     * Set multiplier type
     *
     * @param type
     */
    public void setSelected(MultiplierType type) {
        currentSelected = multiplierOptionMap.get(type);
        currentSelected.setSelected();
    }

    /**
     * Return multiplier options
     *
     * @param key
     * @return
     */
    public MultiplierOption getMultiplierOption(MultiplierType key) {
        return multiplierOptionMap.get(key);
    }

    /**
     * Update options values
     *
     * @param sliderValue
     * @param updateAll
     */
    public void updateOptionsValues(double sliderValue, boolean updateAll) {
        for (MultiplierType type : MultiplierType.values()) {
            if (updateAll || type != notToUpdateField.getType()) {
                double val = calcTypeValue(sliderValue, type);
                getMultiplierOption(type).setValue(val);
            }
        }
    }

    /**
     * Reset options/slider value
     */
    public void resetAllOption() {
        slider.setValue(0);
    }

    /**
     * Calculate multiplier option related value
     *
     * @param sliderValue
     * @param type
     * @return
     */
    private double calcTypeValue(double sliderValue, MultiplierType type) {
        if (rate != null) {
            return Double.parseDouble(FRACTION_FORMATTER.format((sliderValue * type.getMaxRate(rate)) / (MultiplierType.percentage.getMaxRate(rate))));
        }
        return 0;
    }

    /**
     * Return duration value
     *
     * @return
     */
    public int getDuration() {
        if (durationCB.isSelected()) {
            return Integer.parseInt(durationTF.getText());
        }
        return -1;
    }

    /**
     * Fill assign profile value
     *
     * @param assigned
     */
    public void fillAssignedProfileValues(AssignedProfile assigned) {
        rate = assigned.getRate();
        fireUpdateCommand = false;
        resetAllOption();
        MultiplierType type = MultiplierType.valueOf(assigned.getMultiplier().getType());
        setSelected(type);

        // define slider value according to the PPS type
        if (assigned != null && assigned.getRate() != null) {
            double value = assigned.getMultiplier().getValue();
            double sliderValue = getSliderValue(type, value);
            slider.setValue(sliderValue);
            fireUpdateCommand = true;
            // update selection
            MultiplierType selectedType = MultiplierType.valueOf(assigned.getMultiplier().getSelectedType());
            setSelected(selectedType);
        }
        // update duration
        durationCB.setSelected(assigned.isHasDuration());
        durationTF.setText(String.valueOf(assigned.getMultiplier().getDuration()));
    }

    private double getSliderValue(MultiplierType type, double value) {
        
        return value * MultiplierType.percentage.getMaxRate(rate) / type.getMaxRate(rate);
    }

    /**
     * Assign new profile
     *
     * @param assigned
     */
    public void assignNewProfile(AssignedProfile assigned) {
        rate = assigned.getRate();
        fireUpdateCommand = false;
        slider.setValue(MultiplierType.percentage.getMaxRate(rate));
        fireUpdateCommand = true;
        slider.setDisable(false);
    }

    /**
     * Validate input event handler
     *
     * @return
     */
    @Override
    public EventHandler<KeyEvent> validateInput() {
        return (KeyEvent event) -> {
            TextField source = (TextField) event.getSource();
            if (".".equals(event.getCharacter()) && source.getText().contains(".")) {
                event.consume();
            }
            if ((event.getCode() == KeyCode.BACK_SPACE && Util.isNullOrEmpty(source.getText()))
                    || ".".equals(source.getText())) {
                return;
            }
            updateAll(currentSelected);
            optionValueChangeHandler.optionValueChanged();
        };
    }

    /**
     * Update all multiplier option values
     *
     * @param option
     */
    private void updateAll(MultiplierOption option) {
        if (rate != null) {
            notToUpdateField = option;
            double currentValue = option.getValue();

            double sliderValue = getSliderValue(option.getType(), currentValue);
            double maxValue = calcTypeValue(100, option.getType());
            if (currentValue > maxValue) {
                sliderValue = 100;
                option.setValue(maxValue);
            } 
            
            updateAll = false;
            slider.setValue(sliderValue);
            updateAll = true;
        }
    }

    /**
     * Return whether the duration is checked
     *
     * @return
     */
    public boolean isDurationEnable() {
        return durationCB.isSelected();
    }

    /**
     * Return slider value
     *
     * @return
     */
    public double getSliderValue() {
        return slider.getValue();
    }

    /**
     * Set slider value
     *
     * @param sliderValue
     */
    public void setSliderValue(double sliderValue) {
        slider.setValue(sliderValue);
    }
}

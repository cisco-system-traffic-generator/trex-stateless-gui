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
package com.exalttech.trex.ui.components;

import com.exalttech.trex.ui.MultiplierType;
import com.exalttech.trex.ui.components.events.MultiplierSelectionEvent;
import com.exalttech.trex.util.Util;
import java.util.function.UnaryOperator;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.log4j.Logger;

/**
 * Multiplier option view implementation
 *
 * @author GeorgeKh
 */
public class MultiplierOption extends AnchorPane {

    private final static Logger LOG = Logger.getLogger(MultiplierOption.class.getName());

    TextField value;
    RadioButton selection;
    MultiplierType type;
    MultiplierSelectionEvent multiplierSelectionEvent;
    double defaultWidth = 120;
    boolean valueChangedBySet = false;
    double multiplierValue = 0;
    
    /**
     *
     * @param title
     * @param group
     * @param type
     * @param multiplierSelectionEvent
     */
    public MultiplierOption(String title, ToggleGroup group, MultiplierType type, MultiplierSelectionEvent multiplierSelectionEvent) {
        this.type = type;
        this.multiplierSelectionEvent = multiplierSelectionEvent;
        buildUI(title, group);
        setPrefWidth(defaultWidth);
    }

    /**
     * Build multiplier view UI
     *
     * @param title
     * @param group
     */
    private void buildUI(String title, ToggleGroup group) {
        // add radio button
        selection = new RadioButton(title);
        selection.setToggleGroup(group);
        selection.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                multiplierSelectionEvent.onMultiplierSelect(type);
            }
            value.setDisable(!newValue);
        });
        setTooltip();
        getChildren().add(selection);
        MultiplierOption.setTopAnchor(selection, 15d);
        MultiplierOption.setLeftAnchor(selection, 0d);

        // text field
        value = new TextField();
        value.setPrefSize(120, 22);
        value.setDisable(true);
        value.addEventFilter(KeyEvent.KEY_RELEASED, multiplierSelectionEvent.validateInput());

        String regex = unitRegex();
        final UnaryOperator<TextFormatter.Change> keyPressFilter = c -> {
            String text = c.getControlNewText();
            if (text.matches(regex)) {
                return c;
            } else {
                return null;
            }
        };
        value.setTextFormatter(new TextFormatter<>(keyPressFilter));

        getChildren().add(value);
        MultiplierOption.setTopAnchor(value, 43d);
        MultiplierOption.setLeftAnchor(value, 0d);
        MultiplierOption.setBottomAnchor(value, 15d);
    }

    /**
     * Add tooltip to multiplier option
     */
    private void setTooltip() {
        Tooltip tooltip = new Tooltip(type.getTooltipString());
        selection.setTooltip(tooltip);
    }

    /**
     * Set MultiplierSelection event
     *
     * @param multiplierSelectionEvent
     */
    public void setMultiplierSelectionEvent(MultiplierSelectionEvent multiplierSelectionEvent) {
        this.multiplierSelectionEvent = multiplierSelectionEvent;
    }

    /**
     * Set value
     *
     * @param multiplierValue
     */
    public void setValue(double multiplierValue) {
        this.multiplierValue = multiplierValue;
        String formattedValue = getFormattedValue(multiplierValue);
        this.value.setText(formattedValue);
    }

    /**
     * Return formatted double value
     *
     * @param data
     * @return
     */
    private String getFormattedValue(double data) {
        try {
            int unit = 1000;
            if (data < unit) {
                return Util.formatDecimal(data);
            }
            int exp = (int) (Math.log(data) / Math.log(unit));
            String pre = ("KMG").charAt(exp - 1) + "";
            double formattedValue = data / Math.pow(unit, exp);
            return Util.formatDecimal(formattedValue) + pre;
        } catch (Exception ex) {
            LOG.error("Error formatting value", ex);
            return "0";
        }
    }

    /**
     * Return value
     *
     * @return
     */
    public double getValue() {
        String valueData = value.getText();
        if (Util.isNullOrEmpty(valueData)) {
            return 0;
        }
        String lastChar = String.valueOf(valueData.charAt(valueData.length() - 1));
        String data = valueData;
        if (!Util.isDecimal(valueData) && !Util.isDigit(valueData)) {
            data = valueData.substring(0, valueData.length() - 1);
            return Util.convertLargeUnitToValue(data, lastChar);
        }
        this.multiplierValue = Double.parseDouble(data);
        return multiplierValue;
    }

    /**
     * Return multiplier type
     *
     * @return
     */
    public MultiplierType getType() {
        return type;
    }

    /**
     * Set selected
     */
    public void setSelected() {
        selection.setSelected(true);
    }

    /**
     * Reset value
     */
    public void reset() {
        value.setText("1.0");
    }

    /**
     * Return component width
     *
     * @return
     */
    public double getComponentWidth() {
        return defaultWidth;
    }

    /**
     *
     * @return
     */
    private String unitRegex() {
        String partialBlock = "(([0-9]{0,10}))(\\.){0,1}[0-9]{0,1}[K|k|m|M|g|G]";
        String testField = partialBlock + "{0,1}";
        return "^" + testField;
    }
    
    /**
     * Return exact multiplier value
     * @return 
     */
    public double getMultiplierValue() {
        return multiplierValue;
    }
}

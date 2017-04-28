package com.exalttech.trex.ui.components;

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

import javafx.scene.control.Tooltip;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;


public class NumberField extends TextField {
    private final static Logger LOG = Logger.getLogger(NumberField.class.getName());
    private static final int UNIT_VALUE = 1000;
    private static final String UNITS = "KMGTPE";

    private static class NumberFormatter implements UnaryOperator<Change> {
        private final static Pattern PATTERN = Pattern.compile(
                String.format("^(((0|[1-9]\\d*)(\\.\\d*)?|\\.\\d*)[%s]?)?$", UNITS)
        );

        @Override
        public Change apply(final Change change) {
            change.setText(change.getText().toUpperCase());
            return PATTERN.matcher(change.getControlNewText()).matches() ? change : null;
        }
    }

    private static double convertTextToValue(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        final char first = text.charAt(0);
        if (first == '.') {
            text = "0" + text;
        }

        final int lastInd = text.length() - 1;
        final char last = text.charAt(lastInd);
        if (!Character.isAlphabetic(last)) {
            return Double.parseDouble(text);
        }

        final String valueText = text.substring(0, lastInd);
        final int factor = UNITS.indexOf(last) + 1;
        final BigDecimal decValue = BigDecimal.valueOf(Double.parseDouble(valueText));
        final BigDecimal decFactor = BigDecimal.valueOf(Math.pow(UNIT_VALUE, factor));
        return decValue.multiply(decFactor).doubleValue();
    }

    private static String convertValueToText(final double value, final boolean rounded) {
        if (value < UNIT_VALUE) {
            return String.valueOf(value);
        }

        final int exp = (int) (Math.log(value) / Math.log(UNIT_VALUE));
        final char unit = UNITS.charAt(Math.min(exp, UNITS.length()) - 1);
        final double shortValue = value / Math.pow(UNIT_VALUE, exp);
        final String strValue = rounded ?
                String.format(Locale.US,"%.1f", shortValue) :
                String.valueOf(shortValue);
        return strValue + unit;
    }

    private DoubleProperty valueProperty = new SimpleDoubleProperty(0);
    private Double minValue = null;
    private Double maxValue = null;
    private boolean isSkipSetValue = false;
    private boolean isSkipSetText = false;

    public NumberField() {
        setTextFormatter(new TextFormatter<>(new NumberFormatter()));

        focusedProperty().addListener(this::handleFocusChanged);
        textProperty().addListener(this::handleTextChanged);
        valueProperty.addListener(this::handleValueChanged);
    }

    public DoubleProperty valueProperty() {
        return valueProperty;
    }

    public double getValue() {
        return valueProperty.get();
    }

    public void setValue(final double value) {
        valueProperty.set(value);
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(final Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(final Double maxValue) {
        this.maxValue = maxValue;
    }

    private void handleFocusChanged(final Observable observable, final boolean oldValue, final boolean newValue) {
        final String text = getText();
        if (newValue) {
            setTextInner(convertValueToText(valueProperty.get(), false));
        } else if (text == null || text.equals("")) {
            setTextInner(String.valueOf(valueProperty.get()));
        } else {
            setTextInner(convertValueToText(valueProperty.get(), true));
        }
    }

    private void handleTextChanged(final Observable observable, final String oldValue, final String newValue) {
        if (newValue == null) {
            setValueInner(minValue != null ? minValue : 0);
        } else if (!isSkipSetValue) {
            try {
                setValueInner(convertTextToValue(newValue));
            } catch (Exception exc) {
                setText("");
                LOG.error("Invalid format of value", exc);
            }
        }
    }

    private void handleValueChanged(final Observable observable, final Number oldValue, final Number newValue) {
        final double value = newValue.doubleValue();

        if (minValue == null || maxValue == null || minValue < maxValue) {
            if (minValue != null && value < minValue) {
                setValue(minValue);
                return;
            }
            if (maxValue != null && value > maxValue) {
                setValue(maxValue);
                return;
            }
        } else {
            LOG.error("Min value should to be less then max value");
        }

        if (!isSkipSetText) {
            setTextInner(convertValueToText(value, isFocused()));
        }

        setTooltip(new Tooltip(String.valueOf(valueProperty.get())));
    }

    private void setTextInner(final String text) {
        isSkipSetValue = true;
        setText(text);
        isSkipSetValue = false;
    }

    private void setValueInner(final double value) {
        isSkipSetText = true;
        valueProperty.set(value);
        isSkipSetText = false;
    }
}

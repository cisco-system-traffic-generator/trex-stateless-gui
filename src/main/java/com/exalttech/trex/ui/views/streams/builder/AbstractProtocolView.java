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
package com.exalttech.trex.ui.views.streams.builder;

import com.exalttech.trex.ui.views.streams.binders.AbstractStreamDataBinding;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

/**
 * Abstract class for stream builder tabs
 *
 * @author GeorgeKh
 */
public abstract class AbstractProtocolView extends TitledPane {

    AnchorPane container;
    AbstractStreamDataBinding dataBinding;

    /**
     *
     * @param title
     * @param prefHeight
     * @param dataBinding
     */
    public AbstractProtocolView(String title, double prefHeight, AbstractStreamDataBinding dataBinding) {
        setText(title);
        setPrefHeight(prefHeight);
        this.dataBinding = dataBinding;
        buildUI();
    }

    private void buildUI() {
        container = new AnchorPane();
        buildCustomProtocolView();
        setContent(container);
        if (dataBinding != null) {
            bindProperties();
        }
        addInputValidation();
    }

    /**
     *
     * @param title
     * @param top
     * @param left
     */
    protected void addLabel(String title, double top, double left) {
        Label label = new Label(title);
        container.getChildren().add(label);
        container.setTopAnchor(label, top);
        container.setLeftAnchor(label, left);
    }

    /**
     * 
     * @param label
     * @param top
     * @param left 
     */
    protected void addLabel(Label label, double top, double left) {
        container.getChildren().add(label);
        container.setTopAnchor(label, top);
        container.setLeftAnchor(label, left);
    }
    /**
     *
     * @param input
     * @param top
     * @param left
     * @param width
     */
    protected void addInput(TextField input, double top, double left, double width) {
        input.setPrefSize(width, 22);
        container.getChildren().add(input);
        container.setTopAnchor(input, top);
        container.setLeftAnchor(input, left);
    }

    /**
     *
     * @param input
     * @param top
     * @param left
     * @param width
     */
    protected void addCombo(ComboBox input, double top, double left, double width) {
        input.setPrefSize(width, 22);
        container.getChildren().add(input);
        container.setTopAnchor(input, top);
        container.setLeftAnchor(input, left);
    }

    /**
     *
     * @param input
     * @param top
     * @param left
     */
    protected void addCombo(ComboBox input, double top, double left) {
        addCombo(input, top, left, 80);
    }

    /**
     *
     * @param input
     * @param top
     * @param left
     * @param width
     */
    protected void addButton(Button input, double top, double left, double width) {
        input.setPrefSize(width, 22);
        container.getChildren().add(input);
        container.setTopAnchor(input, top);
        container.setLeftAnchor(input, left);
    }
    /**
     *
     * @param input
     * @param top
     * @param left
     */
    protected void addCheckBox(CheckBox input, double top, double left) {
        container.getChildren().add(input);
        container.setTopAnchor(input, top);
        container.setLeftAnchor(input, left);
    }

    /**
     *
     * @param top
     * @param left
     * @param width
     */
    protected void addSeparator(double top, double left, double width) {
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefWidth(width);
        separator.setPrefHeight(1);
        container.getChildren().add(separator);
        container.setTopAnchor(separator, top);
        container.setLeftAnchor(separator, left);
    }

    /**
     *
     */
    protected abstract void buildCustomProtocolView();

    /**
     * Reset view input fields values
     */
    protected void reset() {
        if (dataBinding != null) {
            dataBinding.resetModel();
        }
    }

    /**
     *
     * @return
     */
    protected String getHexRegex() {
        String partialBlock = "[0-9A-Fa-f]{0,4}";
        return "^" + partialBlock;
    }

    /**
     * Bind fields with related properties
     */
    protected abstract void bindProperties();

    /**
     * Add input validation
     */
    protected abstract void addInputValidation();

}

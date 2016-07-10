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

import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Checkbox table view cell implementation
 *
 * @author Georgekh
 */
public class CheckBoxTableViewCell implements Callback<TableColumn, TableCell> {

    CheckBoxTableChangeHandler checkboxChangeHandler;

    /**
     * Constructor
     */
    public CheckBoxTableViewCell() {
        // empty constructor
    }

    /**
     * Constructor
     *
     * @param checkboxChangeHandler
     */
    public CheckBoxTableViewCell(CheckBoxTableChangeHandler checkboxChangeHandler) {
        this.checkboxChangeHandler = checkboxChangeHandler;
    }

    /**
     *
     * @param p
     * @return
     */
    @Override
    public TableCell call(TableColumn p) {
        return new TableCell<Object, Boolean>() {
            private final CheckBox checkBox;

            {
                checkBox = new CheckBox();
                checkBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (checkboxChangeHandler != null) {
                        checkboxChangeHandler.stateChanged(getTableRow().getIndex(), newValue);
                    }
                });
                this.setGraphic(checkBox);
                this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                this.setEditable(true);
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                } else {
                    this.setGraphic(null);
                }
            }
        };
    }

    /**
     *
     */
    public interface CheckBoxTableChangeHandler {

        /**
         * Checkbox value changed handler
         *
         * @param index
         * @param newValue
         */
        public void stateChanged(int index, boolean newValue);
    }
}

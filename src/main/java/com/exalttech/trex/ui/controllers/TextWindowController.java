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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * Text window FXML controller
 *
 * @author Georgekh
 */
public class TextWindowController implements Initializable {

    @FXML
    TextArea content;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Add required initialize code
    }

    /**
     * Set content
     *
     * @param contentString
     */
    public void setContent(String contentString) {
        content.setText(contentString);
    }

    /**
     * Return true if the window is shown, otherwise return false
     *
     * @return
     */
    public boolean isShown() {
        return content.getScene().getWindow().isShowing();
    }

    /**
     * Focus the window
     */
    public void focus() {
        content.getScene().getWindow().requestFocus();
    }
}

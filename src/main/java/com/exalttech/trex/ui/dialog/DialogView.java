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
package com.exalttech.trex.ui.dialog;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Dialog view
 *
 * @author Georgekh
 */
public abstract class DialogView implements DialogKeyPressHandler {

    private Stage currentStage;

    /**
     * Handle escape key pressed
     */
    @Override
    public void onEscapKeyPressed() {
        currentStage.fireEvent(new WindowEvent(currentStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Setup stage
     *
     * @param stage
     */
    @Override
    public void setupStage(Stage stage) {
        currentStage = stage;
        currentStage.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                onEscapKeyPressed();
            } else if (event.getCode() == KeyCode.ENTER) {
                onEnterKeyPressed(currentStage);
            }
        });
    }


    public void shutdown() {}
}

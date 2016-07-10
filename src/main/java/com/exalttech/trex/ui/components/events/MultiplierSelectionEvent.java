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
package com.exalttech.trex.ui.components.events;

import com.exalttech.trex.ui.MultiplierType;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * Interface that present multiplier selection event
 *
 * @author Georgekh
 */
public interface MultiplierSelectionEvent {

    /**
     * On multiplier selection event handler
     *
     * @param type
     */
    public void onMultiplierSelect(MultiplierType type);

    /**
     * @return
     */
    public EventHandler<KeyEvent> validateInput();

}

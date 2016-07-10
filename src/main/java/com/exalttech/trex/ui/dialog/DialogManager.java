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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Dialog window manager
 *
 * @author Georgekh
 */
public class DialogManager {

    private static DialogManager instance = null;

    /**
     * Get instance of dialog manager
     *
     * @return
     */
    public static DialogManager getInstance() {
        if (instance == null) {
            instance = new DialogManager();
        }

        return instance;
    }
    int numberOfOpenedDialog = 0;
    List<DialogCloseHandler> dialogCloseHandlerList;

    /**
     *
     */
    protected DialogManager() {
        dialogCloseHandlerList = new ArrayList<>();
    }

    /**
     * Add close handler
     *
     * @param handler
     */
    public void addHandler(DialogCloseHandler handler) {
        numberOfOpenedDialog++;
        dialogCloseHandlerList.add(handler);
    }

    /**
     * Remove close handler
     *
     * @param handler
     */
    public void removeHandler(DialogCloseHandler handler) {
        numberOfOpenedDialog--;
        dialogCloseHandlerList.remove(handler);
    }

    /**
     * Close all opened dialog
     */
    public void closeAll() {
        dialogCloseHandlerList.stream().forEach(new Consumer<DialogCloseHandler>() {
            @Override
            public void accept(DialogCloseHandler closeHandler) {
                closeHandler.closeDialog();
            }
        });
        dialogCloseHandlerList.clear();
        numberOfOpenedDialog = 0;
    }

    /**
     * Return number of opened dialog
     *
     * @return
     */
    public int getNumberOfOpenedDialog() {
        return numberOfOpenedDialog;
    }

}

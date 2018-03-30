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

import javafx.util.Pair;
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

    private List<Pair<DialogWindow, DialogCloseHandler>> dialogCloseHandlerList;

    /**
     *
     */
    protected DialogManager() {
        dialogCloseHandlerList = new ArrayList<>();
    }

    /**
     * Add close handler
     *
     * @param window dialog which closing will be handled
     * @param handler will be called
     */
    public void addHandler(DialogWindow window, DialogCloseHandler handler) {
        if(dialogCloseHandlerList.stream().noneMatch(pair -> window == pair.getKey())) {
            dialogCloseHandlerList.add(new Pair<>(window, handler));
        }
    }

    /**
     * Remove close handler
     *
     * @param window
     */
    public void removeHandler(DialogWindow window) {
        dialogCloseHandlerList.removeIf( pair -> window == pair.getKey());
    }

    /**
     * Close all opened dialog
     */
    public void closeAll() {
        List<Pair<DialogWindow, DialogCloseHandler>> toBeClosed = new ArrayList<>(dialogCloseHandlerList); //possible modifing list while iterating
        toBeClosed.forEach(pair -> pair.getValue().closeDialog());
        dialogCloseHandlerList.clear();
    }

    /**
     * Return number of opened dialog
     *
     * @return
     */
    public int getNumberOfOpenedDialog() {
        return dialogCloseHandlerList.size();
    }

}

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
package com.exalttech.trex.ui.views.logs;

/**
 *
 * @author GeorgeKh
 */
public class LogsController {

    private static LogsController instance = null;

    /**
     *
     * @return
     */
    public static synchronized LogsController getInstance() {
        if (instance == null) {
            instance = new LogsController();
        }
        return instance;
    }
    private LogsView view;
    private ConsoleLogView consoleLogView;

    /**
     * Protected constructor
     */
    protected LogsController() {
        view = new LogsView();
        consoleLogView = new ConsoleLogView();
    }

    /**
     *
     * @param type
     * @param textToAppend
     */
    public void appendText(LogType type, String textToAppend) {

        view.append(type, textToAppend.trim());

    }

    /**
     *
     * @param textToAppend
     */
    public void appendConsoleViewText(String textToAppend) {
        consoleLogView.append(textToAppend.trim());
    }

    /**
     *
     * @return
     */
    public LogsView getView() {
        return view;
    }

    /**
     *
     * @return
     */
    public ConsoleLogView getConsoleLogView() {
        return consoleLogView;
    }

}

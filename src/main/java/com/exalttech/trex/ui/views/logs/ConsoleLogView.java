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

import com.exalttech.trex.util.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.layout.AnchorPane.setBottomAnchor;
import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setRightAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

/**
 *
 * @author Georgekh
 */
public class ConsoleLogView extends AnchorPane {

    TextArea logsContent;
    StringBuffer messageBuffer;

    /**
     *
     */
    public ConsoleLogView() {
        messageBuffer = new StringBuffer("");
        setTopAnchor(this, 0d);
        setLeftAnchor(this, 0d);
        setBottomAnchor(this, 0d);
        setRightAnchor(this, 0d);
        buildUI();
    }

    private void buildUI() {
        logsContent = new TextArea();
        logsContent.setWrapText(true);
        logsContent.getStyleClass().add("consoleLogsContainer");
        setTopAnchor(logsContent, 0d);
        setLeftAnchor(logsContent, 0d);
        setBottomAnchor(logsContent, 0d);
        setRightAnchor(logsContent, 0d);

        getChildren().add(logsContent);
    }

    /**
     *
     * @param textToAppend
     */
    public void append(String textToAppend) {
        if (textToAppend != null) {
            messageBuffer.setLength(0);
            messageBuffer.append(LogType.INFO.getDisplayedText()).append(" ").append(Util.formatDate(new Date())).append(" ").append(textToAppend).append("\n");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    logsContent.appendText(messageBuffer.toString());
                }
            });

        }
    }

    /**
     * Copy console log to clipboard
     */
    public void copyToClipboard() {
        // select all text
        logsContent.selectAll();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(logsContent.getText());
        clipboard.setContent(content);
    }
}

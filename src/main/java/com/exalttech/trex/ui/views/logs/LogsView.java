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
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Date;

/**
 *
 * @author GeorgeKh
 */
public class LogsView extends AnchorPane {

    VBox logsContent;
    ScrollPane contentWrapper;

    StringBuilder sb = new StringBuilder();

    /**
     *
     */
    public LogsView() {
        setTopAnchor(this, 0d);
        setLeftAnchor(this, 0d);
        setBottomAnchor(this, 0d);
        setRightAnchor(this, 0d);
        buildUI();
    }

    private void buildUI() {
        contentWrapper = new ScrollPane();
        contentWrapper.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        getChildren().add(contentWrapper);

        setTopAnchor(contentWrapper, 0d);
        setLeftAnchor(contentWrapper, 0d);
        setBottomAnchor(contentWrapper, 0d);
        setRightAnchor(contentWrapper, 0d);

        logsContent = new VBox();
        logsContent.setId("logs_view");
        logsContent.heightProperty().addListener((observable, oldValue, newValue) -> contentWrapper.setVvalue((Double) newValue));

        logsContent.setSpacing(5);

        contentWrapper.setContent(logsContent);
    }

    /**
     *
     * @param type
     * @param textToAppend
     */
    public void append(LogType type, String textToAppend) {
        Date date = new Date();

        HBox msgContainer = new HBox();
        msgContainer.getStyleClass().add("logMsgContainer");
        msgContainer.setSpacing(40);

        String typeMsg = type.getDisplayedText() + " " + Util.formatDate(date);
        msgContainer.getChildren().add(getMsgLabel(typeMsg, type.getStyle()));
        msgContainer.getChildren().add(getMsgLabel(textToAppend, type.getStyle()));

        sb.append(typeMsg).append("   ").append(textToAppend).append('\n');
        Platform.runLater(() -> {
            logsContent.getChildren().add(msgContainer);
            contentWrapper.setVvalue(1.0);
        });
    }

    private Label getMsgLabel(String msgText, String className) {
        Label msgLabel = new Label(msgText);
        msgLabel.getStyleClass().add(className);

        return msgLabel;
    }

    /**
     * Copy logs to clipboard
     */
    public void copyToClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(sb.toString());
        clipboard.setContent(content);
    }

    /**
     * Clear log view
     */
    public void clear() {
        sb.setLength(0);
        logsContent.getChildren().clear();
        Util.optimizeMemory();
    }
}

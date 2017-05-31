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
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Georgekh
 */
public class ConsoleLogView extends AnchorPane {

    private TextArea logsContent;
    private MessageQueue queue = new MessageQueue(5);
    private Consumer<Void> logUpdater = (e) -> Platform.runLater(() -> {
        String text = queue.stream().collect(Collectors.joining("\n"));
        logsContent.clear();
        logsContent.appendText(text);
    });

    /**
     *
     */
    public ConsoleLogView() {
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
            String msg = String.format("%s %s %s", LogType.INFO.getDisplayedText(), Util.formatDate(new Date()), textToAppend);
            queue.add(msg);
            logUpdater.accept(null);
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
    
    /**
     * Clear log view
     */
    public void clear(){
        logsContent.setText("");
        logsContent.clear();
        Util.optimizeMemory();
    }

    private class MessageQueue {
        private int size;

        private LinkedList<String> queue;
        
        public MessageQueue(int size) {
            this.size = size;
            this.queue = new LinkedList<>();
        }
        
        public void add(String element) {
            if (queue.size() == size) {
                queue.removeFirst();
            }
            queue.addLast(element);
        }
        
        public Stream<String> stream() {
            return queue.stream();
        }
    }
}

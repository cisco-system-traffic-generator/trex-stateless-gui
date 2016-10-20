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
package com.exalttech.trex.ui.views.streams.builder;

import com.exalttech.trex.ui.views.streams.binders.PayloadDataBinding;
import com.exalttech.trex.util.PreferencesManager;
import com.exalttech.trex.util.Util;
import com.exalttech.trex.util.files.FileManager;
import com.exalttech.trex.util.files.FileType;
import java.io.File;
import java.util.function.UnaryOperator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Window;

/**
 *
 * @author GeorgeKh
 */
public class PayloadView extends AbstractProtocolView {

    ComboBox<String> type;
    TextField pattern;
    Button importPatternButton;
    Label orLabel;

    /**
     * Constructor
     *
     * @param dataBinding
     */
    public PayloadView(PayloadDataBinding dataBinding) {
        super("Payload Data", 100, dataBinding);
        bindComponent();
    }

    /**
     * Bind fields together
     */
    private void bindComponent() {
        pattern.disableProperty().bind(type.valueProperty().isEqualTo("Fixed Word").not());
        importPatternButton.disableProperty().bind(pattern.disableProperty());
        orLabel.disableProperty().bind(pattern.disableProperty());

        pattern.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                pattern.setText(newValue.replaceAll(" ", ""));
            }
        });

        importPatternButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectPatternFile();
            }

        });
    }

    /**
     * Build custom view
     */
    @Override
    protected void buildCustomProtocolView() {
        addLabel("Type", 15, 15);
        type = new ComboBox<>();
        type.setId("payloadType");
        addCombo(type, 12, 120, 170);

        addLabel("Pattern", 55, 15);
        pattern = new TextField();
        addInput(pattern, 52, 120, 300);

        orLabel = new Label("OR");
        addLabel(orLabel, 55, 430);
        importPatternButton = new Button("Select from file");
        importPatternButton.getStyleClass().add("normalButton");
        addButton(importPatternButton, 52, 460, 130);

        type.getItems().clear();
        type.getItems().addAll("Fixed Word", "Increment Byte", "Decrement Byte", "Random");
    }

    /**
     * Return payload pattern value
     *
     * @return
     */
    private String getPayloadPattern() {
        if (!Util.isNullOrEmpty(pattern.getText())) {
            return pattern.getText();
        }
        return "00";
    }

    /**
     * Return payload object
     *
     * @return
     */
    public Payload getPayload() {
        Payload payload = new Payload();
        payload.setPayloadPattern(getPayloadPattern());
        payload.setPayloadType(PayloadType.getPayloadType(type.getValue()));
        return payload;
    }

    /**
     * Add input validation for pattern
     */
    @Override
    protected void addInputValidation() {
        final UnaryOperator<TextFormatter.Change> filter = Util.getTextChangeFormatter(validatePayloadPattern());
        pattern.setTextFormatter(new TextFormatter<>(filter));
    }

    /**
     * Validate payload pattern
     *
     * @return
     */
    public static String validatePayloadPattern() {
        String partialBlock = "(([0-9a-fA-F ])*)";
        return "^" + partialBlock;
    }

    /**
     * Bind properties with related fields
     */
    @Override
    protected void bindProperties() {
        PayloadDataBinding payloadDB = (PayloadDataBinding) dataBinding;
        type.valueProperty().bindBidirectional(payloadDB.getType());
        pattern.textProperty().bindBidirectional(payloadDB.getPattern());
    }

    private void selectPatternFile() {
        try {
            String loadFolderPath = PreferencesManager.getInstance().getLoadLocation();
            Window owner = importPatternButton.getScene().getWindow();
            File loadedFile = FileManager.getSelectedFile("Open Pcap File", "", owner, FileType.TXT, loadFolderPath, false);
            if (loadedFile != null) {
                // read the file and save the string in the pattern
                String data = FileManager.getFileContent(loadedFile).replaceAll(" ", "");
                if (Util.isHex(data)) {
                    pattern.setText(data);
                }else{
                    Alert errorMsg = Util.getAlert(Alert.AlertType.ERROR);
                    errorMsg.setContentText("Invalid payload pattern");
                    errorMsg.showAndWait();
                }
            }
        } catch (Exception ex) {

        }
    }
}

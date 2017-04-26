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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import com.exalttech.trex.ui.views.streams.binders.ProtocolSelectionDataBinding;
import com.exalttech.trex.ui.views.streams.builder.PacketLengthType;
import com.exalttech.trex.util.Util;


public class ProtocolSelectionController implements Initializable {
    private static int NUM_OF_ALLOWED_DIGITS = 4;

    @FXML
    private RadioButton ipv4RB;
    @FXML
    private RadioButton tcpRB;
    @FXML
    private RadioButton udpRB;
    @FXML
    private RadioButton l3NoneRB;
    @FXML
    private RadioButton l4NoneRB;
    @FXML
    private RadioButton payloadRB;
    @FXML
    private RadioButton taggedVlanRB;
    @FXML
    private ComboBox lengthCB;
    @FXML
    private TextField lengthTF;
    @FXML
    private TextField minTF;
    @FXML
    private TextField maxTF;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initialzeComponents();

    }

    public void bindSelections(ProtocolSelectionDataBinding selection) {
        ipv4RB.selectedProperty().bindBidirectional(selection.getIpv4Property());
        payloadRB.selectedProperty().bindBidirectional(selection.getPatternProperty());
        tcpRB.selectedProperty().bindBidirectional(selection.getTcpProperty());
        udpRB.selectedProperty().bindBidirectional(selection.getUdpProperty());
        taggedVlanRB.selectedProperty().bindBidirectional(selection.getTaggedVlanProperty());
        lengthCB.valueProperty().bindBidirectional(selection.getFrameLengthTypeProperty());
        lengthTF.textProperty().bindBidirectional(selection.getFrameLengthProperty());
        minTF.textProperty().bindBidirectional(selection.getMinLengthProperty());
        maxTF.textProperty().bindBidirectional(selection.getMaxLengthProperty());

        l4NoneRB.disableProperty().bind(l3NoneRB.selectedProperty());
        tcpRB.disableProperty().bind(l3NoneRB.selectedProperty());
        udpRB.disableProperty().bind(l3NoneRB.selectedProperty());
        l3NoneRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                l4NoneRB.setSelected(true);
            }
        });
    }

    private void initialzeComponents() {
        lengthCB.getItems().addAll(PacketLengthType.FIXED.getTitle(), PacketLengthType.INCREMENT.getTitle(), PacketLengthType.DECREMENT.getTitle(), PacketLengthType.RANDOM.getTitle());
        lengthCB.getSelectionModel().select(0);

        minTF.disableProperty().bind(lengthCB.valueProperty().isEqualTo(PacketLengthType.FIXED.getTitle()));
        maxTF.disableProperty().bind(lengthCB.valueProperty().isEqualTo(PacketLengthType.FIXED.getTitle()));
        lengthTF.disableProperty().bind(lengthCB.valueProperty().isEqualTo(PacketLengthType.FIXED.getTitle()).not().or(lengthCB.disableProperty()));

        // add valication
        lengthTF.setTextFormatter(Util.getNumberFilter(NUM_OF_ALLOWED_DIGITS));
        minTF.setTextFormatter(Util.getNumberFilter(NUM_OF_ALLOWED_DIGITS));
        maxTF.setTextFormatter(Util.getNumberFilter(NUM_OF_ALLOWED_DIGITS));
    }
}

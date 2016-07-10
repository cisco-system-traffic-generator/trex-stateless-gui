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

import com.exalttech.trex.ui.views.streams.ProtocolSelection;
import com.exalttech.trex.ui.views.streams.buildstream.PacketLengthType;
import com.exalttech.trex.util.Util;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * Protocol selection FXML controller
 *
 * @author Georgekh
 */
public class ProtocolSelectionController implements Initializable {

    @FXML
    RadioButton ipv4RB;
    @FXML
    RadioButton tcpRB;
    @FXML
    RadioButton udpRB;
    @FXML
    RadioButton l3NoneRB;
    @FXML
    RadioButton l4NoneRB;
    @FXML
    RadioButton payloadRB;
    @FXML
    RadioButton nonePattern;
    @FXML
    RadioButton taggedVlanRB;
    @FXML
    RadioButton stackedVlanRB;
    @FXML
    ComboBox lengthCB;
    @FXML
    TextField lengthTF;
    @FXML
    TextField minTF;
    @FXML
    TextField maxTF;

    ProtocolSelection selection;

    int numOfAllowedDigit = 4;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initialzeComponents();

    }

    /**
     * Bind protocol selection with related radio group selection
     *
     * @param selection
     */
    public void bindSelections(ProtocolSelection selection) {
        this.selection = selection;

        ipv4RB.selectedProperty().bindBidirectional(this.selection.getIpv4Property());
        payloadRB.selectedProperty().bindBidirectional(this.selection.getPatternProperty());
        tcpRB.selectedProperty().bindBidirectional(this.selection.getTcpProperty());
        udpRB.selectedProperty().bindBidirectional(this.selection.getUdpProperty());
        taggedVlanRB.selectedProperty().bindBidirectional(this.selection.getTaggedVlanProperty());
        stackedVlanRB.selectedProperty().bindBidirectional(this.selection.getStackedVlanProperty());
        lengthCB.valueProperty().bindBidirectional(this.selection.getFrameLengthTypeProperty());
        lengthTF.textProperty().bindBidirectional(this.selection.getFrameLengthProperty());
        minTF.textProperty().bindBidirectional(this.selection.getMinLengthProperty());
        maxTF.textProperty().bindBidirectional(this.selection.getMaxLengthProperty());

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

    /**
     * Initialize components
     */
    private void initialzeComponents() {
        lengthCB.getItems().addAll(PacketLengthType.FIXED.getTitle(), PacketLengthType.INCREMENT.getTitle(), PacketLengthType.DECREMENT.getTitle(), PacketLengthType.RANDOM.getTitle());
        lengthCB.getSelectionModel().select(0);

        minTF.disableProperty().bind(lengthCB.valueProperty().isEqualTo(PacketLengthType.FIXED.getTitle()));
        maxTF.disableProperty().bind(lengthCB.valueProperty().isEqualTo(PacketLengthType.FIXED.getTitle()));
        lengthTF.disableProperty().bind(lengthCB.valueProperty().isEqualTo(PacketLengthType.FIXED.getTitle()).not().or(lengthCB.disableProperty()));

        // add valication
        lengthTF.setTextFormatter(Util.getNumberFilter(numOfAllowedDigit));
        minTF.setTextFormatter(Util.getNumberFilter(numOfAllowedDigit));
        maxTF.setTextFormatter(Util.getNumberFilter(numOfAllowedDigit));

    }

}

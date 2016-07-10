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
package com.exalttech.trex.ui.views.streams.buildstream;

import com.exalttech.trex.ui.views.models.AddressProtocolData;
import com.exalttech.trex.ui.views.streams.MacAddressDataBinding;
import com.exalttech.trex.util.Util;
import java.util.function.UnaryOperator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 *
 * @author GeorgeKh
 */
public class MacProtocolView extends AbstractProtocolView {

    private final static String MAC_ADDRESS_REG = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

    TextField dstAddress;
    TextField srcAddress;
    ComboBox<String> dstMode;
    ComboBox<String> srcMode;
    TextField dstCount;
    TextField dstStep;
    TextField srcCount;
    TextField srcStep;

    AddressProtocolData srcAddressData;
    AddressProtocolData dstAddressData;

    BooleanProperty srcModeCountDisabledProp = new SimpleBooleanProperty();
    BooleanProperty dstModeCountDisabledProp = new SimpleBooleanProperty();

    /**
     * Constructor
     *
     * @param dataBinding
     */
    public MacProtocolView(MacAddressDataBinding dataBinding) {
        super("Media Access Protocol", 180, dataBinding);
        srcAddressData = new AddressProtocolData();
        dstAddressData = new AddressProtocolData();
        bindComponent();
    }

    /**
     * Bind fields together
     */
    private void bindComponent() {
        srcModeCountDisabledProp.bind(srcMode.valueProperty().isEqualTo(MacType.FIXED.getTitle()).or(srcMode.valueProperty().isEqualTo(MacType.TREX_CONFIG.getTitle())));
        srcCount.disableProperty().bind(srcModeCountDisabledProp);
        srcStep.disableProperty().bind(srcModeCountDisabledProp);

        dstModeCountDisabledProp.bind(dstMode.valueProperty().isEqualTo(MacType.FIXED.getTitle()).or(dstMode.valueProperty().isEqualTo(MacType.TREX_CONFIG.getTitle())));
        dstCount.disableProperty().bind(dstModeCountDisabledProp);
        dstStep.disableProperty().bind(dstModeCountDisabledProp);

        srcAddressData.getAddressProperty().bind(srcAddress.textProperty());
        srcAddressData.getTypeProperty().bind(srcMode.valueProperty());
        srcAddressData.getCountProperty().bind(srcCount.textProperty());
        srcAddressData.getStepProperty().bind(srcStep.textProperty());

        dstAddressData.getAddressProperty().bind(dstAddress.textProperty());
        dstAddressData.getTypeProperty().bind(dstMode.valueProperty());
        dstAddressData.getCountProperty().bind(dstCount.textProperty());
        dstAddressData.getStepProperty().bind(dstStep.textProperty());
    }

    /**
     *
     */
    @Override
    protected void buildCustomProtocolView() {
        addLabel("Destination", 35, 10);
        addLabel("Source", 75, 10);

        addLabel("Address", 5, 100);
        dstAddress = new TextField();
        addInput(dstAddress, 30, 100, 220);
        srcAddress = new TextField();
        addInput(srcAddress, 72, 100, 220);

        addLabel("Mode", 5, 330);
        dstMode = new ComboBox<>();
        addCombo(dstMode, 30, 330, 150);
        srcMode = new ComboBox<>();
        addCombo(srcMode, 70, 330, 150);

        addLabel("Count", 5, 490);
        dstCount = new TextField();
        addInput(dstCount, 30, 490, 80);
        srcCount = new TextField();
        addInput(srcCount, 70, 490, 80);

        addLabel("Step", 5, 580);
        dstStep = new TextField();
        addInput(dstStep, 30, 580, 80);
        srcStep = new TextField();
        addInput(srcStep, 70, 580, 80);

        srcMode.getItems().clear();
        dstMode.getItems().clear();
        for (MacType type : MacType.values()) {
            srcMode.getItems().add(type.getTitle());
            dstMode.getItems().add(type.getTitle());
        }
    }

    /**
     * Return source address data
     *
     * @return
     */
    public AddressProtocolData getSourceAddress() {
        srcAddress.setText(getValidAddress(srcAddress.getText(), false));
        return srcAddressData;
    }

    /**
     * Return destination address data
     *
     * @return
     */
    public AddressProtocolData getDestinationAddress() {
        dstAddress.setText(getValidAddress(dstAddress.getText(), true));
        return dstAddressData;
    }

    /**
     * Return valid Mac address
     *
     * @param data
     * @return
     */
    private String getValidAddress(String data, boolean isDst) {
        if (Util.isNullOrEmpty(data) || !data.matches(MAC_ADDRESS_REG)) {
            return isDst ? StreamBuilderConstants.DEFAULT_DST_MAC_ADDRESS : StreamBuilderConstants.DEFAULT_SRC_MAC_ADDRESS;
        }
        return data;
    }

    /**
     * Add Mac input field validations
     */
    @Override
    protected void addInputValidation() {
        final UnaryOperator<TextFormatter.Change> ipAddressFilter = Util.getTextChangeFormatter(validateAddressRegex());
        srcAddress.setTextFormatter(new TextFormatter<>(ipAddressFilter));
        dstAddress.setTextFormatter(new TextFormatter<>(ipAddressFilter));

        // add format for step and count
        srcCount.setTextFormatter(Util.getNumberFilter(4));
        dstCount.setTextFormatter(Util.getNumberFilter(4));
        srcStep.setTextFormatter(Util.getNumberFilter(3));
        dstStep.setTextFormatter(Util.getNumberFilter(3));
    }

    /**
     * Validate Mac address
     *
     * @return
     */
    private String validateAddressRegex() {
        String partialBlock = "(([0-9a-fA-F]{0,2}))";
        String subsequentPartialBlock = "(\\:" + partialBlock + ")";
        String macAddress = partialBlock + "?" + subsequentPartialBlock + "{0,5}";
        return "^" + macAddress;

    }

    /**
     * Bind Mac fields
     */
    @Override
    protected void bindProperties() {
        MacAddressDataBinding macDB = (MacAddressDataBinding) dataBinding;
        // bind source fields
        srcAddress.textProperty().bindBidirectional(macDB.getSource().getAddressProperty());
        srcMode.valueProperty().bindBidirectional(macDB.getSource().getModeProperty());
        srcCount.textProperty().bindBidirectional(macDB.getSource().getCountProperty());
        srcStep.textProperty().bindBidirectional(macDB.getSource().getStepProperty());

        // bind destination fields
        dstAddress.textProperty().bindBidirectional(macDB.getDestination().getAddressProperty());
        dstMode.valueProperty().bindBidirectional(macDB.getDestination().getModeProperty());
        dstCount.textProperty().bindBidirectional(macDB.getDestination().getCountProperty());
        dstStep.textProperty().bindBidirectional(macDB.getDestination().getStepProperty());
    }

    /**
     * Enumerator present MAC type
     */
    private enum MacType {
        FIXED("Fixed"),
        INCREMENT("Increment"),
        DECREMENET("Decrement"),
        TREX_CONFIG("TRex Config");

        String title;

        /**
         * Constructor
         *
         * @param title
         */
        private MacType(String title) {
            this.title = title;
        }

        /**
         * Return displayed type title
         *
         * @return
         */
        public String getTitle() {
            return title;
        }

    }
}

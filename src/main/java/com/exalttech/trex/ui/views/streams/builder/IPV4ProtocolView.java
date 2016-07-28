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

import com.exalttech.trex.ui.views.models.AddressProtocolData;
import com.exalttech.trex.ui.views.streams.binders.IPV4AddressDataBinding;
import com.exalttech.trex.util.Util;
import java.util.function.UnaryOperator;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 *
 * @author Osama
 */
public class IPV4ProtocolView extends AbstractProtocolView {

    private final static String IP_ADDRESS_REG = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";

    TextField dstAddress;
    TextField srcAddress;
    ComboBox<String> dstMode;
    ComboBox<String> srcMode;
    TextField dstCount;
    TextField srcCount;

    AddressProtocolData srcAddressData;
    AddressProtocolData dstAddressData;

    /**
     * Constructor
     *
     * @param dataBinding
     */
    public IPV4ProtocolView(IPV4AddressDataBinding dataBinding) {
        super("Internet Protocol v4", 180, dataBinding);
        srcAddressData = new AddressProtocolData();
        dstAddressData = new AddressProtocolData();
        bindComponent();
    }

    /**
     * Bind fields together
     */
    private void bindComponent() {
        srcCount.disableProperty().bind(srcMode.valueProperty().isEqualTo("Fixed"));
        dstCount.disableProperty().bind(dstMode.valueProperty().isEqualTo("Fixed"));

        srcAddressData.getAddressProperty().bind(srcAddress.textProperty());
        srcAddressData.getTypeProperty().bind(srcMode.valueProperty());
        srcAddressData.getCountProperty().bind(srcCount.textProperty());

        dstAddressData.getAddressProperty().bind(dstAddress.textProperty());
        dstAddressData.getTypeProperty().bind(dstMode.valueProperty());
        dstAddressData.getCountProperty().bind(dstCount.textProperty());
    }

    /**
     * Build custom view
     */
    @Override
    protected void buildCustomProtocolView() {
        addLabel("Destination", 35, 10);
        addLabel("Source", 75, 10);

        addLabel("Address", 5, 100);
        dstAddress = new TextField();
        addInput(dstAddress, 30, 100, 170);
        srcAddress = new TextField();
        addInput(srcAddress, 72, 100, 170);

        addLabel("Mode", 5, 280);
        dstMode = new ComboBox<>();
        addCombo(dstMode, 30, 280, 170);
        srcMode = new ComboBox<>();
        addCombo(srcMode, 70, 280, 170);

        addLabel("Count", 5, 460);
        dstCount = new TextField();
        addInput(dstCount, 30, 460, 80);
        srcCount = new TextField();
        addInput(srcCount, 70, 460, 80);

        // define options
        srcMode.getItems().clear();
        dstMode.getItems().clear();

        for (IPV4Type type : IPV4Type.values()) {
            srcMode.getItems().add(type.getTitle());
            dstMode.getItems().add(type.getTitle());
        }
    }

    /**
     * Return source IP address data
     *
     * @return
     */
    public AddressProtocolData getSourceAddress() {
        srcAddress.setText(getValidAddress(srcAddress.getText(), false));
        return srcAddressData;
    }

    /**
     * Return destination IP address data
     *
     * @return
     */
    public AddressProtocolData getDestinationAddress() {
        dstAddress.setText(getValidAddress(dstAddress.getText(), true));
        return dstAddressData;
    }

    /**
     * Return valid IP address
     *
     * @param data
     * @return
     */
    private String getValidAddress(String data, boolean isDst) {

        if (Util.isNullOrEmpty(data) || !data.matches(IP_ADDRESS_REG)) {
            return isDst ? StreamBuilderConstants.DEFAULT_DST_IP_ADDRESS : StreamBuilderConstants.DEFAULT_SRC_IP_ADDRESS;
        }
        return data;
    }

    /**
     * Add input validation
     */
    @Override
    protected void addInputValidation() {
        final UnaryOperator<TextFormatter.Change> filter = Util.getTextChangeFormatter(validateAddressRegex());
        srcAddress.setTextFormatter(new TextFormatter<>(filter));
        dstAddress.setTextFormatter(new TextFormatter<>(filter));

        // add format for count
        final UnaryOperator<TextFormatter.Change> unitFormatter = Util.getTextChangeFormatter(Util.getUnitRegex(false));
        srcCount.setTextFormatter(new TextFormatter<>(unitFormatter));
        dstCount.setTextFormatter(new TextFormatter<>(unitFormatter));
        srcCount.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                srcCount.setText(getValidCount(srcCount.getText()));
            }
        });
        dstCount.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                dstCount.setText(getValidCount(dstCount.getText()));
            }
        });
    }

    /**
     * Validate count max value
     *
     * @param count
     * @return
     */
    private String getValidCount(String count) {
        double value = Util.convertUnitToNum(count);
        if (value > Util.convertUnitToNum("100M")) {
            return "100 M";
        }else if(value < 2){
            return "2";
        }
        return count;
    }

    /**
     * Validate address
     *
     * @return
     */
    private String validateAddressRegex() {
        String partialBlock = "(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))";
        String subsequentPartialBlock = "(\\." + partialBlock + ")";
        String ipAddress = partialBlock + "?" + subsequentPartialBlock + "{0,3}";
        return "^" + ipAddress;
    }

    /**
     * Bind input fields
     */
    @Override
    protected void bindProperties() {
        IPV4AddressDataBinding ipv4DB = (IPV4AddressDataBinding) dataBinding;
        // bind source fields
        srcAddress.textProperty().bindBidirectional(ipv4DB.getSource().getAddressProperty());
        srcMode.valueProperty().bindBidirectional(ipv4DB.getSource().getModeProperty());
        srcCount.textProperty().bindBidirectional(ipv4DB.getSource().getCountProperty());

        // bind destination fields
        dstAddress.textProperty().bindBidirectional(ipv4DB.getDestination().getAddressProperty());
        dstMode.valueProperty().bindBidirectional(ipv4DB.getDestination().getModeProperty());
        dstCount.textProperty().bindBidirectional(ipv4DB.getDestination().getCountProperty());
    }

    /**
     * Enumerator presents IPV4 type
     */
    private enum IPV4Type {
        FIXED("Fixed"),
        INCREMENT_HOST("Increment Host"),
        DECREMENT_HOST("Decrement Host"),
        RANDOM_HOST("Random Host");

        String title;

        /**
         * Constructor
         *
         * @param title
         */
        private IPV4Type(String title) {
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

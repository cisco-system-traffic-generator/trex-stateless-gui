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

import com.exalttech.trex.packets.TrexVlanPacket;
import com.exalttech.trex.ui.views.streams.binders.VlanDataBinding;
import com.exalttech.trex.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * VLAN protocol view implementation
 *
 * @author Georgekh
 */
public class VLanProtocolView extends AbstractProtocolView {

    private static final String TAGGED_TITLE = "VLAN";
    private static final String STACKED_TITLE = "Stacked VLAN";

    VlanView taggedVlan;
    VlanView stackedVlan;
    boolean isStacked = false;

    private final List<VlanDataBinding> vlanDataBindingList;

    /**
     * Constructor
     *
     * @param dataBinding
     */
    public VLanProtocolView(List<VlanDataBinding> dataBinding) {
        super(TAGGED_TITLE, 120, null);
        this.vlanDataBindingList = dataBinding;
        bindProperties();
    }

    /**
     * Set stacked VLAN view
     *
     * @param stacked
     */
    public void setStacked(boolean stacked) {
        stackedVlan.setVisible(stacked);
        isStacked = stacked;
        if (stacked) {
            setText(STACKED_TITLE);
        }
    }

    /**
     * Build UI
     */
    @Override
    protected void buildCustomProtocolView() {
        taggedVlan = new VlanView();
        container.getChildren().add(taggedVlan);
        container.setTopAnchor(taggedVlan, 0d);
        container.setLeftAnchor(taggedVlan, 0d);

        stackedVlan = new VlanView();
        container.getChildren().add(stackedVlan);
        container.setTopAnchor(stackedVlan, 60d);
        container.setLeftAnchor(stackedVlan, 0d);
    }

    /**
     * add input validation
     */
    @Override
    protected void addInputValidation() {
        taggedVlan.addValidation();
        stackedVlan.addValidation();
    }

    /**
     * Return list of VLAN data
     *
     * @return
     */
    public List<TrexVlanPacket> getVlanList() {
        List<TrexVlanPacket> vlanList = new ArrayList<>();
        vlanList.add(taggedVlan.getVlanData());
        if (isStacked) {
            vlanList.add(stackedVlan.getVlanData());
        }
        return vlanList;
    }

    /**
     * Bind properties with related properties
     */
    @Override
    protected void bindProperties() {
        taggedVlan.bindProperties(vlanDataBindingList.get(0));
        stackedVlan.bindProperties(vlanDataBindingList.get(1));
    }

    /**
     * Reset model
     */
    @Override
    protected void reset() {
        vlanDataBindingList.stream().forEach(new Consumer<VlanDataBinding>() {
            @Override
            public void accept(VlanDataBinding vlanDB) {
                vlanDB.resetModel();
            }
        });
    }

    /**
     * Class present VLAN view
     */
    private class VlanView extends AnchorPane {

        CheckBox tpidCB;
        ComboBox<String> priority;
        ComboBox<String> cfi;
        TextField tpid;
        TextField vlanID;

        public VlanView() {
            buildUI();
            bindWidget();
        }

        /**
         * Build UI
         */
        private void buildUI() {
            tpidCB = new CheckBox("Override TPID");
            setNode(tpidCB, 28, 10);
            tpid = new TextField();
            tpid.prefWidth(120);
            setNode(tpid, 25, 150);

            Label priorityLabel = new Label("Priority");
            setNode(priorityLabel, 5, 340);
            priority = new ComboBox<>();
            priority.getItems().addAll("0", "1", "2", "3", "4", "5", "6", "7");
            priority.setPrefWidth(60);
            setNode(priority, 25, 340);

            Label cfiDeiLabel = new Label("CFI/DEI");
            setNode(cfiDeiLabel, 5, 420);
            cfi = new ComboBox<>();
            cfi.getItems().addAll("0", "1");
            cfi.setPrefWidth(60);
            setNode(cfi, 25, 420);

            Label vlanLabel = new Label("VLAN");
            setNode(vlanLabel, 5, 500);
            vlanID = new TextField();
            setNode(vlanID, 25, 500);
        }

        /**
         * Set node according to left/top
         *
         * @param node
         * @param top
         * @param left
         */
        private void setNode(Node node, double top, double left) {
            getChildren().add(node);
            setTopAnchor(node, top);
            setLeftAnchor(node, left);
        }

        /**
         * Bind widget properties
         */
        private void bindWidget() {
            tpid.disableProperty().bind(tpidCB.selectedProperty().not());
        }

        /**
         * Return vlan data object
         *
         * @return
         */
        public TrexVlanPacket getVlanData() {
            TrexVlanPacket vlanData = new TrexVlanPacket();
            vlanData.setType(Util.getShortFromString(tpid.getText(), true));
            vlanData.setPriority(Byte.valueOf(priority.getValue()));
            vlanData.setCfi(Integer.valueOf(cfi.getValue()) > 0);
            vlanData.setVid(Util.getShortFromString(vlanID.getText(), false));
            vlanData.setOverrideType(tpidCB.isSelected());
            return vlanData;
        }

        /**
         * Add input validation
         */
        public void addValidation() {
            tpid.setTextFormatter(Util.getHexFilter(4));
            vlanID.setTextFormatter(Util.getNumberFilter(4));
            vlanID.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue && Integer.parseInt(vlanID.getText()) > 4094) {
                    vlanID.setText("4094");
                }
            });
        }

        /**
         * Bind fields with related properties
         *
         * @param dataBinding
         */
        public void bindProperties(VlanDataBinding dataBinding) {
            tpid.textProperty().bindBidirectional(dataBinding.getTpIdProperty());
            tpidCB.selectedProperty().bindBidirectional(dataBinding.getOverrideTPIdProperty());
            priority.valueProperty().bindBidirectional(dataBinding.getPriorityProperty());
            cfi.valueProperty().bindBidirectional(dataBinding.getCfiProperty());
            vlanID.textProperty().bindBidirectional(dataBinding.getVIdProperty());
        }
    }
}

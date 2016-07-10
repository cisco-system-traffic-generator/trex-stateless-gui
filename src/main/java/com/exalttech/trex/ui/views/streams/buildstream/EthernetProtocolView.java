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

import com.exalttech.trex.ui.views.streams.EthernetDataBinding;
import com.exalttech.trex.util.Util;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author GeorgeKh
 */
public class EthernetProtocolView extends AbstractProtocolView {

    CheckBox type;
    TextField typeField;

    /**
     * Constructor
     *
     * @param dataBinding
     */
    public EthernetProtocolView(EthernetDataBinding dataBinding) {
        super("Ethernet", 120, dataBinding);

    }

    /**
     * Build custom view
     */
    @Override
    protected void buildCustomProtocolView() {
        AnchorPane container = new AnchorPane();
        type = new CheckBox("Ethernet Type");
        addCheckBox(type, 20, 10);

        typeField = new TextField();
        addInput(typeField, 20, 165, 220);
        typeField.disableProperty().bind(type.selectedProperty().not());

        setContent(container);
    }

    /**
     * Return type
     *
     * @return
     */
    public short getType() {
        return Util.getShortFromString(typeField.getText(), true);
    }

    /**
     * Return whether type override is selected or not
     *
     * @return
     */
    public boolean isOverrideType() {
        return type.isSelected();
    }

    /**
     * Add input validation
     */
    @Override
    protected void addInputValidation() {
        typeField.setTextFormatter(new TextFormatter<>(Util.getTextChangeFormatter(getHexRegex())));
    }

    /**
     * Bind Ethernet fields
     */
    @Override
    protected void bindProperties() {
        EthernetDataBinding data = (EthernetDataBinding) dataBinding;
        typeField.textProperty().bindBidirectional(data.getTypeProperty());
        type.selectedProperty().bindBidirectional(data.getOverrideProperty());
    }

}

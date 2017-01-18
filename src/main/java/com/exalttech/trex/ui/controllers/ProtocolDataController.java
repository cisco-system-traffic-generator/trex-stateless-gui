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

import com.exalttech.trex.packets.TrexEthernetPacket;
import com.exalttech.trex.ui.views.streams.binders.BuilderDataBinding;
import com.exalttech.trex.ui.views.streams.builder.CacheSize;
import com.exalttech.trex.ui.views.streams.builder.ProtocolDataView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Protocol data FXML controller
 *
 * @author Georgekh
 */
public class ProtocolDataController implements Initializable {

    @FXML
    AnchorPane protocolDataContainer;

    ProtocolDataView dataView;

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeViews();
    }

    /**
     * Initialize view
     */
    private void initializeViews() {
        dataView = new ProtocolDataView();
        protocolDataContainer.getChildren().add(dataView);
    }

    /**
     * Bind protocol selection with related protocol data views
     *
     * @param selection
     */
    public void bindSelection(BuilderDataBinding selection) {
        // bind views
        dataView.doInitializingTabs(selection);
    }

    /**
     * Return protocol data
     *
     * @return @throws Exception
     */
    public TrexEthernetPacket getProtocolData() throws Exception {
        return dataView.getProtocolData();
    }

    /**
     * Return VM
     *
     * @param cacheSize
     * @return
     */
    public Map<String, Object> getVm(CacheSize cacheSize) {
        return dataView.getVm(cacheSize);
    }
}

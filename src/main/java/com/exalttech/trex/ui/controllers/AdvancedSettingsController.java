/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.ui.views.streams.binders.AdvancedPropertiesDataBinding;
import com.exalttech.trex.ui.views.streams.builder.CacheSize;
import com.exalttech.trex.ui.views.streams.builder.CacheSize.CacheSizeType;
import com.exalttech.trex.util.Util;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author GeorgeKH
 */
public class AdvancedSettingsController implements Initializable {

    @FXML
    TextField cacheSizeTF;

    @FXML
    ComboBox cacheSizeType;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeComponent();
    }

    /**
     * Bind properties with fields
     *
     * @param advancedPropertiesData
     */
    public void bindSelections(AdvancedPropertiesDataBinding advancedPropertiesData) {
        cacheSizeType.valueProperty().bindBidirectional(advancedPropertiesData.getCacheSizeType());
        cacheSizeTF.textProperty().bindBidirectional(advancedPropertiesData.getCacheValue());
    }

    /**
     * Initialize components
     */
    private void initializeComponent() {
        cacheSizeType.getItems().clear();
        for (CacheSizeType type : CacheSizeType.values()) {
            cacheSizeType.getItems().addAll(type.getTitle());
        }
        cacheSizeTF.disableProperty().bind(cacheSizeType.valueProperty().isNotEqualTo(CacheSizeType.ENABLE.getTitle()));
    }

    /**
     * Return cache size object
     * @return 
     */
    public CacheSize getCacheSize() {
        CacheSize cachSize = new CacheSize();
        cachSize.setCacheValue(Util.getIntFromString(cacheSizeTF.getText()));
        cachSize.setType(CacheSizeType.getCacheSizeType(cacheSizeType.getValue().toString()));
        return cachSize;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.streamtable;

import javafx.scene.control.Button;

/**
 * Stream table button implementation
 * @author Georgekh
 */
public class StreamTableButton extends Button {

    StreamTableAction buttonActionType;

    public StreamTableButton(StreamTableAction buttonActionType) {
        super(buttonActionType.getTitle(), buttonActionType.getIcon());
        this.buttonActionType = buttonActionType;
        getStyleClass().add("customBtn");
    }

    /**
     * Return button action type
     * @return 
     */
    public StreamTableAction getButtonActionType() {
        return buttonActionType;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.matchers;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import org.hamcrest.Matcher;
import org.testfx.matcher.base.GeneralMatchers;

/**
 * Radio button matcher implementation
 * @author GeorgeKH
 */
public class RadioButtonMatchers {
    
    /**
     * 
     * @param <T>
     * @return 
     */
    public static <T> Matcher<Node> isSelected() {
        String descriptionText = "Radio button is selected ";
        return GeneralMatchers.typeSafeMatcher(RadioButton.class, descriptionText, node -> isSelected(node));
    }
    
    /**
     * 
     * @param <T>
     * @param radioButton
     * @return 
     */
    private static <T> boolean isSelected(RadioButton radioButton) {
        return true == radioButton.isSelected();
    }
}

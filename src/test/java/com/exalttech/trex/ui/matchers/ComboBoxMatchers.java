/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.matchers;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import org.hamcrest.Matcher;
import org.testfx.matcher.base.GeneralMatchers;

/**
 * Combobox matcher implementation
 * @author GeorgeKH
 */
public class ComboBoxMatchers {
    
    /**
     * 
     * @param <T>
     * @param selection
     * @return 
     */
    public static <T> Matcher<Node> hasSelection(T selection) {
        String descriptionText = "has selection " + selection;
        return GeneralMatchers.typeSafeMatcher(ComboBox.class, descriptionText, node -> hasSelection(node, selection));
    }
    
    /**
     * 
     * @param <T>
     * @param comboBox
     * @param selection
     * @return 
     */
    private static <T> boolean hasSelection(ComboBox<?> comboBox, T selection) {
        return selection.equals(comboBox.getSelectionModel().getSelectedItem());
    }
}

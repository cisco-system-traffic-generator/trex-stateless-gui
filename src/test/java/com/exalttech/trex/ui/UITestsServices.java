/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui;

import com.exalttech.trex.ui.matchers.ComboBoxMatchers;
import com.exalttech.trex.ui.matchers.RadioButtonMatchers;
import javafx.scene.Node;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.matcher.control.TableViewMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.service.support.WaitUntilSupport;

/**
 * UI test services
 * @author GeorgeKH
 */
public abstract class UITestsServices extends ApplicationTest{
    
    
    private WaitUntilSupport waitUntilSupport = new WaitUntilSupport();
   
    /**
     * Find node
     * @param <T>
     * @param query
     * @return 
     */
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }
    
    /**
     * Wait for node to be exists 
     * @param nodeID 
     */
    public void waitForNode(String nodeID){
        waitUntilSupport.waitUntil((Node)find(nodeID), NodeMatchers.isVisible(), 500);
    }
    
    /**
     * Verify text exists
     * @param selector
     * @param text 
     */
    public void verifyTextExists(String selector, String text){
         FxAssert.verifyThat(selector, NodeMatchers.hasText(text));
    }
    
    /**
     * Verify node visible
     * @param selector 
     */
    public void verifyNodeVisible(String selector){
         FxAssert.verifyThat(selector, NodeMatchers.isVisible());
    }
    
    /**
     * Verify Item exists in listview
     * @param selector
     * @param childText 
     */
    public void verifyItemExistsInList(String selector, String childText){
        FxAssert.verifyThat(selector, ListViewMatchers.hasListCell(childText));
    }
    
    public void verifyListIsEmpty(String selector){
        FxAssert.verifyThat(selector,NodeMatchers.isNull());
    }
    
    /**
     * Verify tableview has element
     * @param selector
     * @param cell
     */
    public void verifyTableHasElement(String selector, String cell){
        FxAssert.verifyThat(selector, TableViewMatchers.hasTableCell(cell));
    }
    
    /**
     * Verify combobox selection
     * @param selector
     * @param selectedValue 
     */
    public void verifyComboBoxSelection(String selector, String selectedValue){
        FxAssert.verifyThat(selector, ComboBoxMatchers.hasSelection(selectedValue));
    }
    
    /**
     * Verify radioButton is selected
     * @param selector 
     */
    public void verifRadioButtonSelected(String selector){
         FxAssert.verifyThat(selector, RadioButtonMatchers.isSelected());
    }
    
    /**
     * Verify textfield value
     * @param selector
     * @param value 
     */
    public void verifyTextFieldValue(String selector, String value){
        FxAssert.verifyThat(selector, TextInputControlMatchers.hasText(value));
    }
}

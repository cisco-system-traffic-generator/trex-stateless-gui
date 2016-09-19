/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.connection;

import com.exalttech.trex.ui.UIBaseTest;
import org.testng.annotations.Test;


/**
 * Connection tests implementation
 * @author Georgekh
 */
public class ConnectionsTest extends UIBaseTest{
    
    @Test
    public void openConnectDialogTest(){
        clickOn("#connectIcon");
    }
}

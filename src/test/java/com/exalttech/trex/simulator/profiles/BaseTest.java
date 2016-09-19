/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.simulator.profiles;

import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Exalt
 */
public class BaseTest {

    private static final String STL_SIM_COMMAND = "./stl-sim";
    @Test(groups = "init")
    public void verifyStlSimCommandTest() {
        try {
            Process process = Runtime.getRuntime().exec(STL_SIM_COMMAND);
            Assert.assertTrue(true);
        }catch(IOException ex){
            Assert.assertTrue(false, "Cannot run command "+STL_SIM_COMMAND);
        }
    }
}

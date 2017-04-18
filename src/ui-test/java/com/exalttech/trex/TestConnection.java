package com.exalttech.trex;

import javafx.scene.input.KeyCode;

import static org.testfx.api.FxAssert.verifyThat;

import org.junit.Test;

import org.testfx.matcher.base.NodeMatchers;


public class TestConnection extends TestBase {
    @Test
    public void testConnectDisconnectFromToolbar(){
        clickOn(".connectIcon");
        clickOn("#connection-dialog-ip");
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        write(getTRexServerIP());
        clickOn("#connection-dialog-connect");
        sleep(5000);
        verifyThat("#connection-dialog", NodeMatchers.isNull());
        clickOn(".disconnectIcon");
        sleep(1000);
        verifyThat(".disconnectIcon", NodeMatchers.isNull());
    }

    @Test
    public void testConnectDisconnectFromMenu(){
        clickOn("#main-menu");
        clickOn("#main-menu-connect");
        clickOn("#connection-dialog-ip");
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        write(getTRexServerIP());
        clickOn("#connection-dialog-connect");
        sleep(5000);
        verifyThat("#connection-dialog", NodeMatchers.isNull());
        clickOn("#main-menu");
        clickOn("#main-menu-connect");
        sleep(1000);
        verifyThat(".disconnectIcon", NodeMatchers.isNull());
    }
}

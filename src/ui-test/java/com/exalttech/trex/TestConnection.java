package com.exalttech.trex;

import javafx.scene.control.*;
import org.junit.Assert;
import org.junit.Test;


public class TestConnection extends TestBase {
    private void validateDisconnectedState() {
        final MenuBar menuBar = lookup("#headerMenuBar").query();
        Assert.assertEquals(menuBar.getMenus().get(0).getItems().get(0).getText(), "Connect");
        Assert.assertEquals(getText("#main-server-status"), "Disconnected");
    }

    private void validateConnectedState() {
        final MenuBar menuBar = lookup("#headerMenuBar").query();
        Assert.assertEquals(menuBar.getMenus().get(0).getItems().get(0).getText(), "Disconnect");
        Assert.assertEquals(getText("#main-server-status"), "Connected");
    }

    @Test
    public void testConnectDisconnectByToolbar() {
        validateDisconnectedState();
        Assert.assertTrue("Failed to connect", connect(MenuType.TOOLBAR));
        validateConnectedState();
        Assert.assertTrue("Failed to disconnect", disconnect(MenuType.TOOLBAR));
        validateDisconnectedState();
    }

    @Test
    public void testConnectDisconnectByMenu() {
        validateDisconnectedState();
        Assert.assertTrue("Failed to connect", connect(MenuType.MENU));
        validateConnectedState();
        Assert.assertTrue("Failed to disconnect", disconnect(MenuType.MENU));
        validateDisconnectedState();
    }

    @Test
    public void testConnectDisconnectByShortcut() {
        validateDisconnectedState();
        Assert.assertTrue("Failed to connect", connect(MenuType.SHORTCUT));
        validateConnectedState();
        Assert.assertTrue("Failed to disconnect", disconnect(MenuType.SHORTCUT));
        validateDisconnectedState();
    }
}

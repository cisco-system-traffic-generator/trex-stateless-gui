package com.exalttech.trex;

import javafx.scene.control.Label;

import org.junit.Assert;
import org.junit.Test;


public class TestAcquire extends TestBase {
    @Test
    public void testAcquireReleaseByToolbar() {
        connect(MenuType.SHORTCUT);
        resetAllPorts();

        acquirePortViaToolbar("Port 0");
        Assert.assertNotNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());
        clickOn("#main-toolbar-release-port");
        Assert.assertNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());

        acquirePortViaToolbar("Port 1");
        Assert.assertNotNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());
        clickOn("#main-toolbar-release-port");
        Assert.assertNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());

        disconnect(MenuType.SHORTCUT);
    }

    @Test
    public void testAcquireReleaseByContextMenu() {
        connect(MenuType.SHORTCUT);
        resetAllPorts();

        rightClickOn("Port 0");
        lookup("Acquire").queryAll().forEach(node -> {
            if (node instanceof Label) {
                clickOn(node);
            }
        });
        Assert.assertNotNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());
        rightClickOn("Port 0");
        clickOn("Release Acquire");
        Assert.assertNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());

        rightClickOn("Port 1");
        lookup("Acquire").queryAll().forEach(node -> {
            if (node instanceof Label) {
                clickOn(node);
            }
        });
        Assert.assertNotNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());
        rightClickOn("Port 1");
        clickOn("Release Acquire");
        Assert.assertNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());

        disconnect(MenuType.SHORTCUT);
    }

    @Test
    public void testAcquireReleaseByButtons() {
        connect(MenuType.SHORTCUT);
        resetAllPorts();

        clickOn("Port 0");
        clickOn("Acquire");
        Assert.assertNotNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());
        clickOn("Release");
        Assert.assertNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());

        clickOn("Port 1");
        clickOn("Acquire");
        Assert.assertNotNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());
        clickOn("Release");
        Assert.assertNull(lookup(String.format("(%s)", getTRexDefaultUser())).query());

        disconnect(MenuType.SHORTCUT);
    }

    @Test
    public void testAcquireReleaseByGlobal() {
        connect(MenuType.SHORTCUT);
        resetAllPorts();

        rightClickOn(String.format("TRex-%s", getTRexServerIP()));
        clickOn("Acquire All Ports");
        Assert.assertEquals(2, lookup(String.format("(%s)", getTRexDefaultUser())).queryAll().size());
        rightClickOn(String.format("TRex-%s", getTRexServerIP()));
        clickOn("Release All Ports");
        Assert.assertEquals(0, lookup(String.format("(%s)", getTRexDefaultUser())).queryAll().size());

        disconnect(MenuType.SHORTCUT);
    }

    @Test
    public void testForceAcquireReleaseByGlobal() {
        final String testUser = "AcquireTest";

        connect(MenuType.SHORTCUT, testUser);
        resetAllPorts();

        rightClickOn(String.format("TRex-%s", getTRexServerIP()));
        clickOn("Acquire All Ports");
        Assert.assertEquals(2, lookup(String.format("(%s)", testUser)).queryAll().size());

        disconnect(MenuType.SHORTCUT);

        connect(MenuType.SHORTCUT);

        rightClickOn(String.format("TRex-%s", getTRexServerIP()));
        clickOn("Force Acquire All Ports");
        Assert.assertEquals(2, lookup(String.format("(%s)", getTRexDefaultUser())).queryAll().size());
        rightClickOn(String.format("TRex-%s", getTRexServerIP()));
        clickOn("Release All Ports");
        Assert.assertEquals(0, lookup(String.format("(%s)", getTRexDefaultUser())).queryAll().size());

        disconnect(MenuType.SHORTCUT);
    }
}

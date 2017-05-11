package com.exalttech.trex;

import javafx.scene.control.Button;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestPortControlButtons extends TestBase {
    
    private MenuType connectionType;

    @Parameterized.Parameters
    public static Collection<MenuType> data() {
        return Arrays.asList(MenuType.MENU, MenuType.TOOLBAR, MenuType.SHORTCUT);
    }

    public TestPortControlButtons(MenuType type) {
        connectionType = type;
    }
    
    @Test
    public void checkButtonsAfterAcquireTest() {
        connect(connectionType);
        resetAllPorts();

        Button forceAcquireReleaseBtn = lookup("#forceAcquireBtn").query();
        Assert.assertFalse(forceAcquireReleaseBtn.isDisable());

        acquirePortViaToolbar("Port 0");

        Assert.assertTrue(forceAcquireReleaseBtn.isDisable());

        Button acquireReleaseBtn = lookup("#acquireReleaseBtn").query();
        Assert.assertEquals(acquireReleaseBtn.getText(), "Release");
        disconnect(connectionType);
    }
}

package com.exalttech.trex;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import org.controlsfx.control.ToggleSwitch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;
import org.testng.util.Strings;

import java.util.stream.Stream;

public class TestPortConfiguration extends TestBase {
    
    @Before
    public void before() {
        connect(MenuType.TOOLBAR);
        resetAllPorts();
    }
    
    @After
    public void after() {
        disconnect(MenuType.TOOLBAR);
    }
    
    @Test
    public void checkAttributeChangingTest() {
        
        acquirePortViaToolbar("Port 0");
        
        checkPortAttrs();
        
        checkPortAttrsModification();
    }
    
    @Test
    public void checkPortConfigurationNegative() {
        clickOn("Configuration");
        Node tabContent = lookup("#layerConfig").query();
        Assert.assertTrue(tabContent.isDisabled());
    }
    
    @Test
    public void checkPortConfiguration() {
        acquirePortViaToolbar("Port 0");
        clickOn("Configuration");
        Node tabContent = lookup("#layerConfig").query();
        Button applyBtn = lookup("#applyBtn").query();
        Assert.assertFalse(tabContent.isDisabled());
        
        tryCall(
            () -> {
                clearLogs();
                setText("#l2Destination", "de:ad:be:ef:de:ad");
                clickOn("Apply");
            },
            () -> applyBtn.getText().equalsIgnoreCase("Apply")
        );
        
        Assert.assertTrue(checkResultInLogs("L2 mode configured"));
        
        tryCall(
            () -> {
                acquirePortViaToolbar("Port 1");
                clickOn("Configuration");
                clickOn("#l3Mode");
                setText("#l3Source", "192.168.50.156");
                setText("#l3Destination", "192.168.50.155");
                clickOn("Apply");
            },
            () -> applyBtn.getText().equalsIgnoreCase("Apply")
        );
        
        tryCall(
            () -> {
                clearLogs();
                clickOn("Port 0");
                clickOn("Configuration");
                clickOn("#l3Mode");
                setText("#l3Source", "192.168.50.155");
                setText("#l3Destination", "192.168.50.156");
                clickOn("Apply");
            },
            () -> applyBtn.getText().equalsIgnoreCase("Apply")
        );
        Label arpStatus = lookup("#arpStatus").query();
        Assert.assertTrue(arpStatus.getText().equalsIgnoreCase("resolved"));
        resetPort("Port 0");
    }

    private void clickOnToggleSwitch(String controlId) {
        ToggleSwitch multicast = lookup(controlId).query();
        moveTo(multicast);
        moveBy(8, 0);
        clickOn(MouseButton.PRIMARY);
        int a = 1;
    }

    private void checkPortAttrs() {
        Stream.of(
            "index",
            "rxFilterMode",
            "owner",
            "status",
            "numaMode",
            "pciAddress",
            "rxQueueing",
            "gratArp")
        .forEach(attrId -> {
            Label attr = lookup("#"+ attrId).query();
            Assert.assertFalse(Strings.isNullOrEmpty(attr.getText()));
        });
    }

    private void checkPortAttrsModification() {
        Stream.of(
            "multicast",
            "promiscuousMode")
        .forEach(attrId -> {
            clearLogs();
            sleep(1500);
            assertCall(() -> clickOnToggleSwitch("#" + attrId), () -> checkResultInLogs("Set attr"));
        });
    }
}

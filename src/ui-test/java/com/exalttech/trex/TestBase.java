package com.exalttech.trex;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import org.junit.Assert;

import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.*;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.core.ConnectionManager;


public class TestBase extends ApplicationTest {
    enum MenuType {
        MENU,
        SHORTCUT,
        TOOLBAR
    }

    static private final long TIMEOUT_S = 10;
    static private final long POLLING_INTERVAL_MS = 100;
    static private final long RENDER_DELAY_MS = 1000;

    private TrexApp app;

    @Override
    public void start(Stage stage) throws Exception {
        TrexApp.setPrimaryStage(stage);
        app = new TrexApp();
        app.start(stage);
    }

    @Override
    public void stop() throws Exception {
        app.stop();
    }

    private void setText(final String query, final String text) {
        final Node node = lookup(query).query();
        if (node instanceof TextField) {
            ((TextField) node).setText(text);
        } else if (node instanceof ComboBox) {
            ((ComboBox) node).getEditor().setText(text);
        } else {
            Assert.fail("Unsupported node type");
        }
    }

    String getText(final String query) {
        final Node node = lookup(query).query();
        if (node instanceof Label) {
            return ((Label) node).getText();
        } else {
            Assert.fail("Unsupported node type");
        }
        return null;
    }

    private String getTRexServerIP() {
        return "192.168.50.154";
    }

    private String getTRexSyncPort() {
        return "4501";
    }

    private String getTRexAsyncPort() {
        return "4500";
    }

    private String getTRexScapyPort() {
        return "4500";
    }

    private String getTRexDefaultUser() {
        return "Test";
    }

    boolean connect(final MenuType menuType) {
        switch (menuType) {
            case MENU:
                clickOn("#main-menu");
                clickOn("#main-menu-connect");
                break;
            case SHORTCUT:
                push(KeyCode.CONTROL, KeyCode.C);
                break;
            case TOOLBAR:
                clickOn("#main-toolbar-connect");
                break;
            default:
                return false;
        }
        sleep(RENDER_DELAY_MS);

        setText("#connection-dialog-ip", getTRexServerIP());
        clickOn("#connection-dialog-advance");
        setText("#connection-dialog-sync-port", getTRexSyncPort());
        setText("#connection-dialog-async-port", getTRexAsyncPort());
        setText("#connection-dialog-scapy-port", getTRexScapyPort());
        setText("#connection-dialog-user", getTRexDefaultUser());

        clickOn("#connection-dialog-connect");

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<Boolean> future = executor.submit(() -> {
            while (!ConnectionManager.getInstance().isConnected()) {
                sleep(POLLING_INTERVAL_MS);
            }
            return true;
        });
        boolean connected = false;
        try {
            connected = future.get(TIMEOUT_S, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(true);
        }
        executor.shutdownNow();

        if (connected) {
            sleep(RENDER_DELAY_MS);
        }

        return connected;
    }

    boolean disconnect(final MenuType menuType) {
        switch (menuType) {
            case MENU:
                clickOn("#main-menu");
                clickOn("#main-menu-connect");
                break;
            case SHORTCUT:
                push(KeyCode.CONTROL, KeyCode.C);
                break;
            case TOOLBAR:
                clickOn("#main-toolbar-connect");
                break;
            default:
                return false;
        }
        sleep(RENDER_DELAY_MS);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<Boolean> future = executor.submit(() -> {
            while (ConnectionManager.getInstance().isConnected()) {
                sleep(POLLING_INTERVAL_MS);
            }
            return true;
        });
        boolean disconnected = false;
        try {
            disconnected = future.get(TIMEOUT_S, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(true);
        }
        executor.shutdownNow();

        if (disconnected) {
            sleep(RENDER_DELAY_MS);
        }

        return disconnected;
    }
}

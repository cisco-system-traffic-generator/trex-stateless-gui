package com.exalttech.trex;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.core.ConnectionManager;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Assert;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Optional;
import java.util.concurrent.*;


public class TestBase extends ApplicationTest {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    enum MenuType {
        MENU,
        SHORTCUT,
        TOOLBAR
    }

    static private final long TIMEOUT_S = 60;
    static private final long POLLING_INTERVAL_MS = 100;
    static private final long RENDER_DELAY_MS = 2500;

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

    protected void setText(final String query, final String text) {
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

    protected String getTRexServerIP() {
        return "trex-host";
    }

    protected String getTRexSyncPort() {
        return "4501";
    }

    protected String getTRexAsyncPort() {
        return "4500";
    }

    protected String getTRexScapyPort() {
        return "4507";
    }

    protected String getTRexDefaultUser() {
        return "Test";
    }

    boolean connect(final MenuType menuType, final String user) {
        connectOrDisconnectAction(menuType);
        sleep(RENDER_DELAY_MS);

        setText("#connection-dialog-ip", getTRexServerIP());
        clickOn("#connection-dialog-advance");
        setText("#connection-dialog-sync-port", getTRexSyncPort());
        setText("#connection-dialog-async-port", getTRexAsyncPort());
        setText("#connection-dialog-scapy-port", getTRexScapyPort());
        setText("#connection-dialog-user", user);

        clickOn("#connection-dialog-connect");

        final Future<Boolean> future = executor.submit(() -> {
            while (!isConnected()) {
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

        if (connected) {
            sleep(RENDER_DELAY_MS);
        }

        return connected;
    }

    private boolean isConnected() {
        return !isDisconnected();
    }

    protected Boolean isDisconnected() {
        Label connection = lookup("#main-server-status").query();
        return "disconnected".equalsIgnoreCase(connection.getText());
    }

    boolean connect(final MenuType menuType) {
        return connect(menuType, getTRexDefaultUser());
    }

    protected Boolean tryCall(Runnable action, Callable<Boolean> resultValidator) {
        final Future<Boolean> future = executor.submit(() -> {
            try {
                action.run();
            } catch (Exception e) {
                return null;
            }
            while (!resultValidator.call()) {
                sleep(POLLING_INTERVAL_MS);
            }
            return true;
        });

        try {
            return future.get(TIMEOUT_S, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(true);
            return false;
        }
    }
    
    boolean disconnect(final MenuType menuType) {
        connectOrDisconnectAction(menuType);
        sleep(RENDER_DELAY_MS);

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

        if (disconnected) {
            sleep(RENDER_DELAY_MS);
        }

        return disconnected;
    }

    private void connectOrDisconnectAction(MenuType menuType) {
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
        }
    }

    void resetAllPorts() {
        resetPort("Port 0");
        resetPort("Port 1");
    }
    
    private void resetPort(String port) {
        clickOn(port);
        tryCall(
          () -> clickOn("#resetBtn"),
          () -> {
              Button resetBtn = lookup("#resetBtn").query();
              return resetBtn.getText().equalsIgnoreCase("Reset");
          }
        );
        
    }

    public boolean isVisible(String query) {
        Optional<Node> result = lookup(query).tryQuery();
        return result.isPresent() && result.get().isVisible();
    }
}

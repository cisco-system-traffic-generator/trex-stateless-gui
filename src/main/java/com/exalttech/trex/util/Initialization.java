package com.exalttech.trex.util;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Initialization {
    public static void initializeFXML(Object object, String resourceName) {
        FXMLLoader fxmlLoader = new FXMLLoader(
                object.getClass().getResource(resourceName)
        );
        fxmlLoader.setRoot(object);
        fxmlLoader.setController(object);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void initializeCloseEvent(Node root, EventHandler<WindowEvent> eventHandler) {
        Scene scene = root.getScene();
        if (scene == null) {
            root.sceneProperty().addListener(((observable, oldScene, newScene) -> {
                if (oldScene == null && newScene != null) {
                    initializeCloseEvent(newScene, eventHandler);
                }
            }));
        } else {
            initializeCloseEvent(scene, eventHandler);
        }
    }

    public static void initializeCloseEvent(Scene scene, EventHandler<WindowEvent> eventHandler) {
        Window window = scene.getWindow();
        if (window == null) {
            scene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                if (oldWindow == null && newWindow != null) {
                    initializeCloseEvent(newWindow, eventHandler);
                }
            });
        } else {
            initializeCloseEvent(window, eventHandler);
        }
    }

    public static void initializeCloseEvent(Window window, EventHandler<WindowEvent> eventHandler) {
        window.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, eventHandler);
    }
}

package com.exalttech.trex.ui.controllers.PortInfo;

import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import com.google.inject.Injector;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

public class PortInfoTabMain extends GridPane {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MainViewController.class.getName());

    @FXML private GridPane root;

    private EventHandler<ActionEvent> handlerActionSaveExternal;

    private ChangeListener<String> onlyNumberListener = (observable, oldValue, newValue) -> {
        if (!newValue.matches("\\d*")) {
            ((StringProperty) observable).set(oldValue);
        }
    };

    private ChangeListener<String> onlyHexListener = (observable, oldValue, newValue) -> {
        if (!newValue.matches("([0-9a-fA-F]*|0x[0-9a-fA-F]*|\\s*[0-9a-fA-F]*|\\s*0x[0-9a-fA-F]*)*")) {
            ((StringProperty) observable).set(oldValue);
        }
    };

    private EventHandler<ActionEvent> handlerActionSaveInternal = (event) -> {
        if (handlerActionSaveExternal != null) {
            handlerActionSaveExternal.handle(event);
        }
    };

    public PortInfoTabMain(Injector injector, Port port) {
        FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);

        fxmlLoader.setLocation(getClass().getResource("/fxml/PortInfo/TabMain.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception e) {
            LOG.error("Failed to load fxml file: " + e.getMessage());
        }
    }

}

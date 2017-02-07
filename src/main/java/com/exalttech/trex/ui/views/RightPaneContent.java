package com.exalttech.trex.ui.views;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.controllers.PortInfo.PortInfoTabConfig;
import com.exalttech.trex.ui.controllers.PortInfo.PortInfoTabMain;
import com.exalttech.trex.ui.models.Port;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;

/**
 * Created by ichebyki on 07.02.17.
 */
public class RightPaneContent {

    private static final Logger LOG = Logger.getLogger(MainViewController.class.getName());

    private GridPane rootPortInfoTabMain;
    private GridPane rootPortInfoTabConfig;

    TabPane tabPanePortInfo = new TabPane();

    public Node generatePortInfoPane(Port port) {
        Tab tab;

        if (tabPanePortInfo.getTabs().size() == 0) {
            tab = new Tab("Main");
            tab.setClosable(false);
            tabPanePortInfo.getTabs().add(0, tab);
            tab = new Tab("Layer Configuration");
            tab.setClosable(false);
            tabPanePortInfo.getTabs().add(1, tab);
        }
        try {
            tab = tabPanePortInfo.getTabs().get(0);
            rootPortInfoTabMain = new PortInfoTabMain(TrexApp.injector, port);
            tab.setContent(rootPortInfoTabMain);
        } catch (Exception e) {
            LOG.error("Failed to load fxml: ", e);
        }

        try {
            tab = tabPanePortInfo.getTabs().get(1);
            rootPortInfoTabConfig = new PortInfoTabConfig(TrexApp.injector, port);
            tab.setContent(rootPortInfoTabConfig);
        } catch (Exception e) {
            LOG.error("Failed to load fxml: ", e);
        }

        return tabPanePortInfo;
    }

}

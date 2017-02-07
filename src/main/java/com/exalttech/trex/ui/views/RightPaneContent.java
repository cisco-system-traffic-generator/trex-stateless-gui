package com.exalttech.trex.ui.views;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.ui.controllers.MainViewController;
import com.exalttech.trex.ui.models.Port;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;

import static org.testng.CommandLineArgs.LOG;

/**
 * Created by ichebyki on 07.02.17.
 */
public class RightPaneContent {

    private static final Logger LOG = Logger.getLogger(MainViewController.class.getName());

    @FXML
    private GridPane rootPortInfoTabMain;
    @FXML
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

        FXMLLoader fxmlLoader = TrexApp.injector.getInstance(FXMLLoader.class);
        try {
            tab = tabPanePortInfo.getTabs().get(0);
            fxmlLoader.setLocation(getClass().getResource("/fxml/PortInfo/TabMain.fxml"));
            fxmlLoader.load();
            rootPortInfoTabMain = fxmlLoader.getRoot();
            tab.setContent(rootPortInfoTabMain);
        } catch (Exception e) {
            LOG.error("Failed to load fxml: ", e);
        }

        try {
            tab = tabPanePortInfo.getTabs().get(1);
            fxmlLoader.setLocation(getClass().getResource("/fxml/PortInfo/TabConfig.fxml"));
            fxmlLoader.load();
            tab.setContent(rootPortInfoTabConfig);
        } catch (Exception e) {
            LOG.error("Failed to load fxml: ", e);
        }

        return tabPanePortInfo;
    }

}

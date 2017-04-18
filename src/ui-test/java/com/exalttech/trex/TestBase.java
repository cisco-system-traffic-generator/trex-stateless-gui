package com.exalttech.trex;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.testfx.framework.junit.ApplicationTest;

import com.exalttech.trex.application.TrexApp;


public class TestBase extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        TrexApp.setPrimaryStage(stage);
        final AnchorPane page = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
        final Scene scene = new Scene(page);
        scene.getStylesheets().add(TrexApp.class.getResource("/styles/mainStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("TRex");
        stage.setResizable(true);
        stage.setMinWidth(1100);
        stage.setMinHeight(670);
        stage.show();
    }

    public String getTRexServerIP() {
        return "192.168.50.154";
    }
}

/**
 * *****************************************************************************
 * Copyright (c) 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************
 */
package com.exalttech.trex.application;

import com.exalttech.trex.util.Util;
import com.google.inject.Injector;
import com.xored.javafx.packeteditor.TRexPacketCraftingTool;
import com.xored.javafx.packeteditor.guice.TrexGuiceModule;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author GeorgeKh
 */
public class TrexApp extends Application {

    public static Injector injector = TrexGuiceModule.injector();

    private static final Logger LOG = Logger.getLogger(TrexApp.class.getName());

    private static Stage primaryStage;

    /**
     * Entry point to the Application
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        LOG.info("Starting TRex");
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        LOG.info("Current relative path is: " + s);
        LOG.info(Util.getCwd().getAbsolutePath());
        launch(args);
    }

    /**
     *
     * @return
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        TrexApp.primaryStage = primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        TRexPacketCraftingTool packetBuilder = new TRexPacketCraftingTool(injector);
        packetBuilder.initAppController();
        speedupTooltip();
        primaryStage = stage;
        AnchorPane page = (AnchorPane) FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
        Scene scene = new Scene(page);
        scene.getStylesheets().add(TrexApp.class.getResource("/styles/mainStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("TRex");
        stage.setResizable(true);
        stage.setMinWidth(780);
        stage.setMinHeight(700);
        stage.getIcons().add(new Image("/icons/trex.png"));

        stage.show();
    }

    /**
     * Speeding up displaying tootlip for JDK 8 ref:
     * http://stackoverflow.com/questions/26854301/control-javafx-tooltip-delay
     */
    private void speedupTooltip() {
        try {
            Tooltip tooltip = new Tooltip();
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOG.error(e);
        }
    }
}

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
package com.exalttech.trex.ui.dialog;

import com.exalttech.trex.application.TrexApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Class for control and generate dialog window
 *
 * @author GeorgeKh
 */
public class DialogWindow {

    String layoutFile;
    FXMLLoader loader;
    Stage dialogStage;

    /**
     *
     * @param layoutFile
     * @param title
     * @param parentXDistance
     * @param parentYDistance
     * @param resizable
     * @param owner
     * @throws IOException
     */
    public DialogWindow(String layoutFile, String title, double parentXDistance, double parentYDistance, boolean resizable, Stage owner) throws IOException {
        this.layoutFile = layoutFile;
        loader = TrexApp.injector.getInstance(FXMLLoader.class);
        loader.setLocation(TrexApp.class.getResource("/fxml/" + layoutFile));
        dialogStage = buildDialogWindow(title, parentXDistance, parentYDistance, resizable, owner);
    }

    /**
     * Show Dialog
     *
     * @param isLockParent
     */
    public void show(boolean isLockParent) {
        DialogKeyPressHandler controller = (DialogKeyPressHandler) getController();
        controller.setupStage(dialogStage);
        if (isLockParent) {
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();
        } else {
            dialogStage.initModality(Modality.NONE);
            dialogStage.initOwner(null);
            dialogStage.show();
        }
    }

    /**
     * Build dialog view
     *
     * @param title
     * @param parentXDistance
     * @param parentYDistance
     * @return
     * @throws IOException
     */
    private Stage buildDialogWindow(String title, double parentXDistance, double parentYDistance, boolean resizable, Stage owner) throws IOException {

        AnchorPane page = loader.load();
        Stage createdStage = new Stage();
        createdStage.setTitle(title);
        createdStage.initOwner(owner);
        Scene scene = new Scene(page);
        scene.getStylesheets().add(TrexApp.class.getResource("/styles/mainStyle.css").toExternalForm());
        scene.getStylesheets().add(TrexApp.class.getResource("/styles/main-narrow.css").toExternalForm());
        createdStage.setScene(scene);

        createdStage.setResizable(resizable);
        if (resizable) {
            createdStage.initStyle(StageStyle.DECORATED);
        } else {
            createdStage.initStyle(StageStyle.UTILITY);
        }
        createdStage.setX(TrexApp.getPrimaryStage().getX() + parentXDistance);
        createdStage.setY(TrexApp.getPrimaryStage().getY() + parentYDistance);
        createdStage.initModality(Modality.NONE);
        createdStage.getIcons().add(new Image("/icons/trex.png"));
        return createdStage;
    }

    /**
     * Return dialog controller
     *
     * @return
     */
    public Object getController() {
        return loader.getController();
    }

    /**
     * Set min width & height for the displayed dialog
     *
     * @param width
     * @param height
     */
    public void setMinSize(double width, double height) {
        dialogStage.setMinWidth(width);
        dialogStage.setMinHeight(height);
    }
}

package com.exalttech.trex.ui.dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import com.xored.javafx.packeteditor.view.FieldEditorView;

import com.exalttech.trex.application.TrexApp;


public class DialogWindow {
    private FXMLLoader loader;
    private Stage dialogStage;
    private Pane rootPane;
    private boolean hasBeenVisible = false;

    public DialogWindow(
            final String layoutFile,
            final String title,
            final double parentXDistance,
            final double parentYDistance,
            final boolean resizable,
            final Stage owner
    ) throws IOException {
        this(layoutFile, title, parentXDistance, parentYDistance, -1.D, -1.D, resizable, owner);
    }

    public DialogWindow(
            final String layoutFile,
            final String title,
            final double parentXDistance,
            final double parentYDistance,
            final double minWidth,
            final double minHeight,
            final boolean resizable,
            final Stage owner
    ) throws IOException {
        loader = TrexApp.injector.getInstance(FXMLLoader.class);
        loader.setLocation(TrexApp.class.getResource("/fxml/" + layoutFile));

        dialogStage = buildDialogWindow(title, parentXDistance, parentYDistance, minWidth, minHeight, resizable, owner);

        if (minWidth != -1) {
            dialogStage.setMinWidth(minWidth);
        }
        if (minHeight != -1) {
            dialogStage.setMinHeight(minHeight);
        }

        dialogStage.setOnCloseRequest(event -> {
            DialogManager.getInstance().removeHandler(this);
            ((DialogView) loader.getController()).shutdown();
        });
        dialogStage.setOnShowing(event -> {
            DialogManager.getInstance().addHandler(this, () -> dialogStage.close());
        });
    }

    public void show(boolean isLockParent) {
        DialogKeyPressHandler controller = (DialogKeyPressHandler) getController();
        if (controller != null) {
            controller.setupStage(dialogStage);
        }

        if (dialogStage.isShowing()) {
            dialogStage.requestFocus();
        }

        if (!hasBeenVisible) {
            Modality modality = isLockParent? Modality.APPLICATION_MODAL : Modality.NONE;
            dialogStage.initModality(modality);

            if (!isLockParent) {
                dialogStage.initOwner(null);
            }
        }

        if (isLockParent) {
            dialogStage.showAndWait();
        } else {
            dialogStage.show();
        }

        hasBeenVisible = true;
    }

    private Stage buildDialogWindow(
            String title,
            double parentXDistance,
            double parentYDistance,
            double minWidth,
            double minHeight,
            boolean resizable,
            Stage owner
    ) throws IOException {
        rootPane = loader.load();
        Stage createdStage = new Stage();
        createdStage.setTitle(title);
        createdStage.initOwner(owner);
        Scene scene = new Scene(rootPane, minWidth, minHeight);
        scene.getStylesheets().add(TrexApp.class.getResource("/styles/mainStyle.css").toExternalForm());

        // Packet editor
        FieldEditorView.initCss(scene);

        createdStage.setScene(scene);

        createdStage.setResizable(resizable);
        createdStage.initStyle(StageStyle.DECORATED);
        createdStage.setX(TrexApp.getPrimaryStage().getX() + parentXDistance);
        createdStage.setY(TrexApp.getPrimaryStage().getY() + parentYDistance);
        createdStage.initModality(Modality.NONE);
        createdStage.getIcons().add(new Image("/icons/trex.png"));

        return createdStage;
    }

    public Object getController() {
        return loader.getController();
    }
}

package com.exalttech.trex.ui.util;

import com.exalttech.trex.application.TrexApp;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class AlertUtils {
    public static final int WRAPPING_WIDTH = 350;

    static public Alert construct(Alert.AlertType type, String title, String header, String msg, ButtonType ... buttonTypes) {
        Alert alert = new Alert(type, null, buttonTypes);

        if (msg != null) {
            Text text = new Text(msg);
            text.setWrappingWidth(WRAPPING_WIDTH);
            text.getStyleClass().add("alert-text");
            HBox container = new HBox();
            container.getChildren().add(text);
            alert.getDialogPane().setContent(container);

        }

        alert.getDialogPane().getStyleClass().add("alert-content");

        if (title != null) {
            alert.setTitle(title);
        }

        if (header != null) {
            alert.setHeaderText(header);
        }

        alert.getDialogPane()
                .getScene()
                .getStylesheets()
                .add(TrexApp.class.getResource("/styles/mainStyle.css").toExternalForm());



        return alert;
    }

    static public Alert construct(Alert.AlertType type, String title, String header, String msg) {
        return construct(type, title, header, msg, ButtonType.OK);
    }

    static public Alert constructEmpty(Alert.AlertType type, ButtonType ... buttonTypes) {
        return construct(type, null, null, null, buttonTypes);
    }
}

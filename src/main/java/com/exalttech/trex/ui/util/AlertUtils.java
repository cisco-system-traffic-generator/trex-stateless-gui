package com.exalttech.trex.ui.util;

import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class AlertUtils {
    static public Alert construct(Alert.AlertType type, String title, String header, String msg) {
        Text text = new Text(msg);
        text.setWrappingWidth(350);

        HBox container = new HBox();
        container.setSpacing(10);
        container.getChildren().add(text);

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.getDialogPane().setContent(container);

        return alert;
    }
}

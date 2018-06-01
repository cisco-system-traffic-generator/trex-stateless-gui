package com.exalttech.trex.ui.util;

import com.exalttech.trex.application.TrexApp;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;


public class TrexAlertBuilder {
    private static final int WRAPPING_WIDTH = 350;
    private static final int ALERT_X_POSITION = 300;
    private static final int ALERT_Y_POSITION = 150;

    protected Alert alert;

    public TrexAlertBuilder() {
        alert = new Alert(Alert.AlertType.NONE);
        alert.getDialogPane().getStyleClass().add("alert-content");

        alert.getDialogPane()
                .getScene()
                .getStylesheets()
                .add(TrexApp.class.getResource("/styles/mainStyle.css").toExternalForm());

        alert.setX(TrexApp.getPrimaryStage().getX() + ALERT_X_POSITION);
        alert.setY(TrexApp.getPrimaryStage().getY() + ALERT_Y_POSITION);

        alert.setHeaderText(null); // header is hidden by default
    }

    public TrexAlertBuilder setType(Alert.AlertType type) {
        alert.setAlertType(type);
        return this;
    }

    public TrexAlertBuilder setTitle(String title) {
        alert.setTitle(title);
        return this;
    }

    public TrexAlertBuilder setHeader(String header) {
        alert.setHeaderText(header);
        return this;
    }

    public TrexAlertBuilder setContent(String content) {
        Text text = new Text(content);
        text.setWrappingWidth(WRAPPING_WIDTH);
        text.getStyleClass().add("alert-text");
        text.setFontSmoothingType(FontSmoothingType.LCD);

        HBox container = new HBox();
        container.getChildren().add(text);
        alert.getDialogPane().setContent(container);
        return this;
    }

    public TrexAlertBuilder setButtons(ButtonType ... buttonTypes) {
        if (buttonTypes != null && buttonTypes.length > 0) {
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(buttonTypes);
        }
        return this;
    }

    public TrexAlertBuilder setStyle(String style) {
        alert.getDialogPane().getStyleClass().add(style);
        return this;
    }

    public Alert getAlert() {
        return alert;
    }

    public static TrexAlertBuilder build() {
        return new TrexAlertBuilder();
    }

}

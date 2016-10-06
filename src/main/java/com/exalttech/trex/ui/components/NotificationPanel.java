/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Notification panel component implementation
 * @author Georgekh
 */
public class NotificationPanel extends HBox {

    Pane notificationContainer;
    Label notificationLabel;

    public NotificationPanel() {
        buildUI();
    }

    /**
     * Build component UI
     */
    private void buildUI() {

        this.setSpacing(5);
        
        // add notification message
        notificationContainer = new Pane();
        notificationContainer.setPrefSize(184, 64);
        notificationContainer.getStyleClass().add("notificationContainer");
        notificationLabel = new Label();
        notificationLabel.setPrefSize(155, 60);
        notificationLabel.setWrapText(true);
        notificationLabel.getStyleClass().add("notificationMsg");
        notificationContainer.getChildren().add(notificationLabel);
        getChildren().add(notificationContainer);

        // add notification icon
        VBox iconHolder = new VBox();
        iconHolder.setPrefHeight(68);
        iconHolder.setAlignment(Pos.CENTER);
        ImageView notificationIcon = new ImageView(new Image("/icons/info_icon.png"));
        notificationIcon.getStyleClass().add("notificationIcon");
        notificationIcon.setOnMouseClicked((MouseEvent event) -> {
            notificationContainer.setVisible(!notificationContainer.isVisible());
        });
        iconHolder.getChildren().add(notificationIcon);
        getChildren().add(iconHolder);

    }

    /**
     * Set notification message
     * @param msg 
     */
    public void setNotificationMsg(String msg) {
        notificationLabel.setText(msg);
    }

}

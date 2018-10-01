package com.exalttech.trex.ui.controllers.daemon;

import com.exalttech.trex.util.Util;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

class ConfigTreeItem extends TreeItem {

    private Runnable treeConfigUpdateCallback;
    private Runnable yamlUpdateCallback;
    private ConfigNode configNode;
    private Label label;
    private Node control;

    public ConfigTreeItem(ConfigNode configNode, Runnable yamlUpdateCallback, Runnable treeConfigUpdateCallback) {
        this.configNode = configNode;
        this.yamlUpdateCallback = yamlUpdateCallback;
        this.treeConfigUpdateCallback = treeConfigUpdateCallback;
        initTreeItem();
    }

    private void initTreeItem() {
        initTreeItemValue();

        setExpanded(configNode.isMandatory() || configNode.getValue() != null);

        configNode.getChildren().addListener((ListChangeListener<ConfigNode>) c -> {
            updateItemStyle();
        });

        this.configNode.mandatoryProperty().addListener(((observable, oldValue, newValue) -> {
            updateItemStyle();
        }));

        initChildren();
        updateItemStyle();
    }

    private void initChildren() {
        for (ConfigNode configNode : this.configNode.getChildren()) {
            TreeItem treeItem = new ConfigTreeItem(configNode, yamlUpdateCallback, treeConfigUpdateCallback);
            this.getChildren().add(treeItem);
        }
    }

    private void initTreeItemValue() {
        HBox itemContainer = new HBox();
        itemContainer.setSpacing(10);
        itemContainer.setAlignment(Pos.BOTTOM_LEFT);
        itemContainer.getChildren().add(createLabel());

        Region spacing = new Region();
        spacing.setMaxHeight(0d);
        HBox.setHgrow(spacing, Priority.ALWAYS);
        itemContainer.getChildren().add(spacing);

        if (configNode.isRemovable()) {
            itemContainer.getChildren().add(createRemoveButton());
        }

        this.control = createControl();
        if (control != null) {
            itemContainer.getChildren().add(control);
        }

        setValue(itemContainer);
    }

    private Node createRemoveButton() {
        Button removeButton = new Button("\uD83D\uDDD9");
        removeButton.getStyleClass().add("normalButton");
        removeButton.setOnAction(event -> {
            if (configNode.getParent() != null) {
                configNode.getParent().getChildren().remove(configNode);
            }
            getParent().getChildren().remove(this);
            yamlUpdateCallback.run();
        });
        return removeButton;
    }

    private Label createLabel() {
        Label label = new Label(this.configNode.getName());
        if (!configNode.getDescription().isEmpty()) {
            Tooltip tooltip = new Tooltip(configNode.getDescription());
            tooltip.setWrapText(true);
            tooltip.setMaxWidth(300);
            label.setTooltip(tooltip);
        }

        this.label = label;
        return label;
    }

    private void updateItemStyle() {
        if (control != null) {
            control.getStyleClass().remove("invalid-input");
            if(!configNode.validateValue()) {
                control.getStyleClass().add("invalid-input");
            }
        }

        label.getStyleClass().remove("invalid-input");
        if (!configNode.validateValue()){
            label.getStyleClass().add("invalid-input");
        }

        label.getStyleClass().remove("non-mandatory-treeitem");
        if (!configNode.isMandatory()) {
            label.getStyleClass().add("non-mandatory-treeitem");
        }

        Platform.runLater( () -> {
            if (configNode.getParent() != null && getParent() != null) {
                ((ConfigTreeItem) getParent()).updateItemStyle();
            }
        });
    }

    Node createControl() {
        switch(configNode.getType()) {
            case NUMBER:
            case FLOAT:
            case STRING:
            case IP:
            case MAC:
                return createTextFieldControl();
            case BOOLEAN:
                return createCheckBoxControl();
            case LIST:
                return createListControl();
            case OBJECT:
            default:
                return null;
        }
    }

    private Node createListControl() {
        Button button = new Button("➕");
        button.getStyleClass().add("normalButton");
        button.setOnAction(event -> {
            ConfigNode newChild = this.configNode.addListItem();
            yamlUpdateCallback.run();
            TreeItem treeItem = new ConfigTreeItem(newChild, yamlUpdateCallback, treeConfigUpdateCallback);
            this.getChildren().add(treeItem);
        });

        updateItemStyle();

        return button;
    }

    private Node createCheckBoxControl() {
        CheckBox checkboxUse = new CheckBox("Use");
        CheckBox checkboxValue = new CheckBox("Value");

        checkboxValue.disableProperty().bind(checkboxUse.selectedProperty().not());

        ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) -> {
            if (!checkboxUse.isSelected()) {
                configNode.setValue(null);
            } else {
                configNode.setValue(checkboxValue.isSelected());
            }
            updateItemStyle();
            yamlUpdateCallback.run();
        };

        checkboxUse.selectedProperty().addListener(changeListener);
        checkboxValue.selectedProperty().addListener(changeListener);

        HBox hbox = new HBox();
        hbox.setSpacing(5d);
        hbox.getChildren().add(checkboxUse);
        hbox.getChildren().add(checkboxValue);

        if (configNode.getValue() != null) {
            checkboxUse.setSelected(true);
        }

        checkboxUse.setSelected(configNode.isMandatory());
        checkboxValue.setSelected(configNode.getDefaultValue().equals("true"));

        updateItemStyle();

        return hbox;
    }

    private Node createTextFieldControl() {
        TextField textfield = new TextField();
        textfield.setPromptText(configNode.getDefaultValue());

        textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Util.isNullOrEmpty(newValue)) {
                configNode.setValue(null);
            } else {
                configNode.setValue(newValue);

            }
            updateItemStyle();
            yamlUpdateCallback.run();
        });

        if (configNode.getValue() != null) {
            textfield.setText(configNode.getValue().toString());
        }

        updateItemStyle();

        return textfield;
    }
}
package com.exalttech.trex.ui.controllers.daemon;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.ui.dialog.DialogWindow;
import com.exalttech.trex.ui.util.TrexAlertBuilder;
import com.exalttech.trex.util.Util;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.Objects;

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
        itemContainer.setSpacing(5d);
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
                if (configNode.getParent() != null && Objects.equals(configNode.getParent().getId(), "interfaces")) { // hardcoded interfaces control
                    return createInterfacesSelectionControl();
                }
            case IP:
            case MAC:
                return createTextFieldControl();
            case BOOLEAN:
                return createCheckBoxControl();
            case LIST:
                return createListControl();
            case ENUM:
                return createEnumControl();
            case OBJECT:
            default:
                return null;
        }
    }

    private Node createInterfacesSelectionControl() {
        HBox hbox = new HBox();
        hbox.setSpacing(5d);
        TextField textFieldControl = createTextFieldControl();
        Button selectInterface = new Button("Select");
        selectInterface.getStyleClass().add("normalButton");
        selectInterface.setOnAction(event -> {
            try {
                DialogWindow selectInterfaceDialog = new DialogWindow(
                        "InterfaceSelectDialog.fxml",
                        "Select interface",
                        300,
                        100,
                        false,
                        TrexApp.getPrimaryStage()
                );
                selectInterfaceDialog.show(true);
                String selected = ((InterfaceSelectDialogController) selectInterfaceDialog.getController()).getSelectedString();
                textFieldControl.setText(selected);
            } catch (IOException e) {
                TrexAlertBuilder.build()
                        .setType(Alert.AlertType.ERROR)
                        .setContent("Unable to open interface dialog. You should enter interface manually")
                        .getAlert()
                        .showAndWait();
            }
        });

        if (!TrexApp.injector.getInstance(InterfaceInfoProvider.class).hasInfo()) {
            selectInterface.setDisable(true);

            Tooltip tooltip = new Tooltip("Interface info is not available. You should enter interface manually");
            tooltip.setWrapText(true);
            tooltip.setMaxWidth(300);
            textFieldControl.setTooltip(tooltip);
        }

        hbox.getChildren().add(selectInterface);
        hbox.getChildren().add(textFieldControl);

        return hbox;
    }

    private Node createListControl() {
        Button button = new Button("\u2795");
        button.getStyleClass().add("normalButton");
        button.setOnAction(event -> {
            ConfigNode newChild = this.configNode.addListItem();
            yamlUpdateCallback.run();
            TreeItem treeItem = new ConfigTreeItem(newChild, yamlUpdateCallback, treeConfigUpdateCallback);
            this.getChildren().add(treeItem);
            this.setExpanded(true);
        });

        return button;
    }

    private Node createCheckBoxControl() {
        CheckBox checkboxUse = new CheckBox("Use");
        CheckBox checkboxValue = new CheckBox("Value");

        checkboxValue.disableProperty().bind(checkboxUse.selectedProperty().not());

        if (configNode.getValue() != null) {
            checkboxUse.setSelected(true);
            checkboxValue.setSelected((Boolean)configNode.getValue());
        } else {
            checkboxUse.setSelected(configNode.isMandatory());
            checkboxValue.setSelected(configNode.getDefaultValue().equals("true"));
        }

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

        return hbox;
    }

    private TextField createTextFieldControl() {
        TextField textfield = new TextField();
        if (Util.isNullOrEmpty(configNode.getDefaultValue())) {
            textfield.setPromptText(configNode.getType().name());
        } else {
            textfield.setPromptText(configNode.getDefaultValue());
        }

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

        return textfield;
    }


    private ComboBox createEnumControl() {
        ComboBox<Object> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableList(configNode.getValues()));
        if (configNode.getValue() != null && configNode.getValues().contains(configNode.getValue())) {
            comboBox.getSelectionModel().select(configNode.getValue());
        } else {
            comboBox.getSelectionModel().select("Not selected");
        }
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (configNode.getValues().contains(newValue)) {
                configNode.setValue(newValue);
            } else {
                configNode.setValue("Not selected");
            }
            updateItemStyle();
            yamlUpdateCallback.run();
        });

        return comboBox;
    }
}
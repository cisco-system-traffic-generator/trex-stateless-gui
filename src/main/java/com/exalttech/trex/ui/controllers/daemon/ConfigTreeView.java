package com.exalttech.trex.ui.controllers.daemon;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

class ConfigTreeView extends TreeView {
    private UserConfigModel userConfigModel;
    private Runnable yamlUpdateCallback;

    public ConfigTreeView(UserConfigModel metadata, Runnable yamlUpdateCallback) {

        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);

        this.userConfigModel = metadata;
        this.yamlUpdateCallback = yamlUpdateCallback;

        rebuildFromValueData();
    }

    public void rebuildFromValueData() {
        this.setRoot(new TreeItem("TRex config"));

        for (ConfigNode configNode : this.userConfigModel.getValueFields()) {
            TreeItem newItem = new ConfigTreeItem(configNode, yamlUpdateCallback, this::rebuildFromValueData);
            this.getRoot().getChildren().add(newItem);
        }

        this.getRoot().setExpanded(true);
    }
}


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
package com.exalttech.trex.ui.components;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Custom tree item implementation
 *
 * @author Georgekh
 */
public class CustomTreeItem extends TreeItem<Object> {

    private String returnedValue = "";
    private TreeItemType treeItemType;
    private ContextMenu menu;
    Label itemTitle = new Label();
    Label owner;

    /**
     *
     * @param title
     * @param owner
     * @param addIcon
     * @param itemType
     */
    public CustomTreeItem(String title, String owner, String addIcon, TreeItemType itemType) {
        this.treeItemType = itemType;
        buildItem(title, owner, addIcon);
    }

    /**
     *
     * @param title
     * @param itemType
     */
    public CustomTreeItem(String title, TreeItemType itemType) {
        this.treeItemType = itemType;
        buildDefaultItem(title);
    }

    /**
     *
     * @param title
     * @param itemType
     * @param menu
     */
    public CustomTreeItem(String title, TreeItemType itemType, ContextMenu menu) {
        this.treeItemType = itemType;
        this.menu = menu;
        buildItemWithMenu(title);
    }

    /**
     * build default tree item
     *
     * @param title
     */
    private void buildDefaultItem(String title) {
        Image itemIcon = new Image("/icons/" + treeItemType.getIcon());
        ImageView itemIconContainer = new ImageView(itemIcon);
        setValue(title);
        setGraphic(itemIconContainer);
    }

    /**
     * build default tree item
     *
     * @param title
     */
    private void buildItemWithMenu(String title) {
        Image itemIcon = new Image("/icons/" + treeItemType.getIcon());
        ImageView itemIconContainer = new ImageView(itemIcon);
        itemTitle = new Label(title);
        itemTitle.getStyleClass().add("treeItemTitle");
        setValue(itemTitle);
        setGraphic(itemIconContainer);
    }

    /**
     * Return treeItemType
     *
     * @return
     */
    public TreeItemType getTreeItemType() {
        return treeItemType;
    }

    /**
     * Set treeItemType
     *
     * @param treeItemType
     */
    public void setTreeItemType(TreeItemType treeItemType) {
        this.treeItemType = treeItemType;
    }

    /**
     * Set menu
     *
     * @param menu
     */
    public void setMenu(ContextMenu menu) {
        this.menu = menu;
    }

    /**
     * build custom tree item
     *
     * @param title
     * @param owner
     * @param assignedText
     * @param addIcon
     */
    private void buildItem(String title, String owner, String addIcon) {
        GridPane itemContainer = new GridPane();

        // additioanl icon
        if (addIcon != null) {
            Image icon = new Image("/icons/" + addIcon);
            ImageView iconContainer = new ImageView(icon);
            itemContainer.add(iconContainer, 0, 0);
        }

        itemTitle.setText(title);
        itemTitle.getStyleClass().add("treeItemTitle");
        itemContainer.add(itemTitle, 1, 0);

        if (owner != null && !"".equals(owner)) {
            this.owner = new Label("(" + owner + ")");
            this.owner.getStyleClass().add("treeItemChildText");
            itemContainer.add(this.owner, 2, 0);
        }
        Image itemIcon = new Image("/icons/" + treeItemType.getIcon());
        ImageView itemIconContainer = new ImageView(itemIcon);

        setValue(itemContainer);
        setGraphic(itemIconContainer);
    }

    /**
     * Update tree item
     *
     * @param title
     * @param owner
     * @param icon
     */
    public void updateItemValue(String title, String owner, String icon) {
        buildItem(title, owner, icon);
    }

    /**
     * return selected item text
     *
     * @return
     */
    public String getReturnedValue() {
        if (returnedValue == null) {
            returnedValue = "";
        }
        return returnedValue;
    }

    /**
     * set selected item text
     *
     * @param returnedValue
     */
    public void setReturnedValue(String returnedValue) {
        this.returnedValue = returnedValue;
    }

    /**
     * Set tooltip
     *
     * @param tooltipText
     */
    public void setTooltipText(String tooltipText) {
        if (tooltipText != null) {
            itemTitle.setTooltip(new Tooltip(tooltipText));
        }
    }

    /**
     * Show right click menu
     */
    public void showMenu() {
        if (menu != null && itemTitle != null) {
            itemTitle.setContextMenu(menu);
            if (owner != null) {
                owner.setContextMenu(menu);
            }
        }
    }

    /**
     * Tree item type
     */
    public enum TreeItemType {

        /**
         *
         */
        DEVICES("card.png"),
        /**
         *
         */
        GLOBAL_STAT("system.png"),
        /**
         *
         */
        PORT("port.png"),
        /**
         *
         */
        PORT_PROFILE("binary_sheet.png"),
        /**
         *
         */
        PORT_PROFILE_STATS("profileStat.png"),
        /**
         *
         */
        PORT_STATS("portStat.png"),
        /**
         *
         */
        TUI("portStat.png");

        String icon;

        private TreeItemType(String icon) {
            this.icon = icon;
        }

        /**
         *
         * @return
         */
        public String getIcon() {
            return icon;
        }

    }
}

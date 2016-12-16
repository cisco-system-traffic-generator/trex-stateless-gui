/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.streamtable;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * Enumerator that present stream table action type
 * @author Georgekh
 */
public enum StreamTableAction {
    BUILD("Build Stream", "add.png"),
    EDIT("Edit Stream", "edit.png"),
    DELETE("Delete Stream", "delete.png"),
    IMPORT_PCAP("Import Pcap", "import_icon.png"),
    EXPORT_TO_PCAP("Export Pcap", "export_profile_icon.png"),
    EXPORT_TO_YAML("Export To Yaml", "export_profile_icon.png");
    
    String title;
    String icon;
    
    private StreamTableAction(String title, String icon){
        this.title= title;
        this.icon = icon;
    }

    /**
     * Return title
     * @return 
     */
    public String getTitle() {
        return title;
    }

    /**
     * Return icon
     * @return 
     */
    public ImageView getIcon() {
        return new ImageView(new Image("/icons/"+icon));
    }
    
    
}

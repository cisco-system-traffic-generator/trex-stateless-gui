/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.streams.builder;

/**
 * Enumerator presents IPV4 type
 * @author Georgekh
 */
public enum IPV4Type {
    FIXED("Fixed"),
    INCREMENT_HOST("Increment Host"),
    DECREMENT_HOST("Decrement Host"),
    RANDOM_HOST("Random Host");

    String title;

    /**
     * Constructor
     *
     * @param title
     */
    private IPV4Type(String title) {
        this.title = title;
    }

    /**
     * Return displayed type title
     *
     * @return
     */
    public String getTitle() {
        return title;
    }
}

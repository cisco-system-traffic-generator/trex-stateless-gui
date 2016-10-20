/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.streams.builder;

/**
 * Cache size model
 * @author GeorgeKH
 */
public class CacheSize {

    CacheSizeType type;
    int cacheValue;

    /**
     * Constructor set AUto as default type
     */
    public CacheSize(){
        type = CacheSizeType.AUTO;
        cacheValue = 5000;
    }
    
    /**
     * Return cache size type
     * @return 
     */
    public CacheSizeType getType() {
        return type;
    }

    /**
     * Set cache size type
     * @param type 
     */
    public void setType(CacheSizeType type) {
        this.type = type;
    }

    /**
     * Return cache size value
     * @return 
     */
    public int getCacheValue() {
        if (type == CacheSizeType.AUTO) {
            return 5000;
        }
        return cacheValue;
    }

    /**
     * Set cache size value
     * @param cacheValue 
     */
    public void setCacheValue(int cacheValue) {
        this.cacheValue = cacheValue;
    }

    /**
     * Enumerator presents cache size type
     */
    public enum CacheSizeType {
        AUTO("Auto"),
        ENABLE("Enable"),
        DISABLE("Disable");
        private String title;

        private CacheSizeType(String title) {
            this.title = title;
        }

        /**
         * Return displayed text
         * @return 
         */
        public String getTitle() {
            return title;
        }

        /**
         * Return cache size type from title
         * @param type
         * @return 
         */
        public static CacheSizeType getCacheSizeType(String type) {
            return CacheSizeType.valueOf(type.toUpperCase());
        }
    }
}

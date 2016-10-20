/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.streams.binders;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Cache size data binding model
 * @author GeorgeKH
 */
public class AdvancedPropertiesDataBinding extends AbstractStreamDataBinding implements Externalizable{

    StringProperty cacheSizeType = new SimpleStringProperty();
    StringProperty cacheValue = new SimpleStringProperty();

    /**
     * Constructor
     */
    public AdvancedPropertiesDataBinding(){
         setInitialValues();
    }

    /**
     * Return cache size type
     * @return 
     */
    public StringProperty getCacheSizeType() {
        return cacheSizeType;
    }

    /**
     * Set cache size type
     * @param cacheSizeType 
     */
    public void setCacheSizeType(StringProperty cacheSizeType) {
        this.cacheSizeType = cacheSizeType;
    }

    /**
     * Return cache value
     * @return 
     */
    public StringProperty getCacheValue() {
        return cacheValue;
    }

    /**
     * Set cache value
     * @param cacheValue 
     */
    public void setCacheValue(StringProperty cacheValue) {
        this.cacheValue = cacheValue;
    }
    
    /**
     * Set initial values
     */
    @Override
    protected void setInitialValues() {
        cacheSizeType.set("Auto");
        cacheValue.set("5000");
    }

     /**
     * Write serialized property values
     *
     * @param out
     * @throws IOException
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(cacheSizeType.get());
        out.writeObject(cacheValue.get());
    }

    /**
     * Read serialized property values
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        cacheSizeType.set((String) in.readObject());
        cacheValue.set((String) in.readObject());
    }
}

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class AdvancedPropertiesDataBinding extends AbstractStreamDataBinding implements Externalizable{
    @JsonIgnore
    private StringProperty cacheSizeTypeProperty = new SimpleStringProperty();
    @JsonIgnore
    private StringProperty cacheValueProperty = new SimpleStringProperty();

    public AdvancedPropertiesDataBinding(){
         setInitialValues();
    }

    @JsonIgnore
    public StringProperty getCacheSizeTypeProperty() {
        return cacheSizeTypeProperty;
    }

    @JsonIgnore
    public void setCacheSizeTypeProperty(StringProperty cacheSizeTypeProperty) {
        this.cacheSizeTypeProperty = cacheSizeTypeProperty;
    }

    @JsonIgnore
    public StringProperty getCacheValueProperty() {
        return cacheValueProperty;
    }

    @JsonIgnore
    public void setCacheValueProperty(StringProperty cacheValueProperty) {
        this.cacheValueProperty = cacheValueProperty;
    }

    @JsonProperty("cache_size_type")
    public String getCacheSizeType() {
        return cacheSizeTypeProperty.get();
    }

    @JsonProperty("cache_size_type")
    public void setCacheSizeType(final String cacheSizeType) {
        cacheSizeTypeProperty.set(cacheSizeType);
    }

    @JsonProperty("cache_value")
    public String getCacheValue() {
        return cacheValueProperty.get();
    }

    @JsonProperty("cache_value")
    public void setCacheValue(final String cacheValue) {
        cacheValueProperty.set(cacheValue);
    }
    
    @Override
    protected void setInitialValues() {
        cacheSizeTypeProperty.set("Auto");
        cacheValueProperty.set("5000");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(cacheSizeTypeProperty.get());
        out.writeObject(cacheValueProperty.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        cacheSizeTypeProperty.set((String) in.readObject());
        cacheValueProperty.set((String) in.readObject());
    }
}

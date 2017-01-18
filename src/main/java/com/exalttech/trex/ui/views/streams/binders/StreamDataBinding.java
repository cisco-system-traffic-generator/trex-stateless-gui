package com.exalttech.trex.ui.views.streams.binders;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class StreamDataBinding implements Externalizable {
    private BooleanProperty srcMacModeProperty = new SimpleBooleanProperty(false); // true - packet, false - TRex config
    private BooleanProperty dstMacModeProperty = new SimpleBooleanProperty(false); // true - packet, false - TRex config

    public BooleanProperty srcMacModePropertyProperty() {
        return srcMacModeProperty;
    }

    public BooleanProperty dstMacModePropertyProperty() {
        return dstMacModeProperty;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(srcMacModeProperty.get());
        out.writeBoolean(dstMacModeProperty.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        srcMacModePropertyProperty().set(in.readBoolean());
        dstMacModePropertyProperty().set(in.readBoolean());
    }
}

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
/*



 */
package com.exalttech.trex.ui.views.streams;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Georgekh
 */
public class PayloadDataBinding extends AbstractStreamDataBinding implements Externalizable {

    StringProperty type = new SimpleStringProperty();
    StringProperty pattern = new SimpleStringProperty();

    /**
     * Constructor
     */
    public PayloadDataBinding() {
        setInitialValues();
    }

    /**
     * Return type property
     *
     * @return
     */
    public StringProperty getType() {
        return type;
    }

    /**
     * Set type property
     *
     * @param type
     */
    public void setType(StringProperty type) {
        this.type = type;
    }

    /**
     * Return pattern property
     *
     * @return
     */
    public StringProperty getPattern() {
        return pattern;
    }

    /**
     * Set pattern property
     *
     * @param pattern
     */
    public void setPattern(StringProperty pattern) {
        this.pattern = pattern;
    }

    /**
     * Initialize property values
     */
    @Override
    protected void setInitialValues() {
        type.set("Fixed Word");
        pattern.set("00");
    }

    /**
     * Write serialized property values
     *
     * @param out
     * @throws IOException
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(type.get());
        out.writeObject(pattern.get());
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
        type.set((String) in.readObject());
        pattern.set((String) in.readObject());
    }

}

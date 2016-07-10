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
package com.exalttech.trex.core;

import com.exalttech.trex.remote.models.AsyncEvent;
import com.exalttech.trex.ui.views.logs.LogType;
import com.exalttech.trex.ui.views.logs.LogsController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apache.log4j.Logger;

/**
 *
 * @author Georgekh
 */
public class AsyncResponseManager {

    private static final Logger LOG = Logger.getLogger(AsyncResponseManager.class.getName());
    private static AsyncResponseManager instance = null;

    /**
     *
     * @return
     */
    public static AsyncResponseManager getInstance() {
        if (instance == null) {
            instance = new AsyncResponseManager();
        }
        return instance;
    }
    private StringProperty trexGlobalResponse = new SimpleStringProperty();
    private StringProperty trexEventValueProperty = new SimpleStringProperty();
    private StringProperty trexLatencyProperty = new SimpleStringProperty();
    private BooleanProperty trexEventProperty = new SimpleBooleanProperty();

    /**
     * Constructor
     */
    protected AsyncResponseManager() {
        initializeDataBinding();
    }

    /**
     * Return trex global property
     *
     * @return
     */
    public StringProperty getTrexGlobalProperty() {
        return trexGlobalResponse;
    }

    /**
     * Set trex global response
     *
     * @param trexGlobalResponse
     */
    public void setTrexGlobalResponse(String trexGlobalResponse) {
        this.trexGlobalResponse.set(trexGlobalResponse);
    }

    /**
     * Set event value
     *
     * @param val
     */
    public void setTRexEventValue(String val) {
        this.trexEventValueProperty.setValue(val);
    }

    /**
     * Return event property
     *
     * @return
     */
    public BooleanProperty getTrexEventProperty() {
        return trexEventProperty;
    }

    /**
     * Set trex latency response
     *
     * @param trexLatencyResponse
     */
    public void setTrexLatencyProperty(String trexLatencyResponse) {
        this.trexLatencyProperty.set(trexLatencyResponse);
    }

    /**
     * Return trex latency property
     *
     * @return
     */
    public StringProperty getTrexLatencyProperty() {
        return trexLatencyProperty;
    }

    /**
     *
     * @param event
     */
    public void handleAsyncEvent(String event) {
        if (!event.contains("trex-event")) {
            return;
        }
        trexEventProperty.set(!trexEventProperty.get());
        // Update Port status
        ObjectMapper mapper = new ObjectMapper();
        try {
            AsyncEvent serverEvent = mapper.readValue(event, AsyncEvent.class);
            String eventMessage = serverEvent.toString();
            if (eventMessage != null) {
                LogsController.getInstance().appendText(LogType.SERVER_EVENT, eventMessage);
            }
        } catch (IOException ex) {
            LOG.error("Error handling async event", ex);
        }
    }

    /**
     * Listening to changes on TRex event response property
     */
    public void initializeDataBinding() {
        trexEventValueProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                handleAsyncEvent(newValue);
            }
        });

    }
}

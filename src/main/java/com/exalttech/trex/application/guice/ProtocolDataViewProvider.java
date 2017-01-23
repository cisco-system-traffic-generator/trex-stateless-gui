package com.exalttech.trex.application.guice;

import com.exalttech.trex.ui.views.streams.builder.ProtocolDataView;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class ProtocolDataViewProvider implements Provider<ProtocolDataView> {

    @Inject
    Injector injector;
    
    @Override
    public ProtocolDataView get() {
        return new ProtocolDataView(injector.getInstance(EventBus.class));
    }
}

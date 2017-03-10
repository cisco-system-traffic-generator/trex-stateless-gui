package com.exalttech.trex.application.guice;

import com.exalttech.trex.ui.components.GlobalPortFilter;
import com.exalttech.trex.ui.views.streams.builder.ProtocolDataView;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class StatelessGUIModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(EventBus.class).in(Singleton.class);
        bind(GlobalPortFilter.class).in(Singleton.class);
        bind(ProtocolDataView.class).toProvider(ProtocolDataViewProvider.class);
    }
}

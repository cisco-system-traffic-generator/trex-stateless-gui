package com.exalttech.trex.application.guice;

import com.cisco.trex.stateless.util.IDataCompressor;
import com.cisco.trex.stateless.util.TRexDataCompressor;
import com.cisco.trex.stl.gui.storages.StatsStorage;
import com.cisco.trex.stl.gui.util.RunningConfiguration;
import com.exalttech.trex.core.RPCMethods;
import com.exalttech.trex.ui.views.streams.builder.ProtocolDataView;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class StatelessGUIModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(EventBus.class).in(Singleton.class);
        bind(RPCMethods.class).in(Singleton.class);
        bind(RunningConfiguration.class).in(Singleton.class);
        bind(ProtocolDataView.class).toProvider(ProtocolDataViewProvider.class);
        bind(StatsStorage.class).in(Singleton.class);
        bind(IDataCompressor.class).to(TRexDataCompressor.class);
    }
}

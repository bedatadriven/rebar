package com.bedatadriven.rebar.style.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import org.omg.PortableInterceptor.INACTIVE;

public class TestEntryPoint implements EntryPoint {


    @Override
    public void onModuleLoad() {

        TestIconSet.INSTANCE.ensureInjected();

        RootPanel.get().add(new TestPanel());
    }
}

package com.bedatadriven.rebar.style.rebind.passes;

import com.bedatadriven.rebar.style.client.Stylesheet;


public interface TestStylesheet extends Stylesheet {

    String containerBlue();

    @ClassName("alert-bg")
    String alertBackground();

    String importantWidget();

    String emptyClass();
}

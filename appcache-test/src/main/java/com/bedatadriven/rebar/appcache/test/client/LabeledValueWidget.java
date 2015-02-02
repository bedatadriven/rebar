package com.bedatadriven.rebar.appcache.test.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;


public class LabeledValueWidget implements IsWidget {
  
  private static final TestBundle BUNDLE = GWT.create(TestBundle.class);

  private FlowPanel flowPanel = new FlowPanel();
  private InlineLabel valueLabel;

  public LabeledValueWidget(ElementId elementId, String value) {
    
    BUNDLE.style().ensureInjected();
    
    this.valueLabel = new InlineLabel(value);
    this.valueLabel.getElement().setId(elementId.id());

    InlineLabel label = new InlineLabel(elementId.name() + ": ");
    label.setStyleName(BUNDLE.style().label());
    
    this.flowPanel = new FlowPanel();
    this.flowPanel.setStyleName(BUNDLE.style().labeledValueWidget());
    this.flowPanel.add(label);
    this.flowPanel.add(valueLabel);
  }
  
  public Button addButton(ElementId elementId, ClickHandler handler) {
    Button button = new Button();
    button.setText(elementId.name());
    button.getElement().setId(elementId.id());
    button.addClickHandler(handler);
    this.flowPanel.add(button);
    return button;
  }
  
  public void setValue(String value) {
    this.valueLabel.setText(value);
  }

  @Override
  public Widget asWidget() {
    return flowPanel;
  }
}

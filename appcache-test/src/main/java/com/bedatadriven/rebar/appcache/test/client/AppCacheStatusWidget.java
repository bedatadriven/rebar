package com.bedatadriven.rebar.appcache.test.client;

import com.bedatadriven.rebar.appcache.client.AppCache;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays the AppCache's current status
 */
public class AppCacheStatusWidget implements IsWidget {

  private LabeledValueWidget widget;
  private AppCache appCache;

  public AppCacheStatusWidget(final AppCache appCache) {
    this.appCache = appCache;
    this.widget = new LabeledValueWidget(ElementId.APP_CACHE_STATUS, "-");
    Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
      @Override
      public boolean execute() {
        widget.setValue(appCache.getStatus().name());
        return true;
      }
    }, 50);
  }

  @Override
  public Widget asWidget() {
    return widget.asWidget();
  }
}

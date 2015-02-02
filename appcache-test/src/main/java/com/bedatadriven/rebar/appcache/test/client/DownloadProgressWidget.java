package com.bedatadriven.rebar.appcache.test.client;

import com.bedatadriven.rebar.appcache.client.AppCache;
import com.bedatadriven.rebar.appcache.client.events.ProgressEventHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays the progress of the AppCache download
 */
public class DownloadProgressWidget implements IsWidget {
  
  private FlowPanel panel;
  private LabeledValueWidget completeLabel;
  private LabeledValueWidget totalLabel;


  public DownloadProgressWidget(AppCache appCache) {
    completeLabel = new LabeledValueWidget(ElementId.PROGRESS_FILES_COMPLETE, "");
    totalLabel = new LabeledValueWidget(ElementId.PROGRESS_FILES_TOTAL, "");
    panel = new FlowPanel();
    panel.add(completeLabel);
    panel.add(totalLabel);
    
    appCache.addProgressHandler(new ProgressEventHandler() {
      @Override
      public void onProgress(int filesComplete, int filesTotal) {
        completeLabel.setValue(Integer.toString(filesComplete));
        totalLabel.setValue(Integer.toString(filesTotal));
      }
    });
  }

  @Override
  public Widget asWidget() {
    return panel;
  }
}

/*
 * Copyright 2009-2010 BeDataDriven (alex@bedatadriven.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.bedatadriven.rebar.appcache.test.client;

import com.bedatadriven.rebar.appcache.client.AppCache;
import com.bedatadriven.rebar.appcache.client.AppCacheFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.useragent.client.UserAgent;

import java.util.logging.Logger;

public class TestPanel implements IsWidget {
  
  private static final Logger LOGGER = Logger.getLogger(TestPanel.class.getName());

  private final AppCache appCache = AppCacheFactory.get();
  private final UserAgent userAgent = GWT.create(UserAgent.class);
  private final FlowPanel panel;


  public TestPanel(AppVersion applicationVersion) {
    panel = new FlowPanel();
    panel.add(new LabeledValueWidget(ElementId.VERSION_LABEL, applicationVersion.name()));
    panel.add(new LabeledValueWidget(ElementId.COMPILE_USER_AGENT, userAgent.getCompileTimeValue()));
    panel.add(new LabeledValueWidget(ElementId.RUNTIME_USER_AGENT, userAgent.getRuntimeValue()));
    panel.add(new AppCacheStatusWidget(appCache));
    panel.add(new DownloadProgressWidget(appCache));
    panel.add(new AsyncFragmentWidget());
    panel.add(new UpdateAppWidget(appCache));
  }

  @Override
  public Widget asWidget() {
    return panel;
  }

  //
//  private void addRemoveButton() {
//    panel.add(new HTML("<br>"));
//    panel.add(new Button("Remove cache", new ClickHandler() {
//
//      @Override
//      public void onClick(ClickEvent event) {
//        appCache.removeCache(new AsyncCallback<Void>() {
//
//          @Override
//          public void onSuccess(Void result) {
//            Window.alert("Cache successfully removed");
//          }
//
//          @Override
//          public void onFailure(Throwable caught) {
//            Window.alert("Failed to remove cache: " + caught.getMessage());
//          }
//        });
//      }
//    }));
//  }
}

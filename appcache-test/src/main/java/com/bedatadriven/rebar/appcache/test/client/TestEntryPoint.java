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
import com.bedatadriven.rebar.appcache.client.event.ProgressEvent;
import com.bedatadriven.rebar.appcache.client.event.ProgressHandler;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

public class TestEntryPoint implements EntryPoint {

  private int currentPhotoIndex = 1;

  @Override
  public void onModuleLoad() {

    final AppCache appCache = AppCacheFactory.get();

    FlowPanel panel = new FlowPanel();
    Label label = new Label("Hello offline world");
    panel.add(label);

    Greeter greeter = GWT.create(Greeter.class);
    panel.add(new Label(greeter.greet()));


    final Image picture = new Image();
    picture.setUrl(getCurrentPhotoURL());
    panel.add(picture);

    Button nextPhotoButton = new Button("Next photo", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        currentPhotoIndex ++;
        picture.setUrl(getCurrentPhotoURL());

      }
    });
    panel.add(nextPhotoButton);

    Button cacheButton = new Button("Make available offline", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        appCache.ensureCached(new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable caught) {
            Window.alert("Failed to cache app: " + caught.getMessage());
          }

          @Override
          public void onSuccess(String result) {
            Window.alert("AppCache successfully updated to version " + result);
          }
        });
      }
    });
    panel.add(cacheButton);

    final Label cacheStatusLabel = new Label("Cache Status: " + appCache.getStatus().toString());
    panel.add(cacheStatusLabel);

    appCache.addProgressHandler(new ProgressHandler() {
      @Override
      public void onProgress(ProgressEvent event) {
        cacheStatusLabel.setText("Downloaded " + event.getFilesComplete() + " of " +
          event.getFilesTotal());
      }
    });



    RootPanel.get().add(panel);
  }

  private String getCurrentPhotoURL() {
    return GWT.getModuleBaseURL() + "images/photo" + currentPhotoIndex + ".JPG";
  }
}

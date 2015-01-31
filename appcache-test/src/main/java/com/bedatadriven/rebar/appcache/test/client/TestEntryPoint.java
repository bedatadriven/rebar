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
import com.bedatadriven.rebar.appcache.client.AppCache.Status;
import com.bedatadriven.rebar.appcache.client.AppCacheFactory;
import com.bedatadriven.rebar.appcache.client.events.ProgressEventHandler;
import com.bedatadriven.rebar.appcache.client.events.UpdateReadyEventHandler;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

public class TestEntryPoint implements EntryPoint {

  private int currentPhotoIndex = 1;
  private FlowPanel panel;
  private AppCache appCache;

  @Override
  public void onModuleLoad() {

    appCache = AppCacheFactory.get();

    panel = new FlowPanel();

    addGreeting();
    addPhotoAlbum();
    addStatusMonitor();
    addUpdateButton();
    addCheckForUpdatesButton();
    listenForUpdates();
    addProgressHandler();
    addRunAsyncUI();
    addRemoveButton();

    RootPanel.get().add(panel);
  }


  private void addGreeting() {
    Label label = new Label("Hello brave world, courtesy of " +
        appCache.getImplementation());
    panel.add(label);

    Greeter greeter = GWT.create(Greeter.class);
    panel.add(new Label(greeter.greet()));
  }

  private void addPhotoAlbum() {
    panel.add(new HTML("<h1>Photo Album</h1>"));

    final Image picture = new Image();
    picture.setUrl(getCurrentPhotoURL());
    panel.add(picture);

    panel.add(new HTML("<br>"));

    Button nextPhotoButton = new Button("Next photo", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        currentPhotoIndex++;
        picture.setUrl(getCurrentPhotoURL());

      }
    });
    panel.add(nextPhotoButton);
  }

  private void addStatusMonitor() {
    panel.add(new HTML("<h1>Offline management</h1>"));

    Button cacheButton = new Button("Ensure Cached", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        appCache.ensureCached(new AsyncCallback<Void>() {
          @Override
          public void onFailure(Throwable caught) {
            Window.alert("Failed to cache app: " + caught.getMessage());
          }

          @Override
          public void onSuccess(Void result) {
            Window.alert("App ready to serve offline!");
          }
        });
      }
    });
    panel.add(cacheButton);

    final Label cacheStatus = new Label("");
    panel.add(cacheStatus);

    new Timer() {
      @Override
      public void run() {
        cacheStatus.setText(appCache.getStatus().toString());
      }
    }.scheduleRepeating(1000);
  }

  private void addCheckForUpdatesButton() {
    panel.add(new Button("Check For Updates", new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        appCache.checkForUpdate();
      }
    }));
  }

  private void addUpdateButton() {
    panel.add(new Button("Ensure up to date", new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        appCache.ensureUpToDate(new AsyncCallback<Void>() {

          @Override
          public void onSuccess(Void result) {
            if (appCache.getStatus() == Status.UPDATE_READY) {
              Window.alert("The update has been downloaded. Click ok to refresh");
              Window.Location.reload();
            } else {
              Window.alert("Cache is already up to date!");
            }
          }

          @Override
          public void onFailure(Throwable caught) {
            Window.alert("error updating" + caught.getMessage());
          }
        });
      }
    }));
  }

  private void addRunAsyncUI() {
    panel.add(new HTML("<h1>Async Fragments</h1>"));

    Button asyncButton = new Button("Run Async", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        GWT.runAsync(new RunAsyncCallback() {
          @Override
          public void onFailure(Throwable reason) {
            Window.alert("Failed to load code fragment");
          }

          @Override
          public void onSuccess() {
            Window.alert("Code fragment loaded!");
          }
        });
      }
    });
    panel.add(asyncButton);
  }

  private String getCurrentPhotoURL() {
    return GWT.getModuleBaseURL() + "images/photo" + currentPhotoIndex + ".JPG";
  }


  private void addProgressHandler() {
    final Label progressLabel = new Label("No progress yet");
    panel.add(progressLabel);

    appCache.addProgressHandler(new ProgressEventHandler() {

      @Override
      public void onProgress(int filesComplete, int filesTotal) {
        progressLabel.setText(filesComplete + " / " + filesTotal);
      }
    });
  }


  private void listenForUpdates() {
    appCache.addUpdateReadyHandler(new UpdateReadyEventHandler() {

      @Override
      public void onAppCacheUpdateReady() {
        Window.alert("An update is ready, click OK to refresh");
        Window.Location.reload();
      }
    });
  }


  private void addRemoveButton() {
    panel.add(new HTML("<br>"));
    panel.add(new Button("Remove cache", new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        appCache.removeCache(new AsyncCallback<Void>() {

          @Override
          public void onSuccess(Void result) {
            Window.alert("Cache successfully removed");
          }

          @Override
          public void onFailure(Throwable caught) {
            Window.alert("Failed to remove cache: " + caught.getMessage());
          }
        });
      }
    }));
  }
}

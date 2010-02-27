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

package com.bedatadriven.rebar;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.junit.JUnitShell;
import com.google.gwt.junit.RunStyleHtmlUnit;

/**
 * GWT junit RunStyle that runs test using htmlunit in IE7 mode with ActiveX controls
 * enabled, allowing tests to run against the actual gears plugin on windows platforms.
 *
 * To use, include the following argument to your test runner:
 *
 * -Dgwt.args="-runstyle com.bedatadriven.rebar.HtmlUnitIE7Gears"
 *
 */
public class HtmlUnitIE7Gears extends RunStyleHtmlUnit {
  private boolean developmentMode;

  public HtmlUnitIE7Gears(JUnitShell shell) {
    super(shell);
  }

  protected static class GearsEnabledHtmlUnitThread extends HtmlUnitThread {

    public GearsEnabledHtmlUnitThread(String url, TreeLogger treeLogger, boolean developmentMode) {
      super(BrowserVersion.INTERNET_EXPLORER_7, url, treeLogger, developmentMode);
    }

    @Override
    protected void setupWebClient(WebClient webClient) {
      super.setupWebClient(webClient);
      webClient.setActiveXNative(true);
      webClient.setAlertHandler(new AlertHandler() {
        @Override
        public void handleAlert(Page page, String s) {
          System.out.println(s);
        }
      });
    }
  }

  @Override
  public int initialize(String args) {
    return super.initialize(BrowserVersion.INTERNET_EXPLORER_7.getNickname());
  }

  @Override
  public boolean setupMode(TreeLogger logger, boolean developmentMode) {
    this.developmentMode = developmentMode;
    return super.setupMode(logger, this.developmentMode);
  }

  @Override
  protected HtmlUnitThread createHtmlUnitThread(BrowserVersion browser, String url) {
    return new GearsEnabledHtmlUnitThread(url, shell.getTopLogger().branch(
        TreeLogger.SPAM, "logging for HtmlUnit thread"), developmentMode);
  }
}

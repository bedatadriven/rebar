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

package com.bedatadriven.rebar.worker.client.rpc;

import com.google.gwt.gears.client.httprequest.HttpRequest;
import com.google.gwt.http.client.Request;

/**
 * Adapts the Gear's Request object to the GWT Request API.
 * This allows us to play nicely with some of the GWT HTTP tools.
 *
 * @author Alex Bertram
 */
public class RequestAdapter extends Request {

  private final HttpRequest httpRequest;

  public RequestAdapter(HttpRequest httpRequest) {
    this.httpRequest = httpRequest;
  }

  @Override
  public void cancel() {
    httpRequest.abort();
  }

  @Override
  public boolean isPending() {
    return httpRequest.getReadyState() != 4;
  }
}

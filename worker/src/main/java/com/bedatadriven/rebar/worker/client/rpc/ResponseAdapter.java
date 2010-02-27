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
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * Adapts the Gear's API to the GWT Response object.
 *
 * @author Alex Bertram
 */
public class ResponseAdapter extends Response {

  private final HttpRequest httpRequest;

  private class HeaderAdapter extends Header {

    private String name;
    private String value;

    private HeaderAdapter(String name, String value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public String getValue() {
      return null;
    }
  }

  public ResponseAdapter(HttpRequest httpRequest) {
    this.httpRequest = httpRequest;
  }

  @Override
  public String getHeader(String header) {
    return httpRequest.getResponseHeader(header);
  }

  @Override
  public Header[] getHeaders() {
    String[] lines = httpRequest.getAllResponseHeaders().split("\n\r");
    Header[] headers = new Header[lines.length];
    for (int i = 0; i != lines.length; ++i) {
      int semiIndex = lines[i].indexOf(':');
      headers[i] = new HeaderAdapter(
          lines[i].substring(0, semiIndex - 1),
          lines[i].substring(semiIndex + 1).trim());
    }
    return headers;
  }

  @Override
  public String getHeadersAsString() {
    return httpRequest.getAllResponseHeaders();
  }

  @Override
  public int getStatusCode() {
    return httpRequest.getStatus();
  }

  @Override
  public String getStatusText() {
    return httpRequest.getStatusText();
  }

  @Override
  public String getText() {
    return httpRequest.getResponseText();
  }
}

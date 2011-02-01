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

package com.bedatadriven.rebar.appcache.server;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgentProvider implements PropertyProvider {

  private final Pattern geckoRevision;

  public UserAgentProvider() {
    geckoRevision = Pattern.compile("rv:([0-9]+)\\.([0-9]+)");
  }

  @Override
  public String get(HttpServletRequest request) {
    String ua = request.getHeader("User-Agent").toLowerCase();

    if (ua.contains("opera")) {
      return "opera";
    } else if (ua.contains("webkit")) {
      return "safari";
    } else if (ua.contains("msie 8")) {
      return "ie8";
    } else if (ua.contains("msie 7") || ua.contains("msie 6")) {
      return "ie6";
    } else if (ua.indexOf("gecko") != -1) {
      int version = matchVersion(ua);
      if(version >= 1008) {
          return "gecko1_8";
      }
      return "gecko";
    }
    return "unknown";
  }

  private int matchVersion(String ua) {
    Matcher matcher = geckoRevision.matcher(ua);
    if(matcher.matches()) {

      int major = Integer.parseInt(matcher.group(1));
      int minor = Integer.parseInt(matcher.group(2));

      return major * 1000 + minor;
    }
    return 0;
  }
}

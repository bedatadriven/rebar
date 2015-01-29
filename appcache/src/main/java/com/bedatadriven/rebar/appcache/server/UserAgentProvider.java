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
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgentProvider implements PropertyProvider {
	
	private static final Logger LOGGER = Logger.getLogger(UserAgentProvider.class.getName());
	
  private final Pattern geckoRevision;

  public UserAgentProvider() {
    geckoRevision = Pattern.compile("rv:([0-9]+)\\.([0-9]+)");

  }

  @Override
  public String get(HttpServletRequest request) {
    String ua = userAgent(request);

    if (ua.contains("opera")) {
      return "opera";
    } else if (ua.contains("webkit")) {
      return "safari";
    } else if (ua.contains("msie 6") || ua.contains("msie 7")) {
        return "ie6";
    } else if (ua.contains("msie 8")) {
      return "ie8";
    } else if (ua.contains("msie 9")) {
      return "ie9";
    } else if (ua.contains("msie 10") || ua.contains("msie 11")) {
      return "ie10";
    } else if (ua.contains("gecko")) {
      return "gecko1_8";
    }
    
    LOGGER.severe("Cannot match user agent header to supported browser: '" + ua + "'");
    
    throw new UnknownUserAgentException();
  }

	private String userAgent(HttpServletRequest request) {
	  return request.getHeader("User-Agent").toLowerCase();
  }
  
  public boolean isFirefox(HttpServletRequest request) {
  	return isFirefox(userAgent(request));
  }
  
	public boolean isFirefox(String ua) {
	  return ua.contains("gecko");
  }

  private int parseGeckoVersion(String ua) {
    Matcher matcher = geckoRevision.matcher(ua);
    if(matcher.find()) {

      int major = Integer.parseInt(matcher.group(1));
      int minor = Integer.parseInt(matcher.group(2));

      return major * 1000 + minor;
    }
    return 0;
  }
  
  @Deprecated
  public boolean canSupportGears(String ua) {
  	return false;
  }

  @Deprecated
  public boolean canSupportGears(HttpServletRequest req) {
  	return false;
  }
}

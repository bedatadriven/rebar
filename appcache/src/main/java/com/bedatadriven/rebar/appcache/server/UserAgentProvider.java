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
    String ua = userAgent(request);

    if (ua.contains("opera")) {
      return "opera";
    } else if (ua.contains("webkit")) {
      return "safari";
    } else if (ua.contains("msie 8") || ua.contains("msie 9")) {
      return "ie8";
    } else if (ua.contains("msie 7") || ua.contains("msie 6")) {
      return "ie6";
    } else if (ua.contains("gecko")) {
      int version = parseGeckoVersion(ua);
      if(version >= 1008) {
          return "gecko1_8";
      }
      return "gecko";
    }
    throw new UnknownUserAgentException();
  }

	private String userAgent(HttpServletRequest request) {
	  return request.getHeader("User-Agent").toLowerCase();
  }
  
  public boolean isFirefox(HttpServletRequest request) {
  	return isFirefox(userAgent(request));
  }
  
	public boolean isFirefox(String ua) {
	  return ua.indexOf("gecko") != -1;
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
  
  /**
   * 
   * @param ua lowercase User-Agent header
   * @return true if the browser is capable of installing gears
   */
  public boolean canSupportGears(String ua) {
  	if(ua.contains("webkit")) {
  		// earlier versions of chrome could support gears but 
  		// updates happen automatically so we can't really count on gears
  		// be available on chrome for long.
  		return false;
  		
  	} else if(ua.contains("gecko") && ua.contains("rv:")) {
  		// Gears is supported on FireFox versions up to 3.6x
  		return parseGeckoVersion(ua) < 2000;
  		
  	} else if(ua.contains("msie 6") || ua.contains("msie 7") || ua.contains("msie 8")) {
  		// Gears is supported on IE 6-8
  		return true;
  	} else {
  		return false;
  	}
  }
  
  /**
   * 
   * @param req http request
   * @return true if the browser is capable of installing gears
   */
  public boolean canSupportGears(HttpServletRequest req) {
  	return canSupportGears(userAgent(req));
  }
}

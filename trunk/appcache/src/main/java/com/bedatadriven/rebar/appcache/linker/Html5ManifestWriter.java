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

package com.bedatadriven.rebar.appcache.linker;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

class Html5ManifestWriter implements ManifestWriter {

  private StringBuilder entries = new StringBuilder();

  @Override
  public String getSuffix() {
    return "appcache";
  }

  @Override
  public void appendEntry(TreeLogger logger, String path) throws UnableToCompleteException {
    entries.append(escape(logger, path)).append("\n");
  }

  static String escape(TreeLogger logger, String path) throws UnableToCompleteException {
    StringBuilder builder = new StringBuilder(path.length());
    for(int i=0;i!=path.length();++i) {
      int codePoint = path.codePointAt(i);
      if(codePoint > 255) {
        logger.log(TreeLogger.Type.ERROR, "Manifest entry '" + path + "' contains illegal character at index " + i);
        throw new UnableToCompleteException();
      } else {
        char c = path.charAt(i);
        if(isAlphaNum(c) || c == '.' || c == '-' || c == '_') {
          builder.append(c);
        } else if(c == '/' || c == '\\') {
          builder.append('/');
        } else {
          builder.append('%').append(Integer.toHexString(c).toUpperCase());
        }
      }
    }
    return builder.toString();
  }

  private static boolean isAlphaNum(char c) {
    return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c < 'z');
  }

  @Override
  public String getEntries() {
    return entries.toString();
  }
}

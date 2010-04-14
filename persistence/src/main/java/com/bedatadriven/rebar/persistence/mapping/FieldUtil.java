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

package com.bedatadriven.gears.persistence.mapping;

/**
 * @author Alex Bertram
 */
public class FieldUtil {


  /**
   * Returns true if <code>name</code> is a camel-cased name prefixed
   * by <code>prefix</code>. For example, prefixedBy("getCount", "get")
   * returns true, but prefixedBy("getcount","get") returns false.
   *
   * @param name   The name to check
   * @param prefix The prefix
   * @return True if name is camel-cased and starts with <code>prefix</code>
   */
  public static boolean prefixedBy(String name, String prefix) {
    if (!name.startsWith(prefix))
      return false;

    if ((name.length() == prefix.length()) ||
        !Character.isUpperCase(name.charAt(prefix.length()))) {
      return false;
    }

    return true;
  }
}

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

package java.sql;

public enum ClientInfoStatus {

  /**
   * The client info property could not be set for some unknown reason
   *
   * @since 1.6
   */
  REASON_UNKNOWN,

  /**
   * The client info property name specified was not a recognized property
   * name.
   *
   * @since 1.6
   */
  REASON_UNKNOWN_PROPERTY,

  /**
   * The value specified for the client info property was not valid.
   *
   * @since 1.6
   */
  REASON_VALUE_INVALID,

  /**
   * The value specified for the client info property was too large.
   *
   * @since 1.6
   */
  REASON_VALUE_TRUNCATED
}

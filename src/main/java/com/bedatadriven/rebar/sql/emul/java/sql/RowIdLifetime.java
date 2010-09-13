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

public enum RowIdLifetime {

  /**
   * Indicates that this data source does not support the ROWID type.
   */
  ROWID_UNSUPPORTED,

  /**
   * Indicates that the lifetime of a RowId from this data source is indeterminate;
   * but not one of ROWID_VALID_TRANSACTION, ROWID_VALID_SESSION, or,
   * ROWID_VALID_FOREVER.
   */
  ROWID_VALID_OTHER,

  /**
   * Indicates that the lifetime of a RowId from this data source is at least the
   * containing session.
   */
  ROWID_VALID_SESSION,

  /**
   * Indicates that the lifetime of a RowId from this data source is at least the
   * containing transaction.
   */
  ROWID_VALID_TRANSACTION,

  /**
   * Indicates that the lifetime of a RowId from this data source is, effectively,
   * unlimited.
   */
  ROWID_VALID_FOREVER
}

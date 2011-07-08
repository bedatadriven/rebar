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

public interface Savepoint {

  /**
   * Retrieves the generated ID for the savepoint that this
   * <code>Savepoint</code> object represents.
   *
   * @return the numeric ID of this savepoint
   * @throws SQLException if this is a named savepoint
   * @since 1.4
   */
  int getSavepointId() throws SQLException;

  /**
   * Retrieves the name of the savepoint that this <code>Savepoint</code>
   * object represents.
   *
   * @return the name of this savepoint
   * @throws SQLException if this is an un-named savepoint
   * @since 1.4
   */
  String getSavepointName() throws SQLException;
}



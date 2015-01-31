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

package com.bedatadriven.rebar.sql.client.websql;


import com.bedatadriven.rebar.sql.client.SqlException;

/**
 * Thrown if an error occurs during a WebSql transaction.
 *
 * @see <a href="http://www.w3.org/TR/webdatabase/#sqlerror">W3 standard</a>
 */
public final class WebSqlException extends SqlException {

  /**
   * The transaction failed for reasons unrelated to the database itself and not covered by any other error code.
   */
  public static final int UNKNOWN_ERR = 0;
  /**
   * The statement failed for database reasons not covered by any other error code.
   */
  public static final int DATABASE_ERR = 1;
  /**
   * The operation failed because the actual database version was not what it should be. For example, a statement
   * found that the actual database version no longer matched the expected version of the Database or DatabaseSync
   * object, or the Database.changeVersion() or DatabaseSync.changeVersion() methods were passed a version that
   * doesn't match the actual database version.
   */
  public static final int VERSION_ERR = 2;
  /**
   * The statement failed because the data returned from the database was too large. The SQL "LIMIT" modifier
   * might be useful to reduce the size of the result set.
   */
  public static final int TOO_LARGE_ERR = 3;
  /**
   * The statement failed because there was not enough remaining storage space, or the storage quota was
   * reached and the user declined to give more space to the database.
   */
  public static final int QUOTA_ERR = 4;
  /**
   * The statement failed because of a syntax error, or the number of arguments did not match the number of
   * ? placeholders in the statement, or the statement tried to use a statement that is not allowed,
   * such as BEGIN, COMMIT, or ROLLBACK, or the statement tried to use a verb that could modify the
   * database but the transaction was read-only.
   */
  public static final int SYNTAX_ERR = 5;
  /**
   * An INSERT, UPDATE, or REPLACE statement failed due to a constraint failure. For example,
   * because a row was being inserted and the value given for the primary key column duplicated
   * the value of an existing row.
   */
  public static final int CONSTRAINT_ERR = 6;
  /**
   * A lock for the transaction could not be obtained in a reasonable time.
   */
  public static final int TIMEOUT_ERR = 7;
  private int code;

  protected WebSqlException(String message, int code) {
    super(message);
    this.code = code;
  }

  public int getCode() {
    return code;
  }

}

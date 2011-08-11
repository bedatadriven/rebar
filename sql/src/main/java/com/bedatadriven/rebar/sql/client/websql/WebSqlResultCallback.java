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

/**
 * Web-Sql specific callback for query results.
 *
 * @see <a href="http://www.w3.org/TR/webdatabase/#sqlstatementcallback">W3 Standard</a>
 */
public interface WebSqlResultCallback {
  void onSuccess(WebSqlTransaction tx, WebSqlResultSet results);
  boolean onFailure(WebSqlException e);

}

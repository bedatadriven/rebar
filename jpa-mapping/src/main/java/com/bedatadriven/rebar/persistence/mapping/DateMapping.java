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

package com.bedatadriven.rebar.persistence.mapping;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.Date;

/**
 * @author Alex Bertram
 */
public class DateMapping extends SingleColumnPropertyMapping {


  public DateMapping(MethodInfo getterMethod) {
    super(getterMethod);
  }

  // TODO: we should probably be doing something we zeroing out times/dates
  // depending on the @Temporal annotation


  @Override
  protected SqliteTypes getSqlTypeName() {
    return SqliteTypes.integer;
  }

  @Override
  public String getReaderName() {
    return "Readers.readDate";
  }

  @Override
  protected String getStmtSetter() {
    return "setDate";
  }

  @Override
  public String getConvertor() {
    return "Readers.toSqlDate";
  }

  @Override
  public void writeColumnValues(JSONStringer writer, Object entity) throws JSONException {
    Date value = (Date)getValue(entity);
    if(value == null)
      writer.value(null);
    else
      writer.value(value.getTime());
  }
}
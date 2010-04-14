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

import com.bedatadriven.gears.persistence.client.ClientSideGeneratedValue;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import java.util.List;
import java.util.Collections;

import org.json.JSONStringer;
import org.json.JSONException;

/**
 * Maps a single database column to an entity property.
 *
 * @author Alex Bertram
 */
public abstract class SingleColumnPropertyMapping extends PropertyMapping {

  private boolean id;
  private boolean autoincrement;
  private String columnName;

  protected SingleColumnPropertyMapping(MethodInfo getterMethod) {
    super(getterMethod);

    columnName = getName();

    // @Id annotation makes this field an id
    id = (getterMethod.getAnnotation(Id.class)!=null);

    // define default values for the column
    updatable = !id;
    unique = id;

    // @GeneratedValue(strategy = GeneratedType.Auto)
    if(id) {
      ClientSideGeneratedValue generatedValue = getterMethod.getAnnotation(ClientSideGeneratedValue.class);
      if(generatedValue!=null) {
        if(generatedValue.strategy() == GenerationType.AUTO) {
          autoincrement = true;
          insertable = false;
        }
      }
    }

    // @Column annotation -  insertable, updateable etc
    Column column = getterMethod.getAnnotation(Column.class);
    if (column != null) {
      insertable = !autoincrement && column.insertable();
      updatable = !id && column.updatable();
      nullable = !id && column.nullable();
      unique = id || column.unique();

      if (column.name() != null && column.name().length() != 0) {
        columnName = column.name();
      }
    }
  }

  @Override
  public List<ColumnMapping> getColumns() {
    return Collections.singletonList(new ColumnMapping(columnName, getSqlTypeName(), getStmtSetter()));
  }

  @Override
  public boolean isId() {
    return id;
  }

  @Override
  public boolean isAutoincrement() {
    return autoincrement;
  }

  protected abstract SqliteTypes getSqlTypeName();

  protected abstract String getStmtSetter();

   @Override
  public void writeColumnValues(JSONStringer writer, Object entity) throws JSONException {
    writer.value(getValue(entity));
  }


}

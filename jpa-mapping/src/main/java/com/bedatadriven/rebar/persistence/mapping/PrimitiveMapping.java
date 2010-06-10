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

import org.json.JSONStringer;
import org.json.JSONException;

/**
 * @author Alex Bertram
 */
public class PrimitiveMapping extends SingleColumnPropertyMapping {

  private SqliteTypes sqlTypeName;
  private String readerName;
  private String stmtSetter;

  private boolean boxed;

  public PrimitiveMapping(MethodInfo getter) {
    super(getter);

    TypeInfo type = getter.getReturnType();
    Class primitive = type.isPrimitive();

    this.boxed = (primitive == null);

    if (primitive != null) {
      this.nullable = false;
    }

    if (primitive == Integer.TYPE || type.getQualifiedName().equals(Integer.class.getName()) ||
        primitive == Short.TYPE || type.getQualifiedName().equals(Short.class.getName()) ||
        primitive == Long.TYPE || type.getQualifiedName().equals(Long.class.getName()) ||
        primitive == Byte.TYPE || type.getQualifiedName().equals(Byte.class.getName()) ||
        primitive == Boolean.TYPE || type.getQualifiedName().equals(Boolean.class.getName())) {

      sqlTypeName = SqliteTypes.integer;

    } else if (primitive == Float.TYPE || type.getQualifiedName().equals(Float.class.getName()) ||
        primitive == Double.TYPE || type.getQualifiedName().equals(Double.class.getName())) {

      sqlTypeName = SqliteTypes.real;

    } else if (primitive == Character.TYPE || type.getQualifiedName().equals(Character.class.getName())) {

      sqlTypeName = SqliteTypes.text;
    }

    String suffix = type.getSimpleName();

    if (suffix.equals("Integer")) {
      suffix = "Int";
    } else if (suffix.equals("Character")) {
      suffix = "Char";
    }
    suffix = suffix.substring(0, 1).toUpperCase() + suffix.substring(1);

    readerName = "Readers.read" + suffix;
    stmtSetter = "set" + suffix;

  }

  @Override
  public boolean isPrimitive() {
    return !boxed;
  }

  @Override
  protected SqliteTypes getSqlTypeName() {
    return sqlTypeName;
  }

  @Override
  public String getReaderName() {
    return readerName;
  }

  @Override
  protected String getStmtSetter() {
    return stmtSetter;
  }

  @Override
  public void writeColumnValues(JSONStringer writer, Object entity) throws JSONException {
    if("java.lang.Boolean".equals(this.getType().getQualifiedBoxedName())) {
      Boolean value = (Boolean) getValue(entity);
      if(value == null)
        writer.value(null);
      else
        writer.value(value ? 1 : 0);
    } else {
        super.writeColumnValues(writer, entity);
    }
  }
}

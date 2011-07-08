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

/**
 * @author Alex Bertram
 */
public class ColumnMapping {

  private String name;
  private String stmtSetter;
  private String referencedName;
  private SqliteTypes type;

  public ColumnMapping(String name, SqliteTypes type, String stmtSetter) {
    this.name = name;
    this.stmtSetter = stmtSetter;
    this.type = type;
  }

  public ColumnMapping(String name, SqliteTypes type, String stmtSetter, String referencedName) {
    this.name = name;
    this.referencedName = referencedName;
    this.type = type;
    this.stmtSetter = stmtSetter;
  }

  public String getName() {
    return name;
  }

  public SqliteTypes getType() {
    return type;
  }

  public String getTypeName() {
    return type.toString();
  }

  public String getReferencedName() {
    return referencedName;
  }

  public String getStmtSetter() {
    return stmtSetter;
  }

  public String getIndexVar() {
    return name + "Index";
  }
}

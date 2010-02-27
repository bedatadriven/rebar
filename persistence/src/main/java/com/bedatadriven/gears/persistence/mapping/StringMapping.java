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
public class StringMapping extends SingleColumnPropertyMapping {

  public StringMapping(MethodInfo getterMethod) {
    super(getterMethod);
  }


  @Override
  protected SqliteTypes getSqlTypeName() {
    return SqliteTypes.text;
  }

  @Override
  public String getReaderName() {
    return "Readers.readString";
  }

  @Override
  protected String getStmtSetter() {
    return "setString";
  }
}



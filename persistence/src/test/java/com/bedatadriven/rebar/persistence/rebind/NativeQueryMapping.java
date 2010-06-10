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

package com.bedatadriven.rebar.persistence.rebind;

import com.bedatadriven.rebar.persistence.mapping.ParameterMapping;
import com.bedatadriven.rebar.persistence.mapping.UnitMapping;
import com.bedatadriven.rebar.persistence.util.SQLStatement;

import java.util.Map;
import java.util.Collection;

/**
 * @author Alex Bertram
 */
public class NativeQueryMapping {

  private UnitMapping unit;
  private String name;
  private SQLStatement sql;
  private Map<String, ParameterMapping> namedParameters;
  private Map<Integer, ParameterMapping> positionalParameters;

  public NativeQueryMapping(UnitMapping unit, String name, String sql) {
    this.unit = unit;
    this.name = name;
    this.sql = new SQLStatement(sql);

    int index = 1;
    for(SQLStatement.Token token : this.sql.getTokens()) {
      if(token.isParameter()) {
        if(token.getParameterName()!=null) {
          ParameterMapping param = namedParameters.get(token.getParameterName());
          if(param == null) {
            param = new ParameterMapping(token.getParameterName());
            namedParameters.put(token.getParameterName(), param);
          }
          param.addIndex(index++);
        } else {
          ParameterMapping param = positionalParameters.get(token.getParameterPosition());
          if(param == null) {
            param = new ParameterMapping(token.getParameterPosition());
            positionalParameters.put(token.getParameterPosition(), param);
          }
          param.addIndex(index++);
        }
      }
    }
  }

  public UnitMapping getUnit() {
    return unit;
  }

  public String getName() {
    return name;
  }

  public SQLStatement getSql() {
    return sql;
  }

  public Collection<ParameterMapping> getNamedParameters() {
    return namedParameters.values();
  }

  public Collection<ParameterMapping> getPositionalParameters() {
    return positionalParameters.values();
  }

}

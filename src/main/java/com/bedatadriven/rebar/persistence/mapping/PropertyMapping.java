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

import java.lang.reflect.Method;
import java.util.List;

/**
 * Defines a mapping between an entity's property (one value)
 * and one or more SQL columns.
 *
 * @author Alex Bertram
 */
public abstract class PropertyMapping {

  private String name;
  private String getterName;
  private String setterName;
  private TypeInfo type;

  protected boolean unique = false;
  protected boolean insertable = true;
  protected boolean updatable = true;
  protected boolean nullable = true;

  public PropertyMapping(MethodInfo getterMethod) {

    this.getterName = getterMethod.getName();
    this.type = getterMethod.getReturnType();

    int prefixLen = getterName.startsWith("is") ? 2 : 3;
    setterName = "set" + getterName.substring(prefixLen);
    name = Character.toLowerCase(getterName.charAt(prefixLen)) +
        getterName.substring(prefixLen + 1);
  }

  public boolean isNullable() {
    return nullable;
  }

  public boolean isUnique() {
    return unique;
  }

  /**
   * Gets the name of the persistent property or field
   *
   * @return the name of the persistent property or field
   */
  public String getName() {
    return name;
  }

  public String getGetterName() {
    return getterName;
  }

  public TypeInfo getType() {
    return type;
  }

  public String getSetterName() {
    return setterName;
  }

  public boolean isId() {
    return false;
  }

  public boolean isAutoincrement() {
    return false;
  }

  /**
   * Gets the name of the convertor function to use when applying a value of this
   * property to a PreparedStatement
   *
   * @return the name of the convertor function to use when applying a value of this
   * property to a PreparedStatement
   */
  public String getConvertor() {
    return "";
  }

  /**
   * Returns true if the property/field is an embedded class
   *
   * @return
   */
  public boolean isEmbedded() {
    return false;
  }

  /**
   * Returns true if the property/field is an unboxed, primitive type
   * 
   * @return true if the property/field is an unboxed, primitive type
   */
  public boolean isPrimitive() {
    return false;
  }


  /**
   * Returns true if this property/field is a related entity with a One-To-One or
   * Many-To-One relationship
   *
   * @return
   */
  public boolean isToOne() {
    return false;
  }

  /**
   * Gets the mapping
   *
   * @return
   */
  public EntityMapping getEmbeddedClass() {
    return null;
  }

  public EntityMapping getRelatedEntity() {
    return null;
  }


  public String callGetter(String varName) {
    return varName + "." + getterName + "()";
  }

  public boolean isInsertable() {
    return insertable;
  }

  public boolean isUpdatable() {
    return updatable;
  }
  
  public String getReaderName() {
    return null;
  }

  /**
   * Returns a list of SQL column types used to store this entity member.
   * (Most entity members are stored as a single column, but embedded columns, for example,
   * span several columns, and collections are not store in columns at all)
   *
   * @return the list of SQL column types in which to store this field.
   */
  public abstract List<ColumnMapping> getColumns();

  /**
   * Returns information on the column in which this persistent property/field is stored,
   * or null if this property/field is stored in several columns.
   *
   * @return information on the column in which this persistent property/field is stored,
   * or null if this property/field is stored in several columns.
   */
  public ColumnMapping getColumn() {
    List<ColumnMapping> columns = getColumns();
    if(columns.size() == 1)
      return columns.get(0);
    
    return null;
  }

  public abstract void writeColumnValues(JSONStringer writer, Object entity) throws JSONException;


  /**
   * Uses reflection to read the value of this property from an object
   * instance.
   *
   * @param instance The instance of a class to which this mapping is a member
   * @return The value of this field in the given instance
   */
  public Object getValue(Object instance) {
    Object value;
    try {
      Method getter = instance.getClass().getMethod(getGetterName());

      value = getter.invoke(instance);

    } catch (NullPointerException e) {
      value = null;
    } catch (Exception e) {
      throw new MappingException("Exception thrown while getting the value.\n" +
          "Field: " + getName() + "\n" +
          "Mapping:" + this.toString(), e);
    }
    return value;
  }


}

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

import javax.persistence.JoinColumn;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the ManyToOne and OneToOne property mappings
 *
 * @author Alex Bertram
 */
public class ManyToOneMapping extends PropertyMapping {

  /**
   * The class to which this field is bound
   */
  private TypeInfo entityType;

  /**
   * The mapping context used to obtain information about the
   * linked entity. Note: do NOT access from the constructor as this
   * may trigger an infinite loop if a recursive entity structure
   * is found.
   */
  private UnitMapping context;

  private MethodInfo getter;

  public ManyToOneMapping(UnitMapping context, MethodInfo getter) {
    super(getter);

    this.entityType = getter.getReturnType();
    this.context = context;
    this.getter = getter;

    JoinColumn columnAnno = getter.getAnnotation(JoinColumn.class);
    if (columnAnno != null) {
      updatable = columnAnno.updatable();
      insertable = columnAnno.insertable();
    }
  }


  @Override
  public EntityMapping getRelatedEntity() {
    return context.getMapping(entityType);
  }

  @Override
  public boolean isToOne() {
    return true;
  }

  @Override
  public List<ColumnMapping> getColumns() {

    PropertyMapping relatedEntityId = getRelatedEntity().getId();

    if (relatedEntityId.getColumns().size() > 1) {

      throw new MappingException("Multiple join columns not yet supported. Terribly sorry. " +
          "If you've got some time on your hands, implement me in ManyToOneMapping.java");

    } else {

      /* Column name:
       * (Default only applies if a single join column is used.) The concatenation
       * of the following:
       *  - the name of the referencing relationship property or field of the referencing entity;
       *  - "_";
       *  - the name of the referenced primary key column.
       *
       * If there is no such referencing relationship property or field in
       * the entity, the join column name is formed as the concatenation
       * of the following:
       *  - the name of the entity;
       *  - "_";
       *  - the name of the referenced primary key column.
       *
       * Source: Table 8, Section 9.1.6
       */

      ColumnMapping referencedPrimaryKeyColumn = relatedEntityId.getColumns().get(0);

      String columnName = getName() + "_" +
          referencedPrimaryKeyColumn.getName();

      JoinColumn joinColumn = getter.getAnnotation(JoinColumn.class);
      if (joinColumn != null && joinColumn.name() != null && joinColumn.name().length() != 0)
        columnName = joinColumn.name();

      return Collections.singletonList(new ColumnMapping(columnName,
          referencedPrimaryKeyColumn.getType(),
          referencedPrimaryKeyColumn.getStmtSetter(),
          referencedPrimaryKeyColumn.getName()
      ));
    }
  }

  @Override
  public void writeColumnValues(JSONStringer writer, Object entity) throws JSONException {

    PropertyMapping joinColumn = getRelatedEntity().getId();

    // first use reflection to get the entity
    Object linkedEntity = getValue(entity);

    if (linkedEntity == null) {
      for (int i = 0; i != joinColumn.getColumns().size(); i++) {
        writer.value(null);
      }
    } else {
      // otherwise, write the join columns
      joinColumn.writeColumnValues(writer, linkedEntity);
    }
  }
}


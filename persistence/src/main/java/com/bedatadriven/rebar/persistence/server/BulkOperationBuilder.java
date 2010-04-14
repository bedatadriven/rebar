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

package com.bedatadriven.rebar.persistence.server;

import com.bedatadriven.rebar.persistence.mapping.EntityMapping;
import com.bedatadriven.rebar.persistence.mapping.UnitMapping;
import com.bedatadriven.rebar.persistence.mapping.PropertyMapping;
import org.json.JSONException;
import org.json.JSONStringer;

import java.util.List;

/**
 * Generates a jsonnified BulkOperation object that can be sent
 * to and executed by the client against the local sqlite database
 *
 * @author Alex Bertram
 */
public class BulkOperationBuilder {

  private JSONStringer json;
  private UnitMapping context;

  public BulkOperationBuilder() {
    json = new JSONStringer();
    try {
      json.array();
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    context = new UnitMapping();
  }

  public <T> void insert(Class<T> entityClass, List<T> entities) throws JSONException {

    if (entities.size() == 0) {
      return; // nothing to do
    }
    // get/create the mapping for this entity
    EntityMapping mapping = context.getMapping(entityClass);

    // start a new BulkOperation object
    json.object();

    // first build the insert statement
    json.key("statement");
    json.value(mapping.getInsertStatement());

    json.key("executions");
    json.array();

    // loop through each of the entities in the list
    for (T entity : entities) {

      json.array();

      // write the other columns
      for (PropertyMapping property : mapping.getProperties()) {
        if (property.isInsertable()) {
          property.writeColumnValues(json, entity);
        }
      }
      json.endArray();
    }

    json.endArray(); // end our batches
    json.endObject(); //end the BulkOperation object
  }

  public <T> void update(Class<T> entityClass, List<T> entities) throws JSONException {

    if (entities.size() == 0) {
      return; // nothing to do
    }
    // get/create the mapping for this entity
    EntityMapping mapping = context.getMapping(entityClass);

    // start a new BulkOperation object
    json.object();

    // first build the insert statement
    json.key("statement");
    json.value(mapping.getUpdateStatement());

    json.key("executions");
    json.array();

    // loop through each of the entities in the list
    for (T entity : entities) {

      json.array();


      // write the columns to update
      for (PropertyMapping property : mapping.getProperties()) {
        if (property.isUpdatable()) {
          property.writeColumnValues(json, entity);
        }
      }

      // write the ids used to select the row
      mapping.getId().writeColumnValues(json, entity);

      json.endArray();
    }

    json.endArray(); // end our batches
    json.endObject(); //end the BulkOperation object
  }

  public <T> void delete(Class<T> entityClass, List<T> entities) throws JSONException {

    if (entities.size() == 0) {
      return; // nothing to do
    }
    // get/create the mapping for this entity
    EntityMapping mapping = context.getMapping(entityClass);

    // start a new BulkOperation object
    json.object();

    // first build the insert statement
    json.key("statement");
    json.value(mapping.getDeleteStatement());

    json.key("executions");
    json.array();

    // loop through each of the entities in the list
    for (T entity : entities) {

      json.array();

      mapping.getId().writeColumnValues(json, entity);

      json.endArray();
    }

    json.endArray(); // end our batches
    json.endObject(); //end the BulkOperation object
  }


  public String asJson() throws JSONException {
    json.endArray();
    return json.toString();
  }

}

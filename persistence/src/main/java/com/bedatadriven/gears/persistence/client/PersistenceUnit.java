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

package com.bedatadriven.gears.persistence.client;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

public interface PersistenceUnit {


    /**
     *
     * Creates an EntityManagerFactory for this PersistenceUnit that will
     * use the ConnectionProvider <code>provider</code> to obtain a connection for each
     * EntityManager created.
     *
     * @return An open EntityManagerFactory
     */
    EntityManagerFactory createEntityManagerFactory(ConnectionProvider provider);


    /**
     * Returns a map of column names to column values for the given entity.
     * This map can be used, for example, to send changes to a single entity
     * back to the server.
     *
     * For example, for the class:
     * <code>
     * class Child {
     *     private int id;
     *     private Parent parent;
     *
     *     @Id
     *     @Column(name="ChildId")
     *     public int getId() { ... }
     *
     *     @ManyToOne
     *     @JoinColumn(name="ParentId")
     *     public Parent getParent() { ... }
     * }
     *
     * A call to getColumnMap() could return
     *
     * <code>
     * "ChildId" => 21
     * "ParentId" => 12
     * </code>
     *
     * @param entity An entity of a class managed by this persistence unit.
     * @return A map of column names to column values
     */
    Map<String, Object> getColumnMap(Object entity);

    Map<String, Object> getColumnMap(Class entityClass, Map<String,Object> propertyMap);


}

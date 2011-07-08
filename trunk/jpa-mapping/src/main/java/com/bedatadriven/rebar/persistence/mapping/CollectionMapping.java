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

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Bertram
 */
public class CollectionMapping {


  private UnitMapping context;
  private String propertyName;
  private TypeInfo entityType;
  private TypeInfo collectionType;
  private String persistentCollectionClass;
  private String getterName;
  private String setterName;

  private boolean cascadePersist;
  private boolean cascadeMerge;
  private boolean cascadeRemove;
  private boolean cascadeRefresh;

  private JoinTable joinTable;
  private String joinTableName = ""; 
  private String mappedBy;

  public CollectionMapping(UnitMapping context, MethodInfo getter) {
    this.context = context;
    this.collectionType = getter.getReturnType();
    this.entityType = collectionType.getItemType();

    this.getterName = getter.getName();
    this.setterName = "set" + getter.getName().substring(3);
    this.propertyName = getterName.substring(0,1).toLowerCase() + getterName.substring(1);

    if(this.collectionType.getQualifiedName().equals(List.class.getName()))
      persistentCollectionClass = "PersistentList"; // PersistentList.class.getName();
    else
      persistentCollectionClass = "PersistentCollection"; //PersistentSet.class.getName();

    CascadeType[] cascadeTypes = null;
    ManyToMany manyToMany = getter.getAnnotation(ManyToMany.class);
    if (manyToMany != null) {
      cascadeTypes = manyToMany.cascade();
      mappedBy = manyToMany.mappedBy();
      joinTable = getter.getAnnotation(JoinTable.class);
      if (joinTable != null) 
    	  joinTableName = joinTable.name();
    } else {
      OneToMany oneToMany = getter.getAnnotation(OneToMany.class);
      if (oneToMany != null) {
        cascadeTypes = oneToMany.cascade();
        mappedBy = oneToMany.mappedBy();
      }
    }

    if (cascadeTypes != null) {
      for (CascadeType type : cascadeTypes) {
        if (type == CascadeType.ALL) {
          cascadePersist = true;
          cascadeMerge = true;
          cascadeRefresh = true;
          cascadeRemove = true;
        } else if (type == CascadeType.MERGE) {
          cascadeMerge = true;
        } else if (type == CascadeType.PERSIST) {
          cascadePersist = true;
        } else if (type == CascadeType.REFRESH) {
          cascadeRefresh = true;
        } else if (type == CascadeType.REMOVE) {
          cascadeRemove = true;
        }
      }
    }

  }


  public TypeInfo getEntityType() {
    return entityType;
  }

  public TypeInfo getCollectionType() {
    return collectionType;
  }

  public boolean isCascadePersist() {
    return cascadePersist;
  }

  public boolean isCascadeMerge() {
    return cascadeMerge;
  }

  public boolean isCascadeRemove() {
    return cascadeRemove;
  }

  public boolean isCascadeRefresh() {
    return cascadeRefresh;
  }

  public JoinTable getJoinTable() {
    return joinTable;
  }

  public String getGetterName() {
    return getterName;
  }

  public String getSetterName() {
    return setterName;
  }

  public String getName() {
    return propertyName;
  }

  public String getMappedBy() {
    return mappedBy;
  }

  public String getJoinTableName() {
	  return joinTableName;
  }
  
  public String getPersistentCollectionClass() {
    return persistentCollectionClass;
  }
  
  public List <String> getJoinColumns() {
	  ArrayList <String> names = new ArrayList<String> ();
	  if (this.joinTable != null) {
		  JoinColumn[] cols =  joinTable.joinColumns();
		  for (int i = 0 ; i < cols.length; i ++ ) {
			 names.add(cols[i].name());
		  }
		  cols =  joinTable.inverseJoinColumns();
		  for (int i = 0 ; i < cols.length; i ++ ) {
			 names.add(cols[i].name());
		  }
	  }
	  return names;
  }
 
}

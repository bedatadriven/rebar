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

package com.bedatadriven.gears.persistence.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.bedatadriven.gears.persistence.client.BindEntities;
import com.bedatadriven.gears.persistence.client.BindPackages;
import com.bedatadriven.gears.persistence.mapping.EntityMapping;
import com.bedatadriven.gears.persistence.mapping.UnitMapping;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import javax.persistence.Entity;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Alex Bertram
 */
public class PersistenceUnitGenerator extends Generator {


  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {

    logger = logger.branch(TreeLogger.Type.DEBUG, "Creating EntityManagerFactory for " + typeName);

    try {
      TypeOracle typeOracle = context.getTypeOracle();
      JClassType requestedContext = typeOracle.getType(typeName);

      UnitMapping mapping = new UnitMapping(requestedContext);

      /*
      * Step 1: Make a list of all the entites to be managed
      */

      // Check our annotations for a list of Entities to manage
      BindEntities entities = requestedContext.getAnnotation(BindEntities.class);
      if (entities != null) {
        for (Class entityClass : entities.value()) {
          if(entityClass.getAnnotation(Entity.class)==null) {
            logger.log(TreeLogger.Type.ERROR, "Cannot bind " + entityClass.getName() + " to the" +
                "persistence unit " + requestedContext.getSimpleSourceName() + ": no @Entity annotation. ");
            throw new UnableToCompleteException();
          }
          mapping.getMapping(typeOracle.findType(entityClass.getName()));
        }
      }

      // Check our annotations for a list of packages to manage
      BindPackages packages = requestedContext.getAnnotation(BindPackages.class);
      if (packages != null) {
        for (String entityPackageName : packages.value()) {
          JPackage entityPackage = typeOracle.findPackage(entityPackageName);
          for (JType type : entityPackage.getTypes()) {
            JClassType classType = type.isClass();
            if (classType != null && classType.isAnnotationPresent(Entity.class)) {
              mapping.getMapping(classType);
            }
          }
        }
      }
      writeClasses(logger, context, mapping);


      return mapping.getPackageName() + "." + mapping.getPersistenceUnitImplClass();

    } catch (Exception e) {
      logger.log(TreeLogger.Type.ERROR, "Exception thrown while generating PersistenceContext", e);
      throw new UnableToCompleteException();
    }
  }

  public void writeClasses(TreeLogger logger, GeneratorContext context, UnitMapping mapping) throws TemplateException, IOException {
    /*
        * Step 2 : Open up our Template configuration
    */

    Configuration cfg = Templates.get();


    /*
        * Step 3 : Generate delegates, managed instances, and lazy instances for
    * each managed entity
    */

    Set<EntityMapping> toBeWritten = new HashSet<EntityMapping>(mapping.getEntities());
    Set<EntityMapping> alreadyWritten = new HashSet<EntityMapping>();

    do {
      for (EntityMapping entity : toBeWritten) {

        TreeLogger classLogger = logger.branch(TreeLogger.Type.DEBUG, "Generating classes for entity " +
          entity.getSimpleClassName());

        // write the managed entity class
        writeClass(classLogger, context, cfg, entity, "ManagedEntity.java.ftl", entity.getManagedClass());

        // write the lazy entity class
        writeClass(classLogger, context, cfg, entity, "LazyEntity.java.ftl", entity.getLazyClass());

        // the delegate that does the work of the EntityManager
        writeClass(classLogger, context, cfg, entity, "Delegate.java.ftl", entity.getDelegateClass());

        alreadyWritten.add(entity);
      }
      toBeWritten.addAll(mapping.getEntities());
      toBeWritten.removeAll(alreadyWritten);
    } while(!toBeWritten.isEmpty());

    /*
        * Step 4: Write the Em, Emf, and PersistenceContext implementations
    */

    writeClass(logger, context, cfg, mapping, "EntityManager.java.ftl",
        mapping.getEntityManagerClass());

    writeClass(logger, context, cfg, mapping, "EntityManagerFactory.java.ftl",
        mapping.getEntityManagerFactoryClass());


    writeClass(logger, context, cfg, mapping, "PersistenceUnit.java.ftl",
        mapping.getPersistenceUnitImplClass());
  }

  private void writeClass(TreeLogger logger, GeneratorContext context,
                          Configuration templateCfg, EntityMapping entity, String templateName, String className) throws TemplateException, IOException {
    PrintWriter writer = context.tryCreate(logger, entity.getContext().getPackageName(),
        className);
    if (writer != null) {
      templateCfg.getTemplate(templateName).process(entity, writer);
      writer.close();
      context.commit(logger, writer);
    }
  }


  private void writeClass(TreeLogger logger, GeneratorContext context,
                          Configuration templateCfg, UnitMapping mapping, String templateName,
                          String className) throws TemplateException, IOException {
    PrintWriter writer = context.tryCreate(logger, mapping.getPackageName(),
        className);
    if (writer != null) {
      templateCfg.getTemplate(templateName).process(mapping, writer);
      writer.close();
      context.commit(logger, writer);
    }
  }


}

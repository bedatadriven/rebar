<#-- @ftlvariable name="" type="com.bedatadriven.gears.persistence.mapping.EntityMapping" -->

package ${context.packageName};

/**
  * A subclass of an entity that supports automatic updates
  * persistent classes, etc
  */
class ${managedClass} extends ${qualifiedClassName} {

    protected ${context.entityManagerClass} em;

    public ${managedClass}(${context.entityManagerClass} em, ${idBoxedClass} id) {
        this.em = em;
        ${id.setterName}(id);
    }
}
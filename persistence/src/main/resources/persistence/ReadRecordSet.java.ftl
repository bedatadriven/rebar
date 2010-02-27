<#-- @ftlvariable name="" type="com.bedatadriven.gears.persistence.mapping.EntityMapping" -->

<#-- This macro writes a series of statements that reads properties from
     a RecordSet (named "rs") and assign the values to an entity instance
     (must be named "entity")

     The column indices are expected to be in a set of variables
     suffixed with "Index"

     For example:

     Entity:

     @Entity
     public class Person {
        private String name;
        private String organisation

        public String getName() { }
        public String setName(String name) {}

        @Column(name="org")
        public String getOrganisation() { }
     }

     Expected in the current scope:

     int nameIndex = 1;
     int orgIndex = 2;

     Generated:

     entity.setName(Readers.readString(rs, nameIndex));
     entity.setOrganisation(Readers.readString(rs, orgIndex));

  -->

<#macro readRecordSet>

    <#list properties as property>
        <#if property.id == false>

            <#if property.embedded == true>
                <#-- Create a new instance of the embedded class and fill it's properties -->
                <#assign embed = "embedded" + property.name>
                ${property.embeddedClass.qualifiedClassName} ${embed} =
                    new ${property.embeddedClass.qualifiedClassName}();

                <#if property.nullable>
                    <#-- Embedded null values are tricky. If understand the spec correctly (actually I think
                         I'm recalling a passage from Hibernate in Action, so double check), then an embedded property
                         is null if and only if all of its properties are null. So we have to check all
                         the columns first, to see if they're all null -->
                    boolean ${embed}_isNull = true;
                    <#list property.embeddedClass.properties as embeddedProperty>
                        <#-- The property could be an unboxed primitive, like int, or short, so we have to be
                             careful about assigning nulls.

                             Define a local variable here to store the value from the ResultSet
                             before we assign to it the embedded class -->

                        <#assign embedProp = embed + "_" + embeddedProperty.name>
                        ${embeddedProperty.type.qualifiedBoxedName} ${embedProp} =
                            ${embeddedProperty.readerName}(rs, ${embeddedProperty.column.indexVar});
                        if(${embedProp} != null) {
                            ${embed}_isNull = false;
                            <#if embeddedProperty.primitive == true>
                                <#-- if the property is a primitive, we only assign if the value is non-null -->
                                ${embed}.${embeddedProperty.setterName}(${embedProp});
                            </#if>
                        }
                        <#if embeddedProperty.primitive == false>
                            <#-- if the property is NOT a primitive, so we always assign (is this correct?) -->
                            ${embed}.${embeddedProperty.setterName}(${embedProp});
                        </#if>                                
                    </#list>
                    if(!${embed}_isNull)
                        entity.${property.setterName}(${embed});
                <#else>
                    <#-- This embedded property is marked as non-nullable, so we don't have to mess around -->
                    <#list property.embeddedClass.properties as embeddedProperty>
                        ${embed}.${embeddedProperty.setterName}(${embeddedProperty.readerName}(rs, ${embeddedProperty.column.indexVar}));
                    </#list>
                    entity.${property.setterName}(${embed});
                </#if>

            <#elseif property.toOne>
                <#-- Create a new, lazy instance of this class -->
                <#-- TODO: Eager loading ! -->

                // is the primary key null?
                if(${property.relatedEntity.id.readerName}(rs, ${property.column.indexVar}) == null) {
                    entity.${property.setterName}(null);
                } else {
                    // call get reference from the related class' delegate
                    entity.${property.setterName}(em.${property.relatedEntity.delegateField}
                                .getReference(${property.relatedEntity.id.readerName}(rs, ${property.column.indexVar})));
                }

            <#else>
                <#-- Simple one column property -->
                entity.${property.setterName}(${property.readerName}(rs, ${property.column.indexVar}));

            </#if>
        </#if>
    </#list>

</#macro>
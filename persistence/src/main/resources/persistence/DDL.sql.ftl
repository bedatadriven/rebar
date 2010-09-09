<#-- @ftlvariable name="entity" type="com.bedatadriven.gears.persistence.mapping.EntityMapping" -->

<#macro createtable entity>
<@singleline>
    create table if not exists ${entity.tableName} (
        <@csv>
        <#list entity.properties as property>
        	<#if property.id == true && property.embedded == true>
            <#else>
	            <#list property.columns as column>
	                ${column.name}
	                ${column.typeName}
	                <#if property.id == true>
	                    primary key
	                    <#if property.autoincrement>
	                        autoincrement
	                    </#if>
	                <#elseif property.unique == true>
	                    unique
	                </#if>
	                 ,
	            </#list>
            </#if>
        </#list>
        </@csv>
    )
</@singleline>
</#macro>

<#macro createjointable collection>
<@singleline>
    create table if not exists ${collection.joinTableName} (
        <@csv>
        <#list collection.joinColumns as column>
	    	${column}
	        integer,
        </#list>
        </@csv>
    )
</@singleline>
</#macro>

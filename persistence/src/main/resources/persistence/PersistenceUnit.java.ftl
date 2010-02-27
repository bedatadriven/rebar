<#-- @ftlvariable name="" type="com.bedatadriven.gears.persistence.mapping.UnitMapping" -->

package ${packageName};

import java.util.Map;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import com.bedatadriven.gears.jdbc.client.DriverManager;
import com.bedatadriven.gears.persistence.client.ConnectionProvider;
import com.bedatadriven.gears.persistence.client.GearsConnectionProvider;

public class ${persistenceUnitImplClass} implements ${type.qualifiedName} {

   /**
     *
     * @param databaseName
     * @return
     */
    public EntityManagerFactory createEntityManagerFactory(ConnectionProvider provider) {
        return new ${entityManagerFactoryClass}(provider);
    }

    public Map<String, Object> getColumnMap(Object entity) {
        <#list entities as entity>
            if(entity instanceof ${entity.qualifiedClassName})
                return ${entity.delegateClass}.getColumnMap(entity);
        </#list>
        throw new PersistenceException("The class " + entity.getClass().getName() + " is not managed by " +
                "this persistence unit.");
    }

    public Map<String, Object> getColumnMap(Class entityClass, Map<String,Object> propertyMap) {
        <#list entities as entity>
            if(${entity.qualifiedClassName}.class.equals(entityClass))
                return ${entity.delegateClass}.getColumnMap(propertyMap);
        </#list>
        throw new PersistenceException("The class " + entityClass.getName() + " is not managed by " +
                "this persistence unit.");
    }


}

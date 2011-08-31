package com.bedatadriven.rebar.sql.rebind;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.bedatadriven.rebar.sql.shared.dao.annotations.Select;

public class QueryTargetClass {

	private final Class targetClass;
	private final List<Property> properties = new ArrayList<Property>();

	private String tableName;
	
	public QueryTargetClass(Class targetClass) {
		this.targetClass = targetClass;

		for(Method method : targetClass.getMethods()) {
			if(isSetter(method)) {
				Property property = Property.fromSetter(method);
				if(property != null) {
					properties.add(property);
				}
			}
		}
		
		tableName = targetClass.getSimpleName();
		Table table = (Table) targetClass.getAnnotation(Table.class);
		if(table != null) {
			if(table.name() != null) {
				tableName = table.name();
			}
		}
	}
	
	public String buildSql(Method selectMethod) {
		SqlQuery query = new SqlQuery();
		for(Property property : properties) {
			query.appendColumn(property.getColumnName(), property.getColumnName());
		}
		query.from(tableName);
		
		Select select = selectMethod.getAnnotation(Select.class);
		if(select != null) {
			query.whereTrue(select.where());
		}
		
		return query.sql();
	}
	
	
	public Object toInstance(SqlResultSetRow row) {
		try {
			Object instance = targetClass.newInstance();
			for(Property property : properties) {
				property.assign(instance, row);
			}
			
			return instance;
		} catch(Exception e) {
			throw new RuntimeException("Exception while create result instance");
		}
	}
	
	private boolean isSetter(Method method) {
	  return method.getName().startsWith("set") && method.getName().length() > 3 && 
	  		(method.getModifiers() & Modifier.PUBLIC) != 0 && method.getParameterTypes().length == 1;
  }
	
	private static class Property {
		private String name;
		private String columnName;
		private Method setter;
		private Method getter;
		private Class type;
		private ColumnAccessor columnAccessor;
		
		
		public static Property fromSetter(Method setter) {
			Method getter = findGetter(setter);
			if(getter == null) {
				return null;
			}
			
			if(getter.getAnnotation(javax.persistence.Transient.class) != null) {
				return null;
			}
			
			if(!ColumnAccessors.has(getter.getReturnType())) {
				return null;
			}
			
			return new Property(getter, setter);
		}
		
		private static Method findGetter(Method setter) {
			String suffix = setter.getName().substring(3);
			for(Method method : setter.getDeclaringClass().getMethods()) {
				if(method.getName().equals("is" + suffix) ||
					 method.getName().equals("get" + suffix)) {
					
					return method;
				}
			}
			return null;
		}

		private Property(Method getter, Method setter) {
			this.setter = setter;
			this.getter = findGetter(setter);
			this.name = setter.getName().substring(3,4).toLowerCase() + setter.getName().substring(4);
			this.type = getter.getReturnType();
			this.columnAccessor = ColumnAccessors.get(type);
			
			javax.persistence.Column column = getter.getAnnotation(javax.persistence.Column.class);
			if(column != null) {
				columnName = column.name();
			}
			if(columnName == null) {
				columnName = name;
			}
		}
		
		public String getColumnName() {
			return columnName;
		}		
		
		public void assign(Object instance, SqlResultSetRow row) {
			if(!row.isNull(columnName)) {
				try {
					setter.invoke(instance, columnAccessor.get(row, columnName));
				} catch(Exception e) {
					throw new RuntimeException("Error invoking setter for property '" + name + "'", e);
				}
			}
		}
	}
}

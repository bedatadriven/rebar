package com.bedatadriven.rebar.sql.rebind;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SelectMethod  {

	private Method method;
	private QueryTargetClass targetClass;
	private String sql;
	
	
	private int callbackIndex = -1;
	private List<Integer> parameterIndexes = new ArrayList<Integer>();
	
	public SelectMethod(Method method) {
		this.method = method;
					
		int index = 0;
		for(Class clazz : method.getParameterTypes()) {
			
			if(AsyncCallback.class.isAssignableFrom(clazz)) {
				callbackIndex = index;
			} else {
				parameterIndexes.add(index);
			}
			
			index++;
		}
		
		this.targetClass = new QueryTargetClass(method.getReturnType());
		this.sql = targetClass.buildSql(method);
	}
	
	public String getSql() {
		return sql;
	}

	public QueryTargetClass getResultClass() {
		return new QueryTargetClass(method.getReturnType());
	}
	
	public boolean isAsynchronous() {
		return callbackIndex != -1;
	}
	
	public boolean isSingleResult() {
		return !Collection.class.isAssignableFrom(method.getReturnType());
	}
	
	public Object[] buildParamArray(Object[] methodArgs) {
		Object[] params = new Object[parameterIndexes.size()];
		int i=0;
		for(Integer paramIndex : parameterIndexes) {
			params[i++] = methodArgs[paramIndex];
		}
		return params;
	}
	
	
	@SuppressWarnings("rawtypes")
	private Class findElementClass(Class collectionClass) {
		for (Type interfaceType : collectionClass.getGenericInterfaces()) {
			ParameterizedType genericType = (ParameterizedType) interfaceType;
			Class interfaceClass = (Class) genericType.getRawType();
			if (interfaceClass.equals(List.class)) {
				return (Class) genericType.getActualTypeArguments()[0];
			}
		}
		throw new UnsupportedOperationException("cannot determine element class of return type " + collectionClass.getName());
	}
}

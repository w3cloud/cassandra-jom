package org.w3cloud.jom;

import java.util.List;

public class CqlDeleteStatement<T> extends CqlStatement<T>{
	public CqlDeleteStatement(Class<T> entityClass) {
		super(entityClass);
	}

	@Override
	public String buildSelectCql(StringBuilder cql, List<Object>params) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

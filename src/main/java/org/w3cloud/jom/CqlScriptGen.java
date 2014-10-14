package org.w3cloud.jom;

import java.util.List;

import com.datastax.driver.core.Session;

public interface CqlScriptGen {
	String buildCreateCql(Class<?> modelClass);
	List<String>buildIndexCqls(Class<?> modelClass);
	List<String>buildAlterCqls(Session session, Class<?> modelClass);
	void syncModel(Session session, Class<?> modelClass);
	void syncModelPackage(Session session, String packageName);
}

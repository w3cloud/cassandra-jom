package org.w3cloud.jom;

import java.util.List;

public interface CqlEntityManager {
	void insert(Object entity);
	void update(Object entity);
	<T> T findOne(Class<T> clazz, String where, Object...bindParams);
	<T> List<T> findAll(Class<T> clazz, String where, Object...bindParams);
}

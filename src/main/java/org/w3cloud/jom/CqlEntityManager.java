package org.w3cloud.jom;

import java.util.List;
import java.util.Set;

public interface CqlEntityManager {
	/**
	 * Inserts an entity into the cassandra column family
	 * @param entity
	 */
	void insert(Object entity);
	/**
	 * Updates an entity into the cassandra column family 
	 * @param entity
	 */
	void update(Object entity);
	/**
	 * Updates one or few columns specified by the statemnt.set
	 * @param entity
	 * @param statement
	 */
	<T >void updateColumn(T entity, CqlStatement<T> statement);

	<T> T findOne(CqlStatement<T> statement);
	<T> List<T> findAll(CqlStatement<T> statement);
	<T> long count(CqlStatement<T> statement);
	<T> List<T> findAll(CqlStatement<T> statement, CqlFilter<T> filter);
	void delete(Object entity);
	<T>void deleteByKey(Class<T>entityClass, Object...keys);
	<T> Object[] findAllOneColumn(String coloumnNameToBeSelected, CqlStatement<T> statement);
	<T> void findAllOneColumn(Set<Object>objSet, String coloumnNameToBeSelected, CqlStatement<T> statement);
}

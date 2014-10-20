package org.w3cloud.jom;

import java.util.List;

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
	<T> List<T> findAll(CqlStatement<T> statement, CqlFilter<T> filter);
	void delete(Object entity);
}

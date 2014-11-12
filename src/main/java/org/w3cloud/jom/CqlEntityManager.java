package org.w3cloud.jom;

import java.util.List;
import java.util.Set;

import com.datastax.driver.core.BatchStatement;

public interface CqlEntityManager {
	<T> T findOne(CqlStatement<T> statement);
	<T> List<T> findAll(CqlStatement<T> statement);
	<T> long count(CqlStatement<T> statement);
	<T> List<T> findAll(CqlStatement<T> statement, CqlFilter<T> filter);
	<T> Object[] findAllOneColumn(String coloumnNameToBeSelected, CqlStatement<T> statement);
	<T> void findAllOneColumn(Set<Object>objSet, String coloumnNameToBeSelected, CqlStatement<T> statement);

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

	void delete(Object entity);
	<T>void deleteByKey(Class<T>entityClass, Object...keys);
	
	//A conscious decision was made not write wrapper around datastax BatchStatement
	/**
	 * Insert with support for light weight transaction. Don't  forget to call execute at the end.  
	 * @param batchStatement A conscious decision was made not write wrapper around datastax BatchStatement
	 * @param entity
	 */
	void insert(BatchStatement batchStatement, Object entity);
	/**
	 * Update with support for light weight transaction. Don't  forget to call execute at the end.
	 * @param batchStatement A conscious decision was made not write wrapper around datastax BatchStatement
	 * @param entity
	 */
	void update(BatchStatement batchStatement, Object entity);
	
	/**
	 * updateColumn with support for light weight transaction. Don't  forget to call execute at the end.
	 * @param batchStatement A conscious decision was made not write wrapper around datastax BatchStatement
	 * @param entity
	 * @param statement
	 */
	<T >void updateColumn(BatchStatement batchStatement, T entity, CqlStatement<T> statement);

	/**
	 * delete with support for light weight transaction. Don't  forget to call execute at the end. 
	 * @param batchStatement A conscious decision was made not write wrapper around datastax BatchStatement
	 * @param entity
	 */
	void delete(BatchStatement batchStatement, Object entity);
	/**
	 * deleteByKey with support for light weight transaction. Don't  forget to call execute at the end. 
	 * @param batchStatement A conscious decision was made not write wrapper around datastax BatchStatement
	 * @param entityClass
	 * @param keys
	 */
	<T>void deleteByKey(BatchStatement batchStatement, Class<T>entityClass, Object...keys);
	/**
	 * Executes the statements added by prior update calls made by passing a BatchStatement Parameter
	 * @param batchStatement
	 */
	void execute(BatchStatement batchStatement);
	
}

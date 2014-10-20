package org.w3cloud.jom;

public interface CqlFilter<T> {
	boolean allowThisEntity(T entity);
}

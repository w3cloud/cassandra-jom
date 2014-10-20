package org.w3cloud.jom;

public class CqlBuilder {
	public static <T> CqlStatement<T> select(Class<T>entityClass){
		return new CqlStatement<T>(entityClass);
	}
	public static <T> CqlStatement<T> update(Class<T>entityClass){
		return new CqlStatement<T>(entityClass);
	}
	public static <T> CqlStatement<T> delete(Class<T>entityClass){
		return new CqlStatement<T>(entityClass);
	}

}

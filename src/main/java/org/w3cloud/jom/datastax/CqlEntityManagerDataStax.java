package org.w3cloud.jom.datastax;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.w3cloud.jom.CqlEntityManager;
import org.w3cloud.jom.CqlFilter;
import org.w3cloud.jom.CqlScriptGen;
import org.w3cloud.jom.CqlScriptGenFactory;
import org.w3cloud.jom.CqlStatement;
import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEmbed;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlStoreAsJson;
import org.w3cloud.jom.annotations.CqlTransient;
import org.w3cloud.jom.util.UUIDUtil;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.gson.Gson;

public class CqlEntityManagerDataStax implements CqlEntityManager{
	protected Session session;
	
	protected CqlEntityManagerDataStax(){

	}
	public CqlEntityManagerDataStax(Properties props){
		session=createSession(props);
		
		String syncTableSchemaStr=props.getProperty("cql.synctableschema");
		if (syncTableSchemaStr!=null){
			boolean syncTableSchema=syncTableSchemaStr.toLowerCase().equals("true");
			if (syncTableSchema){
				String packagesToScan=getStrProp(props, "cql.packagestoscan");
				String[] packages=packagesToScan.split(",");
				CqlScriptGen gen=CqlScriptGenFactory.create();
				for(String packageName:packages){
					packageName=packageName.trim();
					gen.syncModelPackage(session, packageName);
				}
			}
			
		}
		
	}	
	/**
	 * In cassandra, the table/field names are case insensitive and so
	 * Java class names and field names have to converted to lower 
	 * underscored names. Example: CarModel to car_model. firstName to first_name.
	 * Limitation. All upper case will not work. Example Dont have a field like CQLStr instead define like CqlStr 
	 * @param camelCaseName
	 * @return
	 */
	protected String camelCaseToUnderScore(String camelCaseName) {
		String regex = "([a-z])([A-Z]+)";
		String replacement = "$1_$2";
		return camelCaseName.replaceAll(regex, replacement).toLowerCase();
	}
	/**
	 * Builds the datastax session
	 * Properties used: cql.clustername, cql.contactpoint,
	 * and cql.keyspace

	 * @param props
	 * @return
	 */
	protected Session createSession(Properties props){
		String contactPoint=getStrProp(props, "cql.contactpoint");
		String keySpace=getStrProp(props, "cql.keyspace");
		Cluster cluster = Cluster.builder()
                .addContactPoint(contactPoint)
                .build();
		Session session=cluster.connect(keySpace);
		return session;
	}
	protected String getStrProp(Properties props, String name){
		String retVal=props.getProperty(name);
		if (retVal==null){
			throw new RuntimeException("Prop: "+name+" is null");
		}
		return retVal;
	}
	protected boolean isStoredField(Field field){
		return (field.getAnnotation(CqlTransient.class)==null) &&(!Modifier.isStatic(field.getModifiers()));
	}
	protected void buildFieldList(Field[] fields, String prefix, StringBuilder cql){
		for(Field field:fields){
			// if it is a NoSqlTransient field do nothing
			if (isStoredField(field)){
				if (field.getAnnotation(CqlEmbed.class)!=null){
					String cqlFieldName=camelCaseToUnderScore(field.getName());
					buildFieldList(field.getType().getDeclaredFields(),cqlFieldName, cql);
				}else if (field.getAnnotation(CqlStoreAsJson.class)!=null){
					String cqlFieldName=camelCaseToUnderScore(field.getName());
					cql.append(cqlFieldName);
					cql.append("__json");
					cql.append(", ");
					
				}else{
					String cqlFieldName=camelCaseToUnderScore(field.getName());
					if (prefix!=null){
						cql.append(prefix);
						cql.append("__");
					}
					cql.append(cqlFieldName);
					cql.append(", ");
				}
			}
		}
	}
	protected void buildUpdateSetList(Field[] fields, String prefix, StringBuilder cql){
		for(Field field:fields){
			// if it is a NoSqlTransient field do nothing
			// As well as id coloun will be ommitted
			if ( (isStoredField(field))&& (field.getAnnotation(CqlId.class)==null)){
				if (field.getAnnotation(CqlEmbed.class)!=null){
					String cqlFieldName=camelCaseToUnderScore(field.getName());
					buildUpdateSetList(field.getType().getDeclaredFields(),cqlFieldName, cql);
				}else if (field.getAnnotation(CqlStoreAsJson.class)!=null){
					String cqlFieldName=camelCaseToUnderScore(field.getName());
					cql.append(cqlFieldName);
					cql.append("__json");
					cql.append("=?, ");
					
				}else{
					String cqlFieldName=camelCaseToUnderScore(field.getName());
					if (prefix!=null){
						cql.append(prefix);
						cql.append("__");
					}
					cql.append(cqlFieldName);
					cql.append("=?, ");
				}
			}
		}
	}
	protected void buildUpdateWhereList(Field[] fields, StringBuilder cql){
		for(Field field:fields){
			// if it is a NoSqlTransient field do nothing
			// As well as id coloun will be ommitted
			if ( field.getAnnotation(CqlId.class)!=null){
				String cqlFieldName=camelCaseToUnderScore(field.getName());
				cql.append(cqlFieldName);
				cql.append("=? AND ");
			}
		}
	}


	protected void buildPlaceHolders(Field[] fields, StringBuilder cql){
		for(Field field:fields){
			// if it is a NoSqlTransient field do nothing
			if (isStoredField(field)){
				if (field.getAnnotation(CqlEmbed.class)!=null){
					buildPlaceHolders(field.getType().getDeclaredFields(), cql);
				}else{
					cql.append("?,");
				}
			}
		}
	}
	protected String buildInsertCql(Class<?> modelClass){
		StringBuilder cql=new StringBuilder();
		cql.append("INSERT INTO ");
		cql.append(camelCaseToUnderScore(modelClass.getSimpleName()));
		Field[] fields=modelClass.getDeclaredFields();
		cql.append(" (");
		buildFieldList(fields, null, cql);
		cql.setLength(cql.length()-2); //Trim the last ","
		cql.append(") ");
		cql.append("VALUES (");
		buildPlaceHolders(fields, cql);
		cql.setLength(cql.length()-1); //Trim the last ", "
		cql.append(") IF NOT EXISTS; ");
		return cql.toString();
	}
	protected String buildUpdateCql(Class<?> modelClass){
		StringBuilder cql=new StringBuilder();
		cql.append("UPDATE  ");
		cql.append(camelCaseToUnderScore(modelClass.getSimpleName()));
		Field[] fields=modelClass.getDeclaredFields();
		cql.append(" SET ");
		buildUpdateSetList(fields, null, cql);
		cql.setLength(cql.length()-2); //Trim the last ","
		cql.append(" WHERE ");
		buildUpdateWhereList(fields, cql);
		cql.setLength(cql.length()-4); //Trim the last ","
		return cql.toString();
	}
	protected String buildDeleteCql(Class<?> modelClass){
		StringBuilder cql=new StringBuilder();
		cql.append("DELETE FROM ");
		cql.append(camelCaseToUnderScore(modelClass.getSimpleName()));
		Field[] fields=modelClass.getDeclaredFields();
		cql.append(" WHERE ");
		buildUpdateWhereList(fields, cql);
		cql.setLength(cql.length()-4); //Trim the last ","
		return cql.toString();
	}

	protected enum BindOption{
		allFields, onlyIdFields, exceptIdFields 
	};

	protected void bindFields(Field[] fields, Object entity, List<Object> objList, BindOption bindOption){
		for(Field field:fields){
			if ( (bindOption==BindOption.exceptIdFields)&&
					(field.getAnnotation(CqlId.class)!=null)){
				continue;
			}
			if ( (bindOption==BindOption.onlyIdFields)&&
					(field.getAnnotation(CqlId.class)==null)){
				continue;
			}

			// if it is a NoSqlTransient field do nothing
			if (isStoredField(field)){
				if (field.getAnnotation(CqlEmbed.class)!=null){
					
					bindFields(field.getType().getDeclaredFields(), getField(field, entity), objList, bindOption);
				}else if (entity==null){
					objList.add(null);
				}else if (field.getAnnotation(CqlStoreAsJson.class)!=null){
					Object value=getField(field, entity);
					if (value!=null){
						Gson gson = new Gson();
						String jsonStr=gson.toJson(value);
						objList.add(jsonStr);
					}
				}else{
					Object value=getField(field, entity);
					objList.add(value);
				}
			}
		}
	}

	
	@Override
	public void insert(Object entity) {
		String insertCql=buildInsertCql(entity.getClass());
		PreparedStatement statement = session.prepare(insertCql);
		BoundStatement boundStatement = new BoundStatement(statement);
		List<Object> objList=new ArrayList<Object>();
		autoGenUUID(entity);
		bindFields(entity.getClass().getDeclaredFields(), entity, objList, BindOption.allFields);
		boundStatement.bind(objList.toArray());
		session.execute(boundStatement);
	}
	
	protected void setField(Field field, Object entity, Object 
			value){
		try{
			if (Modifier.isPublic(field.getModifiers())){
				field.set(entity, value);
			}else{
				String setterName="set"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
				Method m=entity.getClass().getMethod(setterName, field.getType());
				m.invoke(entity, value);
		}
		}catch (Throwable ex){
			throw  new RuntimeException("Unable to set field: "+field.getName()+ex.getMessage(), ex);
		}
		
	}
	public Object getField(Field field, Object entity){
		Object value;
		try{
			if (Modifier.isPublic(field.getModifiers())){
				value=field.get(entity);
			}else{
				String getterName="get"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
				Method m=entity.getClass().getMethod(getterName);
				value=m.invoke(entity);
		}
		}catch (Throwable ex){
			throw  new RuntimeException("Unable to set field: "+field.getName()+ex.getMessage(), ex);
		}
		return value;
		
	}
	protected void autoGenUUID(Object entity) {
		Field[] fields=entity.getClass().getDeclaredFields();
		for(Field field:fields){
			// if it is a NoSqlTransient field do nothing
			if (field.getAnnotation(CqlAutoGen.class)!=null){
				if (field.getType().getName().equals("java.util.UUID")){
					try {
						if (!Modifier.isStatic(field.getModifiers())){
							setField(field, entity, (Object)UUIDUtil.getTimeUUID());
						}
					} catch (Throwable e) {
						throw new RuntimeException("Exception while setting UUID", e);
					}
					
				}
				
			}
		}
	}
	@Override
	public void update(Object entity) {
		String updateCql=buildUpdateCql(entity.getClass());
		PreparedStatement statement = session.prepare(updateCql);
		BoundStatement boundStatement = new BoundStatement(statement);
		List<Object> objList=new ArrayList<Object>();
		Field[] fields=entity.getClass().getDeclaredFields();
 		bindFields(fields, entity, objList, BindOption.exceptIdFields);
 		bindFields(fields,entity, objList, BindOption.onlyIdFields);
		boundStatement.bind(objList.toArray());
		
	}
	@Override
	public <T> void updateColumn(T entity, CqlStatement<T> cqlStatement) {
		StringBuilder cql=new StringBuilder();
		List<Object>bindParams=new ArrayList<Object>();
		cqlStatement.buildUpdateCql(cql, bindParams);
		cql.append(" WHERE ");
		Field[] fields=entity.getClass().getDeclaredFields();
		buildUpdateWhereList(fields, cql);
		cql.setLength(cql.length()-4); //Trim the last ","
 		bindFields(fields,entity, bindParams, BindOption.onlyIdFields);
		PreparedStatement statement = session.prepare(cql.toString());
		BoundStatement boundStatement = new BoundStatement(statement);
		boundStatement.bind(bindParams.toArray());
		session.execute(boundStatement);
	}

	protected String buildSelectCql(Class<?> modelClass){
		StringBuilder cql=new StringBuilder();
		cql.append("SELECT * FROM ");
		cql.append(camelCaseToUnderScore(modelClass.getSimpleName()));
		cql.append(" ");
		return cql.toString();
	}
	protected Object getFieldFromRow(String prefix, Field field, Row row ){
		String cqlName;
		if (prefix!=null){
			cqlName=prefix+"__"+camelCaseToUnderScore(field.getName());
		}else{
			cqlName=camelCaseToUnderScore(field.getName());
		}
		String javaTypeName=field.getType().getName();
		
		Object value=null;
		if (javaTypeName.equals("java.util.UUID")){
			value=row.getUUID(cqlName);
		}else if (javaTypeName.equals("java.util.Date")){
			value=row.getDate(cqlName);
		}else if (javaTypeName.equals("java.lang.String")){
			value=row.getString(cqlName);
		}else if (javaTypeName.equals("java.math.BigDecimal")){
			value=row.getDecimal(cqlName);
		}else if (javaTypeName.equals("long")){
			value=row.getLong(cqlName);
		}else if (javaTypeName.equals("int")){
			value=row.getInt(cqlName);
		}else if (javaTypeName.equals("double")){
			value=row.getDouble(cqlName);
		}else if (javaTypeName.equals("float")){
			value=row.getFloat(cqlName);
		}else if (javaTypeName.equals("boolean")){
			value=row.getBool(cqlName);
		}else if (field.getAnnotation(CqlStoreAsJson.class)!=null){
			Gson gson=new Gson();
			String jsonStr=row.getString(cqlName+"__json");
			value=gson.fromJson(jsonStr, field.getType());
		}
		//setField(field, entity, value);
		return value;
		
	}
	protected <T>T getEntityFromRow(Class<T> entClass, String prefix, Row row){
		T entity=null;
		try{
		Field[] fields=entClass.getDeclaredFields();
		for(Field field:fields){
			// if it is a NoSqlTransient field do nothing
			if (isStoredField(field))
			{
				if (field.getAnnotation(CqlEmbed.class)!=null){
					String cqlFieldName=camelCaseToUnderScore(field.getName());
					Object embededEntity=getEntityFromRow(field.getType(), cqlFieldName, row);
					if (embededEntity!=null){
						if (entity==null){
							entity=entClass.newInstance();
						}
						setField(field, entity, embededEntity);
					}
					setField(field, entity, embededEntity);
				}else {
					Object value=getFieldFromRow(prefix, field, row);
					if (value!=null){
						if (entity==null){
							entity=entClass.newInstance();
						}
						setField(field, entity, value);
					}
						
					
				}
			}
		}
		}
		catch(Throwable e){
			throw new RuntimeException("Exceptionin setEntityFromRow", e);
			
		}
		return entity;
		
		
	}
//	@Override
//	public <T> T findOne(Class<T> modelClass, String where, Object... bindParams) {
//		T entity=null;
//		try {
//			String cql=buildSelectCql(modelClass)+where;
//			PreparedStatement statement = session.prepare(cql);
//			BoundStatement boundStatement = new BoundStatement(statement);
//			boundStatement.bind(bindParams);
//			Row row=session.execute(boundStatement).one();
//			if (row!=null){
//				entity=getEntityFromRow(modelClass, null, row);
//			}
//			
//		} catch (Throwable e) {
//			throw new RuntimeException("Error in findOne", e);
//		} 
//		return entity;
//	}
//	@Override
//	public <T> List<T> findAll(Class<T> modelClass, String where, Object... bindParams) {
//		List<T> entities=new ArrayList<T>();
//		try {
//			String cql=buildSelectCql(modelClass)+where;
//			PreparedStatement statement = session.prepare(cql);
//			BoundStatement boundStatement = new BoundStatement(statement);
//			boundStatement.bind(bindParams);
//			List<Row> rows=session.execute(boundStatement).all();
//			for(Row row:rows){
//				T entity=getEntityFromRow(modelClass, null, row);
//				entities.add(entity);
//			}
//			
//		} catch (Throwable e) {
//			throw new RuntimeException("Error in findOne", e);
//		} 
//		return entities;
//	}
	@Override
	public <T> T findOne(CqlStatement<T> cqlSatement) {
		T entity=null;

		StringBuilder cql=new StringBuilder();
		List<Object>bindParams=new ArrayList<Object>();
		cqlSatement.buildSelectCql(cql, bindParams);

		PreparedStatement statement = session.prepare(cql.toString());
		BoundStatement boundStatement = new BoundStatement(statement);
		boundStatement.bind(bindParams.toArray());
		Row row=session.execute(boundStatement).one();
		if (row!=null){
			entity=getEntityFromRow(cqlSatement.getEntityClass(), null, row);
		}

		return entity;
	}
	@Override
	public <T> List<T> findAll(CqlStatement<T> cqlStatement) {
		return findAll(cqlStatement, null);
	}

	@Override
	public <T> List<T> findAll(CqlStatement<T> cqlStatement, CqlFilter<T> filter) {
		List<T> entities=new ArrayList<T>();
		try {
			StringBuilder cql=new StringBuilder();
			List<Object>bindParams=new ArrayList<Object>();
			cqlStatement.buildSelectCql(cql, bindParams);
			PreparedStatement statement = session.prepare(cql.toString());
			BoundStatement boundStatement = new BoundStatement(statement);
			boundStatement.bind(bindParams.toArray());
			List<Row> rows=session.execute(boundStatement).all();
			for(Row row:rows){
				T entity=getEntityFromRow(cqlStatement.getEntityClass(), null, row);
				if (filter==null){
					entities.add(entity);
				} else if ((filter.allowThisEntity(entity))){
					entities.add(entity);
				}
			}
 		} catch (Throwable e) {
			throw new RuntimeException("Error in findOne", e);
		}
		return entities;
	}
	@Override
	public void delete(Object entity) {
		String deleteCql=buildDeleteCql(entity.getClass());
		Field[] fields=entity.getClass().getDeclaredFields();
		List<Object> bindParams=new ArrayList<Object>();
 		bindFields(fields,entity, bindParams, BindOption.onlyIdFields);
		PreparedStatement statement = session.prepare(deleteCql);
		BoundStatement boundStatement = new BoundStatement(statement);
		boundStatement.bind(bindParams.toArray());
		session.execute(boundStatement);
	}
}

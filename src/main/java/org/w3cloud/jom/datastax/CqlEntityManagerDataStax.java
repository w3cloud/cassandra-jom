package org.w3cloud.jom.datastax;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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

import com.datastax.driver.core.BatchStatement;
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
	protected static int DEFAULT_PORT=9042;
	protected List<InetSocketAddress>parseContactPoints(String contactPointsStr){
		List<InetSocketAddress> contactPoints=new ArrayList<InetSocketAddress>();
		contactPointsStr=contactPointsStr.replaceAll("\\s+","");
		String[]tokens=contactPointsStr.split(",");
		for(String token:tokens){
			String[] hostAndPort=token.split(":");
			String host;
			int port;
			if (hostAndPort.length==2){
				port=Integer.parseInt(hostAndPort[1]);
			}else{
				port=DEFAULT_PORT;
				
			}
			host=hostAndPort[0];
			InetSocketAddress contactPoint=new InetSocketAddress(host, port);
			contactPoints.add(contactPoint);
		}
		
		return contactPoints;
		
	}
	/**
	 * Builds the datastax session
	 * Properties used: cql.clustername, cql.contactpoint,
	 * and cql.keyspace

	 * @param props
	 * @return
	 */
	protected Session createSession(Properties props){
		String contactPointsStr=getStrProp(props, "cql.contactpoints");
		List<InetSocketAddress>contactPoints=parseContactPoints(contactPointsStr);
		
		String keySpace=getStrProp(props, "cql.keyspace");
		
		Cluster cluster = Cluster.builder()
				.addContactPointsWithPorts(contactPoints)
                .build();
		Session session=cluster.connect(keySpace);
		return session;
	}
	protected String getStrProp(Properties props, String name){
		String retVal=props.getProperty(name);
		if (retVal==null){
			throw new RuntimeException("Prop: "+name+" is required");
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
		cql.append(") ; ");
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
					}else{
						objList.add(null);
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
		insert(null, entity);
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
				String getterPrefix=(field.getType().getName().equals("boolean"))?"is":"get";
				String getterName=getterPrefix+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
				Method m=entity.getClass().getMethod(getterName);
				value=m.invoke(entity);
		}
		}catch (Throwable ex){
			throw  new RuntimeException("Unable to get field: "+field.getName()+ex.getMessage(), ex);
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
		update(null, entity);
	}
	@Override
	public <T> void updateColumn(T entity, CqlStatement<T> cqlStatement) {
		updateColumn(null, entity, cqlStatement);
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
			entity=entClass.newInstance();

		Field[] fields=entClass.getDeclaredFields();
		for(Field field:fields){
			// if it is a NoSqlTransient field do nothing
			if (isStoredField(field))
			{
				if (field.getAnnotation(CqlEmbed.class)!=null){
					String cqlFieldName=camelCaseToUnderScore(field.getName());
					Object embededEntity=getEntityFromRow(field.getType(), cqlFieldName, row);
					setField(field, entity, embededEntity);
				}else {
					Object value=getFieldFromRow(prefix, field, row);
					if (value!=null){
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

	@Override
	public <T> T findOne(CqlStatement<T> cqlSatement) {
		T entity=null;

		StringBuilder cql=new StringBuilder();
		List<Object>bindParams=new ArrayList<Object>();
		cqlSatement.buildSelectStarCql(cql, bindParams);

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
		StringBuilder cql=new StringBuilder();
		List<Object>bindParams=new ArrayList<Object>();
		cqlStatement.buildSelectStarCql(cql, bindParams);
		PreparedStatement statement = session.prepare(cql.toString());
		BoundStatement boundStatement = new BoundStatement(statement);
		boundStatement.bind(bindParams.toArray());
		boundStatement.setFetchSize(Integer.MAX_VALUE);//Disabling automatic paging as this feature does not work with order by clauses
		List<Row> rows=session.execute(boundStatement).all();
		for(Row row:rows){
			T entity=getEntityFromRow(cqlStatement.getEntityClass(), null, row);
			if (filter==null){
				entities.add(entity);
			} else if ((filter.allowThisEntity(entity))){
				entities.add(entity);
			}
		}
		return entities;
	}
	@Override
	public void delete(Object entity) {
		delete(null, entity);
	}
	@Override
	public <T> void deleteByKey(Class<T> entityClass, Object... keys) {
		deleteByKey(null, entityClass, keys);
	}
	@Override
	public <T> long count(CqlStatement<T> cqlStatement) {
		StringBuilder cql=new StringBuilder();
		List<Object>bindParams=new ArrayList<Object>();
		cqlStatement.buildSelectCountCql(cql, bindParams);
		PreparedStatement statement = session.prepare(cql.toString());
		BoundStatement boundStatement = new BoundStatement(statement);
		boundStatement.bind(bindParams.toArray());
		Row row=session.execute(boundStatement).one();
		return row.getLong(0);
	}
	private Field findField(Field[] fields, String fieldName){
		Field retField=null;
		for (Field field:fields){
			if (field.getName().equals(fieldName)){
				retField=field;
				break;
			}
		}
		return retField;
		
	}
	@Override
	public <T> Object[] findAllOneColumn(String coloumnNameToBeSelected,
			CqlStatement<T> cqlStatement) {
		
		StringBuilder cql=new StringBuilder();
		List<Object>bindParams=new ArrayList<Object>();
		cqlStatement.buildSelectStarCql(cql, bindParams);
		PreparedStatement statement = session.prepare(cql.toString());
		BoundStatement boundStatement = new BoundStatement(statement);
		boundStatement.bind(bindParams.toArray());
		List<Row> rows=session.execute(boundStatement).all();
		Field[] fields=cqlStatement.getEntityClass().getDeclaredFields();
		Field selectfield=findField(fields, coloumnNameToBeSelected);
		Object[] retObjs=null;
		if(rows.size()>0){
			retObjs=new Object[rows.size()];
			int i=0;
		
			for(Row row:rows){
				Object value=getFieldFromRow(null, selectfield, row);
				retObjs[i]=value;
				i++;
			}
		}
		return retObjs;
	}
	@Override
	public <T> void findAllOneColumn(Set<Object> objSet,
			String coloumnNameToBeSelected, CqlStatement<T> cqlStatement) {
		StringBuilder cql=new StringBuilder();
		List<Object>bindParams=new ArrayList<Object>();
		cqlStatement.buildSelectStarCql(cql, bindParams);
		PreparedStatement statement = session.prepare(cql.toString());
		BoundStatement boundStatement = new BoundStatement(statement);
		boundStatement.bind(bindParams.toArray());
		List<Row> rows=session.execute(boundStatement).all();
		Field[] fields=cqlStatement.getEntityClass().getDeclaredFields();
		Field selectfield=findField(fields, coloumnNameToBeSelected);
		
		if(rows.size()>0){
			for(Row row:rows){
				Object value=getFieldFromRow(null, selectfield, row);
				objSet.add(value);
			}
		}
		
	}
	@Override
	public void insert(BatchStatement batchStatement, Object entity) {
		String insertCql=buildInsertCql(entity.getClass());
		PreparedStatement statement = session.prepare(insertCql);
		BoundStatement boundStatement = new BoundStatement(statement);
		List<Object> objList=new ArrayList<Object>();
		autoGenUUID(entity);
		bindFields(entity.getClass().getDeclaredFields(), entity, objList, BindOption.allFields);
		boundStatement.bind(objList.toArray());
		if (batchStatement==null){//execute immediately
			session.execute(boundStatement);
		}else{
			batchStatement.add(boundStatement);
		}
		
	}
	@Override
	public void update(BatchStatement batchStatement, Object entity) {
		String updateCql=buildUpdateCql(entity.getClass());
		PreparedStatement statement = session.prepare(updateCql);
		BoundStatement boundStatement = new BoundStatement(statement);
		List<Object> objList=new ArrayList<Object>();
		Field[] fields=entity.getClass().getDeclaredFields();
 		bindFields(fields, entity, objList, BindOption.exceptIdFields);
 		bindFields(fields,entity, objList, BindOption.onlyIdFields);
		boundStatement.bind(objList.toArray());
		if (batchStatement==null){//execute immediately
			session.execute(boundStatement);
		}else{
			batchStatement.add(boundStatement);
		}		
	}
	@Override
	public <T> void updateColumn(BatchStatement batchStatement, T entity,
			CqlStatement<T> cqlStatement) {
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
		if (batchStatement==null){//execute immediately
			session.execute(boundStatement);
		}else{
			batchStatement.add(boundStatement);
		}		
	}
	@Override
	public void delete(BatchStatement batchStatement, Object entity) {
		String deleteCql=buildDeleteCql(entity.getClass());
		Field[] fields=entity.getClass().getDeclaredFields();
		List<Object> bindParams=new ArrayList<Object>();
 		bindFields(fields,entity, bindParams, BindOption.onlyIdFields);
		PreparedStatement statement = session.prepare(deleteCql);
		BoundStatement boundStatement = new BoundStatement(statement);
		boundStatement.bind(bindParams.toArray());
		if (batchStatement==null){//execute immediately
			session.execute(boundStatement);
		}else{
			batchStatement.add(boundStatement);
		}		
	}
	@Override
	public <T> void deleteByKey(BatchStatement batchStatement,
			Class<T> entityClass, Object... keys) {
		// TODO Auto-generated method stub
		String deleteCql=buildDeleteCql(entityClass);
		PreparedStatement statement = session.prepare(deleteCql);
		BoundStatement boundStatement = new BoundStatement(statement);
		boundStatement.bind(keys);
		if (batchStatement==null){//execute immediately
			session.execute(boundStatement);
		}else{
			batchStatement.add(boundStatement);
		}		
		
		
	}
	@Override
	public void execute(BatchStatement batchStatement) {
		session.execute(batchStatement);
	}

}

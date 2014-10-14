package org.w3cloud.jom.datastax;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.w3cloud.jom.CqlScriptGen;
import org.w3cloud.jom.annotations.CqlEmbed;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;
import org.w3cloud.jom.annotations.CqlIndex;
import org.w3cloud.jom.annotations.CqlStoreAsJson;
import org.w3cloud.jom.annotations.CqlTransient;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;

public class CqlScriptGenDataStax implements CqlScriptGen{
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
	protected boolean isStoredField(Field field){
		return (field.getAnnotation(CqlTransient.class)==null) &&(!Modifier.isStatic(field.getModifiers()));
	}
	protected String javaTypeToCqlType(String javaTypeName){
		String cassandraType=null;
		if (javaTypeName.equals("java.util.UUID")){
			cassandraType="timeuuid";
		}else if (javaTypeName.equals("java.util.Date")){
			cassandraType="timestamp";
		}else if (javaTypeName.equals("java.lang.String")){
			cassandraType="text";
		}else if (javaTypeName.equals("java.math.BigDecimal")){
			cassandraType="decimal";
		}else if (javaTypeName.equals("long")){
			cassandraType="bigint";
		}else if (javaTypeName.equals("int")){
			cassandraType="int";
		}else if (javaTypeName.equals("double")){
			cassandraType="double";
		}else if (javaTypeName.equals("float")){
			cassandraType="float";
		}else if (javaTypeName.equals("boolean")){
			cassandraType="boolean";
		}else if (javaTypeName.equals("java.lang.Integer")){
			cassandraType="int";
		}else if (javaTypeName.equals("java.lang.Long")){
			cassandraType="bigint";
		}else{
			throw new RuntimeException("Unknown data type:"+javaTypeName);
		}
		return cassandraType;
	}

	@Override
	public String buildCreateCql(Class<?> modelClass) {
		StringBuilder cql=new StringBuilder();
		List<String> primaryKeys=new ArrayList<String>(4);
		Field[] fields=modelClass.getDeclaredFields();
		cql.append("CREATE TABLE ");
		cql.append(camelCaseToUnderScore(modelClass.getSimpleName()));
		cql.append(" (");
		
		
		for(Field field:fields){
			// if it is a NoSqlTransient field do nothing
			if (isStoredField(field)){
				// if field annotated with NoSqlId, add to the primary keys list
				if (field.getAnnotation(CqlId.class)!=null){
					primaryKeys.add(camelCaseToUnderScore(field.getName()));
				}
				if (field.getAnnotation(CqlEmbed.class)!=null){
					String cassandraFieldName=camelCaseToUnderScore(field.getName());
					Class<?> fieldClass=field.getType();
					Field[] embFields=fieldClass.getDeclaredFields();
					for(Field embField:embFields){
						String embCqlFieldName=camelCaseToUnderScore(embField.getName());
						Class<?> embFieldType=embField.getType();
						String embFieldTypeName=embFieldType.getName();
						String embCqlFieldTypeName=javaTypeToCqlType(embFieldTypeName);
						cql.append(cassandraFieldName);
						cql.append("__");
						cql.append(embCqlFieldName);
						cql.append(" ");
						cql.append(embCqlFieldTypeName);
						cql.append(", ");
					}
					
				}else if (field.getAnnotation(CqlStoreAsJson.class)!=null){
					cql.append(camelCaseToUnderScore(field.getName()));
					cql.append("__json");
					cql.append(" text, ");
				}else{
					String cassandraFieldName=camelCaseToUnderScore(field.getName());
					Class<?> fieldType=field.getType();
					String fieldTypeName=fieldType.getName();
					String cassandraFieldTypeName=javaTypeToCqlType(fieldTypeName);
					cql.append(cassandraFieldName);
					cql.append(" ");
					cql.append(cassandraFieldTypeName);
					cql.append(", ");
				}
			}
		}
		cql.append("PRIMARY KEY(");
		for(String primaryKey:primaryKeys){
			cql.append(primaryKey);
			cql.append(", ");
		}
		cql.setLength(cql.length()-2);
		cql.append("));");
		return cql.toString();
	}
	@Override
	public List<String> buildIndexCqls(Class<?> modelClass) {
		List<String> indexCqls=new ArrayList<String>(6);
		Field[] fields=modelClass.getDeclaredFields();
		String tableName=camelCaseToUnderScore(modelClass.getSimpleName());
		for(Field field:fields){
			// if it is a CqlIndex annotated, generate index
			if (field.getAnnotation(CqlIndex.class)!=null){
				StringBuilder cql=new StringBuilder();
				cql.append("CREATE INDEX ON ");
				cql.append(tableName);
				cql.append(" (");
				cql.append(camelCaseToUnderScore(field.getName()));
				cql.append(");");
				indexCqls.add(cql.toString());
			}
		}
		return indexCqls;
	}
	@Override
	public List<String> buildAlterCqls(Session session, Class<?> modelClass) {
		List<String> alterCqls=new ArrayList<String>(6);
		Field[] fields=modelClass.getDeclaredFields();
		String tableName=camelCaseToUnderScore(modelClass.getSimpleName());
		for(Field field:fields){
			// if it is a CqlIndex annotated, generate index
			if (isStoredField(field)){
				if (field.getAnnotation(CqlEmbed.class)!=null){
					Class<?> fieldClass=field.getType();
					Field[] embFields=fieldClass.getDeclaredFields();
					for(Field embField:embFields){
						String embCqlFieldName=camelCaseToUnderScore(field.getName())+"__"+
								camelCaseToUnderScore(embField.getName());
						if (ifColumnExists(session, tableName, embCqlFieldName)==false){
							//if column does tot exists build the alter statement
							StringBuilder cql=new StringBuilder();
							//ALTER TABLE addamsFamily ADD gravesite varchar;
							cql.append("ALTER TABLE ");
							cql.append(tableName);
							cql.append(" ADD ");
							cql.append(embCqlFieldName);
							cql.append(" ");
							cql.append(javaTypeToCqlType(embField.getType().getName()));						
							cql.append(";");
							alterCqls.add(cql.toString());
						}
					}
				}else if (field.getAnnotation(CqlStoreAsJson.class)!=null){
					String cqlFieldName=camelCaseToUnderScore(field.getName())+"__json";
					if (ifColumnExists(session, tableName, cqlFieldName)==false){
						StringBuilder cql=new StringBuilder();
						//ALTER TABLE addamsFamily ADD gravesite varchar;
						cql.append("ALTER TABLE ");
						cql.append(tableName);
						cql.append(" ADD ");
						cql.append(cqlFieldName);
						cql.append(" text;");
						alterCqls.add(cql.toString());
					}
				}else{
					String cqlFieldName=camelCaseToUnderScore(field.getName());
					if (ifColumnExists(session, tableName, cqlFieldName)==false){
						//if column does tot exists build the alter statement
						StringBuilder cql=new StringBuilder();
						//ALTER TABLE addamsFamily ADD gravesite varchar;
						cql.append("ALTER TABLE ");
						cql.append(tableName);
						cql.append(" ADD ");
						cql.append(cqlFieldName);
						cql.append(" ");
						cql.append(javaTypeToCqlType(field.getType().getName()));						
						cql.append(";");
						alterCqls.add(cql.toString());
					}
				}
			}
		}
		return alterCqls;	}

	protected BoundStatement tableExistsStatement=null;
	protected BoundStatement columnExistsStatement=null;
	protected boolean ifTableExists(Session session, String familyName){
		if (tableExistsStatement==null){
			String cql="SELECT columnfamily_name FROM system.schema_columnfamilies WHERE keyspace_name=? and columnfamily_name=?";
			PreparedStatement statement = session.prepare(cql);
			tableExistsStatement=new BoundStatement(statement);
		}
		tableExistsStatement.bind(session.getLoggedKeyspace(), familyName);
		Row row=session.execute(tableExistsStatement).one();
		return row!=null;
	}
	protected boolean ifColumnExists(Session session, String familyName, String columnName){
		if (columnExistsStatement==null){
			String cql="SELECT column_name FROM system.schema_columns where keyspace_name=? and columnfamily_name=? and column_name=?";
			PreparedStatement statement = session.prepare(cql);
			columnExistsStatement=new BoundStatement(statement);
		}
		columnExistsStatement.bind(session.getLoggedKeyspace(), familyName, columnName);
		Row row=session.execute(columnExistsStatement).one();
		return row!=null;
	}

	@Override
	public void syncModel(Session session, Class<?> modelClass) {
		String tableName=camelCaseToUnderScore(modelClass.getSimpleName());
		if (ifTableExists(session, tableName)){
			List<String>alterCqls=buildAlterCqls(session, modelClass);
			for(String alterCql:alterCqls){
				System.out.println(alterCql);
				session.execute(alterCql);
			}
		}else{
			String cql=buildCreateCql(modelClass);
			System.out.println(cql);
			session.execute(cql);
		}
		List<String> indexCqls=buildIndexCqls(modelClass);
		for(String indexCql:indexCqls){
			try{
				System.out.println(indexCql);
				session.execute(indexCql);
			}catch ( InvalidQueryException iqe){
				//ignore iqe (already exists) exception
			}
		}
	}
	@Override
	public void syncModelPackage(Session session, String packageName) {
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> entityClasses= reflections.getTypesAnnotatedWith(CqlEntity.class);
		for(Class<?>entityClass:entityClasses){
			syncModel(session, entityClass);
		}
	}
}

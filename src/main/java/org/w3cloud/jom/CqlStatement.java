package org.w3cloud.jom;

import java.util.ArrayList;
import java.util.List;

public class CqlStatement<T> {
	public class Expression{
		protected static final String TYPE_EQ=" = ";
		protected static final String TYPE_GE=" >= ";
		protected static final String TYPE_LE=" <= ";
		protected static final String TYPE_GT=" > ";
		protected static final String TYPE_LT=" < ";
		protected static final String TYPE_IN=" IN ";
		protected static final String TYPE_SET=" SET ";
		private CqlStatement<T> cqlStatment;
		private String fieldName;
		private String operation;
		private Object value;
		public Expression(String fieldName, CqlStatement<T> cqlStatment){
			this.fieldName=fieldName;
			this.cqlStatment=cqlStatment;
		}
		public CqlStatement<T> eq(Object value){
			this.operation=TYPE_EQ;
			this.value=value;
			return this.cqlStatment;
		}
		public CqlStatement<T> ge(Object value){
			this.operation=TYPE_GE;
			this.value=value;
			return this.cqlStatment;
		}
		public CqlStatement<T> le(Object value){
			this.operation=TYPE_LE;
			this.value=value;
			return this.cqlStatment;
		}
		public CqlStatement<T> gt(Object value){
			this.operation=TYPE_GT;
			this.value=value;
			return this.cqlStatment;
		}
		public CqlStatement<T> lt(Object value){
			this.operation=TYPE_LT;
			this.value=value;
			return this.cqlStatment;
		}
		public CqlStatement<T> in(Object...values){
			this.operation=TYPE_IN;
			this.value=values;
			return this.cqlStatment;
		}
		public CqlStatement<T> set(Object value){
			this.operation=TYPE_SET;
			this.value=value;
			return this.cqlStatment;
		}
	}
	protected Class<T> entityClass;
	protected List<Expression> expressions=null;
	public Class<T> getEntityClass() {
		return entityClass;
	}
	protected int limit=0;//no limit
	protected boolean allowFiltering=false;

	
	public CqlStatement(Class<T> entityClass){
		this.entityClass=entityClass;	
	}
	public Expression field(String fieldName){
		if (expressions==null){
			expressions=new ArrayList<Expression>();
		}
		Expression expr=new Expression(fieldName, this);
		expressions.add(expr);

		return expr;
	}
	protected String camelCaseToUnderScore(String camelCaseName) {
		String regex = "([a-z])([A-Z]+)";
		String replacement = "$1_$2";
		return camelCaseName.replaceAll(regex, replacement).toLowerCase();
	}
	public CqlStatement<T> limit(int n){
		limit=n;
		return this;
	}
	public CqlStatement<T> allowFiltering(){
		allowFiltering=true;
		return this;
	}
	protected void buildWhereClause(StringBuilder cql, List<Object>params){
		cql.append(" WHERE ");
		if (expressions==null)
			throw new RuntimeException("No expressions. Use field().eq(v) syntax to add expressions");
		int exprSize=expressions.size();
		for(int i=0;i<exprSize;++i){
			Expression expr=expressions.get(i);
			if (!expr.operation.equals(Expression.TYPE_SET)){
				cql.append(camelCaseToUnderScore(expr.fieldName));
				cql.append(expr.operation);
				cql.append(" ? ");
				if ((i+1)<exprSize){ //not last item
					cql.append(" AND ");
				}
				params.add(expr.value);
			}
		}
	}
	protected void buildSetList(StringBuilder cql, List<Object>params){
		if (expressions==null)
			throw new RuntimeException("No expressions. Use field().eq(v) syntax or set to add expressions");
		for(Expression expr:expressions){
			if (expr.operation.equals(Expression.TYPE_SET)){
				cql.append(" SET ");
				cql.append(camelCaseToUnderScore(expr.fieldName));
				cql.append(" = ");
				cql.append(" ? ");
				cql.append(", ");
				params.add(expr.value);
			}
		}
		cql.setLength(cql.length()-2); //Trim the last ","
	}

	public String buildSelectCql(StringBuilder cql, List<Object>params) {
		cql.append("SELECT * FROM ");
		cql.append(camelCaseToUnderScore(entityClass.getSimpleName()));
		buildWhereClause(cql, params);
		if (limit>0){
			cql.append(" LIMIT ");
			cql.append(limit);
			cql.append(" ");
		}
		if (allowFiltering){
			cql.append(" ALLOW FILTERING ");
		}
		return cql.toString();
	}
	public String buildUpdateCql(StringBuilder cql, List<Object>params) {
		cql.append("UPDATE ");
		cql.append(camelCaseToUnderScore(entityClass.getSimpleName()));
		buildSetList(cql, params);
		return cql.toString();
	}
	public String buildDeleteCql(StringBuilder cql, List<Object>params) {
		cql.append("DELETE FROM ");
		cql.append(camelCaseToUnderScore(entityClass.getSimpleName()));
		buildWhereClause(cql, params);
		return cql.toString();
	}

	
}

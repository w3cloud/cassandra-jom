package org.w3cloud.jom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3cloud.jom.testmodels.AuditLog;


public class CqlStatementTest {
	@BeforeClass
	public static void setUp() {

	}

	@AfterClass
	public static void tearDown() {

	}

	private static UUID restaurantId1=UUID.fromString("1fba8172-52f1-11e4-82f4-e811328fecf4");
	private static UUID employeeId=UUID.fromString("b8d37e30-53bd-11e4-b43f-e811328fecf4");

	@Test
	public void testBuildWhereClause(){
		CqlStatement<AuditLog> cqlStatement=CqlBuilder.select(AuditLog.class).field("restaurantId").eq(restaurantId1).field("employeeId").eq(employeeId);
		StringBuilder cql=new StringBuilder();
		List<Object> params=new ArrayList<Object>();
		cqlStatement.buildWhereClause(cql, params);
		String cqlExpected=" WHERE restaurant_id =  ?  AND employee_id =  ? ";
		assertTrue(cql.toString().equals(cqlExpected));
		assertTrue(params.size()==2);
		
	}
	@Test
	public void testBuildSelect(){
		CqlStatement<AuditLog> cqlStatement=CqlBuilder.select(AuditLog.class).field("restaurantId").eq(restaurantId1).field("employeeId").eq(employeeId).limit(100).allowFiltering();
		StringBuilder cql=new StringBuilder();
		List<Object> params=new ArrayList<Object>();
		cqlStatement.buildSelectStarCql(cql, params);
		String cqlExpected="SELECT * FROM audit_log WHERE restaurant_id =  ?  AND employee_id =  ?  LIMIT 100  ALLOW FILTERING ";
		assertTrue(cql.toString().equals(cqlExpected));
		assertTrue(params.size()==2);
		
	}
	@Test
	public void testBuildUpdateSetList(){
		CqlStatement<AuditLog> cqlStatement=CqlBuilder.update(AuditLog.class).field("action").set("New Value").field("restaurantId").eq(restaurantId1).field("employeeId").eq(employeeId);
		StringBuilder cql=new StringBuilder();
		List<Object> params=new ArrayList<Object>();
		cqlStatement.buildSetList(cql, params);
		String cqlExpected=" SET action =  ? ";
		assertTrue(cql.toString().equals(cqlExpected));
		assertTrue(params.size()==1);
		
	}
	@Test
	public void testBuildUpdateCql(){
		CqlStatement<AuditLog> cqlStatement=CqlBuilder.update(AuditLog.class).field("action").set("New Value").field("restaurantId").eq(restaurantId1).field("employeeId").eq(employeeId);
		StringBuilder cql=new StringBuilder();
		List<Object> params=new ArrayList<Object>();
		cqlStatement.buildUpdateCql(cql, params);
		String cqlExpected="UPDATE audit_log SET action =  ? ";
		assertTrue("Expected:\n"+cqlExpected+"\nActual:\n"+cql.toString(),cql.toString().equals(cqlExpected));
		assertTrue(params.size()==1);
		
	}
	@Test
	public void testBuildDeleteCql(){
		CqlStatement<AuditLog> cqlStatement=CqlBuilder.update(AuditLog.class).field("restaurantId").eq(restaurantId1).field("employeeId").eq(employeeId).field("id").eq(employeeId);
		StringBuilder cql=new StringBuilder();
		List<Object> params=new ArrayList<Object>();
		cqlStatement.buildDeleteCql(cql, params);
		String cqlExpected="DELETE FROM audit_log WHERE restaurant_id =  ?  AND employee_id =  ?  AND id =  ? ";
		assertTrue("Expected:\n"+cqlExpected+"End\nActual:\n"+cql.toString()+"END", cql.toString().equals(cqlExpected));
		assertTrue("Expected 3. Actual:"+params.size(),params.size()==3);	
	}

}

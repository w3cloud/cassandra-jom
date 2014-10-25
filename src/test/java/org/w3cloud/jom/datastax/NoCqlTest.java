package org.w3cloud.jom.datastax;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.w3cloud.jom.datastax.CqlEntityManagerDataStax;
import org.w3cloud.jom.datastax.CqlScriptGenDataStax;
import org.w3cloud.jom.testmodels.AuditLog;
import org.w3cloud.jom.testmodels.AuditLogEnc;
import org.w3cloud.jom.testmodels.AutoGenUuidTest;

import com.datastax.driver.core.Session;

import static org.junit.Assert.*;

public class NoCqlTest {
	@Test
	public void testCamelCaseToUnderScore() {
		CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
		assertTrue(em.camelCaseToUnderScore("CarModel").equals("car_model"));
		assertTrue(em.camelCaseToUnderScore("carModel").equals("car_model"));
		assertTrue(em.camelCaseToUnderScore("carModel2014").equals("car_model2014"));
		assertTrue(em.camelCaseToUnderScore("Car").equals("car"));
		assertTrue(em.camelCaseToUnderScore("car").equals("car"));
		assertTrue(em.camelCaseToUnderScore("UUID").equals("uuid"));
		assertTrue(em.camelCaseToUnderScore("CarID").equals("car_id"));
		assertTrue(em.camelCaseToUnderScore("CarI").equals("car_i"));
		assertTrue(em.camelCaseToUnderScore("CData").equals("cdata"));
		assertTrue(em.camelCaseToUnderScore("CData_model").equals("cdata_model"));
		assertTrue(em.camelCaseToUnderScore("CarModelMake").equals("car_model_make"));
		assertTrue(em.camelCaseToUnderScore("CarIDModel").equals("car_idmodel"));

	}
	@Test
	public void testJavaTypeToCqlTypePassInvalidType() {
		try{
			CqlScriptGenDataStax gen=new CqlScriptGenDataStax();
			gen.javaTypeToCqlType("org.w3cloud.NoExistingClass");
			assertTrue(false);
		}catch(RuntimeException ex){
			assertTrue(ex.getMessage().startsWith("Unknown data type"));
		}
	}
	@Test
	public void testJavaTypeToCqlType() {
		CqlScriptGenDataStax gen=new CqlScriptGenDataStax();
		assertTrue(gen.javaTypeToCqlType("java.util.Date").equals("timestamp"));
		assertTrue(gen.javaTypeToCqlType("java.lang.String").equals("text"));
		assertTrue(gen.javaTypeToCqlType("java.math.BigDecimal").equals("decimal"));
		assertTrue(gen.javaTypeToCqlType("long").equals("bigint"));
		assertTrue(gen.javaTypeToCqlType("int").equals("int"));
		assertTrue(gen.javaTypeToCqlType("double").equals("double"));
		assertTrue(gen.javaTypeToCqlType("float").equals("float"));
		assertTrue(gen.javaTypeToCqlType("java.lang.Integer").equals("int"));
		assertTrue(gen.javaTypeToCqlType("java.lang.Long").equals("bigint"));
	}
	@Test
	public void testCreateSession3() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Properties props=new Properties();
			props.put("cql.contactpoint", "localhostfailing");
			props.put("cql.keyspace", "demo");
			em.createSession(props);
			fail("passing invalid contactpoint should have thrown runtimeexception");
		}catch(RuntimeException ex){
			assertTrue(true);
		}
	}
	@Test
	public void testCreateSession4() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Properties props=new Properties();
			props.put("cql.contactpoint", "localhost");
			props.put("cql.keyspace", "demofailing");
			em.createSession(props);
			fail("passing invalid contactpoint should have thrown runtimeexception");
		}catch(RuntimeException ex){
			assertTrue(true);
		}
	}
	@Test
	public void testBuildFieldList1() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?>testClass=org.w3cloud.jom.testmodels.AuditLog.class;
			StringBuilder cql=new StringBuilder();
			em.buildFieldList(testClass.getDeclaredFields(), null, cql);
			String expected="restaurant_id, employee_id, id, action, details, create_dt__d, create_dt__z, ";
			assertTrue("Expected:\n"+expected+"End\nActual:\n"+cql.toString()+"END", cql.toString().equals(expected));
			
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}
	@Test
	public void testBuildFieldListEnc1() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?>testClass=org.w3cloud.jom.testmodels.AuditLogEnc.class;
			StringBuilder cql=new StringBuilder();
			em.buildFieldList(testClass.getDeclaredFields(), null, cql);
			String expected="restaurant_id, id, employee_id, action, active, details, create_dt__d, create_dt__z, ";
			assertTrue(cql.toString().equals(expected));
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}

	@Test
	public void testBuildFieldList2() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?>testClass=org.w3cloud.jom.testmodels.CustOrder.class;
			StringBuilder cql=new StringBuilder();
			em.buildFieldList(testClass.getDeclaredFields(), null, cql);
			String expected="restaurant_id, id, user_id, order_no, order_type, furture_order_dt__d, furture_order_dt__z, payment_type, estimated_time, order_items__json, order_total, delivery_fee, coupon_code, discount, tip, tax_percent, tax, total, name, phone, send_text_msg, create_dt__d, create_dt__z, status, cancel_reason, db_status, transaction_id, ";
			assertTrue(cql.toString().equals(expected));
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}
	@Test
	public void testBuildFieldListEnc2() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?>testClass=org.w3cloud.jom.testmodels.CustOrderEnc.class;
			StringBuilder cql=new StringBuilder();
			em.buildFieldList(testClass.getDeclaredFields(), null, cql);
			String expected="restaurant_id, id, user_id, order_no, order_type, furture_order_dt__d, furture_order_dt__z, payment_type, estimated_time, order_items__json, order_total, delivery_fee, coupon_code, discount, tip, tax_percent, tax, total, name, phone, send_text_msg, create_dt__d, create_dt__z, status, cancel_reason, db_status, transaction_id, ";
			assertTrue(cql.toString().equals(expected));
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}

	@Test
	public void testAutoGenUuid() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			String cql=em.buildUpdateCql(AuditLog.class);
			AutoGenUuidTest	e=new AutoGenUuidTest();
			
			em.autoGenUUID(e);
			assertTrue(e.id!=null);
			assertTrue(e.id.toString().length()>5);
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}
	@Test
	public void testAutoGenUuidEnc() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			String cql=em.buildUpdateCql(AuditLogEnc.class);
			AutoGenUuidTest	e=new AutoGenUuidTest();
			
			em.autoGenUUID(e);
			assertTrue(e.id!=null);
			assertTrue(e.id.toString().length()>5);
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}


	@Test
	public void testBuildUpdateSetList() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?> modelClass=AuditLog.class;
			StringBuilder cql=new StringBuilder();
			em.buildUpdateSetList(modelClass.getDeclaredFields(), null, cql);
			String expected="action=?, details=?, create_dt__d=?, create_dt__z=?, ";
			assertTrue("Expected:\n"+expected+"End\nActual:\n"+cql.toString()+"END", cql.toString().equals(expected));
			
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}
	@Test
	public void testBuildUpdateSetListEnc() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?> modelClass=AuditLogEnc.class;
			StringBuilder cql=new StringBuilder();
			em.buildUpdateSetList(modelClass.getDeclaredFields(), null, cql);
			String expected="employee_id=?, action=?, active=?, details=?, create_dt__d=?, create_dt__z=?, ";
			assertTrue(cql.toString().equals(expected));
			
			
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}

	@Test
	public void testBuildUpdateWhereList() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?> modelClass=AuditLog.class;
			StringBuilder cql=new StringBuilder();
			em.buildUpdateWhereList(modelClass.getDeclaredFields(), cql);
			String expected="restaurant_id=? AND employee_id=? AND id=? AND ";
			assertTrue("Expected:\n"+expected+"End\nActual:\n"+cql.toString()+"END", cql.toString().equals(expected));
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}
	@Test
	public void testBuildUpdateWhereListEnc() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?> modelClass=AuditLogEnc.class;
			StringBuilder cql=new StringBuilder();
			em.buildUpdateWhereList(modelClass.getDeclaredFields(), cql);
			String expected="restaurant_id=? AND id=? AND ";
			assertTrue(cql.toString().equals(expected));
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}

	@Test
	public void testBuildUpdateCql() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?> modelClass=AuditLog.class;
			String cql=em.buildUpdateCql(modelClass);
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}

	@Test
	public void testBuildUpdateCqlEnc() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			Class<?> modelClass=AuditLogEnc.class;
			String cql=em.buildUpdateCql(modelClass);
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}

	@Test
	public void testBuildIndexCqls1() {
		try{
			CqlScriptGenDataStax gen=new CqlScriptGenDataStax();
			List<String>indexCqls=gen.buildIndexCqls(AuditLog.class);
			assertNotNull(indexCqls);
			assertTrue(indexCqls.size()==1);
			assertTrue(indexCqls.get(0).equals("CREATE INDEX ON audit_log (action);"));
			for(String cql:indexCqls){
				System.out.println(cql);
			}
		}catch(RuntimeException ex){
			fail(ex.getMessage());
		}
	}
	@Test
	public void testSetFieldTest() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			AuditLog e=new AuditLog();
			String expected="WorksGreat!";
			em.setField(e.getClass().getDeclaredField("action"), e, expected);
			assertTrue(e.getAction().equals(expected));
			
		}catch(Throwable ex){
			fail(ex.getMessage());
		}
		
	}

	@Test
	public void testGetFieldTest() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			AuditLog e=new AuditLog();
			String expected="WorksGreat!";
			e.setAction(expected);
			assertTrue(em.getField(e.getClass().getDeclaredField("action"), e).equals(expected));
			
		}catch(Throwable ex){
			fail(ex.getMessage());
		}
		
	}

	@Test
	public void testIsStoredField() {
		try{
			CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
			AuditLog e=new AuditLog();

			assertTrue(em.isStoredField(e.getClass().getDeclaredField("someStatic"))==false);
			assertTrue(em.isStoredField(e.getClass().getDeclaredField("somePublicStatic"))==false);

			String expected="WorksGreat!";
			em.setField(e.getClass().getDeclaredField("action"), e, expected);
			assertTrue(e.getAction().equals(expected));
			
		}catch(Throwable ex){
			fail(ex.getMessage());
		}
		
	}
	@Test
	public void testBuildDeleteCql() {
		CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
		String cql=em.buildDeleteCql(AuditLog.class);
		String expected="DELETE FROM audit_log WHERE restaurant_id=? AND employee_id=? AND id=? ";
		assertTrue("Expected:\n"+expected+"End\nActual:\n"+cql.toString()+"END", cql.toString().equals(expected));
	}
	@Test
	public void testParseContactPoints(){
		CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
		String contactPointsStr="localhost";
		List<InetSocketAddress> cps=em.parseContactPoints(contactPointsStr);
		assertNotNull(cps);
		assertTrue(cps.size()==1);
		InetSocketAddress cp=cps.get(0);
		assertTrue(cp.getHostName().equals("localhost"));
		assertTrue(cp.getPort()==CqlEntityManagerDataStax.DEFAULT_PORT);
	}
	
	@Test
	public void testParseContactPoints2(){
		CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
		String contactPointsStr="localhost:9042";
		List<InetSocketAddress> cps=em.parseContactPoints(contactPointsStr);
		assertNotNull(cps);
		assertTrue(cps.size()==1);
		InetSocketAddress cp=cps.get(0);
		assertTrue(cp.getHostName().equals("localhost"));
		assertTrue(cp.getPort()==CqlEntityManagerDataStax.DEFAULT_PORT);
	}
	@Test
	public void testParseContactPoints3(){
		CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
		String contactPointsStr="localhost:9042,host2,host3:4546";
		List<InetSocketAddress> cps=em.parseContactPoints(contactPointsStr);
		assertNotNull(cps);
		assertTrue(cps.size()==3);
		InetSocketAddress cp=cps.get(0);
		assertTrue(cp.getHostName().equals("localhost"));
		assertTrue(cp.getPort()==CqlEntityManagerDataStax.DEFAULT_PORT);
		cp=cps.get(1);
		assertTrue(cp.getHostName().equals("host2"));
		assertTrue(cp.getPort()==CqlEntityManagerDataStax.DEFAULT_PORT);
		cp=cps.get(2);
		assertTrue(cp.getHostName().equals("host3"));
		assertTrue(cp.getPort()==4546);
		
		
	}
	
}

package org.w3cloud.jom.datastax;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3cloud.jom.CqlBuilder;
import org.w3cloud.jom.CqlEntityManager;
import org.w3cloud.jom.CqlEntityManagerFactory;
import org.w3cloud.jom.CqlFilter;
import org.w3cloud.jom.datastax.CqlEntityManagerDataStax;
import org.w3cloud.jom.datastax.CqlScriptGenDataStax;
import org.w3cloud.jom.testmodels.AuditLog;
import org.w3cloud.jom.testmodels.AuditLogEnc;
import org.w3cloud.jom.testmodels.CountTestModel;
import org.w3cloud.jom.testmodels.DateWithTimeZone;
import org.w3cloud.jom.testmodels.DateWithTimeZoneEnc;
import org.w3cloud.jom.testmodels.IndexTestModel;
import org.w3cloud.jom.testmodels.IndexTestModelIndex;
import org.w3cloud.jom.testmodels.Option;
import org.w3cloud.jom.testmodels.OptionEnc;
import org.w3cloud.jom.testmodels.OptionResponse;
import org.w3cloud.jom.testmodels.OptionResponseEnc;
import org.w3cloud.jom.testmodels.OrderByTestModel;
import org.w3cloud.jom.testmodels.StoreAsJsonTest;
import org.w3cloud.jom.testmodels.StoreAsJsonTestEnc;
import org.w3cloud.jom.util.UUIDUtil;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.gson.Gson;

public class CqlEmTest {
	private  static CqlEntityManager em;
	@BeforeClass
	public static void setUp() {
		
		Cluster cluster=null;
		try{
			cluster= Cluster.builder()
					.addContactPoint("localhost")
					.build();
			Session session=cluster.connect();
			session.execute("CREATE KEYSPACE jom_test WITH replication " + 
					"= {'class':'SimpleStrategy', 'replication_factor':1};");
			session=cluster.connect("jom_test");
			Properties props=new Properties();
			props.put("cql.contactpoints", "127.0.0.1");
			props.put("cql.keyspace","jom_test");
			props.put("cql.synctableschema", "true");
			props.put("cql.packagestoscan", "org.w3cloud.jom.testmodels");
			em=CqlEntityManagerFactory.createEntityManger(props);

			//createTable(session, AuditLog.class);
			//createTable(session, StoreAsJsonTest.class);
			
		}finally{
			if (cluster!=null)
				cluster.close();
		}

	}

	@AfterClass
	public static void tearDown() {
		Cluster cluster=null;
		try{
			cluster= Cluster.builder()
					.addContactPoint("localhost")
					.build();
			Session session=cluster.connect();
			session.execute("DROP KEYSPACE jom_test");
		}finally{
			if (cluster!=null)
				cluster.close();
		}
	}

	
	@Test
	public void testInsert1() {
		AuditLog e=new AuditLog();
		e.restaurantId=UUIDUtil.getTimeUUID();
		e.employeeId=UUIDUtil.getTimeUUID();
		e.setAction("TestAction");
		e.details="InsertCqlTest";
		e.createDt=new DateWithTimeZone();
		e.createDt.d=new Date();
		e.createDt.z="EST";
		em.insert(e);
		assertTrue(e.id!=null);
		assertTrue(e.id.toString().length()>5);
		AuditLog al=em.findOne(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId));
		assertNotNull(al);
		assertNotNull(al.createDt);
	}
	@Test
	public void testInsertEnc1() {
		AuditLogEnc e=new AuditLogEnc();
		e.setRestaurantId(UUIDUtil.getTimeUUID());
		e.setEmployeeId(UUIDUtil.getTimeUUID());
		e.setAction("TestAction");
		e.setDetails("InsertCqlTest");
		e.setCreateDt(new DateWithTimeZoneEnc());
		e.getCreateDt().setD(new Date());
		e.getCreateDt().setZ("EST");
		em.insert(e);
		assertTrue(e.getId()!=null);
		assertTrue(e.getId().toString().length()>5);
		CqlEntityManagerDataStax emDataStax=(CqlEntityManagerDataStax)em;
		   Statement select = QueryBuilder.select().all().from("jom_test", "audit_log_enc")
	                .where(QueryBuilder.eq("restaurant_id", e.getRestaurantId())).and(QueryBuilder.eq("id", e.getId()));
		   ResultSet rs=emDataStax.session.execute(select);
		 assertTrue(rs.all().size()==1);
	}

	@Test
	public void testInsert2() {
		StoreAsJsonTest e=new StoreAsJsonTest();
		e.restaurantId=UUIDUtil.getTimeUUID();
		e.catId=UUIDUtil.getTimeUUID();
		e.code="A1";
		e.name="Bread";
		e.description="Home Backed";
		e.price=new BigDecimal(7.5);
		e.options=new ArrayList<Option>();
		Option o;
		o=new Option();
		e.options.add(o);
		o.id="soup";
		o.name="Soup";
		o.optType="Single";
		o.responses=new ArrayList<OptionResponse>();
		OptionResponse r;
		r=new OptionResponse();
		r.respText="Veg";
		r.extraCost=new BigDecimal(1.75);
		r.selected=false;
		o.responses.add(r);
		r=new OptionResponse();
		r.respText="Chickem";
		r.extraCost=new BigDecimal(1.5);
		r.selected=false;
		o.responses.add(r);
		
		o=new Option();
		e.options.add(o);
		o.id="soup";
		o.name="Salad";
		o.optType="Single";
		o.responses=new ArrayList<OptionResponse>();
		r=new OptionResponse();
		r.respText="Veg";
		r.extraCost=new BigDecimal(1.75);
		r.selected=false;
		o.responses.add(r);
		r=new OptionResponse();
		r.respText="Chickem";
		r.extraCost=new BigDecimal(1.5);
		r.selected=false;
		o.responses.add(r);
		em.insert(e);
		assertTrue(e.id!=null);
		assertTrue(e.id.toString().length()>5);
		CqlEntityManagerDataStax emDataStax=(CqlEntityManagerDataStax)em;
		   Statement select = QueryBuilder.select().all().from("jom_test", "store_as_json_test")
	                .where(QueryBuilder.eq("restaurant_id", e.restaurantId)).and(QueryBuilder.eq("cat_id", e.catId)).and(QueryBuilder.eq("id", e.id));
		   ResultSet rs=emDataStax.session.execute(select);
		 assertTrue(rs.all().size()==1);
	}
	@Test
	public void testInsertEnc2() {
		StoreAsJsonTestEnc e=new StoreAsJsonTestEnc();
		e.setRestaurantId(UUIDUtil.getTimeUUID());
		e.setCatId(UUIDUtil.getTimeUUID());
		e.setCode("A1");
		e.setName("Bread");
		e.setDescription("Home Backed");
		e.setPrice(new BigDecimal(7.5));
		e.setOptions(new ArrayList<OptionEnc>());
		OptionEnc o;
		o=new OptionEnc();
		e.getOptions().add(o);
		o.setId("soup");
		o.setName("Soup");
		o.setOptType("Single");
		o.setResponses(new ArrayList<OptionResponseEnc>());
		OptionResponseEnc r;
		r=new OptionResponseEnc();
		r.setRespText("Veg");
		r.setExtraCost(new BigDecimal(1.75));
		r.setSelected(false);
		o.getResponses().add(r);
		r=new OptionResponseEnc();
		r.setRespText("Chicken");
		r.setExtraCost(new BigDecimal(1.5));
		r.setSelected(false);
		o.getResponses().add(r);
		
		o=new OptionEnc();
		e.getOptions().add(o);
		o.setId("salad");
		o.setName("Salad");
		o.setOptType("Single");
		o.setResponses(new ArrayList<OptionResponseEnc>());
		r=new OptionResponseEnc();
		r.setRespText("Veg");
		r.setExtraCost(new BigDecimal(1.75));
		r.setSelected(false);
		o.getResponses().add(r);
		r=new OptionResponseEnc();
		r.setRespText("Chicken");
		r.setExtraCost(new BigDecimal(1.5));
		r.setSelected(false);
		o.getResponses().add(r);
		
		em.insert(e);
		assertTrue(e.getId()!=null);
		assertTrue(e.getId().toString().length()>5);
		CqlEntityManagerDataStax emDataStax=(CqlEntityManagerDataStax)em;
		   Statement select = QueryBuilder.select().all().from("jom_test", "store_as_json_test_enc")
	                .where(QueryBuilder.eq("restaurant_id", e.getRestaurantId())).and(QueryBuilder.eq("cat_id", e.getCatId())).and(QueryBuilder.eq("id", e.getId()));
		   ResultSet rs=emDataStax.session.execute(select);
		 assertTrue(rs.all().size()==1);
	}
	@Test
	public void testUpdate1() {
		StoreAsJsonTest e=new StoreAsJsonTest();
		e.restaurantId=UUIDUtil.getTimeUUID();
		e.catId=UUIDUtil.getTimeUUID();
		e.code="A1";
		e.name="Bread";
		e.description="Home Backed";
		e.price=new BigDecimal(7.5);
		e.options=new ArrayList<Option>();
		Option o;
		o=new Option();
		e.options.add(o);
		o.id="soup";
		o.name="Soup";
		o.optType="Single";
		o.responses=new ArrayList<OptionResponse>();
		OptionResponse r;
		r=new OptionResponse();
		r.respText="Veg";
		r.extraCost=new BigDecimal(1.75);
		r.selected=false;
		o.responses.add(r);
		r=new OptionResponse();
		r.respText="Chickem";
		r.extraCost=new BigDecimal(1.5);
		r.selected=false;
		o.responses.add(r);
		
		o=new Option();
		e.options.add(o);
		o.id="salad";
		o.name="Salad";
		o.optType="Single";
		o.responses=new ArrayList<OptionResponse>();
		r=new OptionResponse();
		r.respText="Veg";
		r.extraCost=new BigDecimal(1.75);
		r.selected=false;
		o.responses.add(r);
		r=new OptionResponse();
		r.respText="Chickem";
		r.extraCost=new BigDecimal(1.5);
		r.selected=false;
		o.responses.add(r);
		em.insert(e);
		assertTrue(e.id!=null);
		assertTrue(e.id.toString().length()>5);
		//Make some changes
		e.options.remove(0);
		e.code="B1";
		em.update(e);
		
		StoreAsJsonTest e2=em.findOne(CqlBuilder.select(StoreAsJsonTest.class).field("restaurantId").eq(e.restaurantId).field("catId").eq(e.catId).field("id").eq(e.id));

		Gson gson=new Gson();
		String e2str=gson.toJson(e2.options);
		
	}
	@Test
	public void testUpdateEnc1() {
		StoreAsJsonTestEnc e=new StoreAsJsonTestEnc();
		e.setRestaurantId(UUIDUtil.getTimeUUID());
		e.setCatId(UUIDUtil.getTimeUUID());
		e.setCode("A1");
		e.setName("Bread");
		e.setDescription("Home Backed");
		e.setPrice(new BigDecimal(7.5));
		e.setOptions(new ArrayList<OptionEnc>());
		OptionEnc o;
		o=new OptionEnc();
		e.getOptions().add(o);
		o.setId("soup");
		o.setName("Soup");
		o.setOptType("Single");
		o.setResponses(new ArrayList<OptionResponseEnc>());
		OptionResponseEnc r;
		r=new OptionResponseEnc();
		r.setRespText("Veg");
		r.setExtraCost(new BigDecimal(1.75));
		r.setSelected(false);
		o.getResponses().add(r);
		r=new OptionResponseEnc();
		r.setRespText("Chickem");
		r.setExtraCost(new BigDecimal(1.5));
		r.setSelected(false);
		o.getResponses().add(r);
		
		o=new OptionEnc();
		e.getOptions().add(o);
		o.setId("salad");
		o.setName("Salad");
		o.setOptType("Single");
		o.setResponses(new ArrayList<OptionResponseEnc>());
		r=new OptionResponseEnc();
		r.setRespText("Veg");
		r.setExtraCost(new BigDecimal(1.75));
		r.setSelected(false);
		o.getResponses().add(r);
		r=new OptionResponseEnc();
		r.setRespText("Chickem");
		r.setExtraCost(new BigDecimal(1.5));
		r.setSelected(false);
		o.getResponses().add(r);
		em.insert(e);
		assertTrue(e.getId()!=null);
		assertTrue(e.getId().toString().length()>5);
		//Make some changes
		e.getOptions().remove(0);
		e.setCode("B1");
		em.update(e);
		StoreAsJsonTestEnc e2=em.findOne(CqlBuilder.select(StoreAsJsonTestEnc.class).field("restaurantId").eq(e.getRestaurantId()).field("catId").eq(e.getCatId()).field("id").eq(e.getId()));
		Gson gson=new Gson();
		String e2str=gson.toJson(e2.getOptions());
	}

	@Test
	public void testIfTableExists() {
		CqlEntityManagerDataStax em=(CqlEntityManagerDataStax)CqlEmTest.em;
		CqlScriptGenDataStax gen=new CqlScriptGenDataStax();
		assertTrue(gen.ifTableExists(em.session,"audit_log")); 
		assertTrue(gen.ifTableExists(em.session,"audit_log2")==false);
	}
	@Test
	public void testIfColumnExists() {
		CqlEntityManagerDataStax em=(CqlEntityManagerDataStax)CqlEmTest.em;
		CqlScriptGenDataStax gen=new CqlScriptGenDataStax();
		assertTrue(gen.ifColumnExists(em.session,"audit_log", "id")); 
		assertTrue(gen.ifColumnExists(em.session,"audit_log", "id2")==false);
	}
	private static UUID restaurantId1=UUID.fromString("1fba8172-52f1-11e4-82f4-e811328fecf4");
	private static UUID employeeId1=UUID.fromString("b8d37e30-53bd-11e4-b43f-e811328fecf4");

	@Test
	public void testIssue1() {
		AuditLog e=new AuditLog();
		e.restaurantId=restaurantId1;
		e.employeeId=employeeId1;
		e.setAction("TestAction");
		e.details="InsertCqlTest";
		em.insert(e);
		assertTrue(e.id!=null);
		assertTrue(e.id.toString().length()>5);
		AuditLog al=em.findOne(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId));
		assertNotNull(al);
		assertNull(al.createDt);
	}
	public AuditLog createAuditLog(String details) {
		AuditLog e=new AuditLog();
		e.restaurantId=restaurantId1;
		e.employeeId=employeeId1;
		e.setAction("TestAction");
		e.details=details;
		em.insert(e);
		assertTrue(e.id!=null);
		assertTrue(e.id.toString().length()>5);
		return e;
	}
	public AuditLog createAuditLog(){
		return createAuditLog("InsertCqlTest");
	}

	@Test
	public void testStatementFindOne() {
		AuditLog e=createAuditLog();
		AuditLog al=em.findOne(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId));
		assertNotNull(al);
	}

	@Test
	public void testStatementFindAll() {
		AuditLog e=createAuditLog();
		List<AuditLog> als=em.findAll(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId));
		assertNotNull(als);
		assertTrue("Expected:>1; Actual:"+als.size(),als.size()>0);
	}
	@Test
	public void testStatementFindAllWithFilter() {
		final String details="WithFilterTest";
		AuditLog e=createAuditLog(details);
		List<AuditLog> als=em.findAll(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId),
			new CqlFilter<AuditLog>(){
				@Override
				public boolean allowThisEntity(AuditLog entity) {
					return entity.details.equals(details);
				}
			
		}
				);
		assertNotNull(als);
		assertTrue("Expected:1; Actual:"+als.size(),als.size()==1);
		AuditLog al=als.get(0);
		assertTrue("Expected Details:"+details+"\nActual:"+al.details, al.details.equals(details));
	}
	@Test
	public void testStatementUpdate() {
		String updatedDetails="UpdatedToThisValue";
		AuditLog e=createAuditLog();
		em.updateColumn(e, CqlBuilder.update(AuditLog.class).field("details").set(updatedDetails));
		List<AuditLog> als=em.findAll(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId).field("id").eq(e.id));
		assertNotNull(als);
		assertTrue(als.size()==1);
		AuditLog al=als.get(0);
		assertTrue(al.details.equals(updatedDetails));
	}
	@Test
	public void testUpdate2() {
		String updatedDetails="UpdatedToThisValue";
		AuditLog e=createAuditLog();
		e.details=updatedDetails;
		em.update(e);
		AuditLog al=em.findOne(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId).field("id").eq(e.id));
		assertNotNull(al);
		assertTrue(al.details.equals(updatedDetails));
	}

	@Test
	public void testStatementDelete() {
		AuditLog e=createAuditLog("DeleteCqlTest");
		AuditLog al;
		al=em.findOne(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId).field("id").eq(e.id));
		assertNotNull(al);
		em.delete(e);
		al=em.findOne(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId).field("id").eq(e.id));
		assertNull(al);
		
	}
	@Test
	public void testStoreAsJsonShouldAllowNulls() {
		StoreAsJsonTest e=new StoreAsJsonTest();
		e.restaurantId=UUIDUtil.getTimeUUID();
		e.catId=UUIDUtil.getTimeUUID();
		e.code="A1";
		e.name="Bread";
		e.description="Home Backed";
		e.price=new BigDecimal(7.5);
		//NOTE: e.options is null. 1.2.2 code fails with an exception.
		//AFter fixing it should not fail
		em.insert(e);
	}
	@Test
	public void testDeleteByKey() {
		AuditLog e=createAuditLog("DeleteCqlTest");
		long ct=em.count(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId).field("id").eq(e.id));
		assertTrue(ct==1);
		em.deleteByKey(AuditLog.class, e.restaurantId,e.employeeId, e.id);
		ct=em.count(CqlBuilder.select(AuditLog.class).field("restaurantId").eq(e.restaurantId).field("employeeId").eq(e.employeeId).field("id").eq(e.id));
		assertTrue(ct==0);
		
	}
	@Test
	public void testCounterTest() {
		CountTestModel ctm=new CountTestModel();
		String key="one";
		ctm.someKey=key;
		ctm.ct=0L;
		em.updateColumn(ctm, CqlBuilder.select(CountTestModel.class).field("ct").add(new Long(1)));
		CountTestModel ctm3=em.findOne(CqlBuilder.select(CountTestModel.class).field("someKey").eq(key));
		assertNotNull(ctm3);
		assertTrue(ctm3.ct==1);
	}

	@Test
	public void testOrderBy() {
		OrderByTestModel e1=new OrderByTestModel();
		e1.key1="5";
		e1.key2="bbb";
		e1.someData="SomeData";
		em.insert(e1);
		OrderByTestModel e2=new OrderByTestModel();
		e2.key1="3";
		e2.key2="aaa";
		e2.someData="SomeData";
		em.insert(e2);
		OrderByTestModel e3=new OrderByTestModel();
		e3.key1="4";
		e3.key2="ccc";
		e3.someData="SomeData";
		em.insert(e3);
		List<OrderByTestModel>es=em.findAll(CqlBuilder.select(OrderByTestModel.class).field("key1").in("5", "3", "4").orderBy("key2", false).limit(10));
		assertNotNull(es);
		assertTrue("Value got:"+es.get(0).key2, es.get(0).key2.equals("aaa"));
	}
	@Test
	public void testExtTableIndex() {
		IndexTestModel e1=new IndexTestModel();
		e1.key1="5";
		e1.key2="bbb";
		e1.someData="SomeData";
		em.insert(e1);
		IndexTestModel e2=new IndexTestModel();
		e2.key1="3";
		e2.key2="aaa";
		e2.someData="SomeData";
		em.insert(e2);
		IndexTestModel e3=new IndexTestModel();
		e3.key1="4";
		e3.key2="ccc";
		e3.someData="SomeData";
		em.insert(e3);
		IndexTestModelIndex ei1=new IndexTestModelIndex();
		ei1.searchText="great";
		ei1.key1=e1.key1;
		em.insert(ei1);
		ei1=new IndexTestModelIndex();
		ei1.searchText="great";
		ei1.key1=e2.key1;
		em.insert(ei1);
		Object[] objs=em.findAllOneColumn("key1", CqlBuilder.select(IndexTestModelIndex.class).field("searchText").eq("great"));
		assertNotNull(objs);
		List<IndexTestModel>es=em.findAll(CqlBuilder.select(IndexTestModel.class).field("key1").in(objs).orderBy("key2", false).limit(10));
		assertNotNull(es);
		assertTrue(es.size()==2);
		assertTrue("Value got:"+es.get(0).key2, es.get(0).key2.equals("aaa"));
	}





}

package org.w3cloud.jom.datastax;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3cloud.jom.CqlEntityManager;
import org.w3cloud.jom.CqlEntityManagerFactory;
import org.w3cloud.jom.datastax.CqlEntityManagerDataStax;
import org.w3cloud.jom.datastax.CqlScriptGenDataStax;
import org.w3cloud.jom.testmodels.AuditLog;
import org.w3cloud.jom.testmodels.AuditLogEnc;
import org.w3cloud.jom.testmodels.DateWithTimeZone;
import org.w3cloud.jom.testmodels.DateWithTimeZoneEnc;
import org.w3cloud.jom.testmodels.Option;
import org.w3cloud.jom.testmodels.OptionEnc;
import org.w3cloud.jom.testmodels.OptionResponse;
import org.w3cloud.jom.testmodels.OptionResponseEnc;
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
			props.put("cql.contactpoint", "localhost");
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

		System.out.println("setting up");
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

		System.out.println("tearing down");
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
		CqlEntityManagerDataStax emDataStax=(CqlEntityManagerDataStax)em;
		   Statement select = QueryBuilder.select().all().from("jom_test", "audit_log")
	                .where(QueryBuilder.eq("restaurant_id", e.restaurantId)).and(QueryBuilder.eq("id", e.id));
		   ResultSet rs=emDataStax.session.execute(select);
		 assertTrue(rs.all().size()==1);
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
		
		StoreAsJsonTest e2=em.findOne(StoreAsJsonTest.class,"where restaurant_id=? and cat_id=? and id=?" , e.restaurantId, e.catId,  e.id);
		
		Gson gson=new Gson();
		String e2str=gson.toJson(e2.options);
		System.out.println(e2str);
		
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
		
		StoreAsJsonTestEnc e2=em.findOne(StoreAsJsonTestEnc.class,"where restaurant_id=? and cat_id=? and id=?" , e.getRestaurantId(), e.getCatId(),  e.getId());
		Gson gson=new Gson();
		String e2str=gson.toJson(e2.getOptions());
		System.out.println(e2str);
	}

	@Test
	public void testIfTableExists() {
		CqlEntityManagerDataStax em=(CqlEntityManagerDataStax)this.em;
		CqlScriptGenDataStax gen=new CqlScriptGenDataStax();
		assertTrue(gen.ifTableExists(em.session,"audit_log")); 
		assertTrue(gen.ifTableExists(em.session,"audit_log2")==false);
	}
	@Test
	public void testIfColumnExists() {
		CqlEntityManagerDataStax em=(CqlEntityManagerDataStax)this.em;
		CqlScriptGenDataStax gen=new CqlScriptGenDataStax();
		assertTrue(gen.ifColumnExists(em.session,"audit_log", "id")); 
		assertTrue(gen.ifColumnExists(em.session,"audit_log", "id2")==false);
	}
}

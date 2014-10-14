package org.w3cloud.jom;

import java.util.Date;
import java.util.Properties;

import org.junit.Test;
import org.w3cloud.jom.CqlEntityManager;
import org.w3cloud.jom.CqlEntityManagerFactory;
import org.w3cloud.jom.testmodels.AuditLog;
import org.w3cloud.jom.testmodels.DateWithTimeZone;
import org.w3cloud.jom.util.UUIDUtil;

public class CreateCqlTest {
	@Test
	public void main() {
		Properties props=new Properties();
		props.put("cql.contactpoint", "localhost");
		props.put("cql.keyspace","demo");
		CqlEntityManager em=CqlEntityManagerFactory.createEntityManger(props);
		AuditLog al=new AuditLog();
		al.restaurantId=UUIDUtil.getTimeUUID();
		al.id=UUIDUtil.getTimeUUID();
		al.employeeId=UUIDUtil.getTimeUUID();
		al.setAction("TestAction");
		al.details="InsertCqlTest";
		al.createDt=new DateWithTimeZone();
		al.createDt.d=new Date();
		al.createDt.z="EST";
		em.insert(al);
	}

}

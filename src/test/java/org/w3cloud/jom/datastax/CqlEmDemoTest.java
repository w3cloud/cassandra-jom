package org.w3cloud.jom.datastax;

import org.junit.Test;
import org.w3cloud.jom.datastax.CqlEntityManagerDataStax;
import org.w3cloud.jom.testmodels.AuditLog;

public class CqlEmDemoTest {
	@Test
	public void main() {
		CqlEntityManagerDataStax em=new CqlEntityManagerDataStax();
		String insertCql=em.buildInsertCql(AuditLog.class);
		System.out.println(insertCql);
	}

}

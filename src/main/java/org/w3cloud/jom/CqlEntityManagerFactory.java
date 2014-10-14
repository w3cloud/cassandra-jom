package org.w3cloud.jom;

import java.util.Properties;

import org.w3cloud.jom.datastax.CqlEntityManagerDataStax;

public class CqlEntityManagerFactory {
	public static CqlEntityManager createEntityManger(Properties props){
		
		return new CqlEntityManagerDataStax(props);
	}


}

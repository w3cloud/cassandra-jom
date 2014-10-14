package org.w3cloud.jom;

import org.w3cloud.jom.datastax.CqlScriptGenDataStax;

public class CqlScriptGenFactory {
	public static CqlScriptGen create(){
		return new CqlScriptGenDataStax();
	}

}

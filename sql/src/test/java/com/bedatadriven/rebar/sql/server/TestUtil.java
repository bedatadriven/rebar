package com.bedatadriven.rebar.sql.server;

import java.util.Date;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlDatabaseFactory;
import com.bedatadriven.rebar.sql.server.jdbc.JdbcDatabaseFactory;

public abstract class TestUtil {
	
	public static String uniqueDbName() {
		return "target/testdb" + new Date().getTime();
	}
	
	public static SqlDatabase openUniqueDb() {
    String name = uniqueDbName();
    System.err.println("Opening db " + name + " for testing...");
    
    SqlDatabaseFactory factory = new JdbcDatabaseFactory();
		return factory.open(name);
	}

}

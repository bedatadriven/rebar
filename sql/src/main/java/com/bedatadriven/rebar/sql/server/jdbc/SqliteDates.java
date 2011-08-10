package com.bedatadriven.rebar.sql.server.jdbc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SqliteDates {
	
	public static DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	public static String format(Date date) {
		return FORMAT.format(date);
	}
	


}

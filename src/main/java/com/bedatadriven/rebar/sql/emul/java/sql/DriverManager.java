/*
 * @(#)DriverManager.java	1.54 06/10/18
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

import com.bedatadriven.rebar.sql.client.GearsConnectionFactory;

public class DriverManager {

//    public static java.io.PrintWriter getLogWriter() {
//    }

//    public static void setLogWriter(java.io.PrintWriter out) {
//    }


    public static Connection getConnection(String url, java.util.Properties info) throws SQLException {
      return getConnection(url);
    }

    public static Connection getConnection(String url, String user, String password) throws SQLException {
      return getConnection(url);
    }

    public static Connection getConnection(String url) 	throws SQLException {
      if(url.startsWith("jdbc:sqlite:")) {
        return GearsConnectionFactory.getConnection(url.substring(12));
      } else {
        return GearsConnectionFactory.getConnection(url);
      }
    }

//    public static Driver getDriver(String url) throws SQLException {
//    }

    public static void setLoginTimeout(int seconds) {
        loginTimeout = seconds;
    }

    public static int getLoginTimeout() {
        return (loginTimeout);
    }

    private DriverManager(){}

    private static int loginTimeout = 0;
}

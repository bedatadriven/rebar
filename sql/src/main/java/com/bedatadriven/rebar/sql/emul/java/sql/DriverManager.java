/*
 * Copyright 2009-2010 BeDataDriven (alex@bedatadriven.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
      if(url.startsWith("jdbc:jdbc:")) {
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

package com.bedatadriven.rebar.appcache.test;


import com.bedatadriven.rebar.appcache.test.client.AppVersion;

public interface ServerDriver {
  
  public String getUrl();

  /**
   * Starts the server
   */
  void start(AppVersion appVersion) throws Exception;

  /**
   * Changes the application version currently serving
   */
  void deployUpdate(AppVersion appVersion) throws Exception;

  /**
   * Stop the server
   */
  void stop() throws Exception;
  
  void stopIfRunning() throws Exception;

}

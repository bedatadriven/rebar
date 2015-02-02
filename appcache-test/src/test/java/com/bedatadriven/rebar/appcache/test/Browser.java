package com.bedatadriven.rebar.appcache.test;


import org.openqa.selenium.WebDriver;

import java.io.IOException;

public interface Browser {
  
  PageModel navigateTo(ServerDriver server);

  /**
   * 
   * @return the expected GWT user agent property for this browser
   */
  String expectedUserAgent();
  
  WebDriver driver();
  
  void screenshot(String name);
  
  boolean isOpen();

  /**
   * Closes the browser window
   */
  void close();

  /**
   * Shutdowns the browser, disposing of the WebDriver
   */
  void shutdown();
  
}

package com.bedatadriven.rebar.appcache.test.client;

public enum AppVersion {
  
  VERSION1,
  
  VERSION2;
  
  
  public String getWarDir() {
    return name().toLowerCase();
  }

}

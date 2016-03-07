package com.bedatadriven.rebar.appcache.test.client;


public enum ElementId {
  
  
  
  RUNTIME_USER_AGENT,
  COMPILE_USER_AGENT,
  
  UPDATE_STATUS,
  CHECK_FOR_UPDATE,
  UPDATE_BUTTON,
  
  LOAD_ASYNC_BUTTON, 
  ENSURE_CACHE_UPDATED,
  
  APP_CACHE_STATUS,
  PROGRESS_FILES_COMPLETE,
  PROGRESS_FILES_TOTAL,
  
  VERSION_LABEL,
  LOAD_UPDATE,
  I18N_TEXT, 
  LOCALE,
  NETWORK_RESOURCE,
  FETCH_NETWORK_RESOURCE;

  public String id() {
    return name().toLowerCase().replace("_", "-");
  }
}

package com.bedatadriven.rebar.dao;

import org.apache.maven.plugin.logging.Log;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class MavenValidationHandler implements ValidationEventHandler {

  private final Log log;

  public MavenValidationHandler(Log log) {
    this.log = log;
  }

  @Override
  public boolean handleEvent(ValidationEvent event) {
    switch (event.getSeverity()) {
      case ValidationEvent.WARNING:
        log.warn(event.getMessage());
        return true;

      case ValidationEvent.ERROR:
        log.error(event.getMessage());
        return false;
      case ValidationEvent.FATAL_ERROR:
        log.error(event.getMessage());
        return false;

      default:
        log.debug(event.getMessage());
        return true;
    }
  }
}

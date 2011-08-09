package com.bedatadriven.rebar.dao.model;

import javax.xml.bind.annotation.XmlAttribute;


public class QueryParameter {
  private String name;
  private String type;


  @XmlAttribute(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlAttribute(name = "type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}

package com.bedatadriven.rebar.dao.model;

import javax.xml.bind.annotation.XmlAttribute;

public class EmbeddedProperty {
  private String name;
  private String source;

  @XmlAttribute
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlAttribute
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }


}

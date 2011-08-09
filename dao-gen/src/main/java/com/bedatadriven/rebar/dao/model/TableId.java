package com.bedatadriven.rebar.dao.model;

import javax.xml.bind.annotation.XmlAttribute;

public class TableId {
  private TableIdType type;

  @XmlAttribute
  public TableIdType getType() {
    return type;
  }

  public void setType(TableIdType type) {
    this.type = type;
  }


}

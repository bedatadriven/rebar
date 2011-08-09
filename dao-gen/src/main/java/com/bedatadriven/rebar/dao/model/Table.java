package com.bedatadriven.rebar.dao.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;


public class Table {

  private TableIdType idType = TableIdType.AUTO_INT;
  private String name;
  private List<Column> columns = new ArrayList<Column>(0);

  @XmlAttribute
  public TableIdType getIdType() {
    return idType;
  }

  public void setIdType(TableIdType idType) {
    this.idType = idType;
  }

  @XmlAttribute
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlElement(name = "column")
  public List<Column> getColumns() {
    return columns;
  }

  public void setColumns(List<Column> properties) {
    this.columns = properties;
  }

  public Column getColByName(String name) {
    for (Column column : columns) {
      if (column.getName().equals(name)) {
        return column;
      }
    }
    throw new IllegalArgumentException("No column named '" + name + "'");
  }

  public boolean hasColumn(String name) {
    for (Column column : columns) {
      if (column.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }
}

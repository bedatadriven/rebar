package com.bedatadriven.rebar.dao.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Column {

  private String name;
  private FieldType type;
  private boolean required;
  private String description;
  private TableId id;

  @XmlAttribute
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TableId getId() {
    return id;
  }

  public void setId(TableId id) {
    this.id = id;
  }

  @XmlElement
  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  @XmlElement
  public FieldType getType() {
    return type;
  }

  public void setType(FieldType type) {
    this.type = type;
  }

  @XmlElement
  public String getDescription() {
    return description == null ? "" : description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return computes the java type of the property
   */
  public String getJavaClass() {
    switch (type) {
      case TEXT:
        return "String";
      case INTEGER:
        return required ? "int" : "Integer";
      case REAL:
        return required ? "double" : "Double";
    }
    throw new UnsupportedOperationException();
  }

}

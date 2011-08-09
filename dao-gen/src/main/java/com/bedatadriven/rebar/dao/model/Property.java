package com.bedatadriven.rebar.dao.model;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

public class Property {

  private View view;

  private String name;
  private String source;
  private String type;
  private String description;
  private List<EmbeddedProperty> properties;

  @XmlAttribute
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlAttribute
  public String getSource() {
    if (source == null) {
      return name;
    }
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  @XmlElement
  public String getType() {
    if (type != null) {
      return type;
    }
    Column col = view.locateColumn(getSource());
    if (col != null) {
      return col.getJavaClass();
    }
    throw new UnsupportedOperationException("Type of '" + view.getName() + "." + getName() +
      "' cannot be inferred and must be provided with type element");
  }

  @XmlTransient
  public Class getPropertyClass() {
    try {
      String className = getType();
      if (className.indexOf('.') == -1) {
        return Class.forName("java.lang." + className);
      } else {
        return Class.forName(getType());
      }
    } catch (ClassNotFoundException e) {
      throw new ModelException("invalid type '" + getType() + "'");
    }
  }

  public boolean isBoolean() {
    Class clazz = getPropertyClass();
    return clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE);
  }

  public void setType(String type) {
    this.type = type;
  }


  @XmlElementWrapper(name = "properties")
  @XmlElement(name = "property")
  public List<EmbeddedProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<EmbeddedProperty> properties) {
    this.properties = properties;
  }

  @XmlTransient
  public View getView() {
    return view;
  }

  public void setView(View view) {
    this.view = view;
  }

  @XmlElement
  public String getDescription() {
    if (description == null) {
      Column col = view.locateColumn(getSource());
      if (col != null) {
        return col.getDescription();
      } else {
        return "";
      }
    }
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void afterUnmarshal(Unmarshaller u, Object parent) {
    this.view = (View) parent;
  }

  public String getGetter() {
    return (isBoolean() ? "is" : "get") +
      name.substring(0, 1).toUpperCase() +
      name.substring(1);
  }

  public String getSetter() {
    return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
  }
}

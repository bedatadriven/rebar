package com.bedatadriven.rebar.dao.model;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

public class View {

  private DaoModel model;

  private String name;
  private String baseTableName;

  private List<Property> properties;
  private List<Query> queries;


  @XmlAttribute
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlAttribute(name = "baseTable")
  public String getBaseTableName() {
    return baseTableName;
  }

  public void setBaseTableName(String baseTable) {
    this.baseTableName = baseTable;
  }

  public Table getBaseTable() {
    return model.getTableByName(baseTableName);
  }

  @XmlElement(name = "property")
  public List<Property> getProperties() {
    return properties;
  }

  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }

  @XmlElement(name = "query")
  public List<Query> getQueries() {
    return queries;
  }

  public void setQueries(List<Query> queries) {
    this.queries = queries;
  }

  @XmlTransient
  public DaoModel getModel() {
    return model;
  }

  public void setModel(DaoModel model) {
    this.model = model;
  }

  // called automagically by JAXB
  public void afterUnmarshal(Unmarshaller u, Object parent) {
    this.model = (DaoModel) parent;
  }

  public Column locateColumn(String source) {
    return locateColumn(new ColumnReference(source));
  }

  public Column locateColumn(ColumnReference source) {
    if (source.isTableSpecified() && !source.getTable().equalsIgnoreCase(getBaseTableName())) {
      return null; // can only locate columns on the base table
    }
    if (getBaseTable().hasColumn(source.getColumn())) {
      return getBaseTable().getColByName(source.getColumn());
    }
    return null;
  }

  public String getDtoClass() {
    return model.getPackageName() + "." + name + "DTO";
  }

}

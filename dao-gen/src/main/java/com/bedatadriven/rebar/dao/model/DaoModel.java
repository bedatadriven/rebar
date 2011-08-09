package com.bedatadriven.rebar.dao.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.List;

@XmlRootElement(name = "dao")
public class DaoModel {

  private String name;
  private List<Table> tables;
  private List<Query> queries;
  private String packageName;

  /**
   * @return the name of the DAO. Used as a prefix for all DAO-related interfaces/classes
   */
  @XmlAttribute(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlElement(name = "table")
  public List<Table> getTables() {
    return tables;
  }

  public void setTables(List<Table> entities) {
    this.tables = entities;
  }

  public Table getTableByName(String name) {
    for (Table table : tables) {
      if (table.getName().equals(name)) {
        return table;
      }
    }
    throw new IllegalArgumentException("No table by name of '" + name + "'");
  }

  @XmlElement(name = "query")
  public List<Query> getQueries() {
    return queries;
  }

  public void setQueries(List<Query> queries) {
    this.queries = queries;
  }

  /**
   * @return the root java package to place generated java classes in
   */
  @XmlAttribute(name = "package", required = true)
  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String computeSourcePath(File sourceRoot, String className) {
    return sourceRoot.getAbsolutePath() + File.separator +
      packageName.replace('.', File.separatorChar) + className + ".java";
  }

  public void validate() {


  }

}

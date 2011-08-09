package com.bedatadriven.rebar.dao.model;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.StringReader;
import java.util.List;

public class Query {

  private String name;
  private String resultType;
  private String sql;
  private String description;
  private List<QueryParameter> parameters;
  private Statement statement;

  @XmlAttribute
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlElement
  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public Statement getStatement() {
    if (statement == null) {
      CCJSqlParserManager pm = new CCJSqlParserManager();
      try {
        this.statement = pm.parse(new StringReader(sql));
      } catch (JSQLParserException e) {
        throw new ModelException("Error parsing SQL in query '" + name + "'", e);
      }
    }
    return statement;
  }

  public String getEscapedSql() {
    StringBuilder sb = new StringBuilder();
    String sqlTrimmed = sql.trim();
    for (int i = 0; i != sqlTrimmed.length(); ++i) {
      int cp = sqlTrimmed.codePointAt(i);
      if (cp == '\n' || cp == '\r') {
        sb.append(" ");
      } else if (cp == '"') {
        sb.append("\\\"");
      } else {
        sb.appendCodePoint(cp);
      }
    }
    return sb.toString();
  }

  @XmlElement
  public String getDescription() {
    return description == null ? "" : description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @XmlElement(name = "parameter")
  public List<QueryParameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<QueryParameter> parameters) {
    this.parameters = parameters;
  }

  @XmlElement
  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }
}

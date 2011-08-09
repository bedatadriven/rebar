package com.bedatadriven.rebar.dao.model.sql;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;

import java.util.ArrayList;
import java.util.List;

public class ParameterFinder implements SelectVisitor, ExpressionVisitor {

  private List<JdbcParameter> parameters = new ArrayList<JdbcParameter>();

  @Override
  public void visit(PlainSelect statement) {
    statement.getWhere().accept(this);
  }

  @Override
  public void visit(Union statement) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(NullValue arg0) {

  }

  @Override
  public void visit(Function function) {
    function.accept(this);
  }

  @Override
  public void visit(InverseExpression inverseExpression) {
    inverseExpression.accept(this);
  }

  @Override
  public void visit(JdbcParameter jdbcParameter) {
    parameters.add(jdbcParameter);
  }

  @Override
  public void visit(DoubleValue doubleValue) {
  }

  @Override
  public void visit(LongValue longValue) {
  }

  @Override
  public void visit(DateValue dateValue) {
  }

  @Override
  public void visit(TimeValue timeValue) {
  }

  @Override
  public void visit(TimestampValue timestampValue) {
  }

  @Override
  public void visit(Parenthesis parenthesis) {
    parenthesis.accept(this);
  }

  @Override
  public void visit(StringValue stringValue) {
  }

  @Override
  public void visit(Addition addition) {
    addition.accept(this);
  }

  @Override
  public void visit(Division division) {
    division.accept(this);
  }

  @Override
  public void visit(Multiplication multiplication) {
    multiplication.accept(this);
  }

  @Override
  public void visit(Subtraction subtraction) {
    subtraction.accept(this);
  }

  @Override
  public void visit(AndExpression andExpression) {
    andExpression.accept(this);
  }

  @Override
  public void visit(OrExpression orExpression) {
    orExpression.accept(this);
  }

  @Override
  public void visit(Between between) {
    between.accept(this);
  }

  @Override
  public void visit(EqualsTo equalsTo) {
    equalsTo.accept(this);
  }

  @Override
  public void visit(GreaterThan greaterThan) {
    greaterThan.accept(this);
  }

  @Override
  public void visit(GreaterThanEquals greaterThanEquals) {
    greaterThanEquals.accept(this);
  }

  @Override
  public void visit(InExpression inExpression) {
    inExpression.accept(this);
  }

  @Override
  public void visit(IsNullExpression isNullExpression) {

  }

  @Override
  public void visit(LikeExpression likeExpression) {
    likeExpression.accept(this);
  }

  @Override
  public void visit(MinorThan minorThan) {
    minorThan.accept(this);
  }

  @Override
  public void visit(MinorThanEquals minorThanEquals) {
    minorThanEquals.accept(this);
  }

  @Override
  public void visit(NotEqualsTo notEqualsTo) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Column tableColumn) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(SubSelect subSelect) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(CaseExpression caseExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(WhenClause whenClause) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ExistsExpression existsExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(AllComparisonExpression allComparisonExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(AnyComparisonExpression anyComparisonExpression) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Concat concat) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Matches matches) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(BitwiseAnd bitwiseAnd) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(BitwiseOr bitwiseOr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(BitwiseXor bitwiseXor) {
    // TODO Auto-generated method stub

  }


}

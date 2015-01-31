package com.bedatadriven.rebar.sql.server.dao;

import com.bedatadriven.rebar.sql.shared.dao.SqlDao;
import com.bedatadriven.rebar.sql.shared.dao.annotations.Delete;
import com.bedatadriven.rebar.sql.shared.dao.annotations.Select;

public interface MyDao extends SqlDao {

  @Select(where = "name=?")
  MyObject selectbyName(String name);

  void insert(MyObject object);

  @Delete(from = MyObject.class, where = "")
  void delete();


}

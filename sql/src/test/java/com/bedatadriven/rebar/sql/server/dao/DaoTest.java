package com.bedatadriven.rebar.sql.server.dao;

import com.bedatadriven.rebar.sql.server.jdbc.SqliteStubDatabase;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class DaoTest {

  @Test
  public void simpleTest() {

    SqliteStubDatabase db = new SqliteStubDatabase("target/test" + new Date().getTime());
    db.execute("create table MyObject (name TEXT, count INT)");
    db.execute("insert into MyObject (name, count) VALUES (?, ?)", new Object[]{"foo", 66});
    db.execute("insert into MyObject (name, count) VALUES (?, ?)", new Object[]{"bar", 99});

    MyDao dao = DaoFactory.create(db, MyDao.class);
    MyObject obj = dao.selectbyName("foo");

    assertThat(obj.getName(), equalTo("foo"));
    assertThat(obj.getCount(), equalTo(66));


  }


}

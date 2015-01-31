package com.bedatadriven.rebar.sql.server.dao;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.shared.dao.SqlDao;

import java.lang.reflect.Proxy;

public class DaoFactory {

  @SuppressWarnings("unchecked")
  public static <T extends SqlDao> T create(SqlDatabase database, Class<T> daoClass) {
    ClassLoader cl = daoClass.getClassLoader();
    return (T) Proxy.newProxyInstance(cl,
        new Class[]{daoClass},
        new DaoInvocationHandler(database));
  }

}

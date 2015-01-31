package com.bedatadriven.rebar.sql.server;

import com.bedatadriven.rebar.async.NullCallback;
import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.util.SqlKeyValueTable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SqlKeyValueTableTest {


  @Test
  public void simpleTest() {

    SqlDatabase db = TestUtil.openUniqueDb();
    final SqlKeyValueTable table = new SqlKeyValueTable(db, "sync_regions", "id", "lastUpdate");

    db.execute(table.createTableIfNotExists(), NullCallback.forVoid());

    table.put("foo", "bar");
    table.get("foo", new AsyncCallback<String>() {

      @Override
      public void onSuccess(String result) {
        assertThat(result, equalTo("bar"));
      }

      @Override
      public void onFailure(Throwable caught) {
        throw new AssertionError(caught);
      }
    });
  }


}

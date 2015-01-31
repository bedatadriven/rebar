package com.bedatadriven.rebar.async;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AsyncCommand {

  void execute(AsyncCallback<Void> callback);

}

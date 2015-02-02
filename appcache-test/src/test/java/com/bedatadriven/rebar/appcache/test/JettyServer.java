package com.bedatadriven.rebar.appcache.test;

import com.bedatadriven.rebar.appcache.test.client.AppVersion;
import com.google.common.base.Preconditions;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

public class JettyServer implements ServerDriver {

  private final int port;
  private Server server;

  public JettyServer() {
    this.port = 8000 + new Random().nextInt(5000);
  }

  @Override
  public String getUrl() {
    return "http://localhost:" + port + "/";
  }

  @Override
  public void start(AppVersion appVersion) throws Exception {
    WebAppContext context = new WebAppContext(TargetDir.getWarDir(appVersion).toString(), "/");
    server = new Server(port);
    server.setHandler(new SlowHandler(context));
    server.start();
    System.out.println(appVersion + " running at " + getUrl());
  }
  
  @Override
  public void stop() throws Exception {
    Preconditions.checkState(server != null);
    server.stop();
    server = null;
  }

  @Override
  public void stopIfRunning() throws Exception {
    if(server != null) {
      stop();
    }
  }

  @Override
  public void deployUpdate(AppVersion appVersion) throws Exception {
    server.stop();
    start(appVersion);
  }

  private static class SlowHandler extends HandlerWrapper {

    public SlowHandler(WebAppContext context) {
      setHandler(context);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException {

      try {
        // Add a delay to each request to ensure that we have time
        // to observe and verify AppCache events
        Thread.sleep(50);
      } catch (InterruptedException ignored) {
      }
      super.handle(target, baseRequest, request, response);
    }
  }
}

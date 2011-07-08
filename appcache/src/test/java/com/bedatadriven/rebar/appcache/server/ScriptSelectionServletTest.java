/*
 * Copyright 2009-2010 BeDataDriven (alex@bedatadriven.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.bedatadriven.rebar.appcache.server;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class ScriptSelectionServletTest {

  public static final String MS_IE_8 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB0.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; GACID=)";

  @Before
  public void enableLogging() {
    Logger.getLogger(DefaultSelectionServlet.class.getName()).setLevel(Level.FINEST);
  }

  @Test
  public void serveCorrectPermutation() throws ServletException, IOException {


    ServletContext context = createMock(ServletContext.class);
    expect(context.getRealPath(eq("/ActivityInfo/permutations"))).andReturn(permutationPath()).anyTimes();
    expect(context.getRealPath(eq("/ActivityInfo/F461B4925CA75C3608BEFC78A0C4CF03.nocache.js")))
        .andReturn(dummyContentPath()).anyTimes();
    replay(context);

    ServletConfig config = createMock(ServletConfig.class);
    expect(config.getServletContext()).andReturn(context);
    replay(config);

    DefaultSelectionServlet servlet = new DefaultSelectionServlet();
    servlet.init(config);

    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn("/ActivityInfo/ActivityInfo.nocache.js");
    expect(request.getHeader(eq("User-Agent"))).andReturn(MS_IE_8).anyTimes();
    replay(request);

    HttpServletResponse response = createNiceMock(HttpServletResponse.class);
    expect(response.getOutputStream()).andReturn(stdOut());
    replay(response);

    servlet.doGet(request, response);
    
    verify(context, config, request, response);
  }

  private ServletOutputStream stdOut() {
    return new ServletOutputStream() {
      @Override
      public void write(int b) throws IOException {
        System.out.write(b);
      }
    };
  }

  private String dummyContentPath() {
    return getClass().getResource("/permutation.content").getFile();
  }

  private String permutationPath() {
    return getClass().getResource("/permutations").getFile();
  }
}

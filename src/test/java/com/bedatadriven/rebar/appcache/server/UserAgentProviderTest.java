package com.bedatadriven.rebar.appcache.server;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class UserAgentProviderTest {
  
  
  @Test
  public void testUbuntuFirefox() {
    
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getHeader(eq("User-Agent")))
      .andReturn("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
    replay(request);
    
    UserAgentProvider provider = new UserAgentProvider();
    assertThat(provider.get(request), equalTo("gecko1_8"));
    
  }

}

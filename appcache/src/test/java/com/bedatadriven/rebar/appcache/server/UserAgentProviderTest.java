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

  
  @Test
  public void gearsSupported() {
  	
  	UserAgentProvider provider = new UserAgentProvider();
  	assertThat(provider.canSupportGears("Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.12) Gecko/20101026 Firefox/3.6.12 ( .NET CLR 3.5.30729; .NET4.0E)".toLowerCase()), 
  			equalTo(true));
  	assertThat(provider.canSupportGears("Mozilla/5.0 (X11; Linux x86_64; rv:5.0) Gecko/20100101 Firefox/5.0 FirePHP/0.5".toLowerCase()), 
  			equalTo(false));
  	
  	assertThat(provider.canSupportGears("Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 5.0; Trident/4.0; FBSMTWB; .NET CLR 2.0.34861; .NET CLR 3.0.3746.3218; .NET CLR 3.5.33652; msn OptimizedIE8;ENUS)".toLowerCase()), 
  			equalTo(true));
  	assertThat(provider.canSupportGears("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 7.1; Trident/5.0)".toLowerCase()), 
  			equalTo(false));
  }
  
}

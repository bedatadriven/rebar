package com.bedatadriven.rebar.appcache.test;

import com.bedatadriven.rebar.appcache.client.AppCache;
import com.bedatadriven.rebar.appcache.test.client.AppVersion;
import com.bedatadriven.rebar.appcache.test.client.ElementId;
import org.junit.*;


public class AppCacheIT {
  
  @Rule
  public ScreenshotLogger logger = new ScreenshotLogger();
  
  private ServerDriver server;
  private Browser browser;
  
  @Before
  public void setUp() throws Exception {
    browser = new PhantomJsBrowser(logger);
    server = new JettyServer();
    server.start(AppVersion.VERSION1);
  }

  @Test
  public void appCacheDownloaded() throws Exception {

    // Open the browser and navigate to the server for 
    // the first time
    PageModel page = browser.navigateTo(server);
    page.assertAppCacheDownloadsWithProgress();
    page.assertUserAgentMatches(browser);
    page.assertVersionIs(AppVersion.VERSION1);
    page.assertAsyncFragmentLoads();
    browser.screenshot("loaded");
  }


  @Test
  public void offline() throws Exception {

    // Open the browser and navigate to the server for 
    // the first time
    PageModel page = browser.navigateTo(server);
    page.assertAppCacheDownloadsWithProgress();
    browser.screenshot("initial-load");
    browser.close();
    
    // Now shutdown the server and reload the page
    server.stop();
    
    page = browser.navigateTo(server);
    page.assertThatPageLoads();
    page.assertVersionIs(AppVersion.VERSION1);
    page.assertAsyncFragmentLoads();
    browser.screenshot("offline-load");
  }
  
  @Test
  public void forceVersionUpdate() throws Exception {
    
    // load the initial version of the page and ensure it's cached
    PageModel page = browser.navigateTo(server);
    page.assertAppCacheDownloadsWithProgress();
    
    // Now deploy a new version in the background
    server.deployUpdate(AppVersion.VERSION2);
    
    // Trigger a check manually
    page.click(ElementId.CHECK_FOR_UPDATE);
    page.waitUntilUpdateIsAvailable(1);
    page.assertAppCacheStatusIs(AppCache.Status.UPDATE_READY);
    browser.screenshot("update-ready");
    
    // Force browser update to get new version
    page.click(ElementId.LOAD_UPDATE);
    
    // Wait for the reload with the new version
    page.assertThatPageLoads();
    page.assertVersionIs(AppVersion.VERSION2);

    browser.screenshot("update-loaded");
  }
  
  @Ignore
  @Test
  public void versionUpdate() throws Exception {

    // load the initial version of the page and ensure it's cached
    PageModel page = browser.navigateTo(server);
    page.assertAppCacheDownloadsWithProgress();

    // Now deploy a new version in the background
    server.deployUpdate(AppVersion.VERSION2);

    // Wait until the browser finds an update in its
    // periodic checks
    page.waitUntilUpdateIsAvailable(5);
    page.assertAppCacheStatusIs(AppCache.Status.UPDATE_READY);
    browser.screenshot("update-ready");

    // Force browser update to get new version
    page.click(ElementId.LOAD_UPDATE);

    // Wait for the reload with the new version
    page.assertThatPageLoads();
    page.assertVersionIs(AppVersion.VERSION2);

    browser.screenshot("update-loaded");
  }
  
  @Test
  public void versionUpdateOnSecondVisit() throws Exception {

    // load the initial version of the page and ensure it's cached
    PageModel page = browser.navigateTo(server);
    page.assertAppCacheDownloadsWithProgress();
    browser.close();
    
    // Now deploy a new version in the background
    server.deployUpdate(AppVersion.VERSION2);

    // Reopen the browser to the page
    // The original version should be served from the AppCache
    page = browser.navigateTo(server);
    page.assertThatPageLoads();
    page.assertVersionIs(AppVersion.VERSION1);
    
    // Wait until the browsers downloads the updated version in the background
    page.waitUntilUpdateIsAvailable(2);
    page.assertAppCacheStatusIs(AppCache.Status.UPDATE_READY);
    browser.screenshot("update-ready");

    // Force browser update to get new version
    page.click(ElementId.LOAD_UPDATE);

    // Wait for the reload with the new version
    page.assertThatPageLoads();
    page.assertVersionIs(AppVersion.VERSION2);
    browser.screenshot("update-loaded");
  }
  

  @After
  public void tearDown() throws Exception {
    browser.shutdown();
    server.stopIfRunning();
  }
}

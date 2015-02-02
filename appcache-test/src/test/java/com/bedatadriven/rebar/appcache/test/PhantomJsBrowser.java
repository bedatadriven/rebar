package com.bedatadriven.rebar.appcache.test;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PhantomJsBrowser implements Browser {

  private Path workingDir;
  private PhantomJSDriver driver;
  private ScreenshotLogger logger;

  public PhantomJsBrowser(ScreenshotLogger logger) {
    this.logger = logger;
  }

  public void start() {
//    
//    workingDir = workingDir();
//    
//    System.out.println("Working dir: " + workingDir);
//    
//    List<String> args = Lists.newArrayList();
//    args.add("--local-storage-path=" + getLocalStorageDir().toAbsolutePath());
//    args.add("--cookies-file=" + workingDir().resolve("cookies.txt"));
//   
//    DesiredCapabilities capabilities = new DesiredCapabilities();
//    capabilities.setJavascriptEnabled(true);
//    capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, args);
//
//    driver = new PhantomJSDriver(capabilities);
//    driver.manage().window().setSize(new Dimension(800, 600));
//    driver.navigate().to("http://localhost:8080");
    
    driver = new PhantomJSDriver();
  }

  private Path getLocalStorageDir() throws IOException {
    Path localDir = workingDir.resolve("local");
    Files.createDirectories(localDir);
    return localDir;
  }

  @Override
  public WebDriver driver() {
    return driver;
  }

  @Override
  public void screenshot(String name) {
    try {
      logger.takeScreenshot(driver, name);
    } catch (IOException e) {
      throw new RuntimeException("Exception while taking screenshot", e);
    }
  }

  @Override
  public PageModel navigateTo(ServerDriver server) {
    if(driver == null) {
      driver = new PhantomJSDriver();
    }
    driver.navigate().to(server.getUrl());
    return new PageModel(driver);
  }


  @Override
  public String expectedUserAgent() {
    return "safari";
  }
  
  public void shutdown() {
    driver.quit();
  }

  @Override
  public boolean isOpen() {
    return driver != null;
  }

  @Override
  public void close() {
    if(driver != null) {
      driver.quit();
      driver = null;
    }
  }
}

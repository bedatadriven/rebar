package com.bedatadriven.rebar.appcache.test;

import com.google.common.base.Preconditions;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Logs screen shots and attaches them to the junit tests
 */
public class ScreenshotLogger extends TestWatcher {


  private Path reportsDir;
  private String testName;

  @Override
  protected void starting(Description description) {
    try {
      testName = description.getMethodName();
      reportsDir = TargetDir.failSafeReportsDir().resolve(description.getClassName());
      if(!Files.exists(reportsDir)) {
        Files.createDirectory(reportsDir);
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not create directory for test attachments", e);
    }
  }
  
  public void takeScreenshot(TakesScreenshot driver, String name) throws IOException {
    assert driver != null;
    
    byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
    Path screenshotPath = reportsDir.resolve(testName + "-" + name + ".png");
    Files.write(screenshotPath, screenshot);
    
    // Write out tag for the Jenkins JUnit Attachment plugin
    System.out.println(String.format("[[ATTACHMENT|%s]]", screenshotPath.toAbsolutePath().toString()));
  }
}

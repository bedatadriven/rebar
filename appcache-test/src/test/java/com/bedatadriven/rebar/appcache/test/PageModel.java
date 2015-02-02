package com.bedatadriven.rebar.appcache.test;

import com.bedatadriven.rebar.appcache.client.AppCache;
import com.bedatadriven.rebar.appcache.test.client.AppVersion;
import com.bedatadriven.rebar.appcache.test.client.ElementId;
import com.bedatadriven.rebar.appcache.test.client.Messages;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementValue;

public class PageModel {
  private WebDriver driver;
  private Map<ElementId, WebElement> elements = Maps.newHashMap();
  
  public PageModel(WebDriver driver) {
    this.driver = driver;
  }

  /**
   * Waits until the test page loads
   */
  public void assertThatPageLoads() {
    WebDriverWait wait = new WebDriverWait(driver, 10);
    WebElement element = wait.until(presenceOfElementLocated(By.id(ElementId.COMPILE_USER_AGENT.id())));
    elements.put(ElementId.COMPILE_USER_AGENT, element);
  }


  public String get(ElementId elementId) {
    WebElement element = elements.get(elementId);
    if(element == null) {
      List<WebElement> found = driver.findElements(By.id(elementId.id()));
      if(found.isEmpty()) {
        return null;
      } else {
        element = found.get(0);
        elements.put(elementId, element);
      }
    }
    return element.getText();
  }

  public void assertThat(final ElementId id, final Matcher<String> matcher) {
    Assert.assertThat(get(id), matcher);
  }
  
  public void assertThat(final ElementId id, final Matcher<String> matcher, int withinSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, withinSeconds);
    wait.until(new Predicate<WebDriver>() {
      @Override
      public boolean apply(WebDriver input) {
        return matcher.matches(get(id));
      }
    });
  }
  
  public static int within(int seconds) {
    return seconds;
  }
  
  public void assertAppCacheDownloadsWithProgress() {
    WebDriverWait wait = new WebDriverWait(driver, 30, 50);
    wait.until(new Predicate<WebDriver>() {
      @Override
      public boolean apply(WebDriver input) {

        String completed = get(ElementId.PROGRESS_FILES_COMPLETE);
        String total = get(ElementId.PROGRESS_FILES_TOTAL);
        String status = get(ElementId.APP_CACHE_STATUS);

        System.out.println(String.format("%s - %s/%s", status, completed, total));

        if (Strings.isNullOrEmpty(total) || !total.equals(completed) ) {
          // Not complete
          return false;
        }
        
        if (!AppCache.Status.IDLE.name().equals(status)) {
          return false;
        }

      
        return true;
      }
    });
  }
  
  
  public void click(ElementId id) {
    WebElement button = driver.findElement(By.id(id.id()));
    if(!button.isEnabled()) {
      throw new AssertionError("Button " + id.id() + " is not enabled");
    }
    button.click();
    
    // clear our element cache as this might be a new page
    elements.clear();
  }

  public void assertUserAgentMatches(Browser browser) {
    assertThat(ElementId.COMPILE_USER_AGENT, equalTo(browser.expectedUserAgent()));
    assertThat(ElementId.RUNTIME_USER_AGENT, equalTo(browser.expectedUserAgent()));
  }

  public void assertVersionIs(AppVersion version) {
    assertThat(ElementId.VERSION_LABEL, equalTo(version.name()));
  }

  public void assertAsyncFragmentLoads() {
    click(ElementId.LOAD_ASYNC_BUTTON);
    assertThat(ElementId.LOAD_ASYNC_BUTTON, equalTo(Messages.ASYNC_FRAGMENT_LOADED), within(10));
  }
  
  private Predicate<WebDriver> valueToBe(final ElementId element, final String text) {
    return new Predicate<WebDriver>() {
      @Override
      public boolean apply(WebDriver input) {
        return get(element).equals(text);
      }
    };
  }
  
  public void waitForStatus(AppCache.Status expectedStatus) {
    WebDriverWait wait = new WebDriverWait(driver, 30);
    wait.until(valueToBe(ElementId.APP_CACHE_STATUS, expectedStatus.name()));
  }
  
  public void waitUntilUpdateIsAvailable(int minutes) {
    WebDriverWait wait = new WebDriverWait(driver, minutes * 60);
    wait.until(valueToBe(ElementId.UPDATE_STATUS, Messages.UPDATE_AVAILABLE));
  }

  public void assertAppCacheStatusIs(AppCache.Status expectedStatus) {
    assertThat(ElementId.APP_CACHE_STATUS, equalTo(expectedStatus.name()));
  }

}

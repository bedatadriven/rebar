package com.bedatadriven.rebar.style.rebind.icons;

import com.bedatadriven.rebar.style.rebind.icons.source.IconSource;

/**
 * An individual icon to be made available to the application as a CSS class
 */
public class Icon {

  private String accessorName;
  private String className;
  private IconSource source;

  public Icon(String accessorName, IconSource source) {
    this.accessorName = accessorName;
    this.className = accessorName;
    this.source = source;
  }

  public String getAccessorName() {
    return accessorName;
  }

  public IconSource getSource() {
    return source;
  }

  public String getClassName() {
    return className;
  }
}

package com.bedatadriven.rebar.appcache.client;

public enum AppCacheErrorType {
  /**
   * There was a connection problem while checking for updates
   */
  CONNECTION,

  /**
   * Checking/Downloaded timed out. (In chrome, this can be a result of this
   * bug: https://code.google.com/p/chromium/issues/detail?id=258191)
   */
  TIMEOUT,

  /**
   * The cache manifest has been marked as obsolete
   */
  OBSOLETE,

  /**
   * The app cache is unsupported in this browser
   */
  UNSUPPORTED,

  /**
   * A javascript exception occured during the update
   */
  EXCEPTION,

  /**
   * The host page has no manifest
   */
  MISSING_MANIFEST
}

package com.bedatadriven.rebar.style.rebind;

import com.google.gwt.core.ext.*;


/**
 * Convenience wrapper for accessing/testing options
 */
public class GenerationParameters {

    private TreeLogger.Type getNamingConventionsEnforcementLevel;
    private boolean requireAccessorsForAllClasses;
    private UserAgent userAgent;
    private boolean ignoreMissingClasses;

    public TreeLogger.Type getGetNamingConventionsEnforcementLevel() {
        return getNamingConventionsEnforcementLevel;
    }

    public void setNamingConventionEnforcementLevel(TreeLogger.Type enforceNamingConventions) {
        this.getNamingConventionsEnforcementLevel = enforceNamingConventions;
    }

    public boolean isAccessorRequiredForAllClasses() {
        return requireAccessorsForAllClasses;
    }

    public void setRequireAccessorsForAllClasses(boolean requireAccessorsForAllClasses) {
        this.requireAccessorsForAllClasses = requireAccessorsForAllClasses;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public void setIgnoreMissingClasses(boolean ignoreMissingClasses) {
        this.ignoreMissingClasses = ignoreMissingClasses;
    }

    public boolean isIgnoreMissingClasses() {
        return ignoreMissingClasses;
    }
}

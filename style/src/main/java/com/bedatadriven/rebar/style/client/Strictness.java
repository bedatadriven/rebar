package com.bedatadriven.rebar.style.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines how strict we are with regards to the validation of CSS/LESS
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Strictness {
    boolean requireAccessorsForAllClasses() default false;
    boolean ignoreMissingClasses() default false;
}

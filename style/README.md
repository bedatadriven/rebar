
Rebar-Style provides a replacement for GWT {@code CSSResources} that supports LESS stylesheets,
font-faces, icon fonts, and CSS3.

The library includes the LESS compiler version 1.7.0 compiled to JVM byte code using Mozilla Rhino
and a GWT generator that

# Usage

To use a LESS stylesheet in your project, place the LESS source in your java source tree and define
an interface that extends com.bedatadriven.rebar.style.Stylesheet.

For example, in your project, you might have:

    src/main/java/com/acme/client/Base.less
    src/main/java/com/acme/client/BaseStylesheet.java
    src/main/java/com/acme/client/GreetingWidget.java
    src/main/java/com/acme/client/GreetingWidget.ui.xml

Where Base.less contains rule definitions and can include other LESS / CSS files

    @include "bootstrap/variables"
    @include "bootstrap/mixins";
    @include "application-variables"

    .greeting-widget {
        background: @brand-primary;
        color: white;
    }

    .alert-sm {
        font-size: 0.5em;
    }

And your interface might contain:

    public interface BaseStylesheet extends Stylesheet {

       public static final BaseStylesheet INSTANCE = GWT.create(BaseStylesheet.class);

       String greetingWidget();

       @ClassName("alert-sm")
       String smallAlert();
    }

Before using the style names, you must call `ensureInjected()`, for example:

    public class GreetingWidget extends Composite {

       public GreetingWidget() {
          BaseStylesheet.INSTANCE.ensureInjected();
       }

    }


To support the dominant naming convention in the LESS community, the generator will automatically map
camelCase accessor like `greetingWidget` to the hyphenated form `greeting-widget`.

The mapping can also be provided explicitly with the @ClassName annotation.

If the stylesheet contains both a `greetingWidget` and a `greeting-widget` class, the generator will
fail unless a @ClassName annotation is provided to resolve the ambiguity.

# Differences from CSSResource

Rebar-Style differs from CSSResource in a number of important ways.

- The Stylesheet interface and generator is independent of the ClientBundle system. You do not have to/
  cannot include your Stylesheet in a ClientBundle, though the Stylesheet interface extends CSSResource
  and so can be used as a drop-in replacement in all other regards.

- By default, class names are not obfuscated.

- Image sprites are not supported; consider using icon fonts instead (see below for details)

- RTL flipping is not currently supported but could be added in the future.

# Differences from LESS

Rebar-Style includes the official LESS compiler so there are no known differences.

However, Rebar-Style uses the Closure Stylesheet Compiler (GSS) to post-process and optimize the output
of the LESS compiler, so there are a few caveats:

- GSS will not reject some CSS hacks intended to workaround browser compatibility issues, for example,
  Bootstrap includes syntax errors like `color:#000\9;` that are known to be accepted only by IE8-9
  and so can be used to craft browser-specific rules.


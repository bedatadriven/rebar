package com.bedatadriven.rebar.style.rebind;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class AccessorBindingsTest {

    private TreeLogger logger;
    private GenerationParameters params = new GenerationParameters();
    private Set<String> classNames;
    private List<JMethod> methods;
    private AccessorBindings bindings;

    @Before
    public void setup() throws UnableToCompleteException {
        logger = new ConsoleTreeLogger();
        methods = Lists.newArrayList();
    }

    @Test
    public void hyphenatedMatching() throws NotFoundException, UnableToCompleteException {

        givenStylesheetWithClassNames("container-blue", "other-widget");
        withAccessor("containerBlue");

        thenTryMatching();

        assertMapping("containerBlue", "container-blue");
    }

    @Test
    public void strictMatchingFailsOnUnmatched() throws NotFoundException, UnableToCompleteException {

        givenStylesheetWithClassNames("container-blue", "important-widget");
        withAccessor("containerBlue");

        thenTryMatching();
    }

    @Test
    public void strictMatchingCanSucceed() throws UnableToCompleteException {
        usingStrict();
        givenStylesheetWithClassNames("important-widget", "alert-bg", "container-blue");

        withAccessor("containerBlue");
        withAccessor("alertBg");
        withAccessor("importantWidget");

        thenTryMatching();
    }


    @Test
    public void camelCaseMatching() throws UnableToCompleteException {
        givenStylesheetWithClassNames("importantWidget");

        withAccessor("importantWidget");

        thenTryMatching();

        assertMapping("importantWidget", "importantWidget");
    }

    @Test(expected = UnableToCompleteException.class)
    public void ambiguousMatching() throws UnableToCompleteException {
        givenStylesheetWithClassNames("greeting-widget", "greetingWidget");

        withAccessor("greetingWidget");
        thenTryMatching();
    }

    @Test(expected = UnableToCompleteException.class)
    public void throwsErrorOnNoMatch() throws UnableToCompleteException {
        givenStylesheetWithClassNames("important-widget");

        withAccessor("importantwidgest");

        thenTryMatching();
    }

    private void givenStylesheetWithClassNames(String... names) throws UnableToCompleteException {
        this.classNames = Sets.newHashSet(names);
    }

    private void withAccessor(String methodName) {
        JMethod method = createNiceMock(methodName, JMethod.class);
        expect(method.getName()).andReturn(methodName).anyTimes();
        replay(method);
        methods.add(method);
    }

    private void thenTryMatching() throws UnableToCompleteException {
        bindings = new AccessorBindings(params, methods, classNames);
        bindings.build(logger);

        System.out.println(Joiner.on("\n").withKeyValueSeparator(" -> ").join(bindings.getMap()));

        bindings.validate(logger, params);
    }

    private void assertMapping(String accessor, String cssName) {
        assertThat(accessor, bindings.classNameForAccessor(accessor), equalTo(cssName));
    }

    private void usingStrict() {
        params.setRequireAccessorsForAllClasses(true);
    }
}

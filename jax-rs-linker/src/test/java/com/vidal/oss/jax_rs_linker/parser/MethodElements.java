package com.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;

public class MethodElements {

    private final Elements elements;

    public MethodElements(Elements elements) {
        this.elements = elements;
    }

    public ExecutableElement of(String className, final String methodName) {
        return (ExecutableElement) FluentIterable.from(elements.getAllMembers(elements.getTypeElement(className)))
            .firstMatch(new Predicate<Element>() {
                @Override
                public boolean apply(Element input) {
                    return input.getSimpleName().contentEquals(methodName);
                }
            })
            .get();
    }
}

package com.vidal.oss.jax_rs_linker.predicates;

import com.google.common.base.Predicate;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

public class AnnotationMatchesElement implements Predicate<Class<? extends Annotation>> {

    private final Element element;

    private AnnotationMatchesElement(Element element) {
        this.element = element;
    }

    public static Predicate<Class<? extends Annotation>> BY_ELEMENT(Element element) {
        return new AnnotationMatchesElement(element);
    }

    @Override
    public boolean apply(Class<? extends Annotation> input) {
        return element.getAnnotation(input) != null;
    }

}

package com.vidal.oss.jax_rs_linker.predicates;

import com.google.common.base.Predicate;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkArgument;

public class ElementHasAnnotation<T extends Element> implements Predicate<T> {

    private final Class<? extends Annotation> annotationType;

    private ElementHasAnnotation(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    public static <T extends Element> Predicate<T> BY_ANNOTATION(Class<? extends Annotation> annotationType) {
        checkArgument(annotationType != null);
        return new ElementHasAnnotation<>(annotationType);
    }

    @Override
    public boolean apply(T input) {
        return input.getAnnotation(annotationType) != null;
    }
}

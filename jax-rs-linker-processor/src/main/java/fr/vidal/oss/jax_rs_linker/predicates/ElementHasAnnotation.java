package fr.vidal.oss.jax_rs_linker.predicates;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;

public class ElementHasAnnotation implements Predicate<Element> {

    private final Class<? extends Annotation> annotationType;

    private ElementHasAnnotation(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    public static Predicate<Element> byAnnotation(Class<? extends Annotation> annotationType) {
        checkArgument(annotationType != null);
        return new ElementHasAnnotation(annotationType);
    }

    @Override
    public boolean test(Element input) {
        return input.getAnnotation(annotationType) != null;
    }
}

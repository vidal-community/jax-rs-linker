package fr.vidal.oss.jax_rs_linker.predicates;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.annotation.Annotation;
import javax.lang.model.element.Element;
import com.google.common.base.Predicate;

public class ElementHasAnnotation implements Predicate<Element> {

    private final Class<? extends Annotation> annotationType;

    private ElementHasAnnotation(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    public static Predicate<Element> BY_ANNOTATION(Class<? extends Annotation> annotationType) {
        checkArgument(annotationType != null);
        return new ElementHasAnnotation(annotationType);
    }

    @Override
    public boolean apply(Element input) {
        return input.getAnnotation(annotationType) != null;
    }
}

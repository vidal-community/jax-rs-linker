package fr.vidal.oss.jax_rs_linker.predicates;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

public class AnnotationMatchesElement implements Predicate<Class<? extends Annotation>> {

    private final Element element;

    private AnnotationMatchesElement(Element element) {
        this.element = element;
    }

    public static Predicate<Class<? extends Annotation>> BY_ELEMENT(Element element) {
        return new AnnotationMatchesElement(element);
    }

    @Override
    public boolean test(Class<? extends Annotation> input) {
        return element.getAnnotation(input) != null;
    }
}

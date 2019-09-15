package fr.vidal.oss.jax_rs_linker.predicates;

import javax.lang.model.element.AnnotationMirror;
import java.util.function.Predicate;

public class AnnotationMirrorByNamePredicate implements Predicate<AnnotationMirror> {

    private final String annotationName;

    private AnnotationMirrorByNamePredicate(String annotationName) {
        this.annotationName = annotationName;
    }

    public static Predicate<AnnotationMirror> byName(String name) {
        return new AnnotationMirrorByNamePredicate(name);
    }

    @Override
    public boolean test(AnnotationMirror input) {
        return input.getAnnotationType().asElement().getSimpleName().contentEquals(annotationName);
    }
}

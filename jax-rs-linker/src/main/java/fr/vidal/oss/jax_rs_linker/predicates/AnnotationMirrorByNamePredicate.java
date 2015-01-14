package fr.vidal.oss.jax_rs_linker.predicates;

import com.google.common.base.Predicate;

import javax.lang.model.element.AnnotationMirror;

public class AnnotationMirrorByNamePredicate implements Predicate<AnnotationMirror> {

    private final String annotationName;

    private AnnotationMirrorByNamePredicate(String annotationName) {
        this.annotationName = annotationName;
    }

    public static Predicate<AnnotationMirror> byName(String name) {
        return new AnnotationMirrorByNamePredicate(name);
    }

    @Override
    public boolean apply(AnnotationMirror input) {
        return input.getAnnotationType().asElement().getSimpleName().contentEquals(annotationName);
    }
}

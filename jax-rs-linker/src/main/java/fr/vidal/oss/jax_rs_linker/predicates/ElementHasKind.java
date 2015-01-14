package fr.vidal.oss.jax_rs_linker.predicates;

import com.google.common.base.Predicate;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class ElementHasKind implements Predicate<Element> {

    private final ElementKind kind;

    private ElementHasKind(ElementKind kind) {
        this.kind = kind;
    }

    public static Predicate<Element> byKind(ElementKind kind) {
        if (kind == null) {
            throw new IllegalArgumentException("Kind cannot be null");
        }
        return new ElementHasKind(kind);
    }

    @Override
    public boolean apply(Element input) {
        return input != null && input.getKind() == kind;
    }
}

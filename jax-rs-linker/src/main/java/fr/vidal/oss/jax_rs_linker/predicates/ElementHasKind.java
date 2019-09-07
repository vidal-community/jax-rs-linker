package fr.vidal.oss.jax_rs_linker.predicates;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.function.Predicate;

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
    public boolean test(Element input) {
        return input != null && input.getKind() == kind;
    }
}

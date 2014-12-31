package net.biville.florent.jax_rs_linker.predicates;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;

public class EntryByMethodNamePredicate implements Predicate<Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> {

    private final String name;

    private EntryByMethodNamePredicate(String name) {
        this.name = name;
    }

    public static Predicate<Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> byMethodName(String name) {
        return new EntryByMethodNamePredicate(name);
    }

    @Override
    public boolean apply(@Nullable Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> input) {
        return input.getKey().getSimpleName().contentEquals("value");
    }
}

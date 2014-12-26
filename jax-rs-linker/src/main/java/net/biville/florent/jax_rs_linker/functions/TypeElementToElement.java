package net.biville.florent.jax_rs_linker.functions;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class TypeElementToElement<T extends TypeElement> implements Function<T, Set<? extends Element>> {

    private final RoundEnvironment roundEnvironment;

    private TypeElementToElement(RoundEnvironment roundEnvironment) {
        this.roundEnvironment = roundEnvironment;
    }

    public static <T extends TypeElement> Function<T, Set<? extends Element>> intoElement(RoundEnvironment roundEnvironment) {
        return new TypeElementToElement<>(roundEnvironment);
    }

    @Nullable
    @Override
    public Set<? extends Element> apply(T input) {
        return roundEnvironment.getElementsAnnotatedWith(input);
    }
}

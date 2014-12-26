package net.biville.florent.jax_rs_linker.functions;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;

public enum EntryToStringValueFunction implements Function<Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>, String> {

    TO_STRING_VALUE;

    @Nullable
    @Override
    public String apply(@Nullable Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> input) {
        return input.getValue().getValue().toString();
    }
}

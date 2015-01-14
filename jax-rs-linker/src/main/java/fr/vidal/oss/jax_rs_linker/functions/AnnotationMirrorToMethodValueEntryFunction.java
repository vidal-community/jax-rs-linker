package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;

public enum AnnotationMirrorToMethodValueEntryFunction implements Function<AnnotationMirror, Iterable<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>>> {

    TO_METHOD_VALUE_ENTRIES;

    @Nullable
    @Override
    public Iterable<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> apply(AnnotationMirror input) {
        return input.getElementValues().entrySet();
    }
}

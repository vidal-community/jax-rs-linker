package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.SubResourceTarget;

import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;

public enum EntriesToStringValueFunction implements Function<Iterable<? extends Map.Entry<? extends ExecutableElement,? extends AnnotationValue>>, SubResourceTarget> {

    TO_STRING_VALUE;

    @Override
    public SubResourceTarget apply(Iterable<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> methodValues) {
        Optional<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> targetClass = valueByMethodName(methodValues, "value");
        if (!targetClass.isPresent()) {
            throw new IllegalArgumentException("Missing method on @SubResource");
        }
        Optional<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> qualifier = valueByMethodName(methodValues, "qualifier");
        String className = extractStringValue(targetClass);
        String qualifierSuffix = extractStringValue(qualifier);
        return new SubResourceTarget(ClassName.valueOf(className), qualifierSuffix);
    }

    private Optional<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> valueByMethodName(Iterable<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> methodValues,
                                                                                                                    final String name) {

        return FluentIterable.from(methodValues)
            .firstMatch(new Predicate<Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>>() {
                @Override
                public boolean apply(@Nullable Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> input) {
                    return input.getKey().getSimpleName().contentEquals(name);
                }
            });
    }

    private String extractStringValue(Optional<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> target) {
        if (!target.isPresent()) {
            return "";
        }
        return target.get().getValue().getValue().toString();
    }
}

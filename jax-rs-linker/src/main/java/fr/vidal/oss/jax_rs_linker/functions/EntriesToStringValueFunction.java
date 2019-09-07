package fr.vidal.oss.jax_rs_linker.functions;

import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.SubResourceTarget;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

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

        return StreamSupport.stream(methodValues.spliterator(), false)
            .filter(input -> input.getKey().getSimpleName().contentEquals(name))
            .findFirst();
    }

    private String extractStringValue(Optional<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> target) {
        return target.map(e -> e.getValue().getValue().toString()).orElse("");
    }
}

package fr.vidal.oss.jax_rs_linker.parser;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.lang.model.util.Types;
import javax.ws.rs.BeanParam;

class ParameterVisitor<T> extends SimpleElementVisitor7<Collection<T>, Void> {

    private final Types typeUtils;
    private final AnnotatedElementMapping<T> annotatedElementMapping;

    public ParameterVisitor(Types typeUtils, AnnotatedElementMapping<T> annotatedElementMapping) {
        this.typeUtils = typeUtils;
        this.annotatedElementMapping = annotatedElementMapping;
    }

    @Override
    public Collection<T> visitVariable(VariableElement element, Void aVoid) {
        return mapMatchingElements(element);
    }

    @Override
    public Collection<T> visitExecutable(ExecutableElement executableElement, Void aVoid) {
        Collection<T> results = new ArrayList<>();

        if (isInjectedSetter(executableElement)) {
            return map(executableElement);
        }

        for (VariableElement parameter : executableElement.getParameters()) {
            results.addAll(mapMatchingElements(parameter));
        }
        return results;
    }

    private boolean isInjectedSetter(ExecutableElement executableElement) {
        return isSetter(executableElement) && hasAnnotation(executableElement, annotatedElementMapping.getAnnotationType());
    }

    private boolean isSetter(ExecutableElement executableElement) {
        return isMethod(executableElement)
            && executableElement.getSimpleName().toString().startsWith("set")
            && typeUtils.isSameType(executableElement.getReturnType(), typeUtils.getNoType(TypeKind.VOID))
            && executableElement.getParameters().size() == 1;
    }

    private Collection<T> map(ExecutableElement executableElement) {
        return asList(annotatedElementMapping.map(executableElement));
    }

    private boolean hasAnnotation(ExecutableElement executableElement, Class<? extends Annotation> annotationType) {
        return executableElement.getAnnotation(annotationType) != null;
    }

    private boolean isMethod(ExecutableElement executableElement) {
        return executableElement.getKind().equals(ElementKind.METHOD);
    }

    private Collection<T> mapMatchingElements(Element parameter) {
        Collection<T> results = new ArrayList<>();
        Annotation annotation = parameter.getAnnotation(annotatedElementMapping.getAnnotationType());
        if (annotation != null) {
            results.add(annotatedElementMapping.mapParameter(parameter));
        }

        if (parameter.getAnnotation(BeanParam.class) != null) {
            Element object = typeUtils.asElement(parameter.asType());
            for (Element enclosedElement : object.getEnclosedElements()) {
                results.addAll(this.visit(enclosedElement));
            }
        }
        return results;
    }
}

package fr.vidal.oss.jax_rs_linker.parser;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;

public class MethodElements {

    private final Elements elements;

    public MethodElements(Elements elements) {
        this.elements = elements;
    }

    public ExecutableElement of(String className, final String methodName) {
        return (ExecutableElement) elements.getAllMembers(elements.getTypeElement(className)).stream()
            .filter(input -> input.getSimpleName().contentEquals(methodName))
            .findFirst()
            .get();
    }
}

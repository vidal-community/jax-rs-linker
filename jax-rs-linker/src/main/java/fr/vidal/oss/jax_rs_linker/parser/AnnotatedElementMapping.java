package fr.vidal.oss.jax_rs_linker.parser;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.util.function.Function;

public class AnnotatedElementMapping<T> {

    private final Class<? extends Annotation> annotationType;
    private final Function<Element, T> parameterMapper;
    /**
     * A specific mapper is required on setters, given @XxxParam is defined on the method, not on its parameter.
     * e.g.:
     * {@code
     *  // Injection using bean setter method
     *  @HeaderParam("X-header")
     *  public void setHeader(String header) { ... }
     * }
     *
     * @see <a href="https://jersey.java.net/documentation/latest/user-guide.html#d0e2633">Jersey Injection Rules</a>
     */
    private final Function<ExecutableElement, T> setterMapper;

    public AnnotatedElementMapping(Class<? extends Annotation> annotationType,
                                   Function<Element, T> parameterMapper,
                                   Function<ExecutableElement, T> setterMapper) {

        this.annotationType = annotationType;
        this.parameterMapper = parameterMapper;
        this.setterMapper = setterMapper;
    }

    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }


    public T mapParameter(Element parameter) {
        return parameterMapper.apply(parameter);
    }

    public T map(ExecutableElement setter) {
        return setterMapper.apply(setter);
    }
}

package fr.vidal.oss.jax_rs_linker.functions;

import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.ws.rs.PathParam;
import com.google.common.base.Function;

public enum SetterToPathParameter implements Function<ExecutableElement, PathParameter> {

    SETTER_TO_PATH_PARAMETER;

    @Override
    public PathParameter apply(ExecutableElement input) {
        VariableElement singleParameter = input.getParameters().iterator().next();
        return new PathParameter(
            ClassName.valueOf(singleParameter.asType().toString()),
            input.getAnnotation(PathParam.class).value()
        );
    }

}

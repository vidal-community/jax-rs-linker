package net.biville.florent.jax_rs_linker.processor.functions;

import com.google.common.base.Function;
import net.biville.florent.jax_rs_linker.processor.model.ClassName;
import net.biville.florent.jax_rs_linker.processor.model.PathParameter;

import javax.annotation.Nullable;
import javax.lang.model.element.VariableElement;
import javax.ws.rs.PathParam;

public enum VariableElementToPathParameter implements Function<VariableElement, PathParameter> {

    INTO_PATH_PARAMETER;

    @Nullable
    @Override
    public PathParameter apply(VariableElement input) {
        return new PathParameter(
            ClassName.valueOf(input.asType().toString()),
            input.getAnnotation(PathParam.class).value()
        );
    }
}

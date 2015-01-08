package com.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import com.vidal.oss.jax_rs_linker.model.ClassName;
import com.vidal.oss.jax_rs_linker.model.PathParameter;

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

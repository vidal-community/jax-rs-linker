package com.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import com.vidal.oss.jax_rs_linker.model.PathParameter;

import javax.annotation.Nullable;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

public enum PathParameterToString implements Function<PathParameter, String> {

    TO_STRING;

    @Nullable
    @Override
    public String apply(@Nullable PathParameter pathParameter) {
        String pathParameterName = pathParameter.getName();
        return String.format("%s(\"%s\")", UPPER_CAMEL.to(UPPER_UNDERSCORE, pathParameterName), pathParameterName);
    }
}

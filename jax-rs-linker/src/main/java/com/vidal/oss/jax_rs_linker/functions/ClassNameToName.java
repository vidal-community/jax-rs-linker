package com.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import com.vidal.oss.jax_rs_linker.model.ClassName;

import javax.annotation.Nullable;

public enum ClassNameToName implements Function<ClassName, String> {

    INTO_NAME;

    @Nullable
    @Override
    public String apply(ClassName className) {
        return className.getName();
    }
}
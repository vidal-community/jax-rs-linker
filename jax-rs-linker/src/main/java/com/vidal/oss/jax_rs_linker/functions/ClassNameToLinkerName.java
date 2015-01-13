package com.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;
import com.vidal.oss.jax_rs_linker.LinkerAnnotationProcessor;
import com.vidal.oss.jax_rs_linker.model.ClassName;

import javax.annotation.Nullable;

public enum ClassNameToLinkerName implements Function<ClassName, String> {
    TO_LINKER_NAME;

    @Nullable
    @Override
    public String apply(ClassName className) {
        return className.fullyQualifiedName() + LinkerAnnotationProcessor.GENERATED_CLASSNAME_SUFFIX;
    }
}

package fr.vidal.oss.jax_rs_linker.functions;

import com.google.common.base.Function;

import javax.annotation.Nullable;

public enum ClassToName implements Function<Class<?>, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(Class<?> input) {
        return input.getName();
    }
}

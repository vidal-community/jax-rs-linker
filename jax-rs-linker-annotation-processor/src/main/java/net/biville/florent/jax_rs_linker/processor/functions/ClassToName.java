package net.biville.florent.jax_rs_linker.processor.functions;

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
